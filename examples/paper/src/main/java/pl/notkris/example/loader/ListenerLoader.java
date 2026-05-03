package pl.notkris.example.loader;

import org.bukkit.event.Listener;
import org.bukkit.plugin.PluginManager;
import pl.notkris.aaapi.loader.ClassTypeLoader;
import pl.notkris.example.ExamplePlugin;
import pl.notkris.example.annotation.PhysicListener;
import pl.notkris.example.utils.WorldUtils;

import java.lang.annotation.*;
import java.util.logging.Logger;

/**
 * AAAPI loader for Bukkit event listeners.
 *
 * <p>Demonstrates how to check for custom user-defined annotations inside a loader.
 * Listeners additionally annotated with {@link PhysicListener} are tracked in {@link WorldUtils},
 * showing that a single class can carry multiple annotations for different purposes.
 *
 * <pre>{@code
 * @ListenerLoader.RegisteredListener
 * @PhysicListener(isEnabledByDefault = false)
 * public class BlockFromToListener implements Listener { ... }
 * }</pre>
 */
public class ListenerLoader extends ClassTypeLoader {

    /**
     * Annotation associated with this {@link ListenerLoader}.
     * Part of the Paper example - for production use, create your own loader and annotation.
     */
    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.TYPE)
    public @interface RegisteredListener {}

    @Override
    public Class<? extends Annotation> type() {
        return RegisteredListener.class;
    }

    private final PluginManager pluginManager;
    private final ExamplePlugin plugin;
    private final Logger logger;

    public ListenerLoader(PluginManager pluginManager, ExamplePlugin instance) {
        this.pluginManager = pluginManager;
        this.plugin = instance;
        this.logger = plugin.getLogger();
    }

    @Override
    public void process(Class<?> clazz, Object instance) {
        if (!(instance instanceof Listener listener)) return;

        pluginManager.registerEvents(listener, this.plugin);

        // if additionally annotated with @PhysicListener, track it in WorldUtils
        if (clazz.isAnnotationPresent(PhysicListener.class)) {
            PhysicListener physicListener = clazz.getAnnotation(PhysicListener.class);
            boolean isEnabledByDefault = physicListener.isEnabledByDefault();

            WorldUtils.addPhysicListener(listener, isEnabledByDefault);

            logger.info("Registered PhysicListener: " + listener.getClass().getSimpleName());
        }
    }
}
