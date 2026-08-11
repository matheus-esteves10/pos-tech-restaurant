package br.com.fiap.restaurant.service.impl;

import br.com.fiap.restaurant.dto.request.CreateUserRequest;
import br.com.fiap.restaurant.dto.request.UpdateUserRequest;
import br.com.fiap.restaurant.dto.response.UserResponse;
import br.com.fiap.restaurant.exception.DuplicateResourceException;
import br.com.fiap.restaurant.exception.ForbiddenOperationException;
import br.com.fiap.restaurant.exception.ResourceNotFoundException;
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

    @Override
    @Transactional
    public UserResponse createUser(CreateUserRequest request) {
        validateUniquenessForCreate(request.email(), request.login(), request.phone());

        User user = userMapper.toEntity(request, passwordEncoder.encode(request.password()));
        User savedUser = userRepository.save(user);

        return userMapper.toResponse(savedUser);
    }

    @Override
    @Transactional
    public UserResponse updateUser(Long id, UpdateUserRequest request, User authenticatedUser) {
        ensureSelf(id, authenticatedUser);

        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));

        validateUniquenessForUpdate(user, request.email(), request.login(), request.phone());

        userMapper.updateEntity(user, request);

        if (request.password() != null && !request.password().isBlank()) {
            user.setPassword(passwordEncoder.encode(request.password()));
        }

        User updatedUser = userRepository.save(user);

        return userMapper.toResponse(updatedUser);
    }

    @Override
    @Transactional
    public void deleteUser(Long id, User authenticatedUser) {
        ensureSelf(id, authenticatedUser);

        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));

        user.setEnabled(false);
        userRepository.save(user);
    }

    private void ensureSelf(Long id, User authenticatedUser) {
        if (!authenticatedUser.getId().equals(id)) {
            throw new ForbiddenOperationException("You can only manage your own user account");
        }
    }

    private void validateUniquenessForCreate(String email, String login, String phone) {
        if (userRepository.existsByEmail(email)) {
            throw new DuplicateResourceException("Email already in use: " + email);
        }
        if (userRepository.existsByLogin(login)) {
            throw new DuplicateResourceException("Login already in use: " + login);
        }
        if (phone != null && !phone.isBlank() && userRepository.existsByPhone(phone)) {
            throw new DuplicateResourceException("Phone already in use: " + phone);
        }
    }

    private void validateUniquenessForUpdate(User currentUser, String newEmail, String newLogin, String newPhone) {
        if (newEmail != null && !newEmail.equalsIgnoreCase(currentUser.getEmail()) && userRepository.existsByEmail(newEmail)) {
            throw new DuplicateResourceException("Email already in use: " + newEmail);
        }
        if (newLogin != null && !newLogin.equalsIgnoreCase(currentUser.getLogin()) && userRepository.existsByLogin(newLogin)) {
            throw new DuplicateResourceException("Login already in use: " + newLogin);
        }
        if (newPhone != null && !newPhone.isBlank() && !newPhone.equals(currentUser.getPhone())
                && userRepository.existsByPhone(newPhone)) {
            throw new DuplicateResourceException("Phone already in use: " + newPhone);
        }
    }
}
