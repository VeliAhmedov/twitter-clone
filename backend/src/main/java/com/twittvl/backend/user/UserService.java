package com.twittvl.backend.user;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    public UserService(UserRepository userRepository, UserMapper userMapper) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
    }

    public UserResponse createUser(CreateUserRequestTemp createUserRequestTemp) {
        if (userRepository.existsByUsername(createUserRequestTemp.username())) {
            throw new IllegalArgumentException("Username already taken");
        }
        if (userRepository.existsByEmail(createUserRequestTemp.email())) {
            throw new IllegalArgumentException("Email already in use");
        }

        User user = new User();
        user.setUsername(createUserRequestTemp.username());
        user.setPassword(createUserRequestTemp.password()); // TEMPORARY: no hashing yet, plaintext until security phase
        user.setDisplayName(createUserRequestTemp.displayName());
        user.setEmail(createUserRequestTemp.email());

        User saved = userRepository.save(user);
        return userMapper.userToUserResponse(saved);
    }
}
