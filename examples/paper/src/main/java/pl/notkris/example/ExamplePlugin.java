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
 * required by Paper's modern command API. Listeners and tasks are registered once, in {@link #onEnable()}.
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
        LoaderRegistry registry = createRegistry();
        registry.register(new ListenerLoader(getServer().getPluginManager(), this));
        registry.register(new TaskLoader(this));
        registry.loadAll();

        // commands must be registered inside the COMMANDS lifecycle event.
        // It can fire more than once (e.g. on /minecraft:reload), so it uses a separate
        // registry - otherwise listeners and tasks would be registered again on every reload.
        this.getLifecycleManager().registerEventHandler(LifecycleEvents.COMMANDS, event -> {
            LoaderRegistry commandRegistry = createRegistry();
            commandRegistry.register(new CommandLoader(event.registrar()));
            commandRegistry.loadAll();
        });
    }

    private LoaderRegistry createRegistry() {
        return new LoaderRegistry(
                "pl.notkris.example",
                getClass().getClassLoader(),
                new DefaultInstanceProvider(),
                getLogger()
        );
    }
}
