package br.com.fiap.restaurant.service.impl;

import br.com.fiap.restaurant.dto.request.CreateUserRequest;
import br.com.fiap.restaurant.dto.request.UpdateUserPasswordRequest;
import br.com.fiap.restaurant.dto.request.UpdateUserRequest;
import br.com.fiap.restaurant.dto.response.UserResponse;
import br.com.fiap.restaurant.exception.EntityNotFoundException;
import br.com.fiap.restaurant.mapper.UserMapper;
import br.com.fiap.restaurant.model.User;
import br.com.fiap.restaurant.repository.UserRepository;
import br.com.fiap.restaurant.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;

    @Transactional
    public UserResponse createUser(CreateUserRequest request) {
        User user = userMapper.toEntity(request, passwordEncoder.encode(request.password()));
        User savedUser = userRepository.save(user);

        return userMapper.toResponse(savedUser);
    }

    @Transactional
    public UserResponse updateUser(UpdateUserRequest request, User authenticatedUser) {
        User user = findById(authenticatedUser.getId());

        userMapper.updateEntity(user, request);

        User updatedUser = userRepository.save(user);

        return userMapper.toResponse(updatedUser);
    }

    @Transactional
    public void deleteUser(User authenticatedUser) {
        User user = findById(authenticatedUser.getId());

        user.setEnabled(false);
        userRepository.save(user);
    }

    @Transactional
    public UserResponse updatePassword(UpdateUserPasswordRequest request, User authenticatedUser) {
        User user = findById(authenticatedUser.getId());
        user.setPassword(passwordEncoder.encode(request.password()));
        User updatedUser = userRepository.save(user);
        return userMapper.toResponse(updatedUser);
    }

    @Override
    public User findById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(EntityNotFoundException::new);
    }

}
