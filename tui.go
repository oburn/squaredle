package main

import (
	"fmt"
	"sort"
	"strings"

	"github.com/rivo/tview"
	"github.com/samber/lo"
)

type tuiContext struct {
	dict           *Dict
	wordToPaths    map[string][]Path
	app            *tview.Application
	inputArea      *tview.TextArea
	candidatesList *tview.List
	pathsView      *tview.TextView
}

func launch(useDict *Dict) {
	myctx := tuiContext{
		dict:           useDict,
		app:            tview.NewApplication(),
		inputArea:      tview.NewTextArea(),
		candidatesList: tview.NewList(),
		pathsView:      tview.NewTextView(),
	}

	myctx.inputArea.SetPlaceholder("Enter...")
	myctx.inputArea.SetBorder(true)
	myctx.inputArea.SetTitle("Letters")
	myctx.inputArea.SetDisabled(false)

	applyButton := tview.NewButton("Apply")
	applyButton.SetBorder(true)
	applyButton.SetSelectedFunc(func() { myctx.handleApply() })

	restartButton := tview.NewButton("Restart")
	restartButton.SetBorder(true)
	restartButton.SetSelectedFunc(func() { myctx.handleRestart() })

	leftCol := tview.NewFlex()
	leftCol.SetDirection(tview.FlexRow)
	leftCol.AddItem(myctx.inputArea, 0, 1, false)
	leftCol.AddItem(applyButton, 3, 1, false)
	leftCol.AddItem(restartButton, 3, 1, false)

	myctx.candidatesList.SetTitle("Candidates")
	myctx.candidatesList.SetBorder(true)
	myctx.candidatesList.ShowSecondaryText(false)
	myctx.candidatesList.SetSelectedFunc(func(i int, s1, s2 string, r rune) {
		myctx.handleCandidateSelected()
	})
	myctx.candidatesList.SetChangedFunc(func(index int, main, secondary string, shortcut rune) {
		myctx.handleCandidateChanged(main)
	})

	myctx.pathsView.SetTitle("Paths")
	myctx.pathsView.SetBorder(true)

	rightCol := tview.NewFlex()
	rightCol.SetDirection(tview.FlexRow)
	rightCol.AddItem(myctx.candidatesList, 0, 1, false)
	rightCol.AddItem(myctx.pathsView, 6, 1, false)

	flex := tview.NewFlex().
		AddItem(leftCol, 0, 1, false).
		AddItem(rightCol, 0, 2, false)
	if err := myctx.app.SetRoot(flex, true).EnableMouse(true).SetFocus(myctx.inputArea).Run(); err != nil {
		panic(err)
	}
}

func (ctx *tuiContext) handleApply() {
	ctx.buildWordToPaths()

	currentCandidates := []string{}
	for i := ctx.candidatesList.GetItemCount() - 1; i >= 0; i-- {
		main, _ := ctx.candidatesList.GetItemText(i)
		currentCandidates = append(currentCandidates, main)
	}
	validCandidates := lo.Keys(ctx.wordToPaths)

	words := lo.Intersect(currentCandidates, validCandidates)
	sort.Strings(words)

	// add the words as candidates
	ctx.candidatesList.Clear()
	for _, w := range words {
		ctx.candidatesList.AddItem(w, w, 0, nil)
	}
	ctx.app.SetFocus(ctx.candidatesList)
}

func (ctx *tuiContext) handleRestart() {
	ctx.buildWordToPaths()

	// sort the words
	var words []string
	for k := range ctx.wordToPaths {
		words = append(words, k)
	}
	sort.Strings(words)

	// add the words as candidates
	ctx.candidatesList.Clear()
	for _, w := range words {
		ctx.candidatesList.AddItem(w, w, 0, nil)
	}
	ctx.app.SetFocus(ctx.candidatesList)
}

func (ctx *tuiContext) handleCandidateSelected() {
	ctx.candidatesList.RemoveItem(ctx.candidatesList.GetCurrentItem())
}

func (ctx *tuiContext) handleCandidateChanged(main string) {
	steps := lo.Map(ctx.wordToPaths[main], func(item Path, i int) string {
		return fmt.Sprintf("%v", item.steps)
	})
	allPaths := strings.Join(steps, "\n")
	ctx.pathsView.SetText(allPaths)
}

func (ctx *tuiContext) buildWordToPaths() {
	rows := lo.Filter(
		strings.Split(ctx.inputArea.GetText(), "\n"),
		func(item string, index int) bool { return lo.IsNotEmpty(item) },
	)
	grid := Grid{
		rows:     rows,
		searcher: ctx.dict.Search,
	}
	paths := grid.solve()

	// group by word
	ctx.wordToPaths = make(map[string][]Path)
	for _, path := range paths {
		ctx.wordToPaths[path.word] = append(ctx.wordToPaths[path.word], path)
	}
}
