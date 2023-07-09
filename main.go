package main

import (
	"flag"
	"fmt"
)

func main() {
	minLen := flag.Int("min", 4, "Minimum word length")
	maxLen := flag.Int("max", 5, "Maximum word length")
	dictFile := flag.String("dict", "/usr/share/dict/words", "Dictionary file")
	flag.Parse()

	fmt.Printf("Loading dictionary from %s, min len %d, max len %d\n", *dictFile, *minLen, *maxLen)
	dict := NewDict(*dictFile, *minLen, *maxLen)
	fmt.Printf("Loaded %d words\n", len(dict.words))

	grid := Grid{
		rows: []string{
			"umut",
			"rcbu",
			"noal",
			"irte",
		},
		searcher: dict.Search,
	}
	paths := grid.solve()
	for _, path := range paths {
		fmt.Printf("solution: %s -> %v\n", path.word, path.steps)
	}
}
