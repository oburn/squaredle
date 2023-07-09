package main

type Pt struct{ x, y int }
type Grid struct{ rows []string }
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
	return Path{
		steps: append(path.steps, pt),
		word:  path.word + ch,
	}
}
