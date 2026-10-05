package pl.notkris.example;

import com.google.inject.AbstractModule;
import org.bukkit.Server;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;

import java.util.logging.Logger;

/**
 * Guice module exposing the plugin and common server objects,
 * so discovered classes can simply {@code @Inject} them.
 */
public class PluginModule extends AbstractModule {

    private final ExamplePlugin plugin;

    public PluginModule(ExamplePlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    protected void configure() {
        bind(ExamplePlugin.class).toInstance(plugin);
        bind(Plugin.class).toInstance(plugin);
        bind(Server.class).toInstance(plugin.getServer());
        bind(PluginManager.class).toInstance(plugin.getServer().getPluginManager());
        bind(Logger.class).toInstance(plugin.getLogger());
    }
}
