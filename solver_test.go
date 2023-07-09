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

func TestVisited(t *testing.T) {
	args := []struct {
		path   Path
		pt     Pt
		expect bool
	}{
		{path: Path{steps: []Pt{{0, 0}}}, pt: Pt{0, 0}, expect: true},
		{path: Path{steps: []Pt{{0, 0}}}, pt: Pt{0, 1}, expect: false},
		{path: Path{steps: []Pt{{0, 0}, {1, 1}}}, pt: Pt{0, 0}, expect: true},
		{path: Path{steps: []Pt{{0, 0}, {1, 1}}}, pt: Pt{1, 0}, expect: false},
		{path: Path{steps: []Pt{{0, 0}, {1, 1}}}, pt: Pt{1, 1}, expect: true},
	}
	for _, a := range args {
		got := a.path.visited(a.pt)
		if got != a.expect {
			t.Errorf("visited(%v) = %v, expect %v", a.pt, got, a.expect)
		}
	}
}

func TestAddStep(t *testing.T) {
	args := []struct {
		input  Path
		step   Pt
		ch     string
		expect Path
	}{
		{
			input:  Path{steps: []Pt{{0, 0}}, word: "a"},
			step:   Pt{1, 1},
			ch:     "b",
			expect: Path{steps: []Pt{{0, 0}, {1, 1}}, word: "ab"},
		},
		{
			input:  Path{steps: []Pt{{0, 0}, {1, 1}}, word: "ab"},
			step:   Pt{2, 2},
			ch:     "c",
			expect: Path{steps: []Pt{{0, 0}, {1, 1}, {2, 2}}, word: "abc"},
		},
	}
	for _, a := range args {
		got := a.input.addStep(a.step, a.ch)
		if !reflect.DeepEqual(got, a.expect) {
			t.Errorf("addStep(%v, %v, %v) = %v, expect %v", a.input, a.step, a.ch, got, a.expect)
		}
	}
}
