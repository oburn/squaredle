package lantern;

import org.eclipse.collections.api.list.ImmutableList;

public record WordPath(ImmutableList<Pt> steps, String word) {
    boolean visited(Pt pt) {
        return steps.contains(pt);
    }

    WordPath addStep(Pt pt, char ch) {
        return new WordPath(steps.newWith(pt), word + ch);
    }
}
