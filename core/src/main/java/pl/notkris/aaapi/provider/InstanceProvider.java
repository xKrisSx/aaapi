package pl.notkris.aaapi.provider;

/**
 * Strategy interface for creating instances of registered classes.
 *
 * <p>Swap implementations to change how instances are created:
 * <ul>
 *   <li>{@link DefaultInstanceProvider} - plain reflection, no-arg constructor</li>
 *   <li>{@code GuiceInstanceProvider} - Google Guice, supports {@code @Inject}</li>
 * </ul>
 */
public interface InstanceProvider {

    /**
     * Creates or retrieves an instance of the given class.
     *
     * @param clazz the class to instantiate
     * @return the created instance
     * @throws Exception if instantiation fails for any reason
     */
    Object getInstance(Class<?> clazz) throws Exception;
}
