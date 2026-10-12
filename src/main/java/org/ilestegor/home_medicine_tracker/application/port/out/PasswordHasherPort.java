package org.ilestegor.home_medicine_tracker.application.port.out;

public interface PasswordHasherPort {

    String hash(String rawPassword);

    boolean matches(String rawPassword, String hash);
}
