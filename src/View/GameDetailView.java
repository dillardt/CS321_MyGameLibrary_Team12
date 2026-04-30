package View;

import Model.Game;
import Model.Review;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.util.List;

import static View.LoginView.*;

/**
 * Game detail screen.
 *
 * Layout:
 *  NORTH  — image + metadata (title, genre, players, rating)
 *  CENTER — description (left) + community reviews list (right)
 *  SOUTH  — review form (rating spinner + comment area + submit) + Add to Collection + Back
 */
public class GameDetailView extends JPanel {

    private final JLabel    imageLabel;
    private final JLabel    titleValueLabel;
    private final JLabel    genreValueLabel;
    private final JLabel    playersValueLabel;
    private final JLabel    ratingValueLabel;
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
        setBackground(BG_DARK);
        setLayout(new BorderLayout(8, 8));
        setBorder(BorderFactory.createEmptyBorder(8, 10, 8, 10));

        // ── NORTH: image + metadata ───────────────────────────────────────────
        imageLabel = new JLabel("No Image", JLabel.CENTER);
        imageLabel.setForeground(new Color(150, 155, 170));
        imageLabel.setHorizontalTextPosition(JLabel.CENTER);
        imageLabel.setVerticalTextPosition(JLabel.BOTTOM);
        imageLabel.setPreferredSize(new Dimension(148, 148));

        JPanel infoPanel = new JPanel(new GridLayout(0, 2, 6, 8));
        infoPanel.setBackground(BG_PANEL);
        infoPanel.setBorder(BorderFactory.createEmptyBorder(8, 12, 8, 12));

        infoPanel.add(fieldLabel("Title:"));
        titleValueLabel = valueLabel();
        titleValueLabel.setFont(new Font("SansSerif", Font.BOLD, 14));
        infoPanel.add(titleValueLabel);

        infoPanel.add(fieldLabel("Genre:"));
        genreValueLabel = valueLabel();
        infoPanel.add(genreValueLabel);

        infoPanel.add(fieldLabel("Players:"));
        playersValueLabel = valueLabel();
        infoPanel.add(playersValueLabel);

        infoPanel.add(fieldLabel("Avg Rating:"));
        ratingValueLabel = valueLabel();
        ratingValueLabel.setForeground(new Color(255, 200, 80));
        infoPanel.add(ratingValueLabel);

        JPanel headerPanel = new JPanel(new BorderLayout(10, 0));
        headerPanel.setBackground(BG_PANEL);
        headerPanel.setBorder(BorderFactory.createLineBorder(new Color(60, 70, 90)));
        headerPanel.add(imageLabel, BorderLayout.WEST);
        headerPanel.add(infoPanel, BorderLayout.CENTER);
        add(headerPanel, BorderLayout.NORTH);

        // ── CENTER: description + reviews ─────────────────────────────────────
        // Using a noticeably lighter background than BG_DARK so the boxes stand out
        Color boxBg = new Color(55, 62, 78);

        descriptionArea = new JTextArea(7, 35);
        descriptionArea.setLineWrap(true);
        descriptionArea.setWrapStyleWord(true);
        descriptionArea.setEditable(false);
        descriptionArea.setBackground(boxBg);
        descriptionArea.setForeground(new Color(215, 218, 230));
        descriptionArea.setBorder(BorderFactory.createEmptyBorder(6, 8, 6, 8));

        JScrollPane descScroll = new JScrollPane(descriptionArea);
        descScroll.setBorder(darkTitledBorder("Description"));
        descScroll.getViewport().setBackground(boxBg);

        reviewsList = new JList<>(reviewsModel);
        reviewsList.setBackground(boxBg);
        reviewsList.setForeground(new Color(215, 218, 230));
        reviewsList.setSelectionBackground(ACCENT);
        reviewsList.setSelectionForeground(Color.WHITE);

        JScrollPane reviewsScroll = new JScrollPane(reviewsList);
        reviewsScroll.setBorder(darkTitledBorder("Community Reviews"));
        reviewsScroll.getViewport().setBackground(boxBg);

        JPanel centerPanel = new JPanel(new GridLayout(1, 2, 8, 8));
        centerPanel.setBackground(BG_DARK);
        centerPanel.add(descScroll);
        centerPanel.add(reviewsScroll);
        add(centerPanel, BorderLayout.CENTER);

        // ── SOUTH: review form ────────────────────────────────────────────────
        JPanel southPanel = new JPanel(new BorderLayout(6, 6));
        southPanel.setBackground(BG_PANEL);
        southPanel.setBorder(darkTitledBorder("Write a Review"));

        JPanel ratingRow = new JPanel(new FlowLayout(FlowLayout.LEFT));
        ratingRow.setBackground(BG_PANEL);
        JLabel ratingLabel = new JLabel("Your Rating (1-10):");
        ratingLabel.setForeground(new Color(180, 185, 200));
        ratingRow.add(ratingLabel);
        reviewRatingSpinner = new JSpinner(new SpinnerNumberModel(8, 1, 10, 1));
        reviewRatingSpinner.setBackground(new Color(52, 58, 70));
        ratingRow.add(reviewRatingSpinner);
        southPanel.add(ratingRow, BorderLayout.NORTH);

        reviewCommentArea = new JTextArea(4, 30);
        reviewCommentArea.setLineWrap(true);
        reviewCommentArea.setWrapStyleWord(true);
        reviewCommentArea.setBackground(boxBg);
        reviewCommentArea.setForeground(new Color(220, 222, 235));
        reviewCommentArea.setCaretColor(Color.WHITE);

        JScrollPane commentScroll = new JScrollPane(reviewCommentArea);
        commentScroll.getViewport().setBackground(boxBg);
        commentScroll.setBorder(BorderFactory.createLineBorder(new Color(70, 80, 100)));
        southPanel.add(commentScroll, BorderLayout.CENTER);

        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 6, 6));
        actionPanel.setBackground(BG_PANEL);
        backButton            = LoginView.styledButton("Back",               new Color(60, 68, 82));
        addToCollectionButton = LoginView.styledButton("Add to Collection",  new Color(60, 110, 70));
        submitReviewButton    = LoginView.styledButton("Submit Review",       ACCENT);
        actionPanel.add(backButton);
        actionPanel.add(addToCollectionButton);
        actionPanel.add(submitReviewButton);
        southPanel.add(actionPanel, BorderLayout.SOUTH);

        add(southPanel, BorderLayout.SOUTH);
    }

    // ── Display ───────────────────────────────────────────────────────────────

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

    public void setDisplayedReviews(List<Review> reviews) {
        reviewsModel.clear();
        if (reviews == null) return;
        for (Review r : reviews) reviewsModel.addElement(r);
    }

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
    public void addBackListener(ActionListener l)            { backButton.addActionListener(l); }

    // ── Private helpers ───────────────────────────────────────────────────────

    private JLabel fieldLabel(String text) {
        JLabel l = new JLabel(text);
        l.setForeground(new Color(150, 158, 180));
        return l;
    }

    private JLabel valueLabel() {
        JLabel l = new JLabel("-");
        l.setForeground(new Color(220, 222, 235));
        return l;
    }

    private javax.swing.border.Border darkTitledBorder(String title) {
        return BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(70, 85, 115)),
                title,
                javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION,
                javax.swing.border.TitledBorder.DEFAULT_POSITION,
                new Font("SansSerif", Font.BOLD, 12),
                new Color(150, 170, 210));
    }

    private String safeText(String value) {
        return (value == null || value.isBlank()) ? "-" : value;
    }

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