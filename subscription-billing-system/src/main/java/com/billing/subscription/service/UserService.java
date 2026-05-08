package com.billing.subscription.service;

import com.billing.subscription.dto.UserDTO;
import com.billing.subscription.dto.response.PagedResponse;
import com.billing.subscription.entity.User;
import com.billing.subscription.exception.DuplicateResourceException;
import com.billing.subscription.exception.ResourceNotFoundException;
import com.billing.subscription.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuditService auditService;

    public UserDTO createUser(UserDTO userDTO) {
        log.info("Creating new user with email: {}", userDTO.getEmail());
        
        if (userRepository.existsByUsername(userDTO.getUsername())) {
            throw new DuplicateResourceException("Username already exists: " + userDTO.getUsername());
        }
        
        if (userRepository.existsByEmail(userDTO.getEmail())) {
            throw new DuplicateResourceException("Email already exists: " + userDTO.getEmail());
        }

        User user = User.builder()
                .username(userDTO.getUsername())
                .email(userDTO.getEmail())
                .password(passwordEncoder.encode(userDTO.getPassword()))
                .firstName(userDTO.getFirstName())
                .lastName(userDTO.getLastName())
                .phoneNumber(userDTO.getPhoneNumber())
                .role(userDTO.getRole() != null ? userDTO.getRole() : User.UserRole.USER)
                .status(User.UserStatus.ACTIVE)
                .active(true)
                .description(userDTO.getDescription())
                .createdBy("SYSTEM")
                .build();

        User savedUser = userRepository.save(user);
        log.info("User created successfully with ID: {}", savedUser.getId());
        auditService.log("User", savedUser.getId(), "CREATE", "SYSTEM", null, null);
        
        return mapToDTO(savedUser);
    }

    public UserDTO getUserById(Long id) {
        log.info("Fetching user with ID: {}", id);
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + id));
        return mapToDTO(user);
    }

    public UserDTO getUserByUsername(String username) {
        log.info("Fetching user with username: {}", username);
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with username: " + username));
        return mapToDTO(user);
    }

    public PagedResponse<UserDTO> getAllUsers(Pageable pageable) {
        log.info("Fetching all users");
        Page<User> users = userRepository.findAll(pageable);
        return mapPageToDTO(users);
    }

    public PagedResponse<UserDTO> searchUsers(String search, Pageable pageable) {
        log.info("Searching users with keyword: {}", search);
        Page<User> users = userRepository.searchUsers(search, pageable);
        return mapPageToDTO(users);
    }

    public PagedResponse<UserDTO> getUsersByRole(User.UserRole role, Pageable pageable) {
        log.info("Fetching users by role: {}", role);
        Page<User> users = userRepository.findByRole(role, pageable);
        return mapPageToDTO(users);
    }

    public PagedResponse<UserDTO> getUsersByStatus(User.UserStatus status, Pageable pageable) {
        log.info("Fetching users by status: {}", status);
        Page<User> users = userRepository.findByStatus(status, pageable);
        return mapPageToDTO(users);
    }

    public UserDTO updateUser(Long id, UserDTO userDTO) {
        log.info("Updating user with ID: {}", id);
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + id));

        String oldValues = user.toString();

        if (userDTO.getFirstName() != null) {
            user.setFirstName(userDTO.getFirstName());
        }
        if (userDTO.getLastName() != null) {
            user.setLastName(userDTO.getLastName());
        }
        if (userDTO.getPhoneNumber() != null) {
            user.setPhoneNumber(userDTO.getPhoneNumber());
        }
        if (userDTO.getRole() != null) {
            user.setRole(userDTO.getRole());
        }
        if (userDTO.getStatus() != null) {
            user.setStatus(userDTO.getStatus());
        }
        if (userDTO.getActive() != null) {
            user.setActive(userDTO.getActive());
        }
        if (userDTO.getDescription() != null) {
            user.setDescription(userDTO.getDescription());
        }

        user.setUpdatedBy("SYSTEM");
        User updatedUser = userRepository.save(user);
        log.info("User updated successfully with ID: {}", updatedUser.getId());
        auditService.log("User", updatedUser.getId(), "UPDATE", "SYSTEM", oldValues, user.toString());
        
        return mapToDTO(updatedUser);
    }

    public void deleteUser(Long id) {
        log.info("Deleting user with ID: {}", id);
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + id));
        
        userRepository.delete(user);
        log.info("User deleted successfully with ID: {}", id);
        auditService.log("User", id, "DELETE", "SYSTEM", user.toString(), null);
    }

    public void changeUserStatus(Long id, User.UserStatus status) {
        log.info("Changing user status to: {} for user ID: {}", status, id);
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + id));
        
        user.setStatus(status);
        user.setUpdatedBy("SYSTEM");
        userRepository.save(user);
        log.info("User status changed successfully");
    }

    private UserDTO mapToDTO(User user) {
        return UserDTO.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .phoneNumber(user.getPhoneNumber())
                .role(user.getRole())
                .status(user.getStatus())
                .active(user.getActive())
                .description(user.getDescription())
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .build();
    }

    private PagedResponse<UserDTO> mapPageToDTO(Page<User> page) {
        List<UserDTO> content = page.getContent().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
        
        return PagedResponse.<UserDTO>builder()
                .content(content)
                .pageNumber(page.getNumber())
                .pageSize(page.getSize())
                .totalElements(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .first(page.isFirst())
                .last(page.isLast())
                .build();
    }
}