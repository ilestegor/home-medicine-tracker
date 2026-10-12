package org.ilestegor.home_medicine_tracker.application.port.in.auth;

import org.ilestegor.home_medicine_tracker.domain.model.User;

public interface GetCurrentUserQuery {

    User current();
}
