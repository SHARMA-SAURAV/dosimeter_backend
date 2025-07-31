package com.cdac.dosimeter_visualization.security;

//package com.indentmanagement.security;

//import com.indentmanagement.model.User;
//import com.indentmanagement.repository.UserRepository;
//import com.example.demo.model.User;
//import com.example.demo.repository.UserRepository;
import com.cdac.dosimeter_visualization.model.User;
import com.cdac.dosimeter_visualization.repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {
    private final UserRepository userRepository;

    public UserDetailsServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        return new UserDetailsImpl(user);
    }
}

