package pl.notkris.example.service;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import org.bukkit.entity.Player;

import java.util.logging.Logger;

/**
 * {@code @Singleton} - without it Guice would create a new instance
 * for every class that injects this service.
 */
@Singleton
public class GreetingService {

    private final Logger logger;

    @Inject
    public GreetingService(Logger logger) {
        this.logger = logger;
    }

    public String greet(Player player) {
        logger.info("Greeting " + player.getName());
        return "Welcome, " + player.getName() + "!";
    }
}
