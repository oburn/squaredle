package lantern

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class NewPtTest {
    @Test
    fun testBasic() {
        assertThat(NewPt(0, 0).adjacent(4, 4))
            .contains(NewPt(1, 0), NewPt(1, 1), NewPt(0, 1))

        assertThat(NewPt(3, 3).adjacent(4, 4))
            .contains(NewPt(2, 2), NewPt(3, 2), NewPt(2, 3))

        assertThat(NewPt(0, 3).adjacent(4, 4))
            .contains(NewPt(0, 2), NewPt(1, 2), NewPt(1, 3))

        assertThat(NewPt(3, 0).adjacent(4, 4))
            .contains(NewPt(3, 1), NewPt(2, 1), NewPt(2, 0))

        assertThat(NewPt(1, 0).adjacent(4, 4))
            .contains(NewPt(2, 0), NewPt(2, 1), NewPt(1, 1), NewPt(0, 1), NewPt(0, 0))

        assertThat(NewPt(1, 1).adjacent(4, 4))
            .contains(
                NewPt(0, 0), NewPt(1, 0), NewPt(2, 0), NewPt(2, 1),
                NewPt(2, 2), NewPt(1, 2), NewPt(0, 2), NewPt(0, 1)
            )
    }
}