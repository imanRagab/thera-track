package org.dci.theratrack.service;

import org.dci.theratrack.entity.User;
import org.dci.theratrack.enums.UserRole;
import org.dci.theratrack.exceptions.InvalidRequestException;
import org.dci.theratrack.exceptions.ResourceNotFoundException;
import org.dci.theratrack.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    /**
     * Creates a new user with role.
     *
     * @param user the user to create
     * @param role the role to assign to user
     * @return the created user
     * @throws InvalidRequestException if the user is null
     */
    public User createUser(User user, UserRole role) {
        User existingUser = userRepository.findByUsername(user.getUsername())
                .orElseGet(() -> {
                    // Create a new user if it doesn't exist
                    user.setPassword(passwordEncoder.encode(user.getPassword()));
                    user.setUserRole(role); // Assign role
                    return userRepository.save(user);
                });
        return existingUser;



    }

    /**
     * Creates a new user without role.
     *
     * @param user the user to create
     * @return the created user
     * @throws InvalidRequestException if the user is null
     */
    public User createUser(User user) {
        User existingUser = userRepository.findByUsername(user.getUsername())
                .orElseGet(() -> {
                    // Create a new user if it doesn't exist
                    user.setPassword(passwordEncoder.encode(user.getPassword()));
                    return userRepository.save(user);
                });
        return existingUser;
    }

    /**
     * Retrieves a therapist by ID.
     *
     * @param id the ID of the therapist
     * @return the user
     * @throws ResourceNotFoundException if the user is not found
     */
    public User assignRoleToUser(Long id, UserRole role) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Therapist not found with ID: " + id));
        user.setUserRole(role);
        return userRepository.save(user);
    }
}
