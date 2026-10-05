package pl.notkris.aaapi.guice;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import org.junit.jupiter.api.Test;
import pl.notkris.aaapi.guice.fixture.FirstComponent;
import pl.notkris.aaapi.guice.fixture.Registered;
import pl.notkris.aaapi.guice.fixture.SecondComponent;
import pl.notkris.aaapi.loader.ClassTypeLoader;
import pl.notkris.aaapi.registry.LoaderRegistry;

import java.lang.annotation.Annotation;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;

class GuiceInstanceProviderTest {

    @Test
    void injectsDependenciesIntoDiscoveredClasses() {
        Injector injector = Guice.createInjector(new AbstractModule() {
            @Override
            protected void configure() {
                bind(String.class).toInstance("bound-from-module");
            }
        });

        Map<Class<?>, Object> processed = new HashMap<>();
        LoaderRegistry registry = new LoaderRegistry(
                "pl.notkris.aaapi.guice.fixture",
                getClass().getClassLoader(),
                new GuiceInstanceProvider(injector),
                Logger.getLogger("AAAPI-test")
        );
        registry.register(new ClassTypeLoader() {
            @Override
            public Class<? extends Annotation> type() {
                return Registered.class;
            }

            @Override
            public void process(Class<?> clazz, Object instance) {
                processed.put(clazz, instance);
            }
        });
        registry.loadAll();

        FirstComponent first = (FirstComponent) processed.get(FirstComponent.class);
        SecondComponent second = (SecondComponent) processed.get(SecondComponent.class);

        assertEquals(2, processed.size());
        assertNotNull(first.service);
        assertEquals("bound-from-module", first.name);
        // @Singleton service is shared between all discovered classes
        assertSame(first.service, second.service);
    }
}
