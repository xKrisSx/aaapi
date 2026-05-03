package pl.notkris.aaapi.loader;

import java.lang.annotation.Annotation;

/**
 * Base class for all type-specific registration handlers.
 *
 * <p>Each loader defines its own annotation via {@link #type()} and handles
 * the registration logic for all classes carrying that annotation in {@link #process}.
 *
 * <p>Example implementation:
 * <pre>{@code
 * public class ListenerLoader extends ClassTypeLoader {
 *
 *  @Retention(RetentionPolicy.RUNTIME)
 *  @Target(ElementType.TYPE)
 *  public @interface RegisteredListener {}
 *
 *  @Override
 *  public Class<? extends Annotation> type() {
 *      return RegisteredListener.class;
 *  }
 *
 *  private final MyPlugin plugin;
 *  public ListenerLoader(MyPlugin plugin) {
 *      this.plugin = plugin;
 *  }
 *
 *  @Override
 *  public void process(Class<?> clazz, Object instance) {
 *      plugin.getServer().getPluginManager().registerEvents((Listener) instance, plugin);
 *  }
 * }
 * }</pre>
 */
public abstract class ClassTypeLoader {

    /**
     * Returns the annotation class that this loader handles.
     * All classes annotated with this annotation will be passed to {@link #process}.
     */
    public abstract Class<? extends Annotation> type();

    /**
     * Performs the registration logic for the given class and its instance.
     *
     * @param clazz    the discovered class carrying the loader's annotation
     * @param instance the instantiated object created by the {@link pl.notkris.aaapi.provider.InstanceProvider}
     */
    public abstract void process(Class<?> clazz, Object instance);
}
