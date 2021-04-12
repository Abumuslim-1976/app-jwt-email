package uz.pdp.appjwtemail.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import uz.pdp.appjwtemail.entity.Roles;
import uz.pdp.appjwtemail.entity.enums.RoleName;

public interface RoleRepository extends JpaRepository<Roles, Integer> {
    Roles findByRoleName(RoleName roleName);
}
