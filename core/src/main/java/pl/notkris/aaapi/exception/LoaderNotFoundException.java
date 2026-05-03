package pl.notkris.aaapi.exception;

import pl.notkris.aaapi.loader.ClassTypeLoader;
import pl.notkris.aaapi.registry.LoaderRegistry;

/**
 * Thrown when {@link LoaderRegistry} cannot find a registered loader
 * for a discovered class.
 *
 * <p>This usually means you forgot to call {@link LoaderRegistry#register(ClassTypeLoader)}
 * for the corresponding type before {@link LoaderRegistry#loadAll()}.
 */

public class LoaderNotFoundException extends RuntimeException {

    public LoaderNotFoundException(String message) {
        super(message);
    }
}
