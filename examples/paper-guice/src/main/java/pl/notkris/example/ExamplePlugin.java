package pl.notkris.example;

import com.google.inject.Guice;
import com.google.inject.Injector;
import org.bukkit.plugin.java.JavaPlugin;
import pl.notkris.aaapi.guice.GuiceInstanceProvider;
import pl.notkris.aaapi.provider.DefaultInstanceProvider;
import pl.notkris.aaapi.registry.LoaderRegistry;
import pl.notkris.example.listener.PlayerJoinListener;
import pl.notkris.example.loader.ListenerLoader;

/**
 * Example plugin demonstrating AAAPI integration with Google Guice.
 *
 * <p>By swapping {@link DefaultInstanceProvider} for
 * {@link GuiceInstanceProvider}, all discovered classes
 * are instantiated by Guice - enabling full {@code @Inject} constructor injection
 * without any manual wiring.
 *
 * <p>See {@link PlayerJoinListener} for a practical example
 * of injecting a service into a listener.
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
        Injector injector = Guice.createInjector();
        LoaderRegistry registry = new LoaderRegistry(
                "pl.notkris.example",
                getClass().getClassLoader(),
                new GuiceInstanceProvider(injector) // Guice handles instantiation and @Inject
        );

        registry.register(new ListenerLoader(getServer().getPluginManager(), this));
        registry.loadAll();
    }
}
