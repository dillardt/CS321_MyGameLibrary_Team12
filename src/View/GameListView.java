package View;

import Model.Game;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

/**
 * Main game browser screen.
 *
 * Layout:
 *  NORTH  — top bar: collections dropdown + create/delete + logout
 *  CENTER — search/filter bar above; game results list left; recently viewed right
 *  SOUTH  — View Details, Add, Delete, Manage Collections buttons
 *
 * All the original methods are kept. New methods added:
 *  - addRecentlyViewedClickListener  (double-click recently viewed → open detail)
 *  - addManageCollectionsListener    (open the CollectionListView)
 *  - clearNewCollectionField         (reset after create)
 */
public class GameListView extends JPanel {

    // ── Models ────────────────────────────────────────────────────────────────
    private final DefaultListModel<Game>      gameListModel       = new DefaultListModel<>();
    private final DefaultListModel<Game>      recentlyViewedModel = new DefaultListModel<>();

    // ── Lists ─────────────────────────────────────────────────────────────────
    private final JList<Game> gameList;
    private final JList<Game> recentlyViewedList;

    // ── Search / filter controls ──────────────────────────────────────────────
    private final JTextField        searchField;
    private final JComboBox<String> genreFilterCombo;
    private final JComboBox<String> playersFilterCombo;
    private final JComboBox<String> minRatingFilterCombo;
    private final JButton           searchButton;
    private final JButton           filterButton;
    private final JButton           addGameButton;
    private final JButton           deleteGameButton;
    private final JButton           viewDetailsButton;
    private final JButton           manageCollectionsButton;

    // ── Collection controls ───────────────────────────────────────────────────
    private final JComboBox<String> collectionsCombo;
    private final JTextField        newCollectionField;
    private final JButton           createCollectionButton;
    private final JButton           deleteCollectionButton;
    private final JButton           logoutButton;

    public GameListView() {
        setLayout(new BorderLayout(8, 8));

        // ── Top bar: collections + create/delete + logout ─────────────────────
        JPanel topPanel = new JPanel();
        topPanel.setLayout(new BoxLayout(topPanel, BoxLayout.Y_AXIS));

        JPanel homePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));

        homePanel.add(new JLabel("Collections:"));
        collectionsCombo = new JComboBox<>();
        collectionsCombo.setPrototypeDisplayValue("Select Collection          ");
        homePanel.add(collectionsCombo);

        deleteCollectionButton = new JButton("Delete Collection");
        homePanel.add(deleteCollectionButton);

        homePanel.add(new JLabel("  New:"));
        newCollectionField = new JTextField(10);
        homePanel.add(newCollectionField);

        createCollectionButton = new JButton("Create");
        homePanel.add(createCollectionButton);

        manageCollectionsButton = new JButton("Manage Collections");
        homePanel.add(manageCollectionsButton);

        logoutButton = new JButton("Logout");
        homePanel.add(logoutButton);

        topPanel.add(homePanel);

        // ── Search / filter bar ───────────────────────────────────────────────
        JPanel controlsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));

        controlsPanel.add(new JLabel("Search:"));
        searchField = new JTextField(14);
        controlsPanel.add(searchField);

        controlsPanel.add(new JLabel("Genre:"));
        genreFilterCombo = new JComboBox<>(new String[]{
                "All",
                "Abstract Strategy", "Adventure", "Animals", "Bluffing",
                "Card Game", "Children's Game", "City Building", "Civilization",
                "Deduction", "Dice", "Economic", "Exploration",
                "Fantasy", "Farming", "Fighting", "Horror", "Humor",
                "Industry / Manufacturing", "Medieval", "Miniatures",
                "Mythology", "Negotiation", "Party Game", "Political",
                "Puzzle", "Racing", "Science Fiction", "Space Exploration",
                "Sports", "Territory Building", "Transportation",
                "Trivia", "Wargame", "Word Game", "World War II"
        });
        controlsPanel.add(genreFilterCombo);

        controlsPanel.add(new JLabel("Players:"));
        playersFilterCombo = new JComboBox<>(new String[]{"Any","1","2","3","4","5","6+"});
        controlsPanel.add(playersFilterCombo);

        controlsPanel.add(new JLabel("Min Rating:"));
        minRatingFilterCombo = new JComboBox<>(new String[]{"Any","5.0","6.0","7.0","8.0","9.0"});
        controlsPanel.add(minRatingFilterCombo);

        searchButton = new JButton("Search");
        filterButton = new JButton("Filter");
        controlsPanel.add(searchButton);
        controlsPanel.add(filterButton);

        topPanel.add(controlsPanel);
        add(topPanel, BorderLayout.NORTH);

        // ── Center: search results (left) + recently viewed (right) ───────────
        gameList = new JList<>(gameListModel);
        gameList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JPanel resultsPanel = new JPanel(new BorderLayout());
        resultsPanel.setBorder(BorderFactory.createTitledBorder("Search Results"));
        resultsPanel.add(new JScrollPane(gameList), BorderLayout.CENTER);

        recentlyViewedList = new JList<>(recentlyViewedModel);
        recentlyViewedList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JPanel recentPanel = new JPanel(new BorderLayout());
        recentPanel.setBorder(BorderFactory.createTitledBorder("Recently Viewed"));
        recentPanel.add(new JScrollPane(recentlyViewedList), BorderLayout.CENTER);

        JPanel centerPanel = new JPanel(new GridLayout(1, 2, 8, 8));
        centerPanel.add(resultsPanel);
        centerPanel.add(recentPanel);
        add(centerPanel, BorderLayout.CENTER);

        // ── Bottom bar: action buttons ────────────────────────────────────────
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        viewDetailsButton   = new JButton("View Details");
        addGameButton       = new JButton("Add Game");
        deleteGameButton    = new JButton("Delete Game");
        bottomPanel.add(viewDetailsButton);
        bottomPanel.add(addGameButton);
        bottomPanel.add(deleteGameButton);
        add(bottomPanel, BorderLayout.SOUTH);
    }

    // ── Data setters ──────────────────────────────────────────────────────────

    /** Replaces the search results list. */
    public void setGames(List<Game> games) {
        gameListModel.clear();
        if (games == null) return;
        for (Game g : games) gameListModel.addElement(g);
    }

    /** Replaces the recently-viewed list. */
    public void setRecentlyViewedGames(List<Game> games) {
        recentlyViewedModel.clear();
        if (games == null) return;
        for (Game g : games) recentlyViewedModel.addElement(g);
    }

    /** Refreshes the collection names in the dropdown. */
    public void setCollectionNames(List<String> names) {
        DefaultComboBoxModel<String> model = new DefaultComboBoxModel<>();
        if (names != null) for (String n : names) model.addElement(n);
        collectionsCombo.setModel(model);
    }

    /** Clears the new-collection text field after a successful create. */
    public void clearNewCollectionField() {
        newCollectionField.setText("");
    }

    // ── Getters ───────────────────────────────────────────────────────────────

    public int    getSelectedGameIndex()          { return gameList.getSelectedIndex(); }
    public Game   getSelectedGame()               { return gameList.getSelectedValue(); }
    public Game   getSelectedRecentlyViewedGame() { return recentlyViewedList.getSelectedValue(); }
    public String getSearchCriteria()             { return searchField.getText().trim(); }
    public String getSelectedCollectionName() {
        Object s = collectionsCombo.getSelectedItem();
        return s == null ? null : s.toString();
    }

    /** Returns the new collection name typed in the inline field. */
    public String getNewCollectionName() {
        return newCollectionField.getText().trim();
    }

    public String getGenreFilter() {
        Object s = genreFilterCombo.getSelectedItem();
        return (s == null || "All".equals(s)) ? "" : s.toString();
    }

    public int getPlayersFilter() {
        Object s = playersFilterCombo.getSelectedItem();
        if (s == null || "Any".equals(s)) return 0;
        return "6+".equals(s.toString()) ? 6 : parseIntOrDefault(s.toString(), 0);
    }

    public double getMinRatingFilter() {
        Object s = minRatingFilterCombo.getSelectedItem();
        if (s == null || "Any".equals(s)) return -1.0;
        return parseDoubleOrDefault(s.toString(), -1.0);
    }

    // ── Listener registration ─────────────────────────────────────────────────

    public void addSearchListener(ActionListener l)         { searchButton.addActionListener(l); }
    public void addFilterListener(ActionListener l)         { filterButton.addActionListener(l); }
    public void addViewDetailsListener(ActionListener l)    { viewDetailsButton.addActionListener(l); }
    public void addAddGameListener(ActionListener l)        { addGameButton.addActionListener(l); }
    public void addDeleteGameListener(ActionListener l)     { deleteGameButton.addActionListener(l); }
    public void addLogoutListener(ActionListener l)         { logoutButton.addActionListener(l); }
    public void addCreateCollectionListener(ActionListener l) { createCollectionButton.addActionListener(l); }
    public void addDeleteCollectionListener(ActionListener l) { deleteCollectionButton.addActionListener(l); }
    public void addManageCollectionsListener(ActionListener l){ manageCollectionsButton.addActionListener(l); }

    /** Fires when the collection combo box selection changes. */
    public void addCollectionSelectionListener(ActionListener l) {
        collectionsCombo.addActionListener(l);
    }

    /**
     * Double-clicking a game in the recently-viewed list triggers this listener.
     * The AppCoordinator reads getSelectedRecentlyViewedGame() to get the game.
     */
    public void addRecentlyViewedClickListener(ActionListener l) {
        recentlyViewedList.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2 && recentlyViewedList.getSelectedIndex() >= 0) {
                    l.actionPerformed(new java.awt.event.ActionEvent(
                            recentlyViewedList,
                            java.awt.event.ActionEvent.ACTION_PERFORMED,
                            "RECENT_DOUBLE_CLICK"));
                }
            }
        });
    }

    // ── Private helpers ───────────────────────────────────────────────────────

    private int parseIntOrDefault(String v, int fallback) {
        try { return Integer.parseInt(v.trim()); } catch (NumberFormatException e) { return fallback; }
    }

    private double parseDoubleOrDefault(String v, double fallback) {
        try { return Double.parseDouble(v.trim()); } catch (NumberFormatException e) { return fallback; }
    }
}