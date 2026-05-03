package pl.notkris.example.command;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Default;
import co.aikar.commands.annotation.Optional;
import org.bukkit.World;
import org.bukkit.entity.Player;
import pl.notkris.example.loader.CommandLoader;

@CommandLoader.RegisteredCommand
@CommandAlias("spawn")
@CommandPermission("example.spawn")
public class SpawnCommand extends BaseCommand {

    @Default
    public void execute(
            Player player,
            @Optional World world
    ) {
        World w = java.util.Optional.ofNullable(world).orElse(player.getWorld());

        player.teleport(w.getSpawnLocation());
        player.sendMessage("Teleported to spawn in " + w.getName());
    }
}
