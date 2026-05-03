package pl.notkris.example.listener;

import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import pl.notkris.example.loader.ListenerLoader;

@ListenerLoader.RegisteredListener
public class PlayerJoinListener implements Listener {

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        event.joinMessage(MiniMessage.miniMessage()
                .deserialize("<green>Hello, " + player.getName() + "!")
        );
    }
}
