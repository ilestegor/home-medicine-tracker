package org.ilestegor.home_medicine_tracker.application.port.in.auth;

import org.ilestegor.home_medicine_tracker.application.port.in.auth.command.RegisterUserCommand;
import org.ilestegor.home_medicine_tracker.application.port.in.auth.model.Authenticated;

public interface RegisterUserUseCase {

    Authenticated register(RegisterUserCommand command);
}
