package com.twittvl.backend.user;

import com.twittvl.backend.common.exception.ResourceNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
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
    public UserResponse updateProfile(Long id, UserUpdateRequest userUpdateRequest, Long requesterId) {
        User user = userCheck(id, requesterId, "update");
        userMapper.applyUpdate(userUpdateRequest, user);
        return userMapper.userToUserResponse(user);
    }

    @Transactional
    public void deleteUser(Long id, Long requesterId) {
        userCheck(id, requesterId, "delete");
        userRepository.deleteById(id);
    }
    private User userCheck (Long id, Long requesterId, String action) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User with " + id + " not found"));
        if (!user.getId().equals(requesterId)) {
            throw new AccessDeniedException("You can only " + action + " your own account");
        }
        return user;
    }

}
