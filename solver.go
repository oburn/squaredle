package main

const (
	MAX_WORD_LEN = 10
)

type Pt struct{ x, y int }
type Grid struct {
	rows     []string
	searcher func(word string) (exactMatch, partialMatch bool)
}
type Path struct {
	steps []Pt
	word  string
}

func (p Pt) adjacent(width, height int) []Pt {
	possible := []Pt{
		{p.x - 1, p.y - 1},
		{p.x, p.y - 1},
		{p.x + 1, p.y - 1},
		{p.x + 1, p.y},
		{p.x + 1, p.y + 1},
		{p.x, p.y + 1},
		{p.x - 1, p.y + 1},
		{p.x - 1, p.y},
	}
	var result []Pt
	for _, pt := range possible {
		if pt.x >= 0 && pt.x < width && pt.y >= 0 && pt.y < height {
			result = append(result, pt)
		}
	}
	return result
}

func (path Path) visited(pt Pt) bool {
	for _, p := range path.steps {
		if p == pt {
			return true
		}
	}
	return false
}

func (path Path) addStep(pt Pt, ch string) Path {
	// trick here is to clip the capacity of the slice to its length
	// so that when appending we get a new backing array
	currlen := len(path.steps)
	clipped := path.steps[0:currlen:currlen]
	return Path{
		steps: append(clipped, pt),
		word:  path.word + ch,
	}
}

func (g Grid) solve() []Path {
	var result []Path

	for y, row := range g.rows {
		for x, ch := range row {
			words := g.wordsFrom(Path{steps: []Pt{{x, y}}, word: string(ch)})
			result = append(result, words...)
		}
	}
	return result
}

func (g Grid) wordsFrom(path Path) []Path {
	var result []Path

	// Check is length of path not to big
	if len(path.word) > MAX_WORD_LEN {
		return result
	}

	exactMatch, partialMatch := g.searcher(path.word)
	if exactMatch {
		result = append(result, path)
	}
	if partialMatch {
		for _, pt := range path.steps[len(path.steps)-1].adjacent(len(g.rows[0]), len(g.rows)) {
			if !path.visited(pt) {
				nextPath := path.addStep(pt, string(g.rows[pt.y][pt.x]))
				words := g.wordsFrom(nextPath)
				result = append(result, words...)
			}
		}
	}

	return result
}
