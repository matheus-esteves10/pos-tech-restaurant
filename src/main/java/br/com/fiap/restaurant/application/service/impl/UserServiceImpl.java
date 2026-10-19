package br.com.fiap.restaurant.application.service.impl;

import br.com.fiap.restaurant.application.dto.request.CreateUserRequest;
import br.com.fiap.restaurant.application.dto.request.UpdateUserPasswordRequest;
import br.com.fiap.restaurant.application.dto.request.UpdateUserRequest;
import br.com.fiap.restaurant.application.dto.response.UserResponse;
import br.com.fiap.restaurant.domain.exception.EntityNotFoundException;
import br.com.fiap.restaurant.application.mapper.UserMapper;
import br.com.fiap.restaurant.domain.entity.User;
import br.com.fiap.restaurant.domain.repository.UserRepository;
import br.com.fiap.restaurant.application.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.List;

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

    @Override
    public Page<UserResponse> findByName(String name, Pageable pageable) {
        Page<User> page = userRepository.findByNameIgnoreCase(name, pageable);
        if (page.isEmpty()) {
            throw new EntityNotFoundException();
        }
        return page.map(userMapper::toResponse);
    }

    public List<UserResponse> getAllUsers() {
        return userRepository.findAll().stream()
                .map(userMapper::toResponse)
                .toList();
    }

    public UserResponse getUser(Long userId) {
        return userMapper.toResponse(findById(userId));
    }

}
