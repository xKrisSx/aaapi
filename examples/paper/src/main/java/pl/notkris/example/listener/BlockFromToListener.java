package pl.notkris.example.listener;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockFromToEvent;
import pl.notkris.example.annotation.PhysicListener;
import pl.notkris.example.loader.ListenerLoader;
import pl.notkris.example.utils.WorldUtils;

@ListenerLoader.RegisteredListener
@PhysicListener()
public class BlockFromToListener implements Listener {

    @EventHandler
    public void onBlockFromTo(BlockFromToEvent event) {
        if (WorldUtils.checkPhysicListener(this)) {
            event.setCancelled(true);
        }
    }
}
