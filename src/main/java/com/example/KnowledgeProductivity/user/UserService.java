package com.example.KnowledgeProductivity.user;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UserService {
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public List<User> getContacts() {
        return userRepository.findAll();
    }


    public Long getUserIdByEmail(String userEmail) {
        User user = userRepository.findUserIdByEmail(userEmail);
        if (user != null) {
            return user.getId(); // Assuming getEmail() is a getter in your User class that returns the email.
        } else {
            return null;
        }
    }

    public Long findNoOfEmployee() {
        return userRepository.count();
    }

    public List<Map.Entry<String, Long>> findRoleAndEmployeeCount() {
        // Fetch all users
        List<User> users = userRepository.findAll();

        // Stream users, group by role converted to string, and count each group
        Map<String, Long> roleCounts = users.stream()
                .collect(Collectors.groupingBy(user -> user.getRole().name(), Collectors.counting()));

        // Convert the map entries to a list and return
        return new ArrayList<>(roleCounts.entrySet());
    }

    public String getNameAndLastName(Long receiverId) {
        // Find the user by ID using the userRepository
        User user = userRepository.findUserById(receiverId);

        // Check if the user is not null to avoid NullPointerException
        if (user != null) {
            // Return the concatenated first name and last name with a space in between
            return user.getFname() + " " + user.getLname();
        } else {
            // Optionally handle the case where no user is found
            throw new IllegalArgumentException("No user found with ID: " + receiverId);
        }
    }

    public String getFirstname(Long receiverId) {
        User user = userRepository.findById(receiverId).orElse(null);

        if (user != null) {
            return user.getFname();
        } else {
            return null;
        }
    }
}
