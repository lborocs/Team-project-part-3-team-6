package com.example.KnowledgeProductivity.message;

import com.example.KnowledgeProductivity.group_user.GroupUser;
import com.example.KnowledgeProductivity.group_user.GroupUserService;
import com.example.KnowledgeProductivity.groups.GroupChat;
import com.example.KnowledgeProductivity.groups.GroupChatService;
import com.example.KnowledgeProductivity.user.CustomUser;
import com.example.KnowledgeProductivity.user.CustomUserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;


@Controller
@RequestMapping("messages")
public class MessageController {

    private final MessageService messageService;
    private final HttpSession httpSession;
    private final CustomUserService userService;
    private final GroupUserService groupUserService;
    private final GroupChatService groupChatService;

    @Autowired
    private SimpMessagingTemplate template;



    @Autowired
    public MessageController(MessageService messageService, HttpSession httpSession, CustomUserService userService, GroupUserService groupUserService, GroupChatService groupChatService) {
        this.messageService = messageService;
        this.httpSession = httpSession;
        this.userService = userService;
        this.groupUserService = groupUserService;
        this.groupChatService = groupChatService;
    }

    @GetMapping("/set")
    public String setSession(HttpSession session, @RequestParam String userId) {
        // Set session attribute
        session.setAttribute("userId", userId); // currently set the session to 1 but needs to be changed to the person that logs in

        return "redirect:/messages/chat?receiverId=2";
    }



//    @GetMapping("/get")
//    @ResponseBody
//    public String getSession(HttpSession session) {
//        String userId = (String) session.getAttribute("userId");
//        return "Session value for user ID is: " + (userId != null ? userId : "Not set");
//    }

    private String getUserIdFromSession(HttpSession session) {
        return (String) session.getAttribute("userId");
    }







    //serve the html page

    @GetMapping("/chat")
    public String chatPage(Model model, @RequestParam Long receiverId) {
        List<Message> receiverAndSenderMessages = new ArrayList<>(messageService.retrieveMessages(receiverId , Long.valueOf( getUserIdFromSession(httpSession))));
        receiverAndSenderMessages.addAll(messageService.retrieveMessages(Long.valueOf( getUserIdFromSession(httpSession)), receiverId));

        Collections.sort(receiverAndSenderMessages, Comparator.comparing(Message::getTimeStamp));

        model.addAttribute("messages", receiverAndSenderMessages);
//        model.addAttribute("sessionId", getUserIdFromSession(this.httpSession));

        List<CustomUser> contactList = userService.getContacts(Long.parseLong(httpSession.getAttribute("userId").toString()));

        model.addAttribute("contacts", contactList);

        List<GroupUser> listOfGroups = groupUserService.getCurrentUsersGroup(Long.parseLong(getUserIdFromSession(httpSession)));
        List<GroupChat> groupDetails = groupChatService.getAllGroupDetails(listOfGroups);

        model.addAttribute("groups", listOfGroups);
        model.addAttribute("groupDetails", groupDetails);
        return "chat"; // Points to 'chat.html' Thymeleaf template
    }



//    @ResponseBody
//    @GetMapping("/retrieveMessages/{sessions}")
//    public List<List<Message>> retrieveMessages(@RequestParam() Long receiverId , @RequestParam HttpSession session) {
//        List<Message> messagesUser = messageService.retrieveMessages(getUserIdFromSession(session));
//        List<Message> recieverMessages = messageService.retrieveMessages(receiverId);
//        List<List<Message>> messagesList = new ArrayList<>();
//        messagesList.add(messagesUser);
//        messagesList.add(recieverMessages);
//        return messagesList;
//    }


    @MessageMapping("/sendMessage")
    @SendTo("/topic/messages")
    public Message sendMessage(Message message) {
        messageService.sendMessage(message);


        return message;  // Send the message to the subscribed users
    }


    @ResponseBody
    @PutMapping("/editMessage/{messageId}")
    public void editMessage(@PathVariable Long messageId,
                            @RequestParam String content) {
        messageService.editMessage(messageId,content);
    }

    @ResponseBody
    @DeleteMapping("/delete/{messageId}")
    public void deleteMessage(@PathVariable Long messageId) {
        messageService.deleteMessage(messageId);
        template.convertAndSend("/topic/deletedMessages", messageId.toString());
    }


}
