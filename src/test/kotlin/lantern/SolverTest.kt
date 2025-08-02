package lantern

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.nio.file.Path
import java.nio.file.Paths

class SolverTest {
    val samplePath: Path = Paths.get("src", "test", "resources", "sample.dict")

    @Test
    fun testLoad() {
        val solver = loadWords(samplePath, 4, 5)
        assertThat(solver).isNotNull()
        assertThat(solver.words)
            .containsExactly("aaaa", "drive", "from", "fromy", "ttttt")
    }

    @Test
    fun testSolve() {
        val solver = loadWords(Paths.get("/usr/share/dict/words"), 4, 9)
        val xxx = solver.solve(
            listOf(
                "abcd",
                "efgh",
                "ijkl",
                "mnop"
            )
        )
        assertThat(xxx).hasSize(10)
    }

    @Test
    fun testSearch() {
        val dict = loadWords(samplePath, 4, 5)
        val got = dict.search("xx")
        assertThat(got).isEqualTo(SearchResult(exactMatch = false, partialMatch = false))

        listOf(
            Pair("aa", SearchResult(exactMatch = false, partialMatch = true)),
            Pair("ab", SearchResult(exactMatch = false, partialMatch = false)),
            Pair("from", SearchResult(exactMatch = true, partialMatch = true)),
            Pair("drive", SearchResult(exactMatch = true, partialMatch = false)),
        )
            .forEach { pair ->
                val got = dict.search(pair.first)
                assertThat(got).isEqualTo(pair.second)
            }
    }
}