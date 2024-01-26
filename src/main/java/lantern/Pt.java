package lantern;

import org.eclipse.collections.api.list.ImmutableList;
import org.eclipse.collections.api.factory.Lists;

record Pt(int x, int y) {
    ImmutableList<Pt> adjacent(int width, int height) {
        return Lists.immutable.of(
                new Pt(x - 1, y - 1),
                new Pt(x, y - 1),
                new Pt(x + 1, y - 1),
                new Pt(x + 1, y),
                new Pt(x + 1, y + 1),
                new Pt(x, y + 1),
                new Pt(x - 1, y + 1),
                new Pt(x - 1, y)) //
                .select(p -> p.x >= 0 && p.x < width && p.y >= 0 && p.y < height);
    }
}