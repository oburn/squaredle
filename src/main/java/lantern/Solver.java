package lantern;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.eclipse.collections.api.list.ImmutableList;
import org.eclipse.collections.api.factory.Lists;

public class Solver {
    static record SearchResult(boolean exactMatch, boolean partialMatch) {
    }

    private final ImmutableList<String> words;

    private Solver(ImmutableList<String> words) {
        this.words = words;
    }

    static Solver load(Path p, int minLen, int maxLen) throws IOException {
        var wordsStream = Files.lines(p)
                .filter(l -> l.length() >= minLen)
                .filter(l -> l.length() <= maxLen)
                .filter(l -> l.equals(l.toLowerCase()));
        return new Solver(Lists.immutable.fromStream(wordsStream));
    }

    ImmutableList<WordPath> wordsFrom(WordPath wp, ImmutableList<String> rows) {
        if (wp.word().length() > 20) { // love a magic constant
            return Lists.immutable.empty();
        }

        var result = Lists.mutable.<WordPath>empty();
        var searchResult = search(wp.word());
        if (searchResult.exactMatch()) {
            result.add(wp);
        }

        if (searchResult.partialMatch()) {
            wp.steps().getLast().adjacent(rows.getFirst().length(), rows.size())
                    .select(p -> !wp.visited(p))
                    .forEach(p -> {
                        var nextPath = wp.addStep(p, rows.get(p.y()).charAt(p.x()));
                        var extraWords = wordsFrom(nextPath, rows);
                        result.withAll(extraWords);
                    });
            ;
        }

        return result.toImmutableList();
    }

    ImmutableList<WordPath> solve(ImmutableList<String> rows) {
        var result = Lists.mutable.<WordPath>empty();

        for (int y = 0; y < rows.size(); y++) {
            var row = rows.get(y);
            for (int x = 0; x < row.length(); x++) {
                result.withAll(wordsFrom(new WordPath(Lists.immutable.of(new Pt(x, y)), "" + row.charAt(x)), rows));
            }
        }

        return result.toImmutableList();
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

    public ImmutableList<String> getWords() {
        return words;
    }
}
