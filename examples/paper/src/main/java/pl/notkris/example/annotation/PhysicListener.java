package pl.notkris.example.annotation;

import pl.notkris.example.loader.ListenerLoader;
import pl.notkris.example.utils.WorldUtils;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marks a listener as physics-related, enabling additional tracking in {@link WorldUtils}.
 *
 * <p>Used together with {@link ListenerLoader} to opt into
 * the physics listener system. The {@link #isEnabledByDefault()} flag controls
 * the initial state of the listener in {@link WorldUtils}.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface PhysicListener {
    /** Whether this listener is enabled by default. Default: {@code true}. */
    boolean isEnabledByDefault() default true;
}
