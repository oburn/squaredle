package main

import (
	"reflect"
	"testing"
)

func TestPtAdjacent(t *testing.T) {
	args := []struct {
		pt     Pt
		expect []Pt
	}{
		{pt: Pt{0, 0}, expect: []Pt{{1, 0}, {1, 1}, {0, 1}}},
		{pt: Pt{3, 3}, expect: []Pt{{2, 2}, {3, 2}, {2, 3}}},
		{pt: Pt{0, 3}, expect: []Pt{{0, 2}, {1, 2}, {1, 3}}},
		{pt: Pt{3, 0}, expect: []Pt{{3, 1}, {2, 1}, {2, 0}}},
		{pt: Pt{1, 0}, expect: []Pt{{2, 0}, {2, 1}, {1, 1}, {0, 1}, {0, 0}}},
		{pt: Pt{1, 1}, expect: []Pt{{0, 0}, {1, 0}, {2, 0}, {2, 1}, {2, 2}, {1, 2}, {0, 2}, {0, 1}}},
	}

	for _, a := range args {
		got := a.pt.adjacent(4, 4)
		if !reflect.DeepEqual(got, a.expect) {
			t.Errorf("adjacent(%v) = %v, expect %v", a.pt, got, a.expect)
		}
	}
}
