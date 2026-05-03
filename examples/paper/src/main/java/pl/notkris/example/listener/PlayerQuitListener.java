package pl.notkris.example.listener;

import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import pl.notkris.example.loader.ListenerLoader;

@ListenerLoader.RegisteredListener
public class PlayerQuitListener implements Listener {

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();

        event.quitMessage(MiniMessage.miniMessage()
                .deserialize("<gray>Goodbye, " + player.getName() + "!")
        );
    }
}
