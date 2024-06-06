package com.Monster.MainBattleBuilder.ConnectController;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "Users")
public class UserEntity {

    public UserEntity(){
        this.username = "Default";
        this.password = "Password";
        this.dateCreated = LocalDateTime.now();
    }

    public UserEntity( String username, String password) {
        this.username = username;
        this.password = password;
        this.dateCreated = LocalDateTime.now();
    }

    @Id
    @Column(nullable = false, name = "Username")
    public String username;
    @Column(nullable = false, name = "Password")
    public String password;

    @Temporal(TemporalType.TIMESTAMP)
    public LocalDateTime dateCreated;


    public String toJson() {
        return "UserEntity:{" +
                "username:'" + username + '\'' +
                ", password:'" + password + '\'' +
                '}';
    }
}
