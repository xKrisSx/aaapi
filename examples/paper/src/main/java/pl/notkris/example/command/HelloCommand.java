package pl.notkris.example.command;

import io.papermc.paper.command.brigadier.BasicCommand;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import org.bukkit.entity.Player;
import org.jspecify.annotations.NonNull;
import pl.notkris.example.loader.CommandLoader;

@SuppressWarnings("UnstableApiUsage")
@CommandLoader.RegisteredCommand(name = "hello")
public class HelloCommand implements BasicCommand {

    @Override
    public void execute(CommandSourceStack source, String @NonNull [] args) {
        if (source.getExecutor() instanceof Player player) {
            player.sendMessage("Hello AAAPI!");
        }
    }
}