package main

import (
	"flag"
	"fmt"
	"sort"
)

func main() {
	minLen := flag.Int("min", 4, "Minimum word length")
	maxLen := flag.Int("max", 15, "Maximum word length")
	dictFile := flag.String("dict", "/usr/share/dict/words", "Dictionary file")
	tui := flag.Bool("tui", false, "Run the terminal UI")
	flag.Parse()
	fmt.Printf("Loading dictionary from %s, min len %d, max len %d\n", *dictFile, *minLen, *maxLen)
	dict := NewDict(*dictFile, *minLen, *maxLen)
	fmt.Printf("Loaded %d words\n", len(dict.words))
	if *tui {
		launch(dict)
	} else if len(flag.Args()) < 4 {
		fmt.Printf("Error: not enough rows\n")
		fmt.Printf("Usage: %s [options] row...\n", flag.Arg(0))
		flag.PrintDefaults()
	} else {
		batchMode(dict, flag.Args())
	}
}

func batchMode(dict *Dict, rows []string) {
	grid := Grid{
		rows:     rows,
		searcher: dict.Search,
	}
	paths := grid.solve()

	// group by word
	wordToPaths := make(map[string][]Path)
	for _, path := range paths {
		wordToPaths[path.word] = append(wordToPaths[path.word], path)
	}

	// sort the words
	var words []string
	for k := range wordToPaths {
		words = append(words, k)
	}
	sort.Strings(words)

	// output in sorted order, makes it easier to input
	for _, word := range words {
		for _, path := range wordToPaths[word] {
			fmt.Printf("%s\t-> %v\n", word, path.steps)
		}
	}
}
