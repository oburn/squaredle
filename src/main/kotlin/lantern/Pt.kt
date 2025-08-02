package lantern

data class Pt(val x: Int, val y: Int) {
    fun adjacent(width: Int, height: Int): List<Pt> {
        val xx = listOf(
            Pt(x - 1, y - 1),
            Pt(x, y - 1),
            Pt(x + 1, y - 1),
            Pt(x + 1, y),
            Pt(x + 1, y + 1),
            Pt(x, y + 1),
            Pt(x - 1, y + 1),
            Pt(x - 1, y),
        )
        return xx.filter { p -> (p.x >= 0) && (p.x < width) && (p.y >= 0) && (p.y < height) }
    }
}
