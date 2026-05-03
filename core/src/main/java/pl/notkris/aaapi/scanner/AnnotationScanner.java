package pl.notkris.aaapi.scanner;

import org.reflections.Reflections;
import org.reflections.util.ConfigurationBuilder;
import pl.notkris.aaapi.registry.LoaderRegistry;

import java.lang.annotation.Annotation;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Scans a given base package for classes carrying any of the specified annotations.
 *
 * <p>Uses <a href="https://github.com/ronmamo/reflections">Reflections</a> under the hood,
 * which handles both standard classpath environments and JAR files (e.g. Spigot/Paper plugins).
 *
 * <p>You don't need to use this class directly - {@link LoaderRegistry} creates and manages
 * the scanner internally during {@link LoaderRegistry#loadAll()}.
 */
public class AnnotationScanner {

    private final String basePackage;
    private final ClassLoader classLoader;
    private final Set<Class<? extends Annotation>> annotations;

    /**
     * @param basePackage  the root package to scan recursively (e.g. {@code "com.yourpackage"})
     * @param classLoader  the class loader used to load discovered classes
     * @param annotations  the set of annotation types to look for
     */
    public AnnotationScanner(String basePackage, ClassLoader classLoader, Set<Class<? extends Annotation>> annotations) {
        this.basePackage = basePackage;
        this.classLoader = classLoader;
        this.annotations = annotations;
    }

    /**
     * Scans the base package and returns all classes annotated with any of the registered annotations.
     *
     * @return list of discovered classes, deduplicated
     */
    public List<Class<?>> scan() {
        ConfigurationBuilder config = new ConfigurationBuilder()
                .forPackage(basePackage, classLoader)
                .addClassLoaders(classLoader);

        Reflections reflections = new Reflections(config);

        return annotations.stream()
                .flatMap(annotation -> reflections.getTypesAnnotatedWith(annotation).stream())
                .distinct()
                .collect(Collectors.toList());
    }
}
