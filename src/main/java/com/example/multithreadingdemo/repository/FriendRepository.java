package com.example.multithreadingdemo.repository;

import com.example.multithreadingdemo.model.Friend;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FriendRepository extends JpaRepository<Friend, Long> {
}
