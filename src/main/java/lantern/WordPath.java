package lantern;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public record WordPath(List<Pt> steps, String word) {
    boolean visited(Pt pt) {
        return steps.contains(pt);
    }

    WordPath addStep(Pt pt, char ch) {
        var cloneSteps = new ArrayList<Pt>(steps);
        cloneSteps.add(pt);
        return new WordPath(Collections.unmodifiableList(cloneSteps), word + ch);
    }
}
