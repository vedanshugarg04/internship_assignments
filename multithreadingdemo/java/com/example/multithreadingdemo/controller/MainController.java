package com.example.multithreadingdemo.controller;

import com.example.multithreadingdemo.dto.FriendDTO;
import com.example.multithreadingdemo.model.Friend;
import com.example.multithreadingdemo.repository.FriendRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

@RestController
public class MainController {

    private final FriendRepository repository;
    private final RestTemplate restTemplate;

    public MainController(FriendRepository repository, RestTemplate restTemplate) {
        this.repository = repository;
        this.restTemplate = restTemplate;
    }

    @PostMapping("/friend")
    public String addFriend(@RequestBody FriendDTO request) {

        Friend savedFriend = repository.save(new Friend(request.name(), request.city()));
        Long newId = savedFriend.getId();

        String url = "http://localhost:8080/crud/confirm/" + newId;

        try {
            String confirmation = restTemplate.getForObject(url, String.class);
            return "Saved ID: " + newId + " | Verification: " + confirmation;
        } catch (Exception e) {
            return "Saved ID: " + newId + " | Verification FAILED: " + e.getMessage();
        }
    }

    @GetMapping("/crud/confirm/{id}")
    public ResponseEntity<String> confirmOperation(@PathVariable Long id) {

        boolean exists = repository.existsById(id);

        if (exists) {
            Friend f = repository.findById(id).get();
            System.out.println("DB Check Passed for User: " + f.getName());
            return ResponseEntity.ok("CONFIRMED");
        } else {
            System.out.println("DB Check Failed for ID: " + id);
            return ResponseEntity.status(404).body("REJECTED");
        }
    }
}
