package generators;

import config.DatabaseConfig;
import net.datafaker.Faker;
import utils.FakerProvider;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.*;

/**
 * Generator for Patch entities.
 * Creates patches for games with version progression and realistic descriptions.
 */
public class PatchGenerator {
    private final Faker faker;
    private final FakerProvider fakerProvider;
    private final DatabaseConfig config;
    private final Map<UUID, Boolean> patchPublishStatus;
    private final List<UUID> allPatchIds;

    private static final String[] PLATFORMS = {"PC", "XBOX", "PS5", "SWITCH"};

    public PatchGenerator() {
        this.fakerProvider = FakerProvider.getInstance();
        this.faker = fakerProvider.getFaker();
        this.config = DatabaseConfig.getInstance();
        this.patchPublishStatus = new HashMap<>();
        this.allPatchIds = new ArrayList<>();
    }

    /**
     * Generate patches for all games
     */
    public void generatePatches(Connection connection, List<UUID> gameIds) throws SQLException {
        if (config.isVerbose()) {
            System.out.println("Generating patches for games...");
        }

        int totalPatches = 0;

        for (UUID gameId : gameIds) {
            int numPatches = fakerProvider.randomInt(
                config.getNumPatchesPerGameMin(),
                config.getNumPatchesPerGameMax()
            );

            generatePatchesForGame(connection, gameId, numPatches);
            totalPatches += numPatches;
        }

        if (config.isVerbose()) {
            System.out.println("Successfully generated " + totalPatches + " patches\n");
        }
    }

    /**
     * Generate patches for a specific game
     */
    private void generatePatchesForGame(Connection connection, UUID gameId, int numPatches) 
            throws SQLException {
        String sql = "INSERT INTO patch (id, is_patch_of_game, game_id, platform, old_version, " +
                     "new_version, comment, modifications, is_publish) " +
                     "VALUES (?, ?, ?, ?::platform_enum, ?, ?, ?, ?, ?)";

        // Start with initial version
        double currentVersion = 1.0;

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            for (int i = 0; i < numPatches; i++) {
                UUID patchId = UUID.randomUUID();
                allPatchIds.add(patchId);

                boolean isPatchOfGame = fakerProvider.randomBooleanWithProbability(90); // 90% are game patches
                String platform = fakerProvider.randomElement(PLATFORMS);
                
                double oldVersion = currentVersion;
                // Increment version by 0.1 to 1.0
                double versionIncrement = fakerProvider.round(fakerProvider.randomDouble(0.1, 1.0), 1);
                double newVersion = fakerProvider.round(oldVersion + versionIncrement, 1);
                currentVersion = newVersion;

                String comment = generatePatchComment();
                String modifications = generatePatchModifications();
                boolean isPublish = fakerProvider.randomBooleanWithProbability(config.getPublishPercentage());

                patchPublishStatus.put(patchId, isPublish);

                pstmt.setObject(1, patchId);
                pstmt.setBoolean(2, isPatchOfGame);
                pstmt.setObject(3, gameId);
                pstmt.setString(4, platform);
                pstmt.setDouble(5, oldVersion);
                pstmt.setDouble(6, newVersion);
                pstmt.setString(7, comment);
                pstmt.setString(8, modifications);
                pstmt.setBoolean(9, isPublish);
                pstmt.executeUpdate();
            }
        }
    }

    /**
     * Generate a realistic patch comment
     */
    private String generatePatchComment() {
        int variant = fakerProvider.getRandom().nextInt(10);
        
        return switch (variant) {
            case 0 -> "Critical bug fixes and performance improvements";
            case 1 -> "Major update with new features and content";
            case 2 -> "Stability improvements and crash fixes";
            case 3 -> "Balance updates and gameplay adjustments";
            case 4 -> "Security patch and minor bug fixes";
            case 5 -> "Performance optimization for better FPS";
            case 6 -> "Fixed issues reported by community";
            case 7 -> "Updated graphics and UI improvements";
            case 8 -> "Multiplayer fixes and network optimization";
            case 9 -> "Seasonal update with new content";
            default -> faker.lorem().sentence(10);
        };
    }

    /**
     * Generate realistic patch modifications list
     */
    private String generatePatchModifications() {
        StringBuilder modifications = new StringBuilder();
        int numModifications = fakerProvider.randomInt(3, 8);

        String[] modificationTypes = {
            "Fixed crash when %s",
            "Improved performance in %s",
            "Added new %s feature",
            "Balanced %s mechanics",
            "Fixed audio glitches in %s",
            "Updated %s textures",
            "Optimized %s loading times",
            "Fixed multiplayer issue with %s",
            "Adjusted %s difficulty",
            "Enhanced %s visual effects"
        };

        String[] gameElements = {
            "main menu", "inventory system", "combat system", "character creation",
            "boss fights", "cutscenes", "level transitions", "save system",
            "skill tree", "quest log", "map interface", "settings menu",
            "multiplayer lobby", "chat system", "achievements", "tutorials"
        };

        for (int i = 0; i < numModifications; i++) {
            String modType = fakerProvider.randomElement(modificationTypes);
            String element = fakerProvider.randomElement(gameElements);
            modifications.append("- ").append(String.format(modType, element)).append("\n");
        }

        return modifications.toString().trim();
    }

    /**
     * Check if a patch is published
     */
    public boolean isPatchPublished(UUID patchId) {
        return patchPublishStatus.getOrDefault(patchId, false);
    }

    /**
     * Get all published patch IDs
     */
    public List<UUID> getPublishedPatchIds() {
        List<UUID> publishedPatches = new ArrayList<>();
        for (Map.Entry<UUID, Boolean> entry : patchPublishStatus.entrySet()) {
            if (entry.getValue()) {
                publishedPatches.add(entry.getKey());
            }
        }
        return publishedPatches;
    }

    /**
     * Get all patch IDs
     */
    public List<UUID> getAllPatchIds() {
        return allPatchIds;
    }
}
