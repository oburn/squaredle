package main

import (
	"fmt"
	"sort"
	"strings"

	"github.com/rivo/tview"
	"github.com/samber/lo"
)

var app = tview.NewApplication()
var textArea = tview.NewTextArea()
var applyButton = tview.NewButton("Apply")
var restartButton = tview.NewButton("Restart")
var candidates = tview.NewList()
var paths = tview.NewTextView()

var dict *Dict
var wordToPaths map[string][]Path

func launch(useDict *Dict) {
	dict = useDict // TODO find better way

	//textArea.SetPlaceholder("Enter...")
	textArea.SetText("atvo\ntain\nonon\ntnic", true)
	textArea.SetBorder(true)
	textArea.SetTitle("Letters")
	textArea.SetDisabled(false)

	applyButton.SetBorder(true)
	applyButton.SetSelectedFunc(handleApply)
	restartButton.SetBorder(true)
	restartButton.SetSelectedFunc(handleRestart)

	leftCol := tview.NewFlex()
	leftCol.SetDirection(tview.FlexRow)
	leftCol.AddItem(textArea, 0, 1, false)
	leftCol.AddItem(applyButton, 3, 1, false)
	leftCol.AddItem(restartButton, 3, 1, false)

	candidates.SetTitle("Candidates")
	candidates.SetBorder(true)
	candidates.ShowSecondaryText(false)
	candidates.SetSelectedFunc(handleCandidateSelected)
	candidates.SetChangedFunc(handleCandidateChanged)

	paths.SetTitle("Paths")
	paths.SetBorder(true)

	rightCol := tview.NewFlex()
	rightCol.SetDirection(tview.FlexRow)
	rightCol.AddItem(candidates, 0, 1, false)
	rightCol.AddItem(paths, 6, 1, false)

	flex := tview.NewFlex().
		AddItem(leftCol, 0, 1, false).
		AddItem(rightCol, 0, 2, false)
	if err := app.SetRoot(flex, true).EnableMouse(true).SetFocus(textArea).Run(); err != nil {
		panic(err)
	}
}

func handleApply() {
	buildWordToPaths()

	currentCandidates := []string{}
	for i := candidates.GetItemCount() - 1; i >= 0; i-- {
		main, _ := candidates.GetItemText(i)
		currentCandidates = append(currentCandidates, main)
	}
	validCandidates := lo.Keys(wordToPaths)

	words := lo.Intersect(currentCandidates, validCandidates)
	sort.Strings(words)

	// add the words as candidates
	candidates.Clear()
	for _, w := range words {
		candidates.AddItem(w, w, 0, nil)
	}
	app.SetFocus(candidates)
}

func handleRestart() {
	buildWordToPaths()

	// sort the words
	var words []string
	for k := range wordToPaths {
		words = append(words, k)
	}
	sort.Strings(words)

	// add the words as candidates
	candidates.Clear()
	for _, w := range words {
		candidates.AddItem(w, w, 0, nil)
	}
	app.SetFocus(candidates)
}

func handleCandidateSelected(i int, main, secondary string, r rune) {
	paths.SetText(fmt.Sprintf("%d - %s", i, main))
	candidates.RemoveItem(i)
}

func handleCandidateChanged(i int, main, secondary string, r rune) {
	steps := lo.Map(wordToPaths[main], func(item Path, i int) string {
		return fmt.Sprintf("%v", item.steps)
	})
	allPaths := strings.Join(steps, "\n")
	paths.SetText(allPaths)
}

func buildWordToPaths() {
	rows := strings.Split(textArea.GetText(), "\n")
	grid := Grid{
		rows:     rows,
		searcher: dict.Search,
	}
	paths := grid.solve()

	// group by word
	wordToPaths = make(map[string][]Path)
	for _, path := range paths {
		wordToPaths[path.word] = append(wordToPaths[path.word], path)
	}
}
