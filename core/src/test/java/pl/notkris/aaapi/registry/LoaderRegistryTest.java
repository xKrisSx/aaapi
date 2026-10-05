package pl.notkris.aaapi.registry;

import org.junit.jupiter.api.Test;
import pl.notkris.aaapi.fixture.First;
import pl.notkris.aaapi.fixture.Second;
import pl.notkris.aaapi.fixture.scan.Annotated;
import pl.notkris.aaapi.fixture.scan.BothAnnotations;
import pl.notkris.aaapi.fixture.scan.PrivateConstructor;
import pl.notkris.aaapi.fixture.scan.nested.NestedAnnotated;
import pl.notkris.aaapi.loader.ClassTypeLoader;
import pl.notkris.aaapi.provider.DefaultInstanceProvider;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

class LoaderRegistryTest {

    private static final String PACKAGE = "pl.notkris.aaapi.fixture.scan";

    private static class RecordingLoader extends ClassTypeLoader {

        private final Class<? extends Annotation> type;
        private final Map<Class<?>, Object> processed = new LinkedHashMap<>();

        RecordingLoader(Class<? extends Annotation> type) {
            this.type = type;
        }

        @Override
        public Class<? extends Annotation> type() {
            return type;
        }

        @Override
        public void process(Class<?> clazz, Object instance) {
            processed.put(clazz, instance);
        }
    }

    private LoaderRegistry registry() {
        return new LoaderRegistry(
                PACKAGE,
                getClass().getClassLoader(),
                new DefaultInstanceProvider(),
                Logger.getLogger("AAAPI-test")
        );
    }

    @Test
    void passesSameInstanceToEveryMatchingLoader() {
        RecordingLoader first = new RecordingLoader(First.class);
        RecordingLoader second = new RecordingLoader(Second.class);

        LoaderRegistry registry = registry();
        registry.register(first);
        registry.register(second);
        registry.loadAll();

        assertEquals(List.of(Annotated.class, BothAnnotations.class, NestedAnnotated.class),
                List.copyOf(first.processed.keySet()));
        assertEquals(List.of(BothAnnotations.class, PrivateConstructor.class),
                List.copyOf(second.processed.keySet()));

        assertSame(first.processed.get(BothAnnotations.class), second.processed.get(BothAnnotations.class));
        assertInstanceOf(PrivateConstructor.class, second.processed.get(PrivateConstructor.class));
    }

    @Test
    void failureOfOneClassDoesNotStopOthers() {
        RecordingLoader first = new RecordingLoader(First.class) {
            @Override
            public void process(Class<?> clazz, Object instance) {
                if (clazz == Annotated.class) {
                    throw new IllegalStateException("expected test failure");
                }
                super.process(clazz, instance);
            }
        };

        LoaderRegistry registry = registry();
        registry.register(first);
        registry.loadAll();

        assertEquals(List.of(BothAnnotations.class, NestedAnnotated.class), List.copyOf(first.processed.keySet()));
    }

    @Test
    void registeringSameTypeReplacesLoader() {
        RecordingLoader replaced = new RecordingLoader(First.class);
        RecordingLoader replacement = new RecordingLoader(First.class);

        LoaderRegistry registry = registry();
        registry.register(replaced);
        registry.register(replacement);
        registry.loadAll();

        assertTrue(replaced.processed.isEmpty());
        assertEquals(3, replacement.processed.size());
    }

    @Test
    void linkageErrorOfOneClassDoesNotStopOthers() {
        RecordingLoader first = new RecordingLoader(First.class) {
            @Override
            public void process(Class<?> clazz, Object instance) {
                if (clazz == Annotated.class) {
                    throw new NoClassDefFoundError("org/example/MissingSoftDependency");
                }
                super.process(clazz, instance);
            }
        };

        LoaderRegistry registry = registry();
        registry.register(first);
        registry.loadAll();

        assertEquals(List.of(BothAnnotations.class, NestedAnnotated.class), List.copyOf(first.processed.keySet()));
    }

    @Test
    void failedClassIsSkippedByLaterLoaders() {
        RecordingLoader first = new RecordingLoader(First.class) {
            @Override
            public void process(Class<?> clazz, Object instance) {
                if (clazz == BothAnnotations.class) {
                    throw new IllegalStateException("expected test failure");
                }
                super.process(clazz, instance);
            }
        };
        RecordingLoader second = new RecordingLoader(Second.class);

        LoaderRegistry registry = registry();
        registry.register(first);
        registry.register(second);
        registry.loadAll();

        assertEquals(List.of(PrivateConstructor.class), List.copyOf(second.processed.keySet()));
    }

    @Test
    void loadersRunInRegistrationOrder() {
        List<String> calls = new ArrayList<>();
        RecordingLoader first = new RecordingLoader(First.class) {
            @Override
            public void process(Class<?> clazz, Object instance) {
                calls.add("first:" + clazz.getSimpleName());
            }
        };
        RecordingLoader second = new RecordingLoader(Second.class) {
            @Override
            public void process(Class<?> clazz, Object instance) {
                calls.add("second:" + clazz.getSimpleName());
            }
        };

        LoaderRegistry registry = registry();
        registry.register(second);
        registry.register(first);
        registry.loadAll();

        assertEquals(List.of(
                "second:BothAnnotations",
                "second:PrivateConstructor",
                "first:Annotated",
                "first:BothAnnotations",
                "first:NestedAnnotated"
        ), calls);
    }

    @Test
    void replacedLoaderKeepsItsPosition() {
        List<String> calls = new ArrayList<>();
        RecordingLoader second = new RecordingLoader(Second.class) {
            @Override
            public void process(Class<?> clazz, Object instance) {
                calls.add("second");
            }
        };
        RecordingLoader replacement = new RecordingLoader(First.class) {
            @Override
            public void process(Class<?> clazz, Object instance) {
                calls.add("first");
            }
        };

        LoaderRegistry registry = registry();
        registry.register(new RecordingLoader(First.class));
        registry.register(second);
        registry.register(replacement);
        registry.loadAll();

        assertEquals(List.of("first", "first", "first", "second", "second"), calls);
    }
}
