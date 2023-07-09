package main

import (
	"reflect"
	"testing"
)

func TestNewDict(t *testing.T) {
	d := NewDict("sample.dict", 4, 5)
	expected := []string{"drive", "from", "fromy"}
	if !reflect.DeepEqual(expected, d.words) {
		t.Errorf("Expected %v words, got %v", expected, d.words)
	}
}
