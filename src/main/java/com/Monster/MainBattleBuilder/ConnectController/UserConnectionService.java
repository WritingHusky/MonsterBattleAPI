package com.Monster.MainBattleBuilder.ConnectController;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;
import java.util.Objects;

@Service
public class UserConnectionService {
    @Autowired
    private UserRepository userRepository;

    public boolean addUser(String username, String password){
        if(userRepository.existsById(username)){
            return false; //User already exists
        }
        userRepository.save(new UserEntity(username, password));
        return true; //Save was successful
    }

    public boolean hasUser(String username) {
        return userRepository.existsById(username);
    }

    public boolean validateUser(String username, String password){
        if(!userRepository.existsById(username)){
            return false;
        }
        if(userRepository.findById(username).isEmpty()){
            return false;
        }
        UserEntity user = userRepository.findById(username).get();
        return Objects.equals(user.password, password);

    }

    public UserEntity getUserByUserName(String username){
        if (userRepository.findById(username).isEmpty()){
            throw new NoSuchElementException();
        }
        return userRepository.findById(username).get();
    }
}
