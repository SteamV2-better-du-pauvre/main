package generators;

import config.DatabaseConfig;
import net.datafaker.Faker;
import utils.FakerProvider;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Generator for Editor entities.
 * Creates editors with realistic company/person names and descriptions.
 */
public class EditorGenerator {
    private final Faker faker;
    private final FakerProvider fakerProvider;
    private final DatabaseConfig config;
    private final List<UUID> editorIds;

    public EditorGenerator() {
        this.fakerProvider = FakerProvider.getInstance();
        this.faker = fakerProvider.getFaker();
        this.config = DatabaseConfig.getInstance();
        this.editorIds = new ArrayList<>();
    }

    /**
     * Generate and insert editors into the database
     */
    public List<UUID> generateEditors(Connection connection) throws SQLException {
        int numEditors = config.getNumEditors();
        
        if (config.isVerbose()) {
            System.out.println("Generating " + numEditors + " editors...");
        }

        String sql = "INSERT INTO editor (id, name, password, type, description) VALUES (?, ?, ?, ?::type_editor_enum, ?)";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            for (int i = 0; i < numEditors; i++) {
                UUID editorId = UUID.randomUUID();
                editorIds.add(editorId);

                // Determine if entreprise or particulier
                boolean isEnterprise = fakerProvider.randomBooleanWithProbability(
                    config.getEditorEnterprisePercentage()
                );
                String type = isEnterprise ? "entreprise" : "particulier";

                // Generate name based on type
                String name = isEnterprise 
                    ? generateCompanyName()
                    : generatePersonName();

                // Generate password (hashed in real scenario)
                String password = faker.internet().password(8, 20);

                // Generate description
                String description = generateDescription(isEnterprise);

                pstmt.setObject(1, editorId);
                pstmt.setString(2, name);
                pstmt.setString(3, password);
                pstmt.setString(4, type);
                pstmt.setString(5, description);
                pstmt.executeUpdate();

                if (config.isVerbose() && (i + 1) % 10 == 0) {
                    System.out.println("  - Generated " + (i + 1) + "/" + numEditors + " editors");
                }
            }
        }

        if (config.isVerbose()) {
            System.out.println("Successfully generated " + numEditors + " editors\n");
        }

        return editorIds;
    }

    /**
     * Generate a realistic company name for enterprise editors
     */
    private String generateCompanyName() {
        int variant = fakerProvider.getRandom().nextInt(5);
        
        return switch (variant) {
            case 0 -> faker.company().name() + " Games";
            case 1 -> faker.company().name() + " Entertainment";
            case 2 -> faker.company().name() + " Studios";
            case 3 -> faker.name().lastName() + " Interactive";
            case 4 -> faker.ancient().god() + " Games";
            default -> faker.company().name();
        };
    }

    /**
     * Generate a realistic person name for individual editors
     */
    private String generatePersonName() {
        return faker.name().fullName();
    }

    /**
     * Generate a description for the editor
     */
    private String generateDescription(boolean isEnterprise) {
        if (isEnterprise) {
            // Company description
            return faker.company().catchPhrase() + ". " + 
                   faker.lorem().sentence(15) + " " +
                   faker.company().bs();
        } else {
            // Individual description
            return "Independent game developer. " + 
                   faker.lorem().sentence(10) + " " +
                   faker.hobbit().quote();
        }
    }

    /**
     * Get the list of generated editor IDs
     */
    public List<UUID> getEditorIds() {
        return editorIds;
    }
}
