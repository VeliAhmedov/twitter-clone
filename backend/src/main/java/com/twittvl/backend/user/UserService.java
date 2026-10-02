package com.twittvl.backend.user;

import com.twittvl.backend.auth.RefreshTokenRedisService;
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
    private final RefreshTokenRedisService refreshTokenRedisService;

    public UserService(UserRepository userRepository, UserMapper userMapper, RefreshTokenRedisService refreshTokenRedisService) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
        this.refreshTokenRedisService = refreshTokenRedisService;
    }

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
        checkOwnership(id, requesterId, "update");
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User with " + id + " not found"));
        userMapper.applyUpdate(userUpdateRequest, user);
        return userMapper.userToUserResponse(user);
    }

    @Transactional
    public void deleteUser(Long id, Long requesterId) {
        checkOwnership(id, requesterId, "delete");
        refreshTokenRedisService.revokeAllByUserId(id);
        userRepository.deleteById(id);
    }

    private void checkOwnership (Long userId, Long requesterId, String action) {
        if (!userId.equals(requesterId)) {
            throw new AccessDeniedException("You can only " + action + " your own account");
        }
    }

}
