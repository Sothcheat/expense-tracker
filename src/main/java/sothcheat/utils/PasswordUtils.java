package sothcheat.utils;

import org.mindrot.jbcrypt.BCrypt;

public class PasswordUtils {

    private static final int BCRYPT_ROUNDS = 12;

    /**
     * Hashes a plain text password using BCrypt
     * @param plainPassword The plain text password
     * @return The hashed password
     */
    public static String hashPassword(String plainPassword) {
        return BCrypt.hashpw(plainPassword, BCrypt.gensalt(BCRYPT_ROUNDS));
    }

    /**
     * Verifies a plain text password against a hashed password
     * @param plainPassword The plain text password to verify
     * @param hashedPassword The hashed password to check against
     * @return true if the password matches, false otherwise
     */
    public static boolean verifyPassword(String plainPassword, String hashedPassword) {
        try {
            return BCrypt.checkpw(plainPassword, hashedPassword);
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Checks if a password needs to be rehashed (if the hashing algorithm was updated)
     * @param hashedPassword The hashed password to check
     * @return true if rehashing is needed, false otherwise
     */
    public static boolean needsRehash(String hashedPassword) {
        if (hashedPassword == null || hashedPassword.length() < 4) {
            return true;
        }

        try {
            String[] parts = hashedPassword.split("\\$");
            if (parts.length < 3) {
                return true;
            }
            int rounds = Integer.parseInt(parts[2]);
            return rounds < BCRYPT_ROUNDS;
        } catch (Exception e) {
            return true;
        }
    }

    // Prevent instantiation
    private PasswordUtils() {
    }
}