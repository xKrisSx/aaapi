package pl.notkris.example.loader;

import org.bukkit.event.Listener;
import org.bukkit.plugin.PluginManager;
import pl.notkris.aaapi.loader.ClassTypeLoader;
import pl.notkris.example.ExamplePlugin;

import java.lang.annotation.*;

/**
 * AAAPI loader for Bukkit event listeners.
 *
 * <p>Part of the Guice example - instances are created by Guice,
 * so annotated classes can use {@code @Inject} constructors freely:
 *
 * <pre>{@code
 * @ListenerLoader.RegisteredListener
 * public class PlayerJoinListener implements Listener {
 *
 *     private final GreetingService greetingService;
 *
 *     @Inject
 *     public PlayerJoinListener(GreetingService greetingService) {
 *         this.greetingService = greetingService;
 *     }
 * }
 * }</pre>
 */
public class ListenerLoader extends ClassTypeLoader {

    /**
     * Annotation associated with this {@link ListenerLoader}.
     * Part of the Guice example - for production use, create your own loader and annotation.
     */
    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.TYPE)
    public @interface RegisteredListener {}

    private final PluginManager pluginManager;
    private final ExamplePlugin plugin;

    public ListenerLoader(PluginManager pluginManager, ExamplePlugin plugin) {
        this.pluginManager = pluginManager;
        this.plugin = plugin;
    }

    @Override
    public Class<? extends Annotation> type() {
        return RegisteredListener.class;
    }

    @Override
    public void process(Class<?> clazz, Object instance) {
        if (!(instance instanceof Listener listener)) return;

        pluginManager.registerEvents(listener, plugin);
    }
}