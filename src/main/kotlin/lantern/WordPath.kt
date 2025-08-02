package lantern

data class WordPath(val steps: List<Pt>, val word: String) {
    fun visited(pt: Pt): Boolean = steps.contains(pt)

    fun addStep(pt: Pt, ch: Char): WordPath {
        return WordPath(steps.plus(pt), word + ch)
    }
}
