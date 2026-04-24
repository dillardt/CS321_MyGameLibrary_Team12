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
import java.util.List;

/**
 * Home and search-results view with searching, filtering, collections access,
 * recently viewed games, and logout support.
 */
public class GameListView extends JPanel {

    private final JTextField searchField;
    private final JComboBox<String> genreFilterCombo;
    private final JComboBox<String> playersFilterCombo;
    private final JComboBox<String> minRatingFilterCombo;
    private final JComboBox<String> collectionsCombo;
    private final JButton searchButton;
    private final JButton filterButton;
    private final JButton deleteGameButton;
    private final JButton viewDetailsButton;
    private final JButton logoutButton;
    private final JButton addGameButton;
    private final DefaultListModel<Game> gameListModel;
    private final JList<Game> gameList;
    private final DefaultListModel<Game> recentlyViewedModel;
    private final JList<Game> recentlyViewedList;
    private final JTextField newCollectionField;
    private final JButton createCollectionButton;
    private final JButton deleteCollectionButton;



    /**
     * Constructs and lays out the game list view.
     */
    public GameListView() {
        setLayout(new BorderLayout(8, 8));

        JPanel topPanel = new JPanel();
        topPanel.setLayout(new BoxLayout(topPanel, BoxLayout.Y_AXIS));

        JPanel homePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));

        homePanel.add(new JLabel("Collections:"));
        collectionsCombo = new JComboBox<>();
        collectionsCombo.setPrototypeDisplayValue("Select Collection           ");
        homePanel.add(collectionsCombo);
        deleteCollectionButton = new JButton("Delete");
        homePanel.add(deleteCollectionButton);



// NEW: create collection UI
        newCollectionField = new JTextField(10);
        createCollectionButton = new JButton("Create");
        homePanel.add(new JLabel("New:"));
        homePanel.add(newCollectionField);
        homePanel.add(createCollectionButton);

        logoutButton = new JButton("Logout");
        homePanel.add(logoutButton);

        topPanel.add(homePanel);


        JPanel controlsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        controlsPanel.add(new JLabel("Search:"));
        searchField = new JTextField(14);
        controlsPanel.add(searchField);

        controlsPanel.add(new JLabel("Genre:"));
        genreFilterCombo = new JComboBox<>(new String[]{"All", "Strategy", "Family", "Party", "Co-op", "Card", "Euro", "Abstract"});
        controlsPanel.add(genreFilterCombo);

        controlsPanel.add(new JLabel("Players:"));
        playersFilterCombo = new JComboBox<>(new String[]{"Any", "1", "2", "3", "4", "5", "6+"});
        controlsPanel.add(playersFilterCombo);

        controlsPanel.add(new JLabel("Min Rating:"));
        minRatingFilterCombo = new JComboBox<>(new String[]{"Any", "5.0", "6.0", "7.0", "8.0", "9.0"});
        controlsPanel.add(minRatingFilterCombo);

        searchButton = new JButton("Search");
        filterButton = new JButton("Filter");
        controlsPanel.add(searchButton);
        controlsPanel.add(filterButton);

        topPanel.add(controlsPanel);
        add(topPanel, BorderLayout.NORTH);

        gameListModel = new DefaultListModel<>();
        gameList = new JList<>(gameListModel);
        gameList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JPanel resultsPanel = new JPanel(new BorderLayout());
        resultsPanel.setBorder(BorderFactory.createTitledBorder("Search Results"));
        resultsPanel.add(new JScrollPane(gameList), BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        viewDetailsButton = new JButton("View Details");
        bottomPanel.add(viewDetailsButton);
        resultsPanel.add(bottomPanel, BorderLayout.SOUTH);

        recentlyViewedModel = new DefaultListModel<>();
        recentlyViewedList = new JList<>(recentlyViewedModel);
        recentlyViewedList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JPanel recentPanel = new JPanel(new BorderLayout());
        recentPanel.setBorder(BorderFactory.createTitledBorder("Recently Viewed"));
        recentPanel.add(new JScrollPane(recentlyViewedList), BorderLayout.CENTER);

        JPanel centerPanel = new JPanel(new GridLayout(1, 2, 8, 8));
        centerPanel.add(resultsPanel);
        centerPanel.add(recentPanel);
        add(centerPanel, BorderLayout.CENTER);

        addGameButton = new JButton("Add Game");
        controlsPanel.add(addGameButton);

        deleteGameButton = new JButton("Delete Game");
        controlsPanel.add(deleteGameButton);
    }

    /**
     * Replaces the visible list of games.
     *
     * @param games list of games to display
     */
    public void setGames(List<Game> games) {
        gameListModel.clear();
        if (games == null) {
            return;
        }
        for (Game game : games) {
            gameListModel.addElement(game);
        }
    }


    /**
     * Returns index of selected game in list.
     *
     * @return selected index, or -1 if nothing selected
     */
    public int getSelectedGameIndex() {
        return gameList.getSelectedIndex();
    }

    /**
     * Returns the entered search criteria.
     *
     * @return trimmed search text
     */
    public String getSearchCriteria() {
        return searchField.getText().trim();
    }

    /**
     * Returns the entered genre filter.
     *
     * @return trimmed genre text
     */
    public String getGenreFilter() {
        Object selected = genreFilterCombo.getSelectedItem();
        if (selected == null || "All".equals(selected)) {
            return "";
        }
        return selected.toString();
    }

    /**
     * Returns the entered player count filter.
     *
     * @return parsed player count, or 0 if input is blank/invalid
     */
    public int getPlayersFilter() {
        Object selected = playersFilterCombo.getSelectedItem();
        if (selected == null || "Any".equals(selected)) {
            return 0;
        }
        String value = selected.toString();
        if ("6+".equals(value)) {
            return 6;
        }
        return parseIntOrDefault(value, 0);
    }

    /**
     * Returns the entered minimum rating filter.
     *
     * @return parsed rating, or -1 if input is blank/invalid
     */
    public double getMinRatingFilter() {
        Object selected = minRatingFilterCombo.getSelectedItem();
        if (selected == null || "Any".equals(selected)) {
            return -1.0;
        }
        return parseDoubleOrDefault(selected.toString(), -1.0);
    }

    public Game getSelectedGame() {
        return gameList.getSelectedValue();
    }


    /**
     * Updates available collection names in the home-page collection dropdown.
     *
     * @param collectionNames collection names to show
     */
    public void setCollectionNames(List<String> collectionNames) {
        DefaultComboBoxModel<String> model = new DefaultComboBoxModel<>();
        if (collectionNames != null) {
            for (String name : collectionNames) {
                model.addElement(name);
            }
        }
        collectionsCombo.setModel(model);
    }

    /**
     * Returns the currently selected collection name.
     *
     * @return selected collection name, or null
     */
    public String getSelectedCollectionName() {
        Object selected = collectionsCombo.getSelectedItem();
        return selected == null ? null : selected.toString();
    }

    /**
     * Replaces the recently viewed list.
     *
     * @param games recently viewed games in display order
     */
    public void setRecentlyViewedGames(List<Game> games) {
        recentlyViewedModel.clear();
        if (games == null) {
            return;
        }
        for (Game game : games) {
            recentlyViewedModel.addElement(game);
        }
    }

    /**
     * Returns the selected game in the recently viewed list.
     *
     * @return selected recently viewed game, or null
     */
    public Game getSelectedRecentlyViewedGame() {
        return recentlyViewedList.getSelectedValue();
    }

    /**
     * Registers the search button listener.
     *
     * @param listener action listener for search
     */
    public void addSearchListener(ActionListener listener) {
        searchButton.addActionListener(listener);
    }

    /**
     * Registers the filter button listener.
     *
     * @param listener action listener for filtering
     */
    public void addFilterListener(ActionListener listener) {
        filterButton.addActionListener(listener);
    }

    /**
     * Registers the view-details button listener.
     *
     * @param listener action listener for viewing game details
     */
    public void addViewDetailsListener(ActionListener listener) {
        viewDetailsButton.addActionListener(listener);
    }

    /**
     * Registers collection dropdown selection listener.
     *
     * @param listener action listener for collection selection
     */
    public void addCollectionSelectionListener(ActionListener listener) {
        collectionsCombo.addActionListener(listener);
    }

    /**
     * Registers listener for Add Game button.
     */
    public void addAddGameListener(ActionListener listener) {
        addGameButton.addActionListener(listener);
    }

    /**
     * Registers the logout button listener.
     *
     * @param listener action listener for logout
     */
    public void addLogoutListener(ActionListener listener) {
        logoutButton.addActionListener(listener);
    }

    /**
     * Safely parses an integer with default fallback.
     *
     * @param value raw input value
     * @param fallback fallback value
     * @return parsed int or fallback
     */
    private int parseIntOrDefault(String value, int fallback) {
        if (value == null || value.isBlank()) {
            return fallback;
        }
        try {
            return Integer.parseInt(value.trim());
        } catch (NumberFormatException ex) {
            return fallback;
        }
    }

    /**
     * Safely parses a double with default fallback.
     *
     * @param value raw input value
     * @param fallback fallback value
     * @return parsed double or fallback
     */
    private double parseDoubleOrDefault(String value, double fallback) {
        if (value == null || value.isBlank()) {
            return fallback;
        }
        try {
            return Double.parseDouble(value.trim());
        } catch (NumberFormatException ex) {
            return fallback;
        }
    }

    /**
     * Registers listener for Delete Game button.
     *
     * @param listener action listener for deleting a game
     */
    public void addDeleteGameListener(ActionListener listener) {
        deleteGameButton.addActionListener(listener);
    }

    /** Listener for the Create Collection button */
    public void addCreateCollectionListener(ActionListener listener) {
        createCollectionButton.addActionListener(listener);
    }

    /** Returns the text entered for a new collection */
    public String getNewCollectionName() {
        return newCollectionField.getText().trim();
    }

    public void addDeleteCollectionListener(ActionListener listener) {
        deleteCollectionButton.addActionListener(listener);
    }


}
