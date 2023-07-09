package main

import (
	"flag"
	"fmt"
	"sort"
)

func main() {
	minLen := flag.Int("min", 4, "Minimum word length")
	maxLen := flag.Int("max", 5, "Maximum word length")
	dictFile := flag.String("dict", "/usr/share/dict/words", "Dictionary file")
	flag.Parse()
	flag.Args()

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
	// for _, path := range paths {
	// 	fmt.Printf("solution: %s -> %v\n", path.word, path.steps)
	// }

	wordToPath := make(map[string]Path)
	for _, path := range paths {
		wordToPath[path.word] = path
	}
	var words []string
	for k := range wordToPath {
		words = append(words, k)
	}
	sort.Strings(words)

	for _, word := range words {
		fmt.Printf("%s\t-> %v\n", word, wordToPath[word].steps)
	}
}
