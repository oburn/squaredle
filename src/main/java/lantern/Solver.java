package lantern;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Solver {
    static record SearchResult(boolean exactMatch, boolean partialMatch) {
    }

    private final List<String> words;

    private Solver(List<String> words) {
        this.words = words;
    }

    static Solver load(Path p, int minLen, int maxLen) throws IOException {
        var words = Files.lines(p)
                .filter(l -> l.length() >= minLen)
                .filter(l -> l.length() <= maxLen)
                .filter(l -> l.equals(l.toLowerCase()))
                .toList();
        return new Solver(Collections.unmodifiableList(words));
    }

    List<WordPath> wordsFrom(WordPath wp, List<String> rows) {
        var result = new ArrayList<WordPath>();

        if (wp.word().length() > 15) { // love a magic constant
            return result;
        }

        var searchResult = search(wp.word());
        if (searchResult.exactMatch()) {
            result.add(wp);
        }

        if (searchResult.partialMatch()) {
            wp.steps().getLast().adjacent(rows.getFirst().length(), rows.size())
                    .stream()
                    .filter(p -> !wp.visited(p))
                    .forEach(p -> {
                        var nextPath = wp.addStep(p, rows.get(p.y()).charAt(p.x()));
                        var extraWords = wordsFrom(nextPath, rows);
                        result.addAll(extraWords);
                    });
            ;
        }

        return Collections.unmodifiableList(result);
    }

    List<WordPath> solve(List<String> rows) {
        var result = new ArrayList<WordPath>();

        for (int y = 0; y < rows.size(); y++) {
            var row = rows.get(y);
            for (int x = 0; x < row.length(); x++) {
                result.addAll(wordsFrom(new WordPath(List.of(new Pt(x, y)), "" + row.charAt(x)), rows));
            }
        }

        return Collections.unmodifiableList(result);
    }

    SearchResult search(String candidate) {
        var exactMatch = false;
        var partialMatch = false;

        for (var word : words) {
            if (candidate.equals(word)) {
                exactMatch = true;
            } else if (word.startsWith(candidate)) {
                partialMatch = true;
            }

            if (exactMatch && partialMatch) {
                break;
            }
        }

        return new SearchResult(exactMatch, partialMatch);
    }

    public List<String> getWords() {
        return words;
    }
}
