package pl.notkris.example.command;

import io.papermc.paper.command.brigadier.CommandSourceStack;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.incendo.cloud.annotations.Argument;
import org.incendo.cloud.annotations.Command;
import org.incendo.cloud.annotations.Permission;
import pl.notkris.example.loader.CommandLoader;

import java.util.Optional;

@SuppressWarnings("UnstableApiUsage")
@CommandLoader.RegisteredCommand
public class SpawnCommand {

    @Command("spawn [world]")
    @Permission("example.spawn")
    public void execute(
            final @NonNull CommandSourceStack sender,
            final @Argument("world") World world
    ) {
        if (!(sender.getSender() instanceof Player player)) return;

        World w = Optional.ofNullable(world).orElse(player.getWorld());

        player.teleport(w.getSpawnLocation());
        player.sendMessage("Teleported to spawn in " + w.getName());
    }
}
