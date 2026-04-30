package View;

import Model.Game;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.List;

import static View.LoginView.*;

/**
 * Main game browser screen.
 *
 * Layout:
 *  NORTH  — top bar: collections dropdown + create/delete + logout
 *  CENTER — search/filter bar above; game results list left; recently viewed right
 *  SOUTH  — View Details, Add Game (admin only), Delete Game (admin only), Manage Collections
 */
public class GameListView extends JPanel {

    private static final String ALL_GAMES = "-- All Games --";
    private boolean suppressCollectionListener = false;

    private final DefaultListModel<Game> gameListModel       = new DefaultListModel<>();
    private final DefaultListModel<Game> recentlyViewedModel = new DefaultListModel<>();

    private final JList<Game> gameList;
    private final JList<Game> recentlyViewedList;

    private final JTextField        searchField;
    private final JComboBox<String> genreFilterCombo;
    private final JComboBox<String> playersFilterCombo;
    private final JComboBox<String> minRatingFilterCombo;
    private final JButton           searchButton;
    private final JButton           filterButton;
    private final JButton           clearFiltersButton;
    private final JButton           addGameButton;
    private final JButton           deleteGameButton;
    private final JButton           viewDetailsButton;
    private final JButton           manageCollectionsButton;

    private final JComboBox<String> collectionsCombo;
    private final JTextField        newCollectionField;
    private final JButton           createCollectionButton;
    private final JButton           deleteCollectionButton;
    private final JButton           logoutButton;

    public GameListView() {
        setBackground(BG_DARK);
        setLayout(new BorderLayout(8, 8));
        setBorder(BorderFactory.createEmptyBorder(6, 8, 6, 8));

        // ── Top bar ───────────────────────────────────────────────────────────
        JPanel topPanel = new JPanel();
        topPanel.setLayout(new BoxLayout(topPanel, BoxLayout.Y_AXIS));
        topPanel.setBackground(BG_PANEL);

        JPanel homePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 6));
        homePanel.setBackground(BG_PANEL);

        homePanel.add(styledLabel("Collections:"));
        collectionsCombo = new JComboBox<>();
        styleCombo(collectionsCombo);
        collectionsCombo.setPrototypeDisplayValue("Select Collection          ");
        homePanel.add(collectionsCombo);

        deleteCollectionButton = styledButton("Delete Collection", new Color(140, 60, 60));
        homePanel.add(deleteCollectionButton);

        homePanel.add(styledLabel("  New:"));
        newCollectionField = styledField(10);
        homePanel.add(newCollectionField);

        createCollectionButton = styledButton("Create", new Color(60, 110, 70));
        homePanel.add(createCollectionButton);

        manageCollectionsButton = styledButton("Manage Collections", new Color(70, 100, 140));
        homePanel.add(manageCollectionsButton);

        logoutButton = styledButton("Logout", new Color(90, 60, 60));
        homePanel.add(logoutButton);

        topPanel.add(homePanel);

        // ── Search / filter bar ───────────────────────────────────────────────
        JPanel controlsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 6));
        controlsPanel.setBackground(BG_PANEL);

        controlsPanel.add(styledLabel("Search:"));
        searchField = styledField(14);
        controlsPanel.add(searchField);

        controlsPanel.add(styledLabel("Genre:"));
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
        styleCombo(genreFilterCombo);
        controlsPanel.add(genreFilterCombo);

        controlsPanel.add(styledLabel("Players:"));
        playersFilterCombo = new JComboBox<>(new String[]{"Any","1","2","3","4","5","6+"});
        styleCombo(playersFilterCombo);
        controlsPanel.add(playersFilterCombo);

        controlsPanel.add(styledLabel("Min Rating:"));
        minRatingFilterCombo = new JComboBox<>(new String[]{"Any","5.0","6.0","7.0","8.0","9.0"});
        styleCombo(minRatingFilterCombo);
        controlsPanel.add(minRatingFilterCombo);

        searchButton = styledButton("Search", ACCENT);
        filterButton = styledButton("Filter",  new Color(70, 100, 140));
        clearFiltersButton = styledButton("Clear Filters", new Color(80, 70, 60));
        controlsPanel.add(searchButton);
        controlsPanel.add(filterButton);
        controlsPanel.add(clearFiltersButton);

        topPanel.add(controlsPanel);
        add(topPanel, BorderLayout.NORTH);

        // ── Center: results + recently viewed ─────────────────────────────────
        gameList = new JList<>(gameListModel);
        styleList(gameList);
        JPanel resultsPanel = darkTitledPanel("Search Results");
        resultsPanel.add(new JScrollPane(gameList), BorderLayout.CENTER);
        styleScrollPane((JScrollPane) resultsPanel.getComponent(0));

        recentlyViewedList = new JList<>(recentlyViewedModel);
        styleList(recentlyViewedList);
        JPanel recentPanel = darkTitledPanel("Recently Viewed");
        recentPanel.add(new JScrollPane(recentlyViewedList), BorderLayout.CENTER);
        styleScrollPane((JScrollPane) recentPanel.getComponent(0));

        JPanel centerPanel = new JPanel(new GridLayout(1, 2, 8, 8));
        centerPanel.setBackground(BG_DARK);
        centerPanel.add(resultsPanel);
        centerPanel.add(recentPanel);
        add(centerPanel, BorderLayout.CENTER);

        // ── Bottom bar ────────────────────────────────────────────────────────
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 6, 6));
        bottomPanel.setBackground(BG_PANEL);
        viewDetailsButton = styledButton("View Details", ACCENT);
        addGameButton     = styledButton("Add Game",     new Color(60, 110, 70));
        deleteGameButton  = styledButton("Delete Game",  new Color(140, 60, 60));
        addGameButton.setVisible(false);
        deleteGameButton.setVisible(false);
        bottomPanel.add(viewDetailsButton);
        bottomPanel.add(addGameButton);
        bottomPanel.add(deleteGameButton);
        add(bottomPanel, BorderLayout.SOUTH);
    }

    // ── Data setters ──────────────────────────────────────────────────────────

    public void setGames(List<Game> games) {
        gameListModel.clear();
        if (games == null) return;
        for (Game g : games) gameListModel.addElement(g);
    }

    public void setRecentlyViewedGames(List<Game> games) {
        recentlyViewedModel.clear();
        if (games == null) return;
        for (Game g : games) recentlyViewedModel.addElement(g);
    }

    public void setCollectionNames(List<String> names) {
        suppressCollectionListener = true;
        DefaultComboBoxModel<String> model = new DefaultComboBoxModel<>();
        model.addElement(ALL_GAMES);
        if (names != null) for (String n : names) model.addElement(n);
        collectionsCombo.setModel(model);
        collectionsCombo.setSelectedIndex(0);
        suppressCollectionListener = false;
    }

    public void resetCollectionSelection() {
        suppressCollectionListener = true;
        collectionsCombo.setSelectedIndex(0);
        suppressCollectionListener = false;
    }

    public void clearNewCollectionField() { newCollectionField.setText(""); }

    public void setAdminControlsVisible(boolean visible) {
        addGameButton.setVisible(visible);
        deleteGameButton.setVisible(visible);
        revalidate(); repaint();
    }

    // ── Getters ───────────────────────────────────────────────────────────────

    public int    getSelectedGameIndex()          { return gameList.getSelectedIndex(); }
    public Game   getSelectedGame()               { return gameList.getSelectedValue(); }
    public Game   getSelectedRecentlyViewedGame() { return recentlyViewedList.getSelectedValue(); }
    public String getSearchCriteria()             { return searchField.getText().trim(); }

    public String getSelectedCollectionName() {
        Object s = collectionsCombo.getSelectedItem();
        if (s == null || ALL_GAMES.equals(s.toString())) return null;
        return s.toString();
    }

    public String getNewCollectionName() { return newCollectionField.getText().trim(); }

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
    public void addClearFiltersListener(ActionListener l)     { clearFiltersButton.addActionListener(l); }
    public void addViewDetailsListener(ActionListener l)      { viewDetailsButton.addActionListener(l); }
    public void addAddGameListener(ActionListener l)          { addGameButton.addActionListener(l); }
    public void addDeleteGameListener(ActionListener l)       { deleteGameButton.addActionListener(l); }
    public void addLogoutListener(ActionListener l)           { logoutButton.addActionListener(l); }
    public void addCreateCollectionListener(ActionListener l) { createCollectionButton.addActionListener(l); }
    public void addDeleteCollectionListener(ActionListener l) { deleteCollectionButton.addActionListener(l); }
    public void addManageCollectionsListener(ActionListener l){ manageCollectionsButton.addActionListener(l); }

    public void addCollectionSelectionListener(ActionListener l) {
        collectionsCombo.addActionListener(e -> {
            if (!suppressCollectionListener) l.actionPerformed(e);
        });
    }

    public void addRecentlyViewedClickListener(ActionListener l) {
        recentlyViewedList.addMouseListener(new MouseAdapter() {
            @Override public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2 && recentlyViewedList.getSelectedIndex() >= 0) {
                    l.actionPerformed(new ActionEvent(recentlyViewedList,
                            ActionEvent.ACTION_PERFORMED, "RECENT_DOUBLE_CLICK"));
                }
            }
        });
    }

    // ── Style helpers ─────────────────────────────────────────────────────────

    private JLabel styledLabel(String text) {
        JLabel l = new JLabel(text);
        l.setForeground(new Color(180, 185, 200));
        return l;
    }

    private JTextField styledField(int cols) {
        JTextField f = new JTextField(cols);
        f.setBackground(new Color(52, 58, 70));
        f.setForeground(new Color(230, 230, 235));
        f.setCaretColor(Color.WHITE);
        f.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(70, 80, 100)),
                BorderFactory.createEmptyBorder(3, 5, 3, 5)));
        return f;
    }

    private void styleCombo(JComboBox<?> c) {
        c.setBackground(new Color(52, 58, 70));
        c.setForeground(new Color(220, 220, 230));
    }

    private void styleList(JList<?> list) {
        list.setBackground(new Color(35, 40, 52));
        list.setForeground(new Color(210, 215, 230));
        list.setSelectionBackground(ACCENT);
        list.setSelectionForeground(Color.WHITE);
    }

    private void styleScrollPane(JScrollPane sp) {
        sp.getViewport().setBackground(new Color(35, 40, 52));
        sp.setBorder(BorderFactory.createLineBorder(new Color(60, 70, 90)));
    }

    private JPanel darkTitledPanel(String title) {
        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(new Color(35, 40, 52));
        p.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(70, 85, 115)),
                title,
                javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION,
                javax.swing.border.TitledBorder.DEFAULT_POSITION,
                new Font("SansSerif", Font.BOLD, 12),
                new Color(150, 170, 210)));
        return p;
    }

    private int parseIntOrDefault(String v, int fallback) {
        try { return Integer.parseInt(v.trim()); } catch (NumberFormatException e) { return fallback; }
    }

    private double parseDoubleOrDefault(String v, double fallback) {
        try { return Double.parseDouble(v.trim()); } catch (NumberFormatException e) { return fallback; }
    }
}