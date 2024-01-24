package lantern;

import java.util.List;
import java.util.stream.Stream;

record Pt(int x, int y) {
    List<Pt> adjacent(int width, int height) {
        return Stream.of(
                new Pt(x - 1, y - 1),
                new Pt(x, y - 1),
                new Pt(x + 1, y - 1),
                new Pt(x + 1, y),
                new Pt(x + 1, y + 1),
                new Pt(x, y + 1),
                new Pt(x - 1, y + 1),
                new Pt(x - 1, y)) //
                .filter(p -> p.x >= 0 && p.x < width && p.y >= 0 && p.y < height)
                .toList();
    }
}