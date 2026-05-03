package pl.notkris.example.loader;

import io.papermc.paper.command.brigadier.BasicCommand;
import io.papermc.paper.command.brigadier.Commands;
import pl.notkris.aaapi.loader.ClassTypeLoader;

import java.lang.annotation.*;

/**
 * AAAPI loader for Paper's Brigadier command API ({@link BasicCommand}).
 *
 * <p>The command name is declared directly in the annotation:
 * <pre>{@code
 * @CommandLoader.RegisteredCommand(name = "hello")
 * public class HelloCommand implements BasicCommand { ... }
 * }</pre>
 */
@SuppressWarnings("UnstableApiUsage")
public class CommandLoader extends ClassTypeLoader {

    /**
     * Annotation associated with this {@link CommandLoader}.
     * Part of the Paper example - for production use, create your own loader and annotation.
     */
    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.TYPE)
    public @interface RegisteredCommand {
        /** The name of the command (e.g. {@code "hello"} for {@code /hello}). */
        String name();
    }

    private final Commands registrar;

    public CommandLoader(Commands registrar) {
        this.registrar = registrar;
    }

    @Override
    public Class<? extends Annotation> type() {
        return RegisteredCommand.class;
    }

    @Override
    public void process(Class<?> clazz, Object instance) {
        if (!(instance instanceof BasicCommand basicCommand)) return;

        String name = clazz.getAnnotation(RegisteredCommand.class).name();

        registrar.register(name, basicCommand);
    }
}
