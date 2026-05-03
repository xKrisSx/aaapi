package pl.notkris.example;

import dev.rollczi.litecommands.bukkit.LiteBukkitFactory;
import org.bukkit.plugin.java.JavaPlugin;
import pl.notkris.aaapi.provider.DefaultInstanceProvider;
import pl.notkris.aaapi.registry.LoaderRegistry;
import pl.notkris.example.loader.CommandLoader;

/**
 * Example plugin demonstrating AAAPI integration with LiteCommands.
 *
 * <p>All classes annotated with {@link CommandLoader.RegisteredCommand} are automatically
 * discovered and collected, then passed to LiteCommands builder in bulk -
 * no manual registration needed.
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
        CommandLoader commandLoader = new CommandLoader();

        registry.register(commandLoader);
        registry.loadAll();

        // pass all collected instances to LiteCommands builder at once
        LiteBukkitFactory.builder("example", this)
                .commands(commandLoader.getCommands().toArray())
                .build();
    }
}
