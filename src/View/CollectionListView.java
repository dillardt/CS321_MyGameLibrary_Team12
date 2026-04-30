package View;

import Model.Collection;
import Model.Game;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.util.List;

import static View.LoginView.*;

/**
 * Collection manager screen.
 *
 * Layout:
 *  CENTER — split pane: collections list (left) | games in selected collection (right)
 *  SOUTH  — Create Collection, Delete Collection, Remove Game, Back buttons
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
        setBackground(BG_DARK);
        setLayout(new BorderLayout(8, 8));
        setBorder(BorderFactory.createEmptyBorder(8, 10, 8, 10));

        collectionList = new JList<>(collectionListModel);
        styleList(collectionList);
        collectionList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        gamesInCollectionList = new JList<>(gamesInCollectionModel);
        styleList(gamesInCollectionList);
        gamesInCollectionList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        // Left panel
        JLabel leftLabel = new JLabel("Your Collections");
        leftLabel.setForeground(new Color(150, 170, 210));
        leftLabel.setFont(new Font("SansSerif", Font.BOLD, 13));
        leftLabel.setBorder(BorderFactory.createEmptyBorder(4, 6, 4, 0));

        JPanel leftPanel = new JPanel(new BorderLayout(0, 4));
        leftPanel.setBackground(new Color(35, 40, 52));
        leftPanel.setBorder(BorderFactory.createLineBorder(new Color(60, 70, 90)));
        leftPanel.add(leftLabel, BorderLayout.NORTH);
        JScrollPane leftScroll = new JScrollPane(collectionList);
        leftScroll.getViewport().setBackground(new Color(35, 40, 52));
        leftScroll.setBorder(BorderFactory.createEmptyBorder());
        leftPanel.add(leftScroll, BorderLayout.CENTER);

        // Right panel
        JLabel rightLabel = new JLabel("Games in Selected Collection");
        rightLabel.setForeground(new Color(150, 170, 210));
        rightLabel.setFont(new Font("SansSerif", Font.BOLD, 13));
        rightLabel.setBorder(BorderFactory.createEmptyBorder(4, 6, 4, 0));

        JPanel rightPanel = new JPanel(new BorderLayout(0, 4));
        rightPanel.setBackground(new Color(35, 40, 52));
        rightPanel.setBorder(BorderFactory.createLineBorder(new Color(60, 70, 90)));
        rightPanel.add(rightLabel, BorderLayout.NORTH);
        JScrollPane rightScroll = new JScrollPane(gamesInCollectionList);
        rightScroll.getViewport().setBackground(new Color(35, 40, 52));
        rightScroll.setBorder(BorderFactory.createEmptyBorder());
        rightPanel.add(rightScroll, BorderLayout.CENTER);

        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, leftPanel, rightPanel);
        splitPane.setResizeWeight(0.4);
        splitPane.setBackground(BG_DARK);
        splitPane.setBorder(BorderFactory.createEmptyBorder());
        add(splitPane, BorderLayout.CENTER);

        // Bottom button bar
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 6, 6));
        buttonPanel.setBackground(BG_PANEL);
        backButton             = LoginView.styledButton("Back",               new Color(60, 68, 82));
        createCollectionButton = LoginView.styledButton("Create Collection",  new Color(60, 110, 70));
        deleteCollectionButton = LoginView.styledButton("Delete Collection",  new Color(140, 60, 60));
        removeGameButton       = LoginView.styledButton("Remove Game",        new Color(130, 80, 40));
        buttonPanel.add(backButton);
        buttonPanel.add(createCollectionButton);
        buttonPanel.add(deleteCollectionButton);
        buttonPanel.add(removeGameButton);
        add(buttonPanel, BorderLayout.SOUTH);

        collectionList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) showGamesForSelectedCollection();
        });
    }

    // ── Data setters ──────────────────────────────────────────────────────────

    public void setCollections(List<Collection> collections) {
        collectionListModel.clear();
        if (collections != null) for (Collection c : collections) collectionListModel.addElement(c);
        showGamesForSelectedCollection();
    }

    // ── Getters ───────────────────────────────────────────────────────────────

    public Collection getSelectedCollection()      { return collectionList.getSelectedValue(); }
    public Game       getSelectedGameInCollection() { return gamesInCollectionList.getSelectedValue(); }

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

    private void showGamesForSelectedCollection() {
        gamesInCollectionModel.clear();
        Collection selected = collectionList.getSelectedValue();
        if (selected == null) return;
        for (Game g : selected.getGames()) gamesInCollectionModel.addElement(g);
    }

    private void styleList(JList<?> list) {
        list.setBackground(new Color(35, 40, 52));
        list.setForeground(new Color(210, 215, 230));
        list.setSelectionBackground(ACCENT);
        list.setSelectionForeground(Color.WHITE);
    }
}