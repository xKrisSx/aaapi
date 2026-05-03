package pl.notkris.example.loader;

import org.bukkit.Bukkit;
import pl.notkris.aaapi.loader.ClassTypeLoader;
import pl.notkris.example.ExamplePlugin;

import java.lang.annotation.*;

/**
 * AAAPI loader for Bukkit scheduled tasks.
 *
 * <p>The annotated class must implement {@link Runnable}. Timing is configured
 * directly in the annotation:
 *
 * <pre>{@code
 * @TaskLoader.RegisteredTask(period = 300 * 20L)
 * public class DiamondRewardTask implements Runnable { ... }
 * }</pre>
 */
public class TaskLoader extends ClassTypeLoader {

    /**
     * Annotation associated with this {@link TaskLoader}.
     * Part of the Paper example - for production use, create your own loader and annotation.
     */
    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.TYPE)
    public @interface RegisteredTask {
        /** Delay in ticks before the first execution. Default: {@code 0}. */
        long delay() default 0;
        /** Interval in ticks between executions. Default: {@code 0}. */
        long period() default 0;
        /** Whether the task should run asynchronously. Default: {@code true}. */
        boolean async() default true;
    }

    private final ExamplePlugin plugin;

    public TaskLoader(ExamplePlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public Class<? extends Annotation> type() {
        return RegisteredTask.class;
    }

    @Override
    public void process(Class<?> clazz, Object instance) {
        if (!(instance instanceof Runnable task)) return;

        RegisteredTask registeredTask = clazz.getAnnotation(RegisteredTask.class);
        if (registeredTask.async()) {
            Bukkit.getScheduler().runTaskTimerAsynchronously(
                    plugin,
                    task,
                    registeredTask.delay(),
                    registeredTask.period()
            );
        } else {
            Bukkit.getScheduler().runTaskTimer(
                    plugin,
                    task,
                    registeredTask.delay(),
                    registeredTask.period()
            );
        }
    }
}
