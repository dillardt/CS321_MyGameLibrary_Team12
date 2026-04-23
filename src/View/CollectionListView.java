package View;

import Model.Collection;
import Model.Game;
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
 * Collection management view with collection list and selected collection contents.
 */
public class CollectionListView extends JPanel {

    private final DefaultListModel<Collection> collectionListModel;
    private final JList<Collection> collectionList;
    private final DefaultListModel<Game> gamesInCollectionModel;
    private final JList<Game> gamesInCollectionList;
    private final JButton createCollectionButton;
    private final JButton deleteCollectionButton;
    private final JButton removeGameButton;

    /**
     * Constructs and lays out the collection list view.
     */
    public CollectionListView() {
        setLayout(new BorderLayout(8, 8));

        collectionListModel = new DefaultListModel<>();
        collectionList = new JList<>(collectionListModel);
        collectionList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        gamesInCollectionModel = new DefaultListModel<>();
        gamesInCollectionList = new JList<>(gamesInCollectionModel);
        gamesInCollectionList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        JPanel leftPanel = new JPanel(new BorderLayout());
        leftPanel.add(new JLabel("Collections"), BorderLayout.NORTH);
        leftPanel.add(new JScrollPane(collectionList), BorderLayout.CENTER);

        JPanel rightPanel = new JPanel(new BorderLayout());
        rightPanel.add(new JLabel("Games in Selected Collection"), BorderLayout.NORTH);
        rightPanel.add(new JScrollPane(gamesInCollectionList), BorderLayout.CENTER);

        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, leftPanel, rightPanel);
        splitPane.setResizeWeight(0.4);
        add(splitPane, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        createCollectionButton = new JButton("Create Collection");
        deleteCollectionButton = new JButton("Delete Collection");
        removeGameButton = new JButton("Remove Game");
        buttonPanel.add(createCollectionButton);
        buttonPanel.add(deleteCollectionButton);
        buttonPanel.add(removeGameButton);
        add(buttonPanel, BorderLayout.SOUTH);

        collectionList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                showGamesForSelectedCollection();
            }
        });
    }

    /**
     * Replaces the list of visible collections.
     *
     * @param collections collections to display
     */
    public void setCollections(List<Collection> collections) {
        collectionListModel.clear();
        if (collections != null) {
            for (Collection collection : collections) {
                collectionListModel.addElement(collection);
            }
        }
        showGamesForSelectedCollection();
    }

    /**
     * Returns the selected collection.
     *
     * @return selected collection, or null if no selection exists
     */
    public Collection getSelectedCollection() {
        return collectionList.getSelectedValue();
    }

    /**
     * Returns the selected game from the selected collection list.
     *
     * @return selected game, or null if no selection exists
     */
    public Game getSelectedGameInCollection() {
        return gamesInCollectionList.getSelectedValue();
    }

    /**
     * Prompts for a collection name and returns trimmed user input.
     *
     * @return collection name or null if canceled
     */
    public String promptForCollectionName() {
        String name = JOptionPane.showInputDialog(this, "Enter collection name:");
        if (name == null) {
            return null;
        }
        return name.trim();
    }

    /**
     * Registers the create-collection button listener.
     *
     * @param listener action listener for creating collections
     */
    public void addCreateCollectionListener(ActionListener listener) {
        createCollectionButton.addActionListener(listener);
    }

    /**
     * Registers the delete-collection button listener.
     *
     * @param listener action listener for deleting collections
     */
    public void addDeleteCollectionListener(ActionListener listener) {
        deleteCollectionButton.addActionListener(listener);
    }

    /**
     * Registers the remove-game button listener.
     *
     * @param listener action listener for removing games from collection
     */
    public void addRemoveGameListener(ActionListener listener) {
        removeGameButton.addActionListener(listener);
    }

    /**
     * Reloads the right-side game list for the currently selected collection.
     */
    private void showGamesForSelectedCollection() {
        gamesInCollectionModel.clear();
        Collection selected = collectionList.getSelectedValue();
        if (selected == null) {
            return;
        }
        for (Game game : selected.getGames()) {
            gamesInCollectionModel.addElement(game);
        }
    }
}
