package pl.notkris.aaapi.provider;

import java.lang.reflect.Constructor;

/**
 * Default {@link InstanceProvider} implementation.
 *
 * <p>Creates instances using the no-arg constructor via reflection.
 * The constructor doesn't have to be public.
 * Sufficient for classes with no dependencies.
 *
 * <p>For dependency injection support, replace with {@code GuiceInstanceProvider}
 * from the {@code guice} module.
 */
public class DefaultInstanceProvider implements InstanceProvider {

    @Override
    public Object getInstance(Class<?> clazz) throws Exception {
        Constructor<?> constructor = clazz.getDeclaredConstructor();
        constructor.setAccessible(true);
        return constructor.newInstance();
    }
}
