package main

import (
	"reflect"
	"testing"
)

func TestNewDict(t *testing.T) {
	d := NewDict("sample.dict", 4, 5)
	expected := []string{"aaaa", "drive", "from", "fromy", "ttttt"}
	if !reflect.DeepEqual(expected, d.words) {
		t.Errorf("Expected %v words, got %v", expected, d.words)
	}
}
