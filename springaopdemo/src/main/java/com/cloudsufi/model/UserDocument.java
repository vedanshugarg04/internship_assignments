package com.cloudsufi.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import lombok.*;

@Document(collection = "users_sync")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserDocument {
    @Id
    private String id;

    private Long mysqlId;
    private String username;
    private String email;
}
