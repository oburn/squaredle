package main

import (
	"flag"
	"fmt"
	"sort"
)

func main() {
	minLen := flag.Int("min", 4, "Minimum word length")
	maxLen := flag.Int("max", 10, "Maximum word length")
	dictFile := flag.String("dict", "/usr/share/dict/words", "Dictionary file")
	flag.Parse()
	if len(flag.Args()) < 4 {
		fmt.Printf("Error: not enough rows\n")
		fmt.Printf("Usage: %s [options] row...\n", flag.Arg(0))
		flag.PrintDefaults()
		return
	}

	fmt.Printf("Loading dictionary from %s, min len %d, max len %d\n", *dictFile, *minLen, *maxLen)
	dict := NewDict(*dictFile, *minLen, *maxLen)
	fmt.Printf("Loaded %d words\n", len(dict.words))

	grid := Grid{
		rows:     flag.Args(),
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
