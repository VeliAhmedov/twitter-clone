package com.twittvl.backend.user;

import com.twittvl.backend.common.exception.ResourceNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    public UserService(UserRepository userRepository, UserMapper userMapper) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
    }

//    @Transactional
//    public UserResponse createUser(CreateUserRequestTemp createUserRequestTemp) {
//        if (userRepository.existsByUsername(createUserRequestTemp.username())) {
//            throw new IllegalArgumentException("Username already taken");
//        }
//        if (userRepository.existsByEmail(createUserRequestTemp.email())) {
//            throw new IllegalArgumentException("Email already in use");
//        }
//
//        User user = new User();
//        user.setUsername(createUserRequestTemp.username());
//        user.setPassword(createUserRequestTemp.password()); // TEMPORARY: no hashing yet, plaintext until security phase
//        user.setDisplayName(createUserRequestTemp.displayName());
//        user.setEmail(createUserRequestTemp.email());
//        user.setBio(createUserRequestTemp.bio());
//
//        User saved = userRepository.save(user);
//        return userMapper.userToUserResponse(saved);
//    }

    @Transactional(readOnly = true)
    public Page<UserResponse> getAllUsers(Pageable pageable) {
        return userRepository.findAllByOrderByCreatedAtDesc(pageable)
                .map(userMapper::userToUserResponse);
    }

    @Transactional(readOnly = true)
    public UserResponse findUserByUsername(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "User with username '" + username + "' not found"));
        return userMapper.userToUserResponse(user);
    }

    @Transactional
    public UserResponse updateProfile(Long id, UserUpdateRequest userUpdateRequest) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User with " + id + " not found"));
        userMapper.applyUpdate(userUpdateRequest, user);
        return userMapper.userToUserResponse(user);
    }

    @Transactional
    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }

}
