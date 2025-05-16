package com.app.linkedinclone.service;

import com.app.linkedinclone.model.dto.UserInteraction;
import java.util.List;
public interface NotificationService {

    List<UserInteraction> getNotifications(String email);
}
