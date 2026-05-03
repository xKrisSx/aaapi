package pl.notkris.example.command;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.Default;
import org.bukkit.entity.Player;
import pl.notkris.example.loader.CommandLoader;

@CommandLoader.RegisteredCommand
@CommandAlias("hello")
public class HelloCommand extends BaseCommand {

    @Default
    public void execute(
            Player player
    ) {
        player.sendMessage("Hello AAAPI!");
    }
}
