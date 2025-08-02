package lantern

import java.nio.file.Path
import java.util.Locale
import kotlin.io.path.readLines

data class NewSolver(val words: List<String>) {

    fun solve(rows: List<String>): List<NewWordPath> {
        val result = mutableListOf<NewWordPath>()

        for (y in 0..<rows.size) {
            val row = rows.get(y)
            for (x in 0..<row.length) {
                result.addAll(wordsFrom(NewWordPath(listOf(Pt(x, y)), "" + row.get(x)), rows))
            }
        }

        return result.toList()
    }

    fun wordsFrom(wp: NewWordPath, rows: List<String>): List<NewWordPath> {
        if (wp.word.length > 20) { // love a magic constant
            return emptyList()
        }

        val result = mutableListOf<NewWordPath>()
        val searchResult = search(wp.word)
        if (searchResult.exactMatch) {
            result.add(wp)
        }

        if (searchResult.partialMatch) {
            wp.steps.last().adjacent(rows.first().length, rows.size)
                .filter { !wp.visited(it) }
                .forEach { p ->
                    val nextPath = wp.addStep(p, rows.get(p.y).get(p.x))
                    val extraWords = wordsFrom(nextPath, rows)
                    result.addAll(extraWords)
                }
        }

        return result.toList()
    }

    fun search(candidate: String): NewSearchResult {
        var exactMatch = false
        var partialMatch = false

        for (word in words) {
            if (candidate == word) {
                exactMatch = true
            } else if (word.startsWith(candidate)) {
                partialMatch = true
            }

            if (exactMatch && partialMatch) {
                break
            }
        }

        return NewSearchResult(exactMatch, partialMatch)
    }
}

fun loadWords(path: Path, minLen: Int, maxLen: Int): NewSolver {
    val words = path.readLines()
        .filter { it.length >= minLen }
        .filter { it.length <= maxLen }
        .filter { it == it.lowercase(Locale.ENGLISH) }
    return NewSolver(words)
}
