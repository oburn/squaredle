package lantern;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.stream.Stream;

import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;
import org.junit.jupiter.params.provider.ArgumentsSource;

public class PtTest {
    @ParameterizedTest
    @ArgumentsSource(TestInputProvider.class)
    void testAdjacent(TestInput input) {
        var got = input.pt.adjacent(4, 4);
        assertThat(got).containsExactlyElementsOf(input.expect);
    }

    static record TestInput(Pt pt, List<Pt> expect) {
    }

    static class TestInputProvider implements ArgumentsProvider {
        @Override
        public Stream<? extends Arguments> provideArguments(ExtensionContext context) throws Exception {
            return Stream.of(
                    new TestInput(new Pt(0, 0), List.of(new Pt(1, 0), new Pt(1, 1), new Pt(0, 1))),
                    new TestInput(new Pt(3, 3), List.of(new Pt(2, 2), new Pt(3, 2), new Pt(2, 3))),
                    new TestInput(new Pt(0, 3), List.of(new Pt(0, 2), new Pt(1, 2), new Pt(1, 3))),
                    new TestInput(new Pt(3, 0), List.of(new Pt(3, 1), new Pt(2, 1), new Pt(2, 0))),
                    new TestInput(new Pt(1, 0),
                            List.of(new Pt(2, 0), new Pt(2, 1), new Pt(1, 1), new Pt(0, 1), new Pt(0, 0))),
                    new TestInput(new Pt(1, 1), List.of(new Pt(0, 0), new Pt(1, 0), new Pt(2, 0), new Pt(2, 1),
                            new Pt(2, 2), new Pt(1, 2), new Pt(0, 2), new Pt(0, 1))))
                    .map(Arguments::of);
        }
    }
}
