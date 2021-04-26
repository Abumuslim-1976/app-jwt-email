package uz.pdp.appjwtemail.service;

import net.bytebuddy.implementation.bytecode.Throw;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import uz.pdp.appjwtemail.entity.User;

import uz.pdp.appjwtemail.entity.enums.RoleName;
import uz.pdp.appjwtemail.payload.ApiResponse;
import uz.pdp.appjwtemail.payload.LoginDto;
import uz.pdp.appjwtemail.payload.RegisterDto;
import uz.pdp.appjwtemail.repository.RoleRepository;
import uz.pdp.appjwtemail.repository.UserRepository;
import uz.pdp.appjwtemail.security.JwtProvider;

import java.util.Collections;
import java.util.Optional;
import java.util.UUID;

@Service
public class AuthService implements UserDetailsService {

    @Autowired
    UserRepository userRepository;
    @Autowired
    PasswordEncoder passwordEncoder;
    @Autowired
    RoleRepository roleRepository;
    @Autowired
    JavaMailSender javaMailSender;
    @Autowired
    AuthenticationManager authenticationManager;
    @Autowired
    JwtProvider jwtProvider;


    public ApiResponse registerService(RegisterDto registerDto) {
        boolean existsByEmail = userRepository.existsByEmail(registerDto.getEmail());
        if (existsByEmail)
            return new ApiResponse("This is such email", false);

        User user = new User();
        user.setFirstName(registerDto.getFirstName());
        user.setLastName(registerDto.getLastName());
        user.setPassword(passwordEncoder.encode(registerDto.getPassword()));
        user.setEmail(registerDto.getEmail());
        user.setRoles(Collections.singleton(roleRepository.findByRoleName(RoleName.ROLE_USER)));
        user.setEmailCode(UUID.randomUUID().toString());
        userRepository.save(user);

//        sendSimpleMessage(user.getEmail(), user.getEmailCode());
        return new ApiResponse("Muvaffaqiyatli ro`yxatdan o`tdingiz.Akkountni aktivlashtirish uchun emailni tasdiqlang", true);
    }

    public ApiResponse loginService(LoginDto loginDto) {
        try {
            Authentication authenticate = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                    loginDto.getUsername(),
                    loginDto.getPassword()
            ));
            User user = (User) authenticate.getPrincipal();
            String token = jwtProvider.generateToken(user.getEmail(),user.getRoles());
            return new ApiResponse("User sistemaga login qildi", true, token);
        } catch (BadCredentialsException e) {
            return new ApiResponse("User login qila olmadi ", false);
        }

    }

    public void sendSimpleMessage(String email, String emailCode) {
        SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
        simpleMailMessage.setFrom("noreply@gmail.com");
        simpleMailMessage.setTo(email);
        simpleMailMessage.setSubject("Akkountni tasdiqlash");
        simpleMailMessage.setText("<a href='http://localhost:8080/api/auth/verifyEmail?emailCode=" + emailCode + "&email=" + email + "'>Tasdiqlash</a>");
        javaMailSender.send(simpleMailMessage);
    }

    public ApiResponse verify(String emailCode, String email) {
        Optional<User> optionalUser = userRepository.findByEmailAndEmailCode(emailCode, email);
        if (optionalUser.isPresent()) {
            User user = new User();
            user.setEnabled(true);
            user.setEmailCode(null);
            userRepository.save(user);
            return new ApiResponse("Akkount tasdiqlandi", true);
        }
        return new ApiResponse("Akkount allaqachon tasdiqlangan", false);
    }


    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<User> byEmail = userRepository.findByEmail(username);
        if (byEmail.isPresent()){
            return byEmail.get();
        }else {
            throw new UsernameNotFoundException(username + " topilmadi");
        }
//        return userRepository.findByEmail(username).orElseThrow(() -> new UsernameNotFoundException(username + " topilmadi"));
    }
}
