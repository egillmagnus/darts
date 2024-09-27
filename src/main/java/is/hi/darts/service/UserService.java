package is.hi.darts.service;

import is.hi.darts.model.User;
import is.hi.darts.repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, BCryptPasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    // Register new user using constructor
    public User registerUser(String email, String displayName, String password) {
        if (userRepository.existsByEmail(email)) {
            throw new RuntimeException("Email is already registered.");
        }

        // Create User using the constructor and hashed password
        User user = new User(null, email, passwordEncoder.encode(password), displayName);
        return userRepository.save(user);
    }

    // Log in user (Custom login logic)
    public User loginUser(String email, String password) {
        User user = userRepository.findByEmail(email);
        if (user == null || !passwordEncoder.matches(password, user.getPassword())) {
            throw new RuntimeException("Invalid credentials.");
        }
        return user;
    }

    // Load user by email (used for authentication)
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(email);
        if (user == null) {
            throw new UsernameNotFoundException("User not found with email: " + email);
        }
        return user;
    }
}
