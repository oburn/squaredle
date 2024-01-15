package main

import (
	"fmt"
	"sort"
	"strings"

	"github.com/rivo/tview"
	"github.com/samber/lo"
)

type tuiContext struct {
	dict        *Dict
	wordToPaths map[string][]Path
	app         *tview.Application
	textArea    *tview.TextArea
	candidates  *tview.List
	paths       *tview.TextView
}

func launch(useDict *Dict) {
	myctx := tuiContext{
		dict:       useDict,
		app:        tview.NewApplication(),
		textArea:   tview.NewTextArea(),
		candidates: tview.NewList(),
		paths:      tview.NewTextView(),
	}

	//textArea.SetPlaceholder("Enter...")
	myctx.textArea.SetText("atvo\ntain\nonon\ntnic", true)
	myctx.textArea.SetBorder(true)
	myctx.textArea.SetTitle("Letters")
	myctx.textArea.SetDisabled(false)

	applyButton := tview.NewButton("Apply")
	applyButton.SetBorder(true)
	applyButton.SetSelectedFunc(func() { myctx.handleApply() })

	restartButton := tview.NewButton("Restart")
	restartButton.SetBorder(true)
	restartButton.SetSelectedFunc(func() { myctx.handleRestart() })

	leftCol := tview.NewFlex()
	leftCol.SetDirection(tview.FlexRow)
	leftCol.AddItem(myctx.textArea, 0, 1, false)
	leftCol.AddItem(applyButton, 3, 1, false)
	leftCol.AddItem(restartButton, 3, 1, false)

	myctx.candidates.SetTitle("Candidates")
	myctx.candidates.SetBorder(true)
	myctx.candidates.ShowSecondaryText(false)
	myctx.candidates.SetSelectedFunc(func(i int, s1, s2 string, r rune) {
		myctx.handleCandidateSelected()
	})
	myctx.candidates.SetChangedFunc(func(index int, main, secondary string, shortcut rune) {
		myctx.handleCandidateChanged(main)
	})

	myctx.paths.SetTitle("Paths")
	myctx.paths.SetBorder(true)

	rightCol := tview.NewFlex()
	rightCol.SetDirection(tview.FlexRow)
	rightCol.AddItem(myctx.candidates, 0, 1, false)
	rightCol.AddItem(myctx.paths, 6, 1, false)

	flex := tview.NewFlex().
		AddItem(leftCol, 0, 1, false).
		AddItem(rightCol, 0, 2, false)
	if err := myctx.app.SetRoot(flex, true).EnableMouse(true).SetFocus(myctx.textArea).Run(); err != nil {
		panic(err)
	}
}

func (ctx *tuiContext) handleApply() {
	ctx.buildWordToPaths()

	currentCandidates := []string{}
	for i := ctx.candidates.GetItemCount() - 1; i >= 0; i-- {
		main, _ := ctx.candidates.GetItemText(i)
		currentCandidates = append(currentCandidates, main)
	}
	validCandidates := lo.Keys(ctx.wordToPaths)

	words := lo.Intersect(currentCandidates, validCandidates)
	sort.Strings(words)

	// add the words as candidates
	ctx.candidates.Clear()
	for _, w := range words {
		ctx.candidates.AddItem(w, w, 0, nil)
	}
	ctx.app.SetFocus(ctx.candidates)
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
	ctx.candidates.Clear()
	for _, w := range words {
		ctx.candidates.AddItem(w, w, 0, nil)
	}
	ctx.app.SetFocus(ctx.candidates)
}

func (ctx *tuiContext) handleCandidateSelected() {
	ctx.candidates.RemoveItem(ctx.candidates.GetCurrentItem())
}

func (ctx *tuiContext) handleCandidateChanged(main string) {
	steps := lo.Map(ctx.wordToPaths[main], func(item Path, i int) string {
		return fmt.Sprintf("%v", item.steps)
	})
	allPaths := strings.Join(steps, "\n")
	ctx.paths.SetText(allPaths)
}

func (ctx *tuiContext) buildWordToPaths() {
	rows := strings.Split(ctx.textArea.GetText(), "\n")
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
