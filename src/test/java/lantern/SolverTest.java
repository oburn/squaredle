package lantern;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Stream;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;
import org.junit.jupiter.params.provider.ArgumentsSource;

import lantern.Solver.SearchResult;

public class SolverTest {
    private static final Path SAMPLE_PATH = Paths.get("src", "test", "resources", "sample.dict");

    @Test
    void testSolve() throws IOException {
        var solver = Solver.load(Paths.get("/usr/share/dict/words"), 4, 9);
        var xxx = solver.solve(List.of("abcd", "efgh", "ijkl", "mnop"));
        assertThat(xxx).hasSize(10);

    }

    @Test
    void testLoad() throws IOException {
        var solver = Solver.load(SAMPLE_PATH, 4, 5);
        assertThat(solver).isNotNull();
        assertThat(solver.getWords())
                .containsExactly("aaaa", "drive", "from", "fromy", "ttttt");
    }

    @ParameterizedTest
    @ArgumentsSource(TestInputProvider.class)
    void testSearch(TestInput input) throws IOException {
        var dict = Solver.load(SAMPLE_PATH, 4, 5);
        var got = dict.search(input.candidate);
        assertThat(got).isEqualTo(input.result);
    }

    static record TestInput(String candidate, SearchResult result) {
    }

    static class TestInputProvider implements ArgumentsProvider {
        @Override
        public Stream<? extends Arguments> provideArguments(ExtensionContext context) throws Exception {
            return Stream.of(
                    new TestInput("xx", new SearchResult(false, false)),
                    new TestInput("aa", new SearchResult(false, true)),
                    new TestInput("ab", new SearchResult(false, false)),
                    new TestInput("from", new SearchResult(true, true)),
                    new TestInput("drive", new SearchResult(true, false)))
                    .map(Arguments::of);
        }
    }
}
