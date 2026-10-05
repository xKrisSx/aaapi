package pl.notkris.aaapi.registry;

import pl.notkris.aaapi.exception.LoaderNotFoundException;
import pl.notkris.aaapi.loader.ClassTypeLoader;
import pl.notkris.aaapi.provider.InstanceProvider;
import pl.notkris.aaapi.scanner.AnnotationScanner;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.List;
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
 * <p>A class carrying annotations of several registered loaders is instantiated once,
 * and the same instance is passed to each matching loader.
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
     * If a loader for the same annotation is already registered, it will be replaced.
     *
     * @param loader the loader to register
     */
    public void register(ClassTypeLoader loader) {
        loaders.removeIf(existing -> existing.type() == loader.type());
        loaders.add(loader);
    }

    /**
     * Scans the base package, creates instances of all discovered classes,
     * and delegates each to every matching {@link ClassTypeLoader}.
     *
     * <p>A failure for one class (instantiation or processing) is logged
     * and does not stop the registration of the remaining classes.
     */
    public void loadAll() {
        Set<Class<? extends Annotation>> types = loaders.stream()
                .map(ClassTypeLoader::type)
                .collect(Collectors.toSet());

        AnnotationScanner scanner = new AnnotationScanner(basePackage, classLoader, types);
        List<Class<?>> classes = scanner.scan();

        int success = 0;
        int failed = 0;
        long start = System.currentTimeMillis();

        for (Class<?> clazz : classes) {
            try {
                List<ClassTypeLoader> matching = findLoaders(clazz);
                Object instance = instanceProvider.getInstance(clazz);

                for (ClassTypeLoader loader : matching) {
                    loader.process(clazz, instance);
                }
                success++;
            } catch (Exception e) {
                logger.log(Level.SEVERE, "Failed to register: " + clazz.getName(), e);
                failed++;
            }
        }
        long time = System.currentTimeMillis() - start;
        logger.info("Registered " + success + "/" + classes.size() + " classes in " + time + "ms");
        if (failed > 0) {
            logger.warning("Failed to register: " + failed + " classes");
        }
    }

    private List<ClassTypeLoader> findLoaders(Class<?> clazz) {
        List<ClassTypeLoader> matching = loaders.stream()
                .filter(loader -> clazz.isAnnotationPresent(loader.type()))
                .collect(Collectors.toList());

        if (matching.isEmpty()) {
            throw new LoaderNotFoundException("No loader registered for class: " + clazz.getName());
        }
        return matching;
    }
}
