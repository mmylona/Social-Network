package com.app.linkedinclone.service;

import com.app.linkedinclone.model.dao.Message;
import com.app.linkedinclone.model.dto.MessageDto;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public interface MessageService {
     Message sendMessage(MessageDto messageDto);
     List<MessageDto> retrieveMessages(String email,Boolean latest,String recipientEmail);
     Long createChat(String email, Long recipientId);
}
