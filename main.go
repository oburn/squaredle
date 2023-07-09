package main

import (
	"flag"
	"fmt"
)

func main() {
	minLen := flag.Int("min", 4, "Minimum word length")
	maxLen := flag.Int("mxn", 5, "Maximum word length")
	dictFile := flag.String("dict", "/usr/share/dict/words", "Dictionary file")

	fmt.Printf("Loading dictionary from %s, min len %d, max len %d\n", *dictFile, *minLen, *maxLen)
	dict := NewDict(*dictFile, *minLen, *maxLen)
	fmt.Printf("Loaded %d words\n", len(dict.words))
}
