package uz.pdp.appjwtemail.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import uz.pdp.appjwtemail.entity.User;
import uz.pdp.appjwtemail.entity.enums.RoleName;
import uz.pdp.appjwtemail.payload.ApiResponse;
import uz.pdp.appjwtemail.payload.RegisterDto;
import uz.pdp.appjwtemail.repository.RoleRepository;
import uz.pdp.appjwtemail.repository.UserRepository;

import java.util.Collections;
import java.util.Optional;
import java.util.UUID;

@Service
public class AuthService {

    @Autowired
    UserRepository userRepository;
    @Autowired
    PasswordEncoder passwordEncoder;
    @Autowired
    RoleRepository roleRepository;
    @Autowired
    JavaMailSender javaMailSender;


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

        sendSimpleMessage(user.getEmail(), user.getEmailCode());

        return new ApiResponse("Muvaffaqiyatli ro`yxatdan o`tdingiz.Akkountni aktivlashtirish uchun emailni tasdiqlang", true);

    }

    public boolean sendSimpleMessage(String email, String emailCode) {
        try {
            SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
            simpleMailMessage.setFrom("noreply@gmail.com");
            simpleMailMessage.setTo(email);
            simpleMailMessage.setSubject("Akkountni tasdiqlash");
            simpleMailMessage.setText("<a href='http://localhost:8080/api/aut/verifyEmail?emailCode=" + emailCode + "&email=" + email + "'>Tasdiqlash</a>");
            javaMailSender.send(simpleMailMessage); 
            return true;
        } catch (Exception e) {
            return false;
        }
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
}
