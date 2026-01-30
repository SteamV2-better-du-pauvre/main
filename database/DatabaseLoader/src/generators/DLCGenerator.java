package generators;

import config.DatabaseConfig;
import net.datafaker.Faker;
import utils.FakerProvider;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.*;

/**
 * Generator for DLC entities.
 * Creates DLCs for games with appropriate naming and pricing.
 */
public class DLCGenerator {
    private final Faker faker;
    private final FakerProvider fakerProvider;
    private final DatabaseConfig config;
    private final Map<UUID, Boolean> dlcPublishStatus; // Tracks if DLC is published
    private final List<UUID> allDlcIds;

    public DLCGenerator() {
        this.fakerProvider = FakerProvider.getInstance();
        this.faker = fakerProvider.getFaker();
        this.config = DatabaseConfig.getInstance();
        this.dlcPublishStatus = new HashMap<>();
        this.allDlcIds = new ArrayList<>();
    }

    /**
     * Generate DLCs for all games
     */
    public void generateDLCs(Connection connection, Map<UUID, List<UUID>> editorGamesMap) 
            throws SQLException {
        if (config.isVerbose()) {
            System.out.println("Generating DLCs for games...");
        }

        int totalDlcs = 0;

        for (Map.Entry<UUID, List<UUID>> entry : editorGamesMap.entrySet()) {
            UUID editorId = entry.getKey();
            List<UUID> gameIds = entry.getValue();

            for (UUID gameId : gameIds) {
                int numDlcs = fakerProvider.randomInt(
                    config.getNumDlcPerGameMin(),
                    config.getNumDlcPerGameMax()
                );

                if (numDlcs > 0) {
                    generateDLCsForGame(connection, gameId, editorId, numDlcs);
                    totalDlcs += numDlcs;
                }
            }
        }

        if (config.isVerbose()) {
            System.out.println("Successfully generated " + totalDlcs + " DLCs\n");
        }
    }

    /**
     * Generate DLCs for a specific game
     */
    private void generateDLCsForGame(Connection connection, UUID gameId, UUID editorId, int numDlcs) 
            throws SQLException {
        String sql = "INSERT INTO dlc (id, game_id, editor_id, name, price, num_version, is_publish) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            for (int i = 0; i < numDlcs; i++) {
                UUID dlcId = UUID.randomUUID();
                allDlcIds.add(dlcId);

                String dlcName = generateDLCName();
                double price = fakerProvider.round(fakerProvider.randomDouble(2.99, 29.99), 2);
                double numVersion = fakerProvider.round(fakerProvider.randomDouble(1.0, 5.0), 1);
                boolean isPublish = fakerProvider.randomBooleanWithProbability(config.getPublishPercentage());

                dlcPublishStatus.put(dlcId, isPublish);

                pstmt.setObject(1, dlcId);
                pstmt.setObject(2, gameId);
                pstmt.setObject(3, editorId);
                pstmt.setString(4, dlcName);
                pstmt.setDouble(5, price);
                pstmt.setDouble(6, numVersion);
                pstmt.setBoolean(7, isPublish);
                pstmt.executeUpdate();
            }
        }
    }

    /**
     * Generate a realistic DLC name
     */
    private String generateDLCName() {
        int variant = fakerProvider.getRandom().nextInt(15);
        
        // Adjectives for DLC names
        String[] adjectives = {"Epic", "Ultimate", "Legendary", "Supreme", "Dark", "Golden", 
                               "Royal", "Ancient", "Mystic", "Shadow", "Divine"};
        
        return switch (variant) {
            case 0 -> "Season Pass";
            case 1 -> fakerProvider.randomElement(adjectives) + " Expansion";
            case 2 -> "Deluxe Edition Content";
            case 3 -> faker.color().name() + " Pack";
            case 4 -> faker.ancient().primordial() + " DLC";
            case 5 -> faker.music().genre() + " Collection";
            case 6 -> faker.space().planet() + " Expansion Pack";
            case 7 -> "Ultimate Edition Upgrade";
            case 8 -> faker.animal().name() + " Skin Pack";
            case 9 -> "Premium " + fakerProvider.randomElement(adjectives) + " Bundle";
            case 10 -> faker.superhero().name() + " Character Pack";
            case 11 -> "Map Pack: " + faker.elderScrolls().city();
            case 12 -> faker.ancient().god() + "'s Blessing";
            case 13 -> "Weapon Pack: " + faker.ancient().hero();
            case 14 -> "Story Extension: " + faker.book().title();
            default -> "Expansion Pack";
        };
    }

    /**
     * Check if a DLC is published
     */
    public boolean isDLCPublished(UUID dlcId) {
        return dlcPublishStatus.getOrDefault(dlcId, false);
    }

    /**
     * Get all published DLC IDs
     */
    public List<UUID> getPublishedDLCIds() {
        List<UUID> publishedDLCs = new ArrayList<>();
        for (Map.Entry<UUID, Boolean> entry : dlcPublishStatus.entrySet()) {
            if (entry.getValue()) {
                publishedDLCs.add(entry.getKey());
            }
        }
        return publishedDLCs;
    }

    /**
     * Get all DLC IDs
     */
    public List<UUID> getAllDlcIds() {
        return allDlcIds;
    }
}
