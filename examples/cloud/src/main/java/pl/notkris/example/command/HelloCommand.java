package pl.notkris.example.command;

import io.papermc.paper.command.brigadier.CommandSourceStack;
import org.bukkit.entity.Player;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.incendo.cloud.annotations.Command;
import pl.notkris.example.loader.CommandLoader;

@SuppressWarnings("UnstableApiUsage")
@CommandLoader.RegisteredCommand
public class HelloCommand {

    @Command("hello")
    public void execute(
            final @NonNull CommandSourceStack sender
    ) {
        if (!(sender.getSender() instanceof Player player)) return;

        player.sendMessage("Hello AAAPI!");
    }
}
