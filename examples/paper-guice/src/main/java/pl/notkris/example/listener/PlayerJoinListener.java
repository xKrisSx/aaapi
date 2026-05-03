package pl.notkris.example.listener;

import com.google.inject.Inject;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import pl.notkris.example.loader.ListenerLoader;
import pl.notkris.example.service.GreetingService;

@ListenerLoader.RegisteredListener
public class PlayerJoinListener implements Listener {

    private final GreetingService greetingService;

    @Inject
    public PlayerJoinListener(GreetingService greetingService) {
        this.greetingService = greetingService;
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        event.getPlayer().sendMessage(greetingService.greet(event.getPlayer()));
    }
}
