package main

import (
	"github.com/rivo/tview"
)

var app = tview.NewApplication()
var textArea = tview.NewTextArea()
var applyButton = tview.NewButton("Apply")
var restartButton = tview.NewButton("Restart")
var candidates = tview.NewList()
var paths = tview.NewTextView()

func launch() {
	textArea.SetPlaceholder("Enter...")
	textArea.SetBorder(true)
	textArea.SetTitle("Letters")
	textArea.SetDisabled(false)

	applyButton.SetBorder(true)
	applyButton.SetSelectedFunc(func() {
		textArea.SetText("Apply clicked!", true)
	})
	restartButton.SetBorder(true)
	restartButton.SetSelectedFunc(func() {
		textArea.SetText("Restart clicked!", true)
	})

	leftCol := tview.NewFlex()
	leftCol.SetDirection(tview.FlexRow)
	leftCol.AddItem(textArea, 0, 1, false)
	leftCol.AddItem(applyButton, 3, 1, false)
	leftCol.AddItem(restartButton, 3, 1, false)

	candidates.SetTitle("Candidates")
	candidates.SetBorder(true)

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
