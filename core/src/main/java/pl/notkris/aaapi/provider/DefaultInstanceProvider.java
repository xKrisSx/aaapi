package pl.notkris.aaapi.provider;

/**
 * Default {@link InstanceProvider} implementation.
 *
 * <p>Creates instances using the no-arg constructor via reflection.
 * Sufficient for classes with no dependencies.
 *
 * <p>For dependency injection support, replace with {@code GuiceInstanceProvider}
 * from the {@code guice} module.
 */
public class DefaultInstanceProvider implements InstanceProvider {

    @Override
    public Object getInstance(Class<?> clazz) throws Exception {
        return clazz.getDeclaredConstructor().newInstance();
    }
}
