package se2203b.assignments.repo;
import org.springframework.data.jpa.repository.JpaRepository;
import se2203b.assignments.domain.Role;
import se2203b.assignments.domain.UserAccount;

import java.util.List;

public interface UserAccountRepository extends JpaRepository<UserAccount, Long> {
    List<UserAccount> findByRole(Role role);

    int countByRole(Role role);

    int countByUsername(String username);

    UserAccount findByUsername(String username);
}
