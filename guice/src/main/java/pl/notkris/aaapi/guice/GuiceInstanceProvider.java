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
