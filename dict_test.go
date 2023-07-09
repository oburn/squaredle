package main

import (
	"reflect"
	"testing"
)

func TestNewDict(t *testing.T) {
	d := loadTestDict()
	expected := []string{"aaaa", "drive", "from", "fromy", "ttttt"}
	if !reflect.DeepEqual(expected, d.words) {
		t.Errorf("Expected %v words, got %v", expected, d.words)
	}
}

func TestSearchMissing(t *testing.T) {
	d := loadTestDict()
	args := []struct {
		word    string
		exact   bool
		partial bool
	}{
		{word: "xx", exact: false, partial: false},
		{word: "aa", exact: false, partial: true},
		{word: "ab", exact: false, partial: false},
	}
	for _, a := range args {
		exactMatch, partialMatch := d.Search(a.word)
		if exactMatch != a.exact {
			t.Errorf("For %s, expected exactMatch %v", a.word, a.exact)
		}
		if partialMatch != a.partial {
			t.Errorf("For %s, expected partialMatch %v", a.word, a.partial)
		}
	}
}

func loadTestDict() *Dict {
	return NewDict("sample.dict", 4, 5)
}
