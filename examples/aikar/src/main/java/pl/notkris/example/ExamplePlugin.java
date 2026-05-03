package pl.notkris.example;

import co.aikar.commands.PaperCommandManager;
import org.bukkit.plugin.java.JavaPlugin;
import pl.notkris.aaapi.provider.DefaultInstanceProvider;
import pl.notkris.aaapi.registry.LoaderRegistry;
import pl.notkris.example.loader.CommandLoader;

/**
 * Example plugin demonstrating AAAPI integration with Aikar's Command Framework (ACF).
 *
 * <p>All classes annotated with {@link CommandLoader.RegisteredCommand} are automatically
 * discovered and registered - no manual registration needed.
 */
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
        PaperCommandManager manager = new PaperCommandManager(this);

        registry.register(new CommandLoader(manager));
        registry.loadAll();
    }
}
