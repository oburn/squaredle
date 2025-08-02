package lantern

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class PtTest {
    @Test
    fun testBasic() {
        assertThat(Pt(0, 0).adjacent(4, 4))
            .contains(Pt(1, 0), Pt(1, 1), Pt(0, 1))

        assertThat(Pt(3, 3).adjacent(4, 4))
            .contains(Pt(2, 2), Pt(3, 2), Pt(2, 3))

        assertThat(Pt(0, 3).adjacent(4, 4))
            .contains(Pt(0, 2), Pt(1, 2), Pt(1, 3))

        assertThat(Pt(3, 0).adjacent(4, 4))
            .contains(Pt(3, 1), Pt(2, 1), Pt(2, 0))

        assertThat(Pt(1, 0).adjacent(4, 4))
            .contains(Pt(2, 0), Pt(2, 1), Pt(1, 1), Pt(0, 1), Pt(0, 0))

        assertThat(Pt(1, 1).adjacent(4, 4))
            .contains(
                Pt(0, 0), Pt(1, 0), Pt(2, 0), Pt(2, 1),
                Pt(2, 2), Pt(1, 2), Pt(0, 2), Pt(0, 1)
            )
    }
}