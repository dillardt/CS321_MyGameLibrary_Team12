package View;

import Model.Collection;
import Model.Game;
import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.ListSelectionModel;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionListener;
import java.util.List;

/**
 * Collection manager screen.
 *
 * Layout:
 *  CENTER — split pane: collections list (left) | games in selected collection (right)
 *  SOUTH  — Create Collection, Delete Collection, Remove Game, Back buttons
 *
 * Changes from original:
 *  - addBackListener() added so AppCoordinator can return to game list
 *  - Labels added above each list panel so users know what they're looking at
 */
public class CollectionListView extends JPanel {

    private final DefaultListModel<Collection> collectionListModel    = new DefaultListModel<>();
    private final DefaultListModel<Game>       gamesInCollectionModel = new DefaultListModel<>();

    private final JList<Collection> collectionList;
    private final JList<Game>       gamesInCollectionList;

    private final JButton createCollectionButton;
    private final JButton deleteCollectionButton;
    private final JButton removeGameButton;
    private final JButton backButton;

    public CollectionListView() {
        setLayout(new BorderLayout(8, 8));

        collectionList = new JList<>(collectionListModel);
        collectionList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        gamesInCollectionList = new JList<>(gamesInCollectionModel);
        gamesInCollectionList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        // Left panel — list of all collections
        JPanel leftPanel = new JPanel(new BorderLayout());
        leftPanel.add(new JLabel("Your Collections"), BorderLayout.NORTH);
        leftPanel.add(new JScrollPane(collectionList), BorderLayout.CENTER);

        // Right panel — games inside the selected collection
        JPanel rightPanel = new JPanel(new BorderLayout());
        rightPanel.add(new JLabel("Games in Selected Collection"), BorderLayout.NORTH);
        rightPanel.add(new JScrollPane(gamesInCollectionList), BorderLayout.CENTER);

        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, leftPanel, rightPanel);
        splitPane.setResizeWeight(0.4);
        add(splitPane, BorderLayout.CENTER);

        // Bottom button bar
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        backButton             = new JButton("Back");
        createCollectionButton = new JButton("Create Collection");
        deleteCollectionButton = new JButton("Delete Collection");
        removeGameButton       = new JButton("Remove Game");
        buttonPanel.add(backButton);
        buttonPanel.add(createCollectionButton);
        buttonPanel.add(deleteCollectionButton);
        buttonPanel.add(removeGameButton);
        add(buttonPanel, BorderLayout.SOUTH);

        // Selecting a collection refreshes the games panel automatically
        collectionList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                showGamesForSelectedCollection();
            }
        });
    }

    // ── Data setters ──────────────────────────────────────────────────────────

    /** Replaces the list of collections and refreshes the games panel. */
    public void setCollections(List<Collection> collections) {
        collectionListModel.clear();
        if (collections != null) {
            for (Collection c : collections) collectionListModel.addElement(c);
        }
        showGamesForSelectedCollection();
    }

    // ── Getters ───────────────────────────────────────────────────────────────

    public Collection getSelectedCollection()     { return collectionList.getSelectedValue(); }
    public Game       getSelectedGameInCollection(){ return gamesInCollectionList.getSelectedValue(); }

    /** Shows an input dialog and returns the trimmed name, or null if cancelled. */
    public String promptForCollectionName() {
        String name = JOptionPane.showInputDialog(this, "Enter collection name:");
        return name == null ? null : name.trim();
    }

    // ── Listener registration ─────────────────────────────────────────────────

    public void addBackListener(ActionListener l)             { backButton.addActionListener(l); }
    public void addCreateCollectionListener(ActionListener l) { createCollectionButton.addActionListener(l); }
    public void addDeleteCollectionListener(ActionListener l) { deleteCollectionButton.addActionListener(l); }
    public void addRemoveGameListener(ActionListener l)       { removeGameButton.addActionListener(l); }

    // ── Private helpers ───────────────────────────────────────────────────────

    /**
     * Reloads the right-side game list based on the currently selected collection.
     * Called automatically when the collection selection changes.
     */
    private void showGamesForSelectedCollection() {
        gamesInCollectionModel.clear();
        Collection selected = collectionList.getSelectedValue();
        if (selected == null) return;
        for (Game g : selected.getGames()) {
            gamesInCollectionModel.addElement(g);
        }
    }
}