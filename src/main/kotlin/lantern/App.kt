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
    private var wordToPaths: Map<String, List<WordPath>>? = null
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
            .addListener { itemIndex, checked -> handleToggling(checked) }

    private val maskBox: TextBox =
        TextBox(TerminalSize(18, 1))
            .setTextChangeListener { newText, ignored -> handleMask(newText) }

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

    fun handleApply() {
        buildWordToPaths()

        for (i in candidateListBox.itemCount - 1 downTo 0) {
            // ugly hack relying on toString of the Runnable being to word
            val word: String? = candidateListBox.getItemAt(i).toString()
            if (!wordToPaths!!.containsKey(word)) {
                candidateListBox.removeItem(i)
            }
        }
    }

    fun handleRestart() {
        buildWordToPaths()

        candidateListBox.clearItems()
        historyBox.text = ""
        wordToPaths!!.keys.sorted()
            .forEach { w ->
                candidateListBox.addItem(w) { this.handleCandidate() }
            }
    }

    fun handleToggling(checked: Boolean) {
        debugBox.text = "Toggle state: $checked"
    }

    fun handleCandidate() {
        debugBox.text = "Need to handle: " + candidateListBox.selectedIndex
        historyBox.text = candidateListBox.selectedItem.toString() + "\n" + historyBox.text
        candidateListBox.removeItem(candidateListBox.selectedIndex)
    }

    private fun handleMask(text: String) {
        debugBox.text = "Mask became: $text"
    }

    private fun buildWordToPaths() {
        val rows = lettersBox.text.split("\\n".toRegex())
            .map { it.trim() }
            .filter { it.isNotEmpty() }
        val solution = solver.solve(rows)
        wordToPaths = solution.groupBy { it.word }
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
