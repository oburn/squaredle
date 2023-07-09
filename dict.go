package main

import (
	"bufio"
	"os"
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
	return
}
