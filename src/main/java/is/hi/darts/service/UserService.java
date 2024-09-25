package is.hi.darts.service;

import is.hi.darts.model.User;
import is.hi.darts.repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.core.userdetails.User.UserBuilder;
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

    // Register new user
    public User registerUser(String email, String username, String password) {
        if (userRepository.existsByEmail(email)) {
            System.out.println("User already exists");
            throw new RuntimeException("Email is already registered.");
        }
        System.out.println("Registering User with info:");
        System.out.println(email);
        System.out.println(username);
        System.out.println(password);
        User user = new User();
        user.setEmail(email);
        user.setUsername(username);
        user.setPassword(passwordEncoder.encode(password)); // Hash the password
        return userRepository.save(user);
    }

    // Log in user
    public User loginUser(String email, String password) {
        User user = userRepository.findByEmail(email);
        if (user == null || !passwordEncoder.matches(password, user.getPassword())) {
            throw new RuntimeException("Invalid credentials.");
        }
        return user;
    }


    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(email);
        if (user == null) {
                throw new UsernameNotFoundException("User not found with email: " + email);
        }

        // Build UserDetails with user information
        UserBuilder builder = org.springframework.security.core.userdetails.User.withUsername(user.getUsername());
        builder.password(user.getPassword());
        builder.roles("USER");

        return builder.build();
    }

}
