package sothcheat.services;

import sothcheat.database.DBConnection;
import sothcheat.models.User;
import sothcheat.utils.PasswordUtils;
import sothcheat.utils.ValidationUtils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class UserService {

    public boolean emailExists(String email) {
        String sql = "SELECT 1 FROM users WHERE LOWER(email) = LOWER(?) LIMIT 1";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement stm = con.prepareStatement(sql)) {

            stm.setString(1, email.trim());
            ResultSet rs = stm.executeQuery();

            return rs.next();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public RegistrationResult registerUser(String userName, String email, String password) {
        if (!ValidationUtils.isValidUsername(userName)) {
            return RegistrationResult.fail("Username must be 3-50 characters and contain only letters, numbers or '_'");
        }
        if (!ValidationUtils.isValidEmail(email)) {
            return RegistrationResult.fail("Please enter a valid e-mail address.");
        }
        if (!ValidationUtils.isValidPassword(password)) {
            return RegistrationResult.fail(ValidationUtils.getPasswordValidationMessage());
        }

        if(emailExists(email)) {
            return RegistrationResult.fail("This e-mail is already registered.");
        }

        String hash = PasswordUtils.hashPassword(password);

        String sql = "INSERT INTO users(username, email, password_hash) VALUES (?, ?, ?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, userName);
            stmt.setString(2, email);
            stmt.setString(3, hash);

            int rows = stmt.executeUpdate();
            return rows > 0 ? RegistrationResult.ok() : RegistrationResult.fail("Failed to create user – please try again.");

        } catch (Exception e) {
            e.printStackTrace();
            return RegistrationResult.fail("Database error: " + e.getMessage());
        }
    }

    public User loginUser(String email, String password) {
        String sql = "SELECT user_id, username, email, password_hash FROM users WHERE email = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, email);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                String storedHash = rs.getString("password_hash");
                if (PasswordUtils.verifyPassword(password, storedHash)) {
                    User user = new User();
                    user.setUserId(rs.getInt("user_id"));
                    user.setUsername(rs.getString("username"));
                    user.setEmail(rs.getString("email"));
                    return user;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
