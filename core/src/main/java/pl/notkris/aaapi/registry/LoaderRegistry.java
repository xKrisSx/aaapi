package pl.notkris.aaapi.registry;

import pl.notkris.aaapi.loader.ClassTypeLoader;
import pl.notkris.aaapi.provider.InstanceProvider;
import pl.notkris.aaapi.scanner.AnnotationScanner;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 * Central component that ties together scanning, instantiation, and registration.
 *
 * <p>Typical setup:
 * <pre>{@code
 * LoaderRegistry registry = new LoaderRegistry(
 *     "com.yourpackage",
 *     getClass().getClassLoader(),
 *     new DefaultInstanceProvider(),
 *     getLogger()
 * );
 *
 * registry.register(new CommandLoader(this));
 * registry.register(new ListenerLoader(this));
 * registry.loadAll();
 * }</pre>
 *
 * <p>Loaders run in registration order, so in the example above all commands are
 * registered before any listener. A class carrying annotations of several registered
 * loaders is instantiated once, and the same instance is passed to each matching loader.
 */
public class LoaderRegistry {

    private final String basePackage;
    private final ClassLoader classLoader;
    private final InstanceProvider instanceProvider;
    private final Logger logger;
    private final List<ClassTypeLoader> loaders = new ArrayList<>();

    /**
     * Creates a registry that logs to the {@code "AAAPI"} logger.
     *
     * @param basePackage      the root package to scan recursively (e.g. {@code "com.yourpackage"})
     * @param classLoader      the class loader used to scan and load classes
     * @param instanceProvider the provider used to create instances of discovered classes
     */
    public LoaderRegistry(String basePackage, ClassLoader classLoader, InstanceProvider instanceProvider) {
        this(basePackage, classLoader, instanceProvider, Logger.getLogger("AAAPI"));
    }

    /**
     * @param basePackage      the root package to scan recursively (e.g. {@code "com.yourpackage"})
     * @param classLoader      the class loader used to scan and load classes
     * @param instanceProvider the provider used to create instances of discovered classes
     * @param logger           the logger used to report results (e.g. {@code plugin.getLogger()})
     */
    public LoaderRegistry(String basePackage, ClassLoader classLoader, InstanceProvider instanceProvider, Logger logger) {
        this.basePackage = basePackage;
        this.classLoader = classLoader;
        this.instanceProvider = instanceProvider;
        this.logger = logger;
    }

    /**
     * Registers a loader for a specific annotation type.
     * If a loader for the same annotation is already registered, it is replaced
     * and the replacement keeps its position in the processing order.
     *
     * <p>Loaders are run in registration order, see {@link #loadAll()}.
     *
     * @param loader the loader to register
     */
    public void register(ClassTypeLoader loader) {
        for (int i = 0; i < loaders.size(); i++) {
            if (loaders.get(i).type() == loader.type()) {
                loaders.set(i, loader);
                return;
            }
        }
        loaders.add(loader);
    }

    /**
     * Scans the base package once, creates instances of all discovered classes,
     * and delegates each to every matching {@link ClassTypeLoader}.
     *
     * <p>Loaders are run in registration order: the first registered loader processes
     * all of its classes before the next one starts. A class matched by several loaders
     * is instantiated once, when the first of them needs it.
     *
     * <p>A failure for one class (instantiation, processing or a {@link LinkageError}
     * such as {@link NoClassDefFoundError} caused by a missing optional dependency)
     * is logged, the class is skipped by the remaining loaders,
     * and the registration of other classes continues.
     */
    public void loadAll() {
        Set<Class<? extends Annotation>> types = loaders.stream()
                .map(ClassTypeLoader::type)
                .collect(Collectors.toSet());

        AnnotationScanner scanner = new AnnotationScanner(basePackage, classLoader, types);
        List<Class<?>> classes = scanner.scan();

        Map<Class<?>, Object> instances = new HashMap<>();
        Set<Class<?>> failed = new HashSet<>();
        long start = System.currentTimeMillis();

        for (ClassTypeLoader loader : loaders) {
            for (Class<?> clazz : classes) {
                if (failed.contains(clazz)) {
                    continue;
                }
                try {
                    if (!clazz.isAnnotationPresent(loader.type())) {
                        continue;
                    }
                    Object instance = instances.get(clazz);
                    if (instance == null) {
                        instance = instanceProvider.getInstance(clazz);
                        instances.put(clazz, instance);
                    }
                    loader.process(clazz, instance);
                } catch (Exception | LinkageError e) {
                    logger.log(Level.SEVERE, "Failed to register: " + clazz.getName(), e);
                    failed.add(clazz);
                }
            }
        }
        long time = System.currentTimeMillis() - start;
        int success = classes.size() - failed.size();
        logger.info("Registered " + success + "/" + classes.size() + " classes in " + time + "ms");
        if (!failed.isEmpty()) {
            logger.warning("Failed to register: " + failed.size() + " classes");
        }
    }
}
