package pl.notkris.aaapi.guice;

import com.google.inject.Injector;
import pl.notkris.aaapi.provider.InstanceProvider;

/**
 * {@link InstanceProvider} implementation backed by a Guice {@link Injector}.
 *
 * <p>Enables full constructor, field, and method injection via {@code @Inject}
 * for all classes processed by AAAPI.
 *
 * <p>Setup example:
 * <pre>{@code
 * Injector injector = Guice.createInjector(new YourModule());
 *
 * LoaderRegistry registry = new LoaderRegistry(
 *     "com.yourpackage",
 *     getClass().getClassLoader(),
 *     new GuiceInstanceProvider(injector)
 * );
 * }</pre>
 *
 * <p>Notes:
 * <ul>
 *   <li>Guice creates a new instance for every injection point unless the type is scoped -
 *       annotate shared services with {@code @Singleton} (or bind them in a module).</li>
 *   <li>Guice 7 supports {@code com.google.inject.Inject} and {@code jakarta.inject.Inject}.
 *       The legacy {@code javax.inject.Inject} is <b>not</b> recognized.</li>
 * </ul>
 */
public class GuiceInstanceProvider implements InstanceProvider {

    private final Injector injector;

    /**
     * @param injector the Guice injector used to create and inject instances
     */
    public GuiceInstanceProvider(Injector injector) {
        this.injector = injector;
    }

    @Override
    public Object getInstance(Class<?> clazz) {
        return injector.getInstance(clazz);
    }
}
