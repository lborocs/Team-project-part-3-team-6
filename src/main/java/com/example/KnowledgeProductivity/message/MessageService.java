package com.example.KnowledgeProductivity.message;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpSession;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.security.SignatureException;
import java.util.List;
import java.util.Optional;


//This Service class acts as the backend operation required with any dealings relating to the database
//for example checking if a message you want to edit exists?

@Service
public class MessageService {

    private final MessageRepository messageRepository;

    @Autowired
    public MessageService(MessageRepository messageRepository) {
        this.messageRepository = messageRepository;
    }

    public List<Message> retrieveMessages(Long recieverId , Long userId) {
        return messageRepository.findAllByReceiverIdAndSenderId(recieverId , userId);
    }


    public void sendMessage(Message message) {
        //saves messages to the database
        messageRepository.save(message);
    }

    @Transactional
    public void editMessage(Long messageId, String content) {

       Optional<Message> messageToEdit = messageRepository.findById(messageId);

        //checks if the message is within the database if so replace the content
        messageToEdit.ifPresent(message -> message.setContent(content));
    }

    public void deleteMessage(Long messageId) {

        //deletes the message of that unique id
        messageRepository.deleteById(messageId);
    }

    public List<Message> getMesagesByGroupId(Long groupId) {
        return messageRepository.findAllByGroupId(groupId);
    }

    public List<Message> getAllGroupChatMessages(Long groupId) {
        return messageRepository.findMessageByGroupId(groupId);
    }


}
