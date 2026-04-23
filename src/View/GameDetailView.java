package View;

import Model.Game;
import Model.Review;
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
import java.awt.Image;
import java.awt.GridLayout;
import java.awt.event.ActionListener;
import java.util.List;

/**
 * Game detail view showing game image/data, public review list,
 * and user review controls.
 */
public class GameDetailView extends JPanel {

    private final JLabel imageLabel;
    private final JLabel titleValueLabel;
    private final JLabel genreValueLabel;
    private final JLabel playersValueLabel;
    private final JLabel ratingValueLabel;
    private final JTextArea descriptionArea;
    private final DefaultListModel<Review> reviewsModel;
    private final JList<Review> reviewsList;
    private final JButton writeReviewButton;
    private final JSpinner reviewRatingSpinner;
    private final JTextArea reviewCommentArea;
    private final JButton submitReviewButton;
    private final JButton addToCollectionButton;
    private Game displayedGame;

    /**
     * Constructs and lays out the game detail view.
     */
    public GameDetailView() {
        setLayout(new BorderLayout(8, 8));

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

        infoPanel.add(new JLabel("Average Rating:"));
        ratingValueLabel = new JLabel("-");
        infoPanel.add(ratingValueLabel);
        JPanel headerPanel = new JPanel(new BorderLayout(8, 8));
        headerPanel.add(imageLabel, BorderLayout.WEST);
        headerPanel.add(infoPanel, BorderLayout.CENTER);
        add(headerPanel, BorderLayout.NORTH);

        descriptionArea = new JTextArea(7, 35);
        descriptionArea.setLineWrap(true);
        descriptionArea.setWrapStyleWord(true);
        descriptionArea.setEditable(false);

        reviewsModel = new DefaultListModel<>();
        reviewsList = new JList<>(reviewsModel);
        JPanel centerPanel = new JPanel(new GridLayout(1, 2, 8, 8));
        centerPanel.add(new JScrollPane(descriptionArea));
        centerPanel.add(new JScrollPane(reviewsList));
        add(centerPanel, BorderLayout.CENTER);

        JPanel reviewPanel = new JPanel(new BorderLayout(6, 6));
        JPanel topReviewControls = new JPanel(new GridLayout(0, 2, 6, 6));
        writeReviewButton = new JButton("Write Review");
        topReviewControls.add(writeReviewButton);
        topReviewControls.add(new JLabel());
        topReviewControls.add(new JLabel("Your Rating (1-10):"));
        reviewRatingSpinner = new JSpinner(new SpinnerNumberModel(8, 1, 10, 1));
        topReviewControls.add(reviewRatingSpinner);
        reviewPanel.add(topReviewControls, BorderLayout.NORTH);

        reviewCommentArea = new JTextArea(5, 30);
        reviewCommentArea.setLineWrap(true);
        reviewCommentArea.setWrapStyleWord(true);
        reviewPanel.add(new JScrollPane(reviewCommentArea), BorderLayout.CENTER);

        JPanel actionPanel = new JPanel();
        submitReviewButton = new JButton("Submit Review");
        addToCollectionButton = new JButton("Add to Collection");
        actionPanel.add(submitReviewButton);
        actionPanel.add(addToCollectionButton);
        reviewPanel.add(actionPanel, BorderLayout.SOUTH);

        add(reviewPanel, BorderLayout.SOUTH);
    }

    /**
     * Updates the UI to display the provided game details.
     *
     * @param game game to display
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

    /**
     * Returns the game currently shown on the screen.
     *
     * @return displayed game, or null
     */
    public Game getDisplayedGame() {
        return displayedGame;
    }

    /**
     * Returns the review rating chosen in the spinner.
     *
     * @return integer rating from 1 to 10
     */
    public int getEnteredReviewRating() {
        return (Integer) reviewRatingSpinner.getValue();
    }

    /**
     * Returns the review comment entered by the user.
     *
     * @return trimmed comment text
     */
    public String getEnteredReviewComment() {
        return reviewCommentArea.getText().trim();
    }

    /**
     * Clears the review form inputs.
     */
    public void clearReviewForm() {
        reviewRatingSpinner.setValue(8);
        reviewCommentArea.setText("");
    }

    /**
     * Replaces the publicly visible review list for the displayed game.
     *
     * @param reviews reviews to display
     */
    public void setDisplayedReviews(List<Review> reviews) {
        reviewsModel.clear();
        if (reviews == null) {
            return;
        }
        for (Review review : reviews) {
            reviewsModel.addElement(review);
        }
    }

    /**
     * Registers the submit-review button listener.
     *
     * @param listener action listener for submitting reviews
     */
    public void addSubmitReviewListener(ActionListener listener) {
        submitReviewButton.addActionListener(listener);
    }

    /**
     * Registers the write-review button listener.
     *
     * @param listener action listener for opening review entry
     */
    public void addWriteReviewListener(ActionListener listener) {
        writeReviewButton.addActionListener(listener);
    }

    /**
     * Registers the add-to-collection button listener.
     *
     * @param listener action listener for collection action
     */
    public void addAddToCollectionListener(ActionListener listener) {
        addToCollectionButton.addActionListener(listener);
    }

    /**
     * Returns non-null display text.
     *
     * @param value source text
     * @return value or "-"
     */
    private String safeText(String value) {
        return (value == null || value.isBlank()) ? "-" : value;
    }

    /**
     * Updates the game image display from a file path.
     *
     * @param imagePath image file path
     */
    private void updateImage(String imagePath) {
        if (imagePath == null || imagePath.isBlank()) {
            imageLabel.setIcon(null);
            imageLabel.setText("No Image");
            return;
        }

        ImageIcon icon = new ImageIcon(imagePath);
        if (icon.getIconWidth() <= 0 || icon.getIconHeight() <= 0) {
            imageLabel.setIcon(null);
            imageLabel.setText("No Image");
            return;
        }

        Image scaled = icon.getImage().getScaledInstance(140, 140, Image.SCALE_SMOOTH);
        imageLabel.setText("");
        imageLabel.setIcon(new ImageIcon(scaled));
    }
}
