package lantern

import com.googlecode.lanterna.TerminalSize
import com.googlecode.lanterna.TextColor
import com.googlecode.lanterna.gui2.*
import com.googlecode.lanterna.gui2.Window.Hint
import com.googlecode.lanterna.screen.Screen
import com.googlecode.lanterna.screen.TerminalScreen
import com.googlecode.lanterna.terminal.DefaultTerminalFactory
import com.googlecode.lanterna.terminal.Terminal

class App(private val solver: Solver) {
    private val knownSolutions = mutableSetOf<String>()
    private val historyWords = mutableSetOf<String>()
    private val terminal: Terminal = DefaultTerminalFactory().createTerminal()
    private val screen: Screen = TerminalScreen(terminal)
    private val lettersBox: TextBox =
        TextBox(TerminalSize(18, terminal.terminalSize.rows - 10))

    private val candidateListBox: ActionListBox =
        ActionListBox(
            TerminalSize(
                terminal.terminalSize.columns - 23,
                terminal.terminalSize.rows - 6
            )
        )

    private val historyBox: TextBox =
        TextBox(
            TerminalSize(
                terminal.terminalSize.columns - 23,
                terminal.terminalSize.rows - 6
            )
        ).setReadOnly(true)

    private val toggleGroupBy: CheckBoxList<String> =
        CheckBoxList<String>()
            .addItem("Group by len")
            .addListener { itemIndex, checked -> updateCandidates() }

    private val maskBox: TextBox =
        TextBox(TerminalSize(18, 1))
            .setTextChangeListener { newText, ignored -> handleMaskChange() }

    private val debugBox: TextBox =
        TextBox(TerminalSize(terminal.terminalSize.columns - 2, 2))
            .setReadOnly(true)
            .setEnabled(false)

    fun buildPathsPanel(): Border? {
        val result = Panel(LinearLayout(Direction.HORIZONTAL))
        result.addComponent(debugBox)
        return result.withBorder(Borders.doubleLineBevel("Debug:"))
    }

    fun buildLettersPanel(): Border? {
        val result = Panel(LinearLayout(Direction.HORIZONTAL))
        result.addComponent(lettersBox)
        return result.withBorder(Borders.doubleLineBevel("Letters:"))
    }

    fun buildCandidatesPanel(): Border {
        val result = Panel(LinearLayout(Direction.HORIZONTAL))
        result.addComponent(candidateListBox)
        return result.withBorder(Borders.doubleLineBevel("Candidates:"))
    }

    fun buildHistoryPanel(): Border {
        val result = Panel(LinearLayout(Direction.HORIZONTAL))
        result.addComponent(historyBox)
        return result.withBorder(Borders.doubleLineBevel("History:"))
    }

    fun buildActionsPanel(): Border {
        val result = Panel(LinearLayout(Direction.VERTICAL))

        val applyButton = Button("Apply") { this.handleApply() }
        val restartButton = Button("Restart") { this.handleRestart() }

        result.addComponent(applyButton)
        result.addComponent(restartButton)
        result.addComponent(toggleGroupBy)
        result.addComponent(Label("Mask:"))
        result.addComponent(maskBox)

        return result.withBorder(Borders.doubleLineBevel("Actions:"))
    }

    fun maskFilter(words: List<String>): List<String> {
        return try {
            words.filter { maskBox.text.toRegex().containsMatchIn(it) }
        } catch (e: IllegalArgumentException) {
            debugBox.text = "Mask is not a regex: ${e.message}"
            words
        }
    }

    fun updateCandidates() {
        val untriedWords = knownSolutions.subtract(historyWords)

        // Apply grouping and sorting
        val groupedWords = if (toggleGroupBy.isChecked(0)) {
            untriedWords.groupBy { it.length }.entries.sortedByDescending { it.key }.flatMap { it.value.sorted() }
        } else {
            untriedWords.sorted()
        }

        // Apply mask
        val maskedWords = maskFilter(groupedWords)

        candidateListBox.clearItems()
        maskedWords.forEach { w ->
            candidateListBox.addItem(w) { this.handleCandidate() }
        }
    }

    fun updateHistory() {
        val sortedWords = historyWords.sorted()
        // Apply mask
        val maskedWords = maskFilter(sortedWords)
        historyBox.text = maskedWords.joinToString(separator = "\n")
    }

    fun handleMaskChange() {
        updateHistory()
        updateCandidates()
    }

    fun handleApply() {
        calculateSolutions()
        updateCandidates()
    }

    fun handleRestart() {
        historyWords.clear()
        handleApply()
        updateHistory()
    }

    fun handleCandidate() {
        historyWords.add(candidateListBox.selectedItem.toString())
        updateHistory()
        candidateListBox.removeItem(candidateListBox.selectedIndex)
    }

    private fun calculateSolutions() {
        knownSolutions.clear()
        val rows = lettersBox.text.split('\n')
            .map { it.trim() }
            .filter { it.isNotEmpty() }
        knownSolutions.addAll(solver.solve(rows).map { it.word })
    }

    fun display() {
        screen.startScreen()
        val window = BasicWindow("Solver")
        window.setHints(listOf(Hint.FULL_SCREEN, Hint.NO_DECORATIONS))

        val lettersActionsPanel = Panel(LinearLayout(Direction.VERTICAL))
        lettersActionsPanel.addComponent(buildLettersPanel())
        lettersActionsPanel.addComponent(buildActionsPanel())

        val lettersCandidatesActionsPanel = Panel(LinearLayout(Direction.HORIZONTAL))
        lettersCandidatesActionsPanel.addComponent(lettersActionsPanel)
        lettersCandidatesActionsPanel.addComponent(buildCandidatesPanel())
        lettersCandidatesActionsPanel.addComponent(buildHistoryPanel())

        val outerPanel = Panel(LinearLayout(Direction.VERTICAL))
        outerPanel.addComponent(lettersCandidatesActionsPanel)
        outerPanel.addComponent(buildPathsPanel())
        window.component = outerPanel

        // Create gui and start gui
        val gui = MultiWindowTextGUI(
            screen,
            DefaultWindowManager(),
            EmptySpace(TextColor.ANSI.BLUE)
        )
        gui.addWindowAndWait(window)
    }
}
