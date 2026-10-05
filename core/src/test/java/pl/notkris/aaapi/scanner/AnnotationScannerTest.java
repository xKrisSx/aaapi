package pl.notkris.aaapi.scanner;

import org.junit.jupiter.api.Test;
import pl.notkris.aaapi.fixture.First;
import pl.notkris.aaapi.fixture.Second;
import pl.notkris.aaapi.fixture.scan.Annotated;
import pl.notkris.aaapi.fixture.scan.BothAnnotations;
import pl.notkris.aaapi.fixture.scan.PrivateConstructor;
import pl.notkris.aaapi.fixture.scan.nested.NestedAnnotated;

import java.lang.annotation.Annotation;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AnnotationScannerTest {

    private static final String PACKAGE = "pl.notkris.aaapi.fixture.scan";

    private List<Class<?>> scan(Set<Class<? extends Annotation>> annotations) {
        return new AnnotationScanner(PACKAGE, getClass().getClassLoader(), annotations).scan();
    }

    @Test
    void findsOnlyDirectlyAnnotatedConcreteClassesInsidePackage() {
        List<Class<?>> classes = scan(Set.of(First.class));

        // excluded: SubclassOfAnnotated, AbstractAnnotated, AnnotatedInterface,
        // SiblingPackage (fixture.scanned) and Outside (fixture.outside)
        assertEquals(List.of(Annotated.class, BothAnnotations.class, NestedAnnotated.class), classes);
    }

    @Test
    void deduplicatesClassesWithMultipleAnnotations() {
        List<Class<?>> classes = scan(Set.of(First.class, Second.class));

        assertEquals(
                List.of(Annotated.class, BothAnnotations.class, PrivateConstructor.class, NestedAnnotated.class),
                classes
        );
    }

    @Test
    void returnsEmptyListWithoutAnnotations() {
        assertTrue(scan(Set.of()).isEmpty());
    }
}
