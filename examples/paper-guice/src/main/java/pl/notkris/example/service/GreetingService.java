package pl.notkris.example.service;

import com.google.inject.Inject;
import org.bukkit.entity.Player;

public class GreetingService {

    @Inject
    public GreetingService() {}

    public String greet(Player player) {
        return "Welcome, " + player.getName() + "!";
    }
}
