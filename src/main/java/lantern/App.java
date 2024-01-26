package lantern;

import static java.util.Objects.requireNonNull;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.Arrays;

import org.eclipse.collections.api.multimap.list.ImmutableListMultimap;
import org.eclipse.collections.impl.factory.Lists;

import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.gui2.ActionListBox;
import com.googlecode.lanterna.gui2.BasicWindow;
import com.googlecode.lanterna.gui2.Border;
import com.googlecode.lanterna.gui2.Borders;
import com.googlecode.lanterna.gui2.Button;
import com.googlecode.lanterna.gui2.DefaultWindowManager;
import com.googlecode.lanterna.gui2.Direction;
import com.googlecode.lanterna.gui2.EmptySpace;
import com.googlecode.lanterna.gui2.LinearLayout;
import com.googlecode.lanterna.gui2.MultiWindowTextGUI;
import com.googlecode.lanterna.gui2.Panel;
import com.googlecode.lanterna.gui2.TextBox;
import com.googlecode.lanterna.gui2.Window;
import com.googlecode.lanterna.screen.Screen;
import com.googlecode.lanterna.screen.TerminalScreen;
import com.googlecode.lanterna.terminal.DefaultTerminalFactory;
import com.googlecode.lanterna.terminal.Terminal;

public class App {
    private final Terminal terminal;
    private final Screen screen;
    private final TextBox lettersBox;
    private final ActionListBox candidateListBox;
    private final TextBox pathsBox;
    private final Solver solver;
    private ImmutableListMultimap<String, WordPath>  wordToPaths;

    public App(Solver solver) throws IOException {
        this.solver = requireNonNull(solver);
        terminal = new DefaultTerminalFactory().createTerminal();
        var terminalRows = terminal.getTerminalSize().getRows();
        var terminalCols = terminal.getTerminalSize().getColumns();
        screen = new TerminalScreen(terminal);
        lettersBox = new TextBox(new TerminalSize(18, terminalRows - 10));
        candidateListBox = new ActionListBox(new TerminalSize(terminalCols - 23, terminalRows - 8));
        pathsBox = new TextBox(new TerminalSize(terminalCols - 2, 4));
        pathsBox.setReadOnly(true);
        pathsBox.setEnabled(false);
    }

    Border buildPathsPanel() {
        var result = new Panel(new LinearLayout(Direction.HORIZONTAL));
        result.addComponent(pathsBox);
        return result.withBorder(Borders.doubleLineBevel("Paths:"));
    }

    Border buildLettersPanel() {
        var result = new Panel(new LinearLayout(Direction.HORIZONTAL));
        result.addComponent(lettersBox);
        return result.withBorder(Borders.doubleLineBevel("Letters:"));
    }

    Border buildCandidatesPanel() {
        var result = new Panel(new LinearLayout(Direction.HORIZONTAL));
        result.addComponent(candidateListBox);
        return result.withBorder(Borders.doubleLineBevel("Candidates:"));
    }

    Border buildActionsPanel() {
        var result = new Panel(new LinearLayout(Direction.HORIZONTAL));

        var applyButton = new Button("Apply", () -> {
            handleApply();
        });
        var restartButton = new Button("Restart", () -> {
            handleRestart();
        });

        result.addComponent(applyButton);
        result.addComponent(restartButton);

        return result.withBorder(Borders.doubleLineBevel("Actions:"));
    }

    void display() throws IOException {
        screen.startScreen();
        var window = new BasicWindow("Solver");
        window.setHints(Arrays.asList(Window.Hint.FULL_SCREEN, Window.Hint.NO_DECORATIONS));

        var lettersActionsPanel = new Panel(new LinearLayout(Direction.VERTICAL));
        lettersActionsPanel.addComponent(buildLettersPanel());
        lettersActionsPanel.addComponent(buildActionsPanel());

        var lettersCandidatesActionsPanel = new Panel(new LinearLayout(Direction.HORIZONTAL));
        lettersCandidatesActionsPanel.addComponent(lettersActionsPanel);
        lettersCandidatesActionsPanel.addComponent(buildCandidatesPanel());

        var outerPanel = new Panel(new LinearLayout(Direction.VERTICAL));
        outerPanel.addComponent(lettersCandidatesActionsPanel);
        outerPanel.addComponent(buildPathsPanel());
        window.setComponent(outerPanel);

        // Create gui and start gui
        var gui = new MultiWindowTextGUI(
                screen,
                new DefaultWindowManager(),
                new EmptySpace(TextColor.ANSI.BLUE));
        gui.addWindowAndWait(window);
    }

    void handleApply() {
        buildWordToPaths();

        for (int i = candidateListBox.getItemCount() - 1; i >= 0; i--) {
            // ugly hack relying on toString of the Runnable being to word
            var word = candidateListBox.getItemAt(i).toString();
            if (!wordToPaths.containsKey(word)) {
                candidateListBox.removeItem(i);
            }
        }
    }

    void handleRestart() {
        buildWordToPaths();

        candidateListBox.clearItems();
        wordToPaths.forEachKey(w -> {
            candidateListBox.addItem(w, () -> {
                handleCandidate();
            });
        });
    }

    void handleCandidate() {
        pathsBox.setText("Need to handle: " + candidateListBox.getSelectedIndex());
        candidateListBox.removeItem(candidateListBox.getSelectedIndex());
    }

    private void buildWordToPaths() {
        var rows = Lists.immutable.fromStream(
            Arrays.stream(lettersBox.getText().split("\\n"))
                .map(l -> l.trim())
                .filter(l -> l.length() > 0));

        var soln = solver.solve(rows);
        wordToPaths =  soln.groupBy(v -> v.word());
    }

    public static void main(String[] args) throws IOException {
        var solver = Solver.load(Paths.get("/usr/share/dict/words"), 4, 15);
        var soln = solver.solve(Lists.immutable.of("titd", "aprz", "rlaw", "blyv"));
        System.out.printf("Solved, found %d words\n", soln.size());
        // soln.stream().forEach(wp -> System.out.println(wp.word()));
    }

    public static void mainTui(String[] args) throws IOException {
        var solver = Solver.load(Paths.get("/usr/share/dict/words"), 4, 15);
        var app = new App(solver);
        app.display();
    }
}
