package lantern

data class Solver(val words: List<String>) {

    fun solve(rows: List<String>): List<WordPath> {
        val result = mutableListOf<WordPath>()

        for (y in 0..<rows.size) {
            val row = rows.get(y)
            for (x in 0..<row.length) {
                result.addAll(wordsFrom(WordPath(listOf(Pt(x, y)), "" + row.get(x)), rows))
            }
        }

        return result.toList()
    }

    fun wordsFrom(wp: WordPath, rows: List<String>): List<WordPath> {
        if (wp.word.length > 20) { // love a magic constant
            return emptyList()
        }

        val result = mutableListOf<WordPath>()
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

    fun search(candidate: String): SearchResult {
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

        return SearchResult(exactMatch, partialMatch)
    }
}
