package lantern

data class NewWordPath(val steps: List<NewPt>, val word: String) {
    fun visited(pt: NewPt): Boolean = steps.contains(pt)

    fun addStep(pt: NewPt, ch: Char): NewWordPath {
        return NewWordPath(steps.plus(pt), word + ch)
    }
}
