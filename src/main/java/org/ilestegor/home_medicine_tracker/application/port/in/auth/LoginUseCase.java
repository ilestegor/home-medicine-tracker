package org.ilestegor.home_medicine_tracker.application.port.in.auth;

import org.ilestegor.home_medicine_tracker.application.port.in.auth.command.LoginCommand;
import org.ilestegor.home_medicine_tracker.application.port.in.auth.model.Authenticated;

public interface LoginUseCase {

    Authenticated login(LoginCommand command);
}
