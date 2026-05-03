package pl.notkris.example;

import io.papermc.paper.command.brigadier.CommandSourceStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.incendo.cloud.annotations.AnnotationParser;
import org.incendo.cloud.execution.ExecutionCoordinator;
import org.incendo.cloud.paper.PaperCommandManager;
import pl.notkris.aaapi.provider.DefaultInstanceProvider;
import pl.notkris.aaapi.registry.LoaderRegistry;
import pl.notkris.example.loader.CommandLoader;

/**
 * Example plugin demonstrating AAAPI integration with Cloud command framework.
 *
 * <p>All classes annotated with {@link CommandLoader.RegisteredCommand} are automatically
 * discovered and registered - no manual registration needed.
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

        PaperCommandManager<CommandSourceStack> commandManager = PaperCommandManager.builder()
                .executionCoordinator(ExecutionCoordinator.simpleCoordinator())
                .buildOnEnable(this);

        AnnotationParser<CommandSourceStack> annotationParser = new AnnotationParser<>(commandManager, CommandSourceStack.class);

        registry.register(new CommandLoader(annotationParser));
        registry.loadAll();
    }
}
