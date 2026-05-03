package pl.notkris.example.loader;

import dev.rollczi.litecommands.bukkit.LiteBukkitFactory;
import pl.notkris.aaapi.loader.ClassTypeLoader;

import java.lang.annotation.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * AAAPI loader for LiteCommands.
 *
 * <p>Unlike other loaders, this one does not register commands immediately in {@link #process}.
 * Instead, it collects all instances and exposes them via {@link #getCommands()},
 * so they can be passed to {@link LiteBukkitFactory} builder at once.
 *
 * <pre>{@code
 * CommandLoader commandLoader = new CommandLoader();
 * registry.register(commandLoader);
 * registry.loadAll();
 *
 * LiteBukkitFactory.builder("prefix", this)
 *     .commands(commandLoader.getCommands().toArray())
 *     .build();
 * }</pre>
 */
public class CommandLoader extends ClassTypeLoader {

    /**
     * Annotation associated with this {@link CommandLoader}.
     * Part of the LiteCommands example - for production use, create your own loader and annotation.
     */
    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.TYPE)
    public @interface RegisteredCommand {}

    private final List<Object> commands = new ArrayList<>();

    @Override
    public Class<? extends Annotation> type() {
        return RegisteredCommand.class;
    }

    @Override
    public void process(Class<?> clazz, Object instance) {
        // collect instances - registration happens later via getCommands()
        commands.add(instance);
    }

    /**
     * Returns an unmodifiable view of all collected command instances.
     * Pass this to {@link LiteBukkitFactory} builder.
     */
    public List<Object> getCommands() {
        return Collections.unmodifiableList(commands);
    }
}
