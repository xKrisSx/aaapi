package pl.notkris.example.command;

import dev.rollczi.litecommands.annotations.command.Command;
import dev.rollczi.litecommands.annotations.context.Context;
import dev.rollczi.litecommands.annotations.execute.Execute;
import org.bukkit.command.CommandSender;
import pl.notkris.example.loader.CommandLoader;

@CommandLoader.RegisteredCommand
@Command(name = "hello")
public class HelloCommand {

    @Execute
    void hello(
            @Context CommandSender sender
    ) {
        sender.sendMessage("Hello AAAPI!");
    }
}
