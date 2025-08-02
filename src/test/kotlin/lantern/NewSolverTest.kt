package lantern

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.nio.file.Path
import java.nio.file.Paths

class NewSolverTest {
    val SAMPLE_PATH: Path = Paths.get("src", "test", "resources", "sample.dict")

    @Test
    fun testLoad() {
        val solver = loadWords(SAMPLE_PATH, 4, 5)
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
        val dict = loadWords(SAMPLE_PATH, 4, 5)
        val got = dict.search("xx")
        assertThat(got).isEqualTo(NewSearchResult(false, false))

        listOf(
            Pair("aa", NewSearchResult(false, true)),
            Pair("ab", NewSearchResult(false, false)),
            Pair("from", NewSearchResult(true, true)),
            Pair("drive", NewSearchResult(true, false)),
        )
            .forEach { pair ->
                val got = dict.search(pair.first)
                assertThat(got).isEqualTo(pair.second)
            }
    }
}