package org.dci.theratrack.config;

import org.dci.theratrack.entity.User;
import org.dci.theratrack.enums.UserRole;
import org.dci.theratrack.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        // Check if admin user already exists
        if (userRepository.findByUsername("admin").isEmpty()) {
            // Create default admin user
            User adminUser = new User();
            adminUser.setId(0L);
            adminUser.setUsername("admin");
            adminUser.setPassword(passwordEncoder.encode("admin123")); // Default password
            adminUser.setUserRole(UserRole.ADMIN); // Assign ADMIN role

            // Save to the database
            userRepository.save(adminUser);
            System.out.println("Default admin user created: admin / admin123");
        } else {
            System.out.println("Admin user already exists.");
        }
    }
}
