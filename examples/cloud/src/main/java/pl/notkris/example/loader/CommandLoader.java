package pl.notkris.example.loader;

import io.papermc.paper.command.brigadier.CommandSourceStack;
import org.incendo.cloud.annotations.AnnotationParser;
import pl.notkris.aaapi.loader.ClassTypeLoader;

import java.lang.annotation.*;

/**
 * AAAPI loader for the Cloud command framework.
 *
 * <p>Annotate your command classes with {@link RegisteredCommand} and they will
 * be automatically discovered and parsed by Cloud's {@link AnnotationParser}:
 *
 * <pre>{@code
 * @CommandLoader.RegisteredCommand
 * @CommandDescription("Hello command")
 * public class HelloCommand {
 *
 *     @Command("hello")
 *     public void execute(CommandSourceStack source) { ... }
 * }
 * }</pre>
 */
@SuppressWarnings("UnstableApiUsage")
public class CommandLoader extends ClassTypeLoader {

    /**
     * Annotation associated with this {@link CommandLoader}.
     * Part of the Cloud example - for production use, create your own loader and annotation.
     */
    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.TYPE)
    public @interface RegisteredCommand {}

    @Override
    public Class<? extends Annotation> type() {
        return RegisteredCommand.class;
    }

    private final AnnotationParser<CommandSourceStack> annotationParser;

    public CommandLoader(AnnotationParser<CommandSourceStack> annotationParser) {
        this.annotationParser = annotationParser;
    }

    @Override
    public void process(Class<?> clazz, Object instance) {
        annotationParser.parse(instance);
    }
}
