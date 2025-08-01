package lantern

import com.googlecode.lanterna.TerminalSize
import com.googlecode.lanterna.TextColor
import com.googlecode.lanterna.gui2.*
import com.googlecode.lanterna.gui2.Window.Hint
import com.googlecode.lanterna.screen.Screen
import com.googlecode.lanterna.screen.TerminalScreen
import com.googlecode.lanterna.terminal.DefaultTerminalFactory
import com.googlecode.lanterna.terminal.Terminal
import java.io.IOException
import java.util.*

class App(private val solver: Solver) {
    private var wordToPaths: Map<String, List<WordPath>>? = null
    private val terminal: Terminal = DefaultTerminalFactory().createTerminal()
    private val screen: Screen = TerminalScreen(terminal)
    private val lettersBox: TextBox = TextBox(
        TerminalSize(18, terminal.terminalSize.rows - 10)
    )
    private val candidateListBox: ActionListBox = ActionListBox(
        TerminalSize(
            terminal.terminalSize.columns - 23,
            terminal.terminalSize.rows - 8
        )
    )
    private val pathsBox: TextBox = TextBox(
        TerminalSize(terminal.terminalSize.columns - 2, 4)
    )

    init {
        pathsBox.setReadOnly(true)
        pathsBox.setEnabled(false)
    }

    fun buildPathsPanel(): Border? {
        val result = Panel(LinearLayout(Direction.HORIZONTAL))
        result.addComponent(pathsBox)
        return result.withBorder(Borders.doubleLineBevel("Paths:"))
    }

    fun buildLettersPanel(): Border? {
        val result = Panel(LinearLayout(Direction.HORIZONTAL))
        result.addComponent(lettersBox)
        return result.withBorder(Borders.doubleLineBevel("Letters:"))
    }

    fun buildCandidatesPanel(): Border? {
        val result = Panel(LinearLayout(Direction.HORIZONTAL))
        result.addComponent(candidateListBox)
        return result.withBorder(Borders.doubleLineBevel("Candidates:"))
    }

    fun buildActionsPanel(): Border? {
        val result = Panel(LinearLayout(Direction.HORIZONTAL))

        val applyButton = Button("Apply") { this.handleApply() }
        val restartButton = Button("Restart") { this.handleRestart() }

        result.addComponent(applyButton)
        result.addComponent(restartButton)

        return result.withBorder(Borders.doubleLineBevel("Actions:"))
    }

    fun handleApply() {
        buildWordToPaths()

        for (i in candidateListBox.getItemCount() - 1 downTo 0) {
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
        wordToPaths!!.keys.sorted()
            .forEach { w ->
                candidateListBox.addItem(w) { this.handleCandidate() }
            }
    }

    fun handleCandidate() {
        pathsBox.setText("Need to handle: " + candidateListBox.getSelectedIndex())
        candidateListBox.removeItem(candidateListBox.getSelectedIndex())
    }

    private fun buildWordToPaths() {
        val rows = lettersBox.text.split("\\n".toRegex())
            .map { it.trim() }
            .filter { it.isNotEmpty() }
        val soln = solver.solve(rows)
        wordToPaths = soln.groupBy { it.word }
    }

    @Throws(IOException::class)
    fun display() {
        screen.startScreen()
        val window = BasicWindow("Solver")
        window.setHints(Arrays.asList<Hint?>(Hint.FULL_SCREEN, Hint.NO_DECORATIONS))

        val lettersActionsPanel = Panel(LinearLayout(Direction.VERTICAL))
        lettersActionsPanel.addComponent(buildLettersPanel())
        lettersActionsPanel.addComponent(buildActionsPanel())

        val lettersCandidatesActionsPanel = Panel(LinearLayout(Direction.HORIZONTAL))
        lettersCandidatesActionsPanel.addComponent(lettersActionsPanel)
        lettersCandidatesActionsPanel.addComponent(buildCandidatesPanel())

        val outerPanel = Panel(LinearLayout(Direction.VERTICAL))
        outerPanel.addComponent(lettersCandidatesActionsPanel)
        outerPanel.addComponent(buildPathsPanel())
        window.setComponent(outerPanel)

        // Create gui and start gui
        val gui = MultiWindowTextGUI(
            screen,
            DefaultWindowManager(),
            EmptySpace(TextColor.ANSI.BLUE)
        )
        gui.addWindowAndWait(window)
    }
}
