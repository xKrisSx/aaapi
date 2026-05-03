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
 *     new DefaultInstanceProvider()
 * );
 *
 * registry.register(new CommandLoader(this));
 * registry.register(new ListenerLoader(this));
 * registry.loadAll();
 * }</pre>
 */
public class LoaderRegistry {

    private static final Logger logger = Logger.getLogger("AAAPI");

    private final String basePackage;
    private final ClassLoader classLoader;
    private final InstanceProvider instanceProvider;
    private final List<ClassTypeLoader> loaders = new ArrayList<>();

    /**
     * @param basePackage      the root package to scan recursively (e.g. {@code "com.yourpackage"})
     * @param classLoader      the class loader used to scan and load classes
     * @param instanceProvider the provider used to create instances of discovered classes
     */
    public LoaderRegistry(String basePackage, ClassLoader classLoader, InstanceProvider instanceProvider) {
        this.basePackage = basePackage;
        this.classLoader = classLoader;
        this.instanceProvider = instanceProvider;
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
     * and delegates each to the appropriate {@link ClassTypeLoader}.
     *
     * @throws LoaderNotFoundException if a discovered class has no matching registered loader
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
            ClassTypeLoader loader = findLoader(clazz);

            Object instance;
            try {
                instance = instanceProvider.getInstance(clazz);
                loader.process(clazz, instance);
                success++;
            } catch (Exception e) {
                logger.log(Level.SEVERE, "Failed to register: " + clazz.getSimpleName(), e);
                failed++;
            }
        }
        long time = System.currentTimeMillis() - start;
        logger.info("Registered " + success + "/" + classes.size() + " classes in " + time + "ms");
        if (failed > 0) {
            logger.warning("Failed to register: " + failed + " classes");
        }
    }

    private ClassTypeLoader findLoader(Class<?> clazz) {
        return loaders.stream()
                .filter(loader -> clazz.isAnnotationPresent(loader.type()))
                .findFirst()
                .orElseThrow(() -> new LoaderNotFoundException("No loader registered for class: " + clazz.getName()));
    }
}
