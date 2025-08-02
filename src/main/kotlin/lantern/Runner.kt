package lantern

import java.nio.file.Path
import java.nio.file.Paths
import java.util.Locale
import kotlin.io.path.readLines

fun main() {
    val solver = loadWords(Paths.get("/usr/share/dict/words"), 4, 15)
    val app = App(solver)
    app.display()
}

fun loadWords(path: Path, minLen: Int, maxLen: Int): Solver {
    val words = path.readLines()
        .filter { it.length >= minLen }
        .filter { it.length <= maxLen }
        .filter { it == it.lowercase(Locale.ENGLISH) }
    return Solver(words)
}
