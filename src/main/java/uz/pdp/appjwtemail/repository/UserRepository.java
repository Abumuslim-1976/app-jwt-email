package uz.pdp.appjwtemail.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import uz.pdp.appjwtemail.entity.User;

import javax.validation.constraints.Email;
import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {

    boolean existsByEmail(@Email String email);

    Optional<User> findByEmail(@Email String email);

    Optional<User> findByEmailAndEmailCode(@Email String email, String emailCode);
}
