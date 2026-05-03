package pl.notkris.example.command;

import io.papermc.paper.command.brigadier.BasicCommand;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.jspecify.annotations.NonNull;
import pl.notkris.example.loader.CommandLoader;
import pl.notkris.example.utils.WorldUtils;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@SuppressWarnings("UnstableApiUsage")
@CommandLoader.RegisteredCommand(name = "toggle-physic")
public class TogglePhysicCommand implements BasicCommand {

    @Override
    public String permission() {
        return "example.toggle-physic";
    }

    @Override
    public void execute(CommandSourceStack source, String @NonNull [] args) {
        if (!(source.getSender() instanceof Player player)) {
            source.getSender().sendMessage("Command can be executed only by players!");
            return;
        }

        if (args.length == 0) {
            player.sendMessage("Usage: /toggle-physic <listener>");
            return;
        }

        String name = args[0];

        Listener listener = WorldUtils.getPhysicListeners().keySet().stream()
                .filter(l -> l.getClass().getSimpleName().equals(name))
                .findFirst()
                .orElse(null);

        if (listener == null) {
            player.sendMessage("Listener " + name + " not found!");
            return;
        }

        WorldUtils.togglePhysicListener(listener);
        boolean state = WorldUtils.checkPhysicListener(listener);
        player.sendMessage("Listener " + name + " is now " + (state ? "enabled" : "disabled"));
    }

    @Override
    public @NonNull Collection<String> suggest(@NonNull CommandSourceStack source, String @NonNull [] args) {
        if (args.length == 1) {
            return WorldUtils.getPhysicListeners().keySet().stream()
                    .map(l -> l.getClass().getSimpleName())
                    .collect(Collectors.toList());
        }
        return List.of();
    }
}
