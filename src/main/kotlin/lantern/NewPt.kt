package lantern

data class NewPt(val x: Int, val y: Int) {
    fun adjacent(width: Int, height: Int): List<NewPt> {
        val xx = listOf(
            NewPt(x - 1, y - 1),
            NewPt(x, y - 1),
            NewPt(x + 1, y - 1),
            NewPt(x + 1, y),
            NewPt(x + 1, y + 1),
            NewPt(x, y + 1),
            NewPt(x - 1, y + 1),
            NewPt(x - 1, y),
        )
        return xx.filter { p -> (p.x >= 0) && (p.x < width) && (p.y >= 0) && (p.y < height) }
    }
}
