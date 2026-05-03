package pl.notkris.example.loader;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.PaperCommandManager;
import pl.notkris.aaapi.loader.ClassTypeLoader;

import java.lang.annotation.*;

/**
 * AAAPI loader for Aikar's Command Framework (ACF).
 *
 * <p>Annotate your command classes with {@link RegisteredCommand} and they will
 * be automatically registered with ACF's {@link PaperCommandManager}:
 *
 * <pre>{@code
 * @CommandLoader.RegisteredCommand
 * @CommandAlias("hello")
 * public class HelloCommand extends BaseCommand { ... }
 * }</pre>
 */
public class CommandLoader extends ClassTypeLoader {

    /**
     * Annotation associated with this {@link CommandLoader}.
     * Part of the ACF example - for production use, create your own loader and annotation.
     */
    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.TYPE)
    public @interface RegisteredCommand {}

    private final PaperCommandManager manager;

    public CommandLoader(PaperCommandManager manager) {
        this.manager = manager;
    }

    @Override
    public Class<? extends Annotation> type() {
        return RegisteredCommand.class;
    }

    @Override
    public void process(Class<?> clazz, Object instance) {
        if (!(instance instanceof BaseCommand baseCommand)) return;
        manager.registerCommand(baseCommand);
    }
}