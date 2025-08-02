package lantern

import com.googlecode.lanterna.TerminalSize
import com.googlecode.lanterna.TextColor
import com.googlecode.lanterna.gui2.*
import com.googlecode.lanterna.gui2.Window.Hint
import com.googlecode.lanterna.screen.Screen
import com.googlecode.lanterna.screen.TerminalScreen
import com.googlecode.lanterna.terminal.DefaultTerminalFactory
import com.googlecode.lanterna.terminal.Terminal
import org.eclipse.collections.api.block.procedure.Procedure
import org.eclipse.collections.api.factory.Lists
import org.eclipse.collections.api.multimap.list.ImmutableListMultimap
import java.io.IOException
import java.nio.file.Paths
import java.util.*

class NewApp(private val solver: Solver) {
    private var wordToPaths: ImmutableListMultimap<String, WordPath>? = null
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

        val applyButton = Button("Apply", Runnable { this.handleApply() })
        val restartButton = Button("Restart", Runnable { this.handleRestart() })

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
        wordToPaths!!.keySet().toSortedList()
            .forEach(Procedure { w: String? -> candidateListBox.addItem(w, Runnable { this.handleCandidate() }) })
    }

    fun handleCandidate() {
        pathsBox.setText("Need to handle: " + candidateListBox.getSelectedIndex())
        candidateListBox.removeItem(candidateListBox.getSelectedIndex())
    }

    private fun buildWordToPaths() {
        val rows = Lists.immutable.fromStream<String?>(
            Arrays.stream<String>(lettersBox.getText().split("\\n".toRegex()).dropLastWhile { it.isEmpty() }
                .toTypedArray())
                .map<String?> { obj: String? -> obj!!.trim { it <= ' ' } }
                .filter { l: String? -> !l!!.isEmpty() })

        val soln = solver.solve(rows)
        wordToPaths = soln.groupBy<String?>(WordPath::word)
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

fun main(args: Array<String>) {
    val solver = Solver.load(Paths.get("/usr/share/dict/words"), 4, 15)
    val app = NewApp(solver)
    app.display()
}
