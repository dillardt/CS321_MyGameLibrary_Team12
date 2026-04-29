package View;

import Model.Game;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.List;

/**
 * Main game browser screen.
 *
 * Layout:
 *  NORTH  — top bar: collections dropdown + create/delete + logout
 *  CENTER — search/filter bar above; game results list left; recently viewed right
 *  SOUTH  — View Details, Add Game (admin only), Delete Game (admin only), Manage Collections
 *
 * Key fixes:
 *  - Collections dropdown always has "-- All Games --" at index 0 so the user
 *    is never locked inside an empty collection after creating one.
 *  - setCollectionNames() suppresses the ActionListener during model rebuild
 *    so it doesn't auto-fire and scope the game list to the first collection.
 *  - setAdminControlsVisible() shows/hides Add Game and Delete Game buttons
 *    based on whether the logged-in user has ADMIN role.
 *  - resetCollectionSelection() resets the dropdown back to "-- All Games --".
 */
public class GameListView extends JPanel {

    // Sentinel value shown at index 0 — means "show full game list"
    private static final String ALL_GAMES = "-- All Games --";

    // Flag to suppress the collection ActionListener during model rebuilds
    private boolean suppressCollectionListener = false;

    // ── Models ────────────────────────────────────────────────────────────────
    private final DefaultListModel<Game> gameListModel       = new DefaultListModel<>();
    private final DefaultListModel<Game> recentlyViewedModel = new DefaultListModel<>();

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
        viewDetailsButton       = new JButton("View Details");
        addGameButton           = new JButton("Add Game");
        deleteGameButton        = new JButton("Delete Game");
        addGameButton.setVisible(false);   // hidden until admin logs in
        deleteGameButton.setVisible(false);
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

    /**
     * Refreshes the collections dropdown.
     * Always inserts "-- All Games --" at index 0 so the user can always
     * get back to the full list.
     * Suppresses the ActionListener during rebuild so it doesn't auto-fire
     * and accidentally scope the game list to the first collection.
     */
    public void setCollectionNames(List<String> names) {
        suppressCollectionListener = true;
        DefaultComboBoxModel<String> model = new DefaultComboBoxModel<>();
        model.addElement(ALL_GAMES); // index 0 — always present
        if (names != null) {
            for (String n : names) model.addElement(n);
        }
        collectionsCombo.setModel(model);
        collectionsCombo.setSelectedIndex(0); // default to All Games
        suppressCollectionListener = false;
    }

    /**
     * Resets the collections dropdown back to "-- All Games --".
     * Called after creating a new collection so the user isn't locked
     * inside the empty collection they just made.
     */
    public void resetCollectionSelection() {
        suppressCollectionListener = true;
        collectionsCombo.setSelectedIndex(0);
        suppressCollectionListener = false;
    }

    /** Clears the new-collection text field after a successful create. */
    public void clearNewCollectionField() {
        newCollectionField.setText("");
    }

    /**
     * Shows or hides the Add Game and Delete Game buttons.
     * Should be called with true only when an ADMIN user is logged in.
     *
     * @param visible true to show admin controls, false to hide them
     */
    public void setAdminControlsVisible(boolean visible) {
        addGameButton.setVisible(visible);
        deleteGameButton.setVisible(visible);
        revalidate();
        repaint();
    }

    // ── Getters ───────────────────────────────────────────────────────────────

    public int    getSelectedGameIndex()          { return gameList.getSelectedIndex(); }
    public Game   getSelectedGame()               { return gameList.getSelectedValue(); }
    public Game   getSelectedRecentlyViewedGame() { return recentlyViewedList.getSelectedValue(); }
    public String getSearchCriteria()             { return searchField.getText().trim(); }

    /**
     * Returns the selected collection name, or null if "-- All Games --" is selected.
     */
    public String getSelectedCollectionName() {
        Object s = collectionsCombo.getSelectedItem();
        if (s == null || ALL_GAMES.equals(s.toString())) return null;
        return s.toString();
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

    public void addSearchListener(ActionListener l)           { searchButton.addActionListener(l); }
    public void addFilterListener(ActionListener l)           { filterButton.addActionListener(l); }
    public void addViewDetailsListener(ActionListener l)      { viewDetailsButton.addActionListener(l); }
    public void addAddGameListener(ActionListener l)          { addGameButton.addActionListener(l); }
    public void addDeleteGameListener(ActionListener l)       { deleteGameButton.addActionListener(l); }
    public void addLogoutListener(ActionListener l)           { logoutButton.addActionListener(l); }
    public void addCreateCollectionListener(ActionListener l) { createCollectionButton.addActionListener(l); }
    public void addDeleteCollectionListener(ActionListener l) { deleteCollectionButton.addActionListener(l); }
    public void addManageCollectionsListener(ActionListener l){ manageCollectionsButton.addActionListener(l); }

    /**
     * Fires when the user manually changes the collection dropdown.
     * Suppressed during model rebuilds via the suppressCollectionListener flag.
     */
    public void addCollectionSelectionListener(ActionListener l) {
        collectionsCombo.addActionListener(e -> {
            if (!suppressCollectionListener) l.actionPerformed(e);
        });
    }

    /**
     * Double-clicking a game in the recently-viewed list triggers this listener.
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