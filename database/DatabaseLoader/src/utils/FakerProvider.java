package utils;

import net.datafaker.Faker;

import java.util.Locale;
import java.util.Random;

/**
 * Centralized Faker provider for consistent data generation.
 * Uses singleton pattern to ensure the same Faker instance across all generators.
 */
public class FakerProvider {
    private static FakerProvider instance;
    private final Faker faker;
    private final Random random;

    private FakerProvider() {
        this.faker = new Faker(Locale.ENGLISH);
        this.random = new Random();
    }

    /**
     * Get singleton instance of FakerProvider
     */
    public static FakerProvider getInstance() {
        if (instance == null) {
            instance = new FakerProvider();
        }
        return instance;
    }

    /**
     * Get the Faker instance
     */
    public Faker getFaker() {
        return faker;
    }

    /**
     * Get the Random instance
     */
    public Random getRandom() {
        return random;
    }

    /**
     * Generate a random boolean with the given probability of being true
     * @param probability Percentage probability (0-100)
     */
    public boolean randomBooleanWithProbability(int probability) {
        return random.nextInt(100) < probability;
    }

    /**
     * Generate a random integer between min and max (inclusive)
     */
    public int randomInt(int min, int max) {
        return random.nextInt(max - min + 1) + min;
    }

    /**
     * Generate a random double between min and max
     */
    public double randomDouble(double min, double max) {
        return min + (max - min) * random.nextDouble();
    }

    /**
     * Round a double to specified decimal places
     */
    public double round(double value, int places) {
        double scale = Math.pow(10, places);
        return Math.round(value * scale) / scale;
    }

    /**
     * Pick a random element from an array
     */
    public <T> T randomElement(T[] array) {
        return array[random.nextInt(array.length)];
    }
}
