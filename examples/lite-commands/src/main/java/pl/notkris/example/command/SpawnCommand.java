package pl.notkris.example.command;

import dev.rollczi.litecommands.annotations.command.Command;
import dev.rollczi.litecommands.annotations.context.Context;
import dev.rollczi.litecommands.annotations.execute.Execute;
import dev.rollczi.litecommands.annotations.optional.OptionalArg;
import org.bukkit.World;
import org.bukkit.entity.Player;
import pl.notkris.example.loader.CommandLoader;

import java.util.Optional;

@CommandLoader.RegisteredCommand
@Command(name = "spawn")
public class SpawnCommand {

    @Execute
    public void execute(
            final @Context Player player,
            final @OptionalArg World world
    ) {
        World w = Optional.ofNullable(world).orElse(player.getWorld());

        player.teleport(w.getSpawnLocation());
        player.sendMessage("Teleported to spawn in " + w.getName());
    }
}