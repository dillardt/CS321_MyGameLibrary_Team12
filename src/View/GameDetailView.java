package View;

import Model.Game;
import Model.Review;
import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTextArea;
import javax.swing.SpinnerNumberModel;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.event.ActionListener;
import java.util.List;

/**
 * Game detail screen.
 *
 * Layout:
 *  NORTH  — image + metadata (title, genre, players, rating)
 *  CENTER — description (left) + community reviews list (right)
 *  SOUTH  — review form (rating spinner + comment area + submit) + Add to Collection + Back
 *
 * Changes from original:
 *  - addBackListener() added so AppCoordinator can wire the Back button
 *  - setDisplayedReviews() is now called (not commented out) — reviews show immediately
 *  - writeReviewButton removed (was wired but did nothing — form is always visible)
 */
public class GameDetailView extends JPanel {

    private final JLabel   imageLabel;
    private final JLabel   titleValueLabel;
    private final JLabel   genreValueLabel;
    private final JLabel   playersValueLabel;
    private final JLabel   ratingValueLabel;
    private final JTextArea descriptionArea;

    private final DefaultListModel<Review> reviewsModel = new DefaultListModel<>();
    private final JList<Review>            reviewsList;

    private final JSpinner  reviewRatingSpinner;
    private final JTextArea reviewCommentArea;
    private final JButton   submitReviewButton;
    private final JButton   addToCollectionButton;
    private final JButton   backButton;

    private Game displayedGame;

    public GameDetailView() {
        setLayout(new BorderLayout(8, 8));

        // ── NORTH: image + metadata ───────────────────────────────────────────
        imageLabel = new JLabel("No Image", JLabel.CENTER);
        imageLabel.setHorizontalTextPosition(JLabel.CENTER);
        imageLabel.setVerticalTextPosition(JLabel.BOTTOM);

        JPanel infoPanel = new JPanel(new GridLayout(0, 2, 6, 6));
        infoPanel.add(new JLabel("Title:"));
        titleValueLabel = new JLabel("-");
        infoPanel.add(titleValueLabel);

        infoPanel.add(new JLabel("Genre:"));
        genreValueLabel = new JLabel("-");
        infoPanel.add(genreValueLabel);

        infoPanel.add(new JLabel("Players:"));
        playersValueLabel = new JLabel("-");
        infoPanel.add(playersValueLabel);

        infoPanel.add(new JLabel("Avg Rating:"));
        ratingValueLabel = new JLabel("-");
        infoPanel.add(ratingValueLabel);

        JPanel headerPanel = new JPanel(new BorderLayout(8, 8));
        headerPanel.add(imageLabel, BorderLayout.WEST);
        headerPanel.add(infoPanel,  BorderLayout.CENTER);
        add(headerPanel, BorderLayout.NORTH);

        // ── CENTER: description + reviews ─────────────────────────────────────
        descriptionArea = new JTextArea(7, 35);
        descriptionArea.setLineWrap(true);
        descriptionArea.setWrapStyleWord(true);
        descriptionArea.setEditable(false);

        reviewsList = new JList<>(reviewsModel);
        JPanel centerPanel = new JPanel(new GridLayout(1, 2, 8, 8));
        centerPanel.add(new JScrollPane(descriptionArea));

        JPanel reviewsPanel = new JPanel(new BorderLayout());
        reviewsPanel.setBorder(BorderFactory.createTitledBorder("Community Reviews"));
        reviewsPanel.add(new JScrollPane(reviewsList), BorderLayout.CENTER);
        centerPanel.add(reviewsPanel);
        add(centerPanel, BorderLayout.CENTER);

        // ── SOUTH: review form + action buttons ───────────────────────────────
        JPanel southPanel = new JPanel(new BorderLayout(6, 6));
        southPanel.setBorder(BorderFactory.createTitledBorder("Write a Review"));

        JPanel ratingRow = new JPanel(new FlowLayout(FlowLayout.LEFT));
        ratingRow.add(new JLabel("Your Rating (1-10):"));
        reviewRatingSpinner = new JSpinner(new SpinnerNumberModel(8, 1, 10, 1));
        ratingRow.add(reviewRatingSpinner);
        southPanel.add(ratingRow, BorderLayout.NORTH);

        reviewCommentArea = new JTextArea(4, 30);
        reviewCommentArea.setLineWrap(true);
        reviewCommentArea.setWrapStyleWord(true);
        southPanel.add(new JScrollPane(reviewCommentArea), BorderLayout.CENTER);

        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        submitReviewButton    = new JButton("Submit Review");
        addToCollectionButton = new JButton("Add to Collection");
        backButton            = new JButton("Back");
        actionPanel.add(backButton);
        actionPanel.add(addToCollectionButton);
        actionPanel.add(submitReviewButton);
        southPanel.add(actionPanel, BorderLayout.SOUTH);

        add(southPanel, BorderLayout.SOUTH);
    }

    // ── Display ───────────────────────────────────────────────────────────────

    /**
     * Populates all fields with data from the given game.
     * Passing null clears all fields.
     */
    public void displayGame(Game game) {
        displayedGame = game;
        if (game == null) {
            titleValueLabel.setText("-");
            genreValueLabel.setText("-");
            playersValueLabel.setText("-");
            ratingValueLabel.setText("-");
            descriptionArea.setText("");
            reviewsModel.clear();
            updateImage(null);
            return;
        }
        titleValueLabel.setText(safeText(game.getTitle()));
        genreValueLabel.setText(safeText(game.getGenre()));
        playersValueLabel.setText(game.getMinPlayers() + " - " + game.getMaxPlayers());
        ratingValueLabel.setText(String.format("%.1f / 10", game.getAverageRating()));
        descriptionArea.setText(safeText(game.getDescription()));
        descriptionArea.setCaretPosition(0);
        updateImage(game.getImagePath());
    }

    /** Replaces the community reviews list. */
    public void setDisplayedReviews(List<Review> reviews) {
        reviewsModel.clear();
        if (reviews == null) return;
        for (Review r : reviews) reviewsModel.addElement(r);
    }

    /** Resets the review form to defaults. */
    public void clearReviewForm() {
        reviewRatingSpinner.setValue(8);
        reviewCommentArea.setText("");
    }

    // ── Getters ───────────────────────────────────────────────────────────────

    public Game   getDisplayedGame()        { return displayedGame; }
    public int    getEnteredReviewRating()  { return (Integer) reviewRatingSpinner.getValue(); }
    public String getEnteredReviewComment() { return reviewCommentArea.getText().trim(); }

    // ── Listener registration ─────────────────────────────────────────────────

    public void addSubmitReviewListener(ActionListener l)    { submitReviewButton.addActionListener(l); }
    public void addAddToCollectionListener(ActionListener l) { addToCollectionButton.addActionListener(l); }

    /**
     * Registers the Back button listener.
     * This was missing in the original — back button was added inline in AppCoordinator
     * but placed in WEST which broke the layout.
     */
    public void addBackListener(ActionListener l) { backButton.addActionListener(l); }

    // ── Private helpers ───────────────────────────────────────────────────────

    private String safeText(String value) {
        return (value == null || value.isBlank()) ? "-" : value;
    }

    /**
     * Loads and scales a game image from a URL or local path.
     * Shows "No Image" placeholder on any failure.
     */
    private void updateImage(String imagePath) {
        if (imagePath == null || imagePath.isBlank()) {
            imageLabel.setIcon(null);
            imageLabel.setText("No Image");
            return;
        }
        try {
            java.net.URL url = java.net.URI.create(imagePath).toURL();
            ImageIcon icon   = new ImageIcon(url);
            if (icon.getIconWidth() <= 0) {
                imageLabel.setIcon(null);
                imageLabel.setText("No Image");
                return;
            }
            Image scaled = icon.getImage().getScaledInstance(140, 140, Image.SCALE_SMOOTH);
            imageLabel.setText("");
            imageLabel.setIcon(new ImageIcon(scaled));
        } catch (Exception e) {
            imageLabel.setIcon(null);
            imageLabel.setText("No Image");
        }
    }
}