package pl.radeko.scoreapp.manager;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.view.RedirectView;
import pl.radeko.scoreapp.repository.UserRepository;
import pl.radeko.scoreapp.repository.entity.User;
import pl.radeko.scoreapp.repository.enums.Role;

import javax.annotation.PostConstruct;
import javax.servlet.ServletRegistration;
import javax.servlet.http.HttpServletRequest;

@Service
public class AuthManager {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;

    @Autowired
    public AuthManager(UserRepository userRepository, BCryptPasswordEncoder passwordEncoder, AuthenticationManager authenticationManager) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
    }


    public boolean login(String username, String password, HttpServletRequest request){
        Authentication authentication = authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(username, password)
        );
        if (authentication != null) {
            // saving the authentication in the session
            SecurityContextHolder.getContext().setAuthentication(authentication);
            request.getSession().setAttribute("SPRING_SECURITY_CONTEXT", SecurityContextHolder.getContext());
            return true;
        }
            return false;
    }

    public boolean register(String username, String password){
        if (!userRepository.findByUsername(username).isPresent()) {
            // creating new user
            User user = new User();
            user.setUsername(username);
            user.setPassword(passwordEncoder.encode(password));
            user.setRole(Role.USER); // assigning default role
            userRepository.save(user);
            return true;
        }
        return false;
    }

    @Value("${scoreapp.defaultLogin}")
    private String login;
    @Value("${scoreapp.defaultPassword}")
    private String password;
    @PostConstruct
    public void createAdminAccount() {
        // create an account if it doesn't exist
        if (!userRepository.findByUsername(login).isPresent()) {
            User admin = new User();
            admin.setUsername(login);
            admin.setPassword(passwordEncoder.encode(password));
            admin.setRole(Role.ADMIN);
            userRepository.save(admin);
            return;
        }
        // force the admin to have password the same as in application.properties
        User admin = userRepository.findByUsername("admin").get();
        if (!passwordEncoder.matches(password, admin.getPassword())) {
            admin.setPassword(passwordEncoder.encode(password));
            userRepository.save(admin);
        }
    }
}
