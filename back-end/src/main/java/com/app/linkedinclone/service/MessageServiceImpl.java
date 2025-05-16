package com.app.linkedinclone.service;

import com.app.linkedinclone.model.dao.Chat;
import com.app.linkedinclone.model.dao.Message;
import com.app.linkedinclone.model.dao.User;
import com.app.linkedinclone.model.dto.MessageDto;
import com.app.linkedinclone.repository.ChatRepository;
import com.app.linkedinclone.repository.MessageRepository;
import com.app.linkedinclone.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

import static com.app.linkedinclone.model.dao.Message.convertToDto;
import static java.lang.Boolean.TRUE;
import static java.util.Objects.isNull;
import static org.springframework.util.CollectionUtils.isEmpty;

@Service
@RequiredArgsConstructor
@Slf4j
public class MessageServiceImpl implements MessageService {
    private static final String USER_NOT_FOUND="User not found";
    private final UserRepository userRepository;
    private final MessageRepository messageRepository;
    private final ImageService imageService;
    private final ChatRepository chatRepository;

    @Override
    public Message sendMessage(MessageDto messageDto) {

        User sender = userRepository.findByEmail(messageDto.getSenderEmail())
                .orElseThrow(() -> new UsernameNotFoundException("Sender not found"));
        User recipient = userRepository.findByEmail(messageDto.getRecipientEmail())
                .orElseThrow(() -> new UsernameNotFoundException("Recipient not found"));

        Chat checkedChat=ensureChatExistsForUser(recipient,sender);
        Chat chat;
        if(isNull(checkedChat)){
            chat=chatRepository.findByUsersContainingAndUsersContaining(sender, recipient)
                    .orElseGet(() -> {
                        Chat newChat = new Chat();
                        newChat.setUsers(List.of(sender, recipient));
                        return chatRepository.save(newChat);
                    });
        }else {
            chat=checkedChat;
        }

        Message message = new Message();
        message.setSender(sender);
        message.setRecipient(recipient);
        message.setContent(messageDto.getContent());
        message.setTimestamp(LocalDateTime.now());
        chat.addMessage(message);
        return messageRepository.save(message);
    }

    @Override
    public List<MessageDto> retrieveMessages(String email, Boolean latest, String recipientEmail) {
        log.debug("Trying to retrieve messages for user with email: {} , and latest : {} and recipientEmail:{}", email, latest, recipientEmail);
        if (TRUE.equals(latest)) {
            List<MessageDto> messageDtos = getLatestMessages(email, recipientEmail);
            log.info("Retrieving latest messages for user with email: {} and List of messages : {}", email, messageDtos);
            return messageDtos;
        }
        log.debug("Retrieving messages for user conversations on chat click with email: {} , and recipient email: {} ", email, recipientEmail);
        return getAllMessagesBetweenUsers(email, recipientEmail);
    }

    @Override
    public Long createChat(String email, Long recipientId) {
        log.debug("Creating chat between sender : {} and recipientId : {}", email, recipientId);
        User sender = userRepository.findByEmail(email).orElseThrow(() -> new UsernameNotFoundException(USER_NOT_FOUND));
        Optional<User> recipient = userRepository.findById(recipientId);
        if (recipient.isEmpty())
            return null;
        Chat chatChecked=ensureChatExistsForUser(recipient.get(),sender);
        if(!isNull(chatChecked))
            return chatChecked.getId();

        Chat chat = chatRepository.findByUsersContainingAndUsersContaining(sender, recipient.get())
                .orElseGet(() -> {
                    log.debug("Creating new chat between sender : {} and recipient : {}", sender, recipient);
                    Chat newChat = new Chat();
                    newChat.setUsers(List.of(sender, recipient.get()));
                    return chatRepository.save(newChat);
                });
        log.info("Id of chat created is : {}", chat.getId());
        return chat.getId();
    }

    private Chat ensureChatExistsForUser(User recipient,User sender){
        if (recipient.getId().equals(sender.getId())) {
            log.debug("Creating chat between the same user : {}", sender);
            Optional<List<Chat>> chats = chatRepository.findByUsersContaining(sender);

            if (chats.isPresent()) {
                List<Chat> chatList = chats.get().stream().filter(chats1 -> chats1.getUsers().size() == 1).toList();
                if (chatList.size() == 1) {
                    log.info("Chats size is one , we found it with id:{}", chats.get().get(0).getId());
                    log.debug("Chat already exists for user : {}", sender);

                    return chats.get().get(0);
                }

            }

            Chat newChat = new Chat();
            newChat.setUsers(List.of(sender));
            chatRepository.save(newChat);
            log.info("Chat id to return: {}", newChat.getId());
            return newChat;
        }
        return null;
    }

    private List<MessageDto> getLatestMessages(String email, String recipientEmail) {
        User currentUser = userRepository.findByEmail(email).orElseThrow(() -> new UsernameNotFoundException(USER_NOT_FOUND));
        Optional<List<Chat>> chat = chatRepository.findByUsersContaining(currentUser);
        List<Message> messages = new ArrayList<>();
        log.info("Chats are : {}", chat.get().size());
        chat.ifPresent(chats -> chats.forEach(chat1 -> messages.add(getLatestMessageFromChat(chat1))));
        if (isEmpty(messages)) {
            return new ArrayList<>();
        }
        return messages.stream()
                .filter(message -> !isNull(message.getId()))
                .map(message -> convertToDto(message, imageService))
                .sorted(Comparator.comparing(MessageDto::getTimestamp).reversed())
                .toList();
    }

    private Message getLatestMessageFromChat(Chat chat) {
        if (chat.getMessages().size() - 1 < 0)
            return new Message();
        return chat.getMessages().stream().sorted(Comparator.comparing(Message::getTimestamp)).toList().get(chat.getMessages().size() - 1);
    }

    private List<MessageDto> getAllMessagesBetweenUsers(String email, String recipientEmail) {
        User sender = userRepository.findByEmail(email).orElseThrow(() -> new UsernameNotFoundException(USER_NOT_FOUND));
        User recipient = userRepository.findByEmail(recipientEmail).orElseThrow(() -> new UsernameNotFoundException("Recipient not found"));
        Chat chatChecked=ensureChatExistsForUser(recipient,sender);
        Chat chat;
        if(isNull(chatChecked)){
             chat= chatRepository.findByUsersContainingAndUsersContaining(sender, recipient)
                    .orElseGet(() -> {
                        Chat newChat = new Chat();
                        newChat.setUsers(List.of(sender, recipient));
                        return chatRepository.save(newChat);
                    });
        }else {
            chat=chatChecked;
        }

        List<Message> messages = chat.getMessages();
        if (isEmpty(messages)) {
            return new ArrayList<>();
        }
        return messages.stream()
                .map(message -> convertToDto(message, imageService))
                .sorted(Comparator.comparing(MessageDto::getTimestamp))
                .toList();
    }


}
