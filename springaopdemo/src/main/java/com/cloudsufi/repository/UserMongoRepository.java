package com.cloudsufi.repository;

import com.cloudsufi.model.UserDocument;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface UserMongoRepository extends MongoRepository<UserDocument, String> { }
