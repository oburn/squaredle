package main

import (
	"bufio"
	"os"
	"strings"
	"unicode"
)

type Dict struct {
	words []string
}

func NewDict(dict string, minLen, maxLen int) *Dict {

	readFile, err := os.Open(dict)

	if err != nil {
		panic(err) // very lazy error handling
	}
	defer readFile.Close()

	fileScanner := bufio.NewScanner(readFile)
	fileScanner.Split(bufio.ScanLines)

	var words []string
	for fileScanner.Scan() {
		w := fileScanner.Text()
		if len(w) >= minLen &&
			len(w) <= maxLen &&
			IsLower(w) {
			words = append(words, w)
		}
	}

	return &Dict{words: words}
}

func IsLower(s string) bool {
	for _, r := range s {
		if !unicode.IsLower(r) && unicode.IsLetter(r) {
			return false
		}
	}
	return true
}

func (d *Dict) Search(word string) (exactMatch, partialMatch bool) {
	for _, w := range d.words {
		if w == word {
			exactMatch = true
		} else if strings.HasPrefix(w, word) {
			partialMatch = true
		}
		if exactMatch && partialMatch {
			break
		}
		// should really check if gone past where a match is possible
	}
	return
}
