package pl.notkris.example.command;

import io.papermc.paper.command.brigadier.BasicCommand;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.jspecify.annotations.NonNull;
import pl.notkris.example.loader.CommandLoader;

@SuppressWarnings("UnstableApiUsage")
@CommandLoader.RegisteredCommand(name = "spawn")
public class SpawnCommand implements BasicCommand {

    @Override
    public String permission() {
        return "example.spawn";
    }

    @Override
    public void execute(CommandSourceStack source, String @NonNull [] args) {
        if (!(source.getSender() instanceof Player player)) {
            source.getSender().sendMessage("Command can be executed only by players!");
            return;
        }

        World world = args.length > 0
                ? Bukkit.getWorld(args[0])
                : player.getWorld();

        if (world == null) {
            player.sendMessage("World not found!");
            return;
        }

        player.teleport(world.getSpawnLocation());
        player.sendMessage("Teleported to spawn in " + world.getName());
    }
}