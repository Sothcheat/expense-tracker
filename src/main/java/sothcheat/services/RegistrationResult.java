package sothcheat.services;

public class RegistrationResult {
    public final boolean success;
    public final String  message;   // null when success

    private RegistrationResult(boolean success, String message) {
        this.success = success;
        this.message = message;
    }

    public static RegistrationResult ok() {
        return new RegistrationResult(true, null);
    }

    public static RegistrationResult fail(String msg) {
        return new RegistrationResult(false, msg);
    }
}