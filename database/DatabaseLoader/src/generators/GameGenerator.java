package generators;

import config.DatabaseConfig;
import net.datafaker.Faker;
import utils.FakerProvider;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.*;

/**
 * Generator for Game entities.
 * Creates games with platforms and genres.
 */
public class GameGenerator {
    private final Faker faker;
    private final FakerProvider fakerProvider;
    private final DatabaseConfig config;
    private final Map<UUID, List<UUID>> editorGamesMap; // Maps editor ID to list of game IDs
    private final Map<UUID, Boolean> gamePublishStatus; // Tracks if game is published

    private static final String[] PLATFORMS = {"PC", "XBOX", "PS5", "SWITCH"};
    private static final String[] GENRES = {"ACTION", "RPG", "STRATEGY", "SPORTS"};

    public GameGenerator() {
        this.fakerProvider = FakerProvider.getInstance();
        this.faker = fakerProvider.getFaker();
        this.config = DatabaseConfig.getInstance();
        this.editorGamesMap = new HashMap<>();
        this.gamePublishStatus = new HashMap<>();
    }

    /**
     * Generate games for all editors
     */
    public void generateGames(Connection connection, List<UUID> editorIds) throws SQLException {
        if (config.isVerbose()) {
            System.out.println("Generating games for " + editorIds.size() + " editors...");
        }

        int totalGames = 0;

        for (UUID editorId : editorIds) {
            int numGames = fakerProvider.randomInt(
                config.getNumGamesPerEditorMin(),
                config.getNumGamesPerEditorMax()
            );

            List<UUID> gameIds = generateGamesForEditor(connection, editorId, numGames);
            editorGamesMap.put(editorId, gameIds);
            totalGames += numGames;
        }

        if (config.isVerbose()) {
            System.out.println("Successfully generated " + totalGames + " games\n");
        }
    }

    /**
     * Generate games for a specific editor
     */
    private List<UUID> generateGamesForEditor(Connection connection, UUID editorId, int numGames) 
            throws SQLException {
        List<UUID> gameIds = new ArrayList<>();

        String gameSql = "INSERT INTO game (id, editor_id, name, price, num_version, is_publish) VALUES (?, ?, ?, ?, ?, ?)";
        
        try (PreparedStatement gamePstmt = connection.prepareStatement(gameSql)) {
            for (int i = 0; i < numGames; i++) {
                UUID gameId = UUID.randomUUID();
                gameIds.add(gameId);

                String gameName = generateGameName();
                double price = fakerProvider.round(fakerProvider.randomDouble(5.99, 79.99), 2);
                double numVersion = fakerProvider.round(fakerProvider.randomDouble(1.0, 10.0), 1);
                boolean isPublish = fakerProvider.randomBooleanWithProbability(config.getPublishPercentage());

                gamePublishStatus.put(gameId, isPublish);

                gamePstmt.setObject(1, gameId);
                gamePstmt.setObject(2, editorId);
                gamePstmt.setString(3, gameName);
                gamePstmt.setDouble(4, price);
                gamePstmt.setDouble(5, numVersion);
                gamePstmt.setBoolean(6, isPublish);
                gamePstmt.executeUpdate();

                // Generate platforms and genres for this game
                insertGamePlatforms(connection, gameId);
                insertGameGenres(connection, gameId);
            }
        }

        return gameIds;
    }

    /**
     * Generate a realistic game name
     */
    private String generateGameName() {
        int variant = fakerProvider.getRandom().nextInt(10);
        
        // Gerund verbs (ing form) for game titles
        String[] verbs = {"Fighting", "Racing", "Building", "Exploring", "Hunting", 
                         "Conquering", "Defending", "Surviving", "Crafting", "Rising"};
        
        return switch (variant) {
            case 0 -> faker.videoGame().title();
            case 1 -> faker.ancient().hero() + ": " + faker.ancient().primordial();
            case 2 -> faker.elderScrolls().city() + " Chronicles";
            case 3 -> "The Legend of " + faker.name().firstName();
            case 4 -> faker.space().galaxy() + " Warriors";
            case 5 -> faker.animal().name() + " Simulator";
            case 6 -> faker.superhero().name() + ": The Game";
            case 7 -> faker.esports().game();
            case 8 -> faker.ancient().god() + "'s " + fakerProvider.randomElement(verbs);
            case 9 -> faker.music().genre() + " Fighter";
            default -> faker.app().name() + " Adventure";
        };
    }

    /**
     * Insert random platforms for a game (1-4 platforms)
     */
    private void insertGamePlatforms(Connection connection, UUID gameId) throws SQLException {
        String sql = "INSERT INTO game_platforms (game_id, platform) VALUES (?, ?::platform_enum)";
        
        // Shuffle platforms and select random number
        List<String> shuffledPlatforms = new ArrayList<>(Arrays.asList(PLATFORMS));
        Collections.shuffle(shuffledPlatforms, fakerProvider.getRandom());
        
        int numPlatforms = fakerProvider.randomInt(1, 4);
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            for (int i = 0; i < numPlatforms; i++) {
                pstmt.setObject(1, gameId);
                pstmt.setString(2, shuffledPlatforms.get(i));
                pstmt.executeUpdate();
            }
        }
    }

    /**
     * Insert random genres for a game (1-3 genres)
     */
    private void insertGameGenres(Connection connection, UUID gameId) throws SQLException {
        String sql = "INSERT INTO game_genres (game_id, genre) VALUES (?, ?::genre_enum)";
        
        // Shuffle genres and select random number
        List<String> shuffledGenres = new ArrayList<>(Arrays.asList(GENRES));
        Collections.shuffle(shuffledGenres, fakerProvider.getRandom());
        
        int numGenres = fakerProvider.randomInt(1, 3);
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            for (int i = 0; i < numGenres; i++) {
                pstmt.setObject(1, gameId);
                pstmt.setString(2, shuffledGenres.get(i));
                pstmt.executeUpdate();
            }
        }
    }

    /**
     * Get all game IDs
     */
    public List<UUID> getAllGameIds() {
        List<UUID> allGameIds = new ArrayList<>();
        for (List<UUID> gameIds : editorGamesMap.values()) {
            allGameIds.addAll(gameIds);
        }
        return allGameIds;
    }

    /**
     * Get game IDs for a specific editor
     */
    public List<UUID> getGameIdsForEditor(UUID editorId) {
        return editorGamesMap.getOrDefault(editorId, new ArrayList<>());
    }

    /**
     * Check if a game is published
     */
    public boolean isGamePublished(UUID gameId) {
        return gamePublishStatus.getOrDefault(gameId, false);
    }

    /**
     * Get all published game IDs
     */
    public List<UUID> getPublishedGameIds() {
        List<UUID> publishedGames = new ArrayList<>();
        for (Map.Entry<UUID, Boolean> entry : gamePublishStatus.entrySet()) {
            if (entry.getValue()) {
                publishedGames.add(entry.getKey());
            }
        }
        return publishedGames;
    }

    public Map<UUID, List<UUID>> getEditorGamesMap() {
        return editorGamesMap;
    }
}
