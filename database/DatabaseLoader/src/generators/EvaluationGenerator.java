package generators;

import config.DatabaseConfig;
import net.datafaker.Faker;
import utils.FakerProvider;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import java.util.UUID;

/**
 * Generator for Evaluation entities.
 * Creates game evaluations with ratings and reviews (simulating sync from platform).
 */
public class EvaluationGenerator {
    private final Faker faker;
    private final FakerProvider fakerProvider;
    private final DatabaseConfig config;

    private static final String[] PLATFORMS = {"PC", "XBOX", "PS5", "SWITCH"};

    public EvaluationGenerator() {
        this.fakerProvider = FakerProvider.getInstance();
        this.faker = fakerProvider.getFaker();
        this.config = DatabaseConfig.getInstance();
    }

    /**
     * Generate evaluations for games
     */
    public void generateEvaluations(Connection connection, List<UUID> gameIds) throws SQLException {
        if (config.isVerbose()) {
            System.out.println("Generating evaluations...");
        }

        int totalEvaluations = 0;

        for (UUID gameId : gameIds) {
            int numEvaluations = fakerProvider.randomInt(
                config.getNumEvaluationsPerGameMin(),
                config.getNumEvaluationsPerGameMax()
            );

            generateEvaluationsForGame(connection, gameId, numEvaluations);
            totalEvaluations += numEvaluations;
        }

        if (config.isVerbose()) {
            System.out.println("Successfully generated " + totalEvaluations + " evaluations\n");
        }
    }

    /**
     * Generate evaluations for a specific game
     */
    private void generateEvaluationsForGame(Connection connection, UUID gameId, int numEvaluations) 
            throws SQLException {
        String sql = "INSERT INTO evaluation (id_game, description, plateforme, note) " +
                     "VALUES (?, ?, ?::platform_enum, ?)";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            for (int i = 0; i < numEvaluations; i++) {
                String platform = fakerProvider.randomElement(PLATFORMS);
                int note = generateRealisticRating();
                String description = generateReviewDescription(note);

                pstmt.setObject(1, gameId);
                pstmt.setString(2, description);
                pstmt.setString(3, platform);
                pstmt.setInt(4, note);
                pstmt.executeUpdate();
            }
        }
    }

    /**
     * Generate a realistic rating (bell curve, most ratings 6-9)
     */
    private int generateRealisticRating() {
        int random = fakerProvider.getRandom().nextInt(100);
        
        // Distribution: 5% (0-3), 15% (4-5), 30% (6-7), 40% (8-9), 10% (10)
        if (random < 5) {
            return fakerProvider.randomInt(0, 3); // 5% very bad
        } else if (random < 20) {
            return fakerProvider.randomInt(4, 5); // 15% bad
        } else if (random < 50) {
            return fakerProvider.randomInt(6, 7); // 30% average
        } else if (random < 90) {
            return fakerProvider.randomInt(8, 9); // 40% good
        } else {
            return 10; // 10% perfect
        }
    }

    /**
     * Generate a review description based on rating
     */
    private String generateReviewDescription(int note) {
        String review;
        
        if (note >= 9) {
            review = generatePositiveReview();
        } else if (note >= 7) {
            review = generateMixedPositiveReview();
        } else if (note >= 5) {
            review = generateMixedReview();
        } else if (note >= 3) {
            review = generateNegativeReview();
        } else {
            review = generateVeryNegativeReview();
        }

        // Add some random context
        return review + " " + faker.lorem().sentence(8);
    }

    /**
     * Generate a positive review (9-10/10)
     */
    private String generatePositiveReview() {
        String[] templates = {
            "Absolutely amazing game! The graphics are stunning and gameplay is incredibly smooth.",
            "Best game I've played this year. Highly recommend to everyone!",
            "Perfect in every way. The story, mechanics, and visuals are all top-notch.",
            "A masterpiece! This game sets a new standard for the genre.",
            "Incredible experience from start to finish. Worth every penny!",
            "Flawless execution. The developers really nailed everything.",
            "Game of the year material. Everything about it is phenomenal."
        };
        return fakerProvider.randomElement(templates);
    }

    /**
     * Generate a mixed positive review (7-8/10)
     */
    private String generateMixedPositiveReview() {
        String[] templates = {
            "Great game overall! A few minor bugs but nothing game-breaking.",
            "Really enjoyed it. Some performance issues but the gameplay makes up for it.",
            "Solid experience. Could use some improvements but definitely worth playing.",
            "Very good game with a great story. Some mechanics feel a bit clunky though.",
            "Impressive work! A few rough edges but still highly enjoyable.",
            "Fun gameplay with beautiful graphics. A few optimization issues on my platform.",
            "Strong entry in the series. Not perfect but definitely recommended."
        };
        return fakerProvider.randomElement(templates);
    }

    /**
     * Generate a mixed review (5-6/10)
     */
    private String generateMixedReview() {
        String[] templates = {
            "It's okay. Has potential but needs more polish and content.",
            "Average game. Some good ideas but execution could be better.",
            "Decent but nothing special. Lots of room for improvement.",
            "Mixed feelings about this one. Good concept, mediocre execution.",
            "Not bad but not great either. Wait for a sale maybe.",
            "Has its moments but also significant flaws. Needs patches.",
            "Serviceable game. Fun in parts but repetitive and buggy."
        };
        return fakerProvider.randomElement(templates);
    }

    /**
     * Generate a negative review (3-4/10)
     */
    private String generateNegativeReview() {
        String[] templates = {
            "Disappointing. So many bugs and performance issues.",
            "Not worth the price. Lacks content and feels unfinished.",
            "Frustrating experience. Controls are clunky and AI is terrible.",
            "Expected much more. Feels like a rushed release.",
            "Poor optimization and boring gameplay. Skip this one.",
            "Too many issues to enjoy. Needs serious work from developers.",
            "Underwhelming in every aspect. Better alternatives exist."
        };
        return fakerProvider.randomElement(templates);
    }

    /**
     * Generate a very negative review (0-2/10)
     */
    private String generateVeryNegativeReview() {
        String[] templates = {
            "Complete waste of money. Crashes constantly and barely playable.",
            "Absolutely terrible. Save your money and time.",
            "Broken mess. How did this even get released?",
            "Unplayable. Constant crashes and game-breaking bugs everywhere.",
            "Worst game purchase I've made. Requesting refund.",
            "Total disaster. Nothing works as intended.",
            "Avoid at all costs. Fundamentally broken on multiple levels."
        };
        return fakerProvider.randomElement(templates);
    }
}
