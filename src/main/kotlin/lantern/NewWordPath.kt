package lantern

data class NewWordPath(val steps: List<Pt>, val word: String) {
    fun visited(pt: Pt): Boolean = steps.contains(pt)

    fun addStep(pt: Pt, ch: Char): NewWordPath {
        return NewWordPath(steps.plus(pt), word + ch)
    }
}
