package pl.notkris.example;

import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import org.bukkit.plugin.java.JavaPlugin;
import pl.notkris.aaapi.provider.DefaultInstanceProvider;
import pl.notkris.aaapi.registry.LoaderRegistry;
import pl.notkris.example.loader.CommandLoader;
import pl.notkris.example.loader.ListenerLoader;
import pl.notkris.example.loader.TaskLoader;

/**
 * Example plugin demonstrating AAAPI integration with Paper's Brigadier command API,
 * Bukkit event listeners, and scheduled tasks.
 *
 * <p>Commands are registered inside {@link LifecycleEvents#COMMANDS} handler -
 * required by Paper's modern command API. Listeners and tasks are registered beforehand.
 */
@SuppressWarnings("UnstableApiUsage")
public class ExamplePlugin extends JavaPlugin {

    @Override
    public void onEnable() {
        register();

        getLogger().info("plugin enabled");
    }

    @Override
    public void onDisable() {
        getLogger().info("plugin disabled");
    }

    private void register() {
        LoaderRegistry registry = new LoaderRegistry(
                "pl.notkris.example",
                getClass().getClassLoader(),
                new DefaultInstanceProvider()
        );

        registry.register(new ListenerLoader(getServer().getPluginManager(), this));
        registry.register(new TaskLoader(this));

        // commands must be registered inside the COMMANDS lifecycle event
        this.getLifecycleManager().registerEventHandler(LifecycleEvents.COMMANDS, event -> {
            registry.register(new CommandLoader(event.registrar()));
            registry.loadAll();
        });
    }
}
