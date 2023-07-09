package main

import (
	"flag"
	"fmt"
)

func main() {
	minLen := flag.Int("min", 4, "Minimum word length")
	maxLen := flag.Int("mxn", 4, "Maximum word length")
	dictFile := flag.String("dict", "/usr/share/dict/words", "Dictionary file")

	fmt.Printf("Loading dictionary from %s, min len %d, max len %d\n", *dictFile, *minLen, *maxLen)
}
