package se2203b.assignments.service;

import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import org.springframework.stereotype.Service;
import se2203b.assignments.domain.Role;
import se2203b.assignments.domain.UserAccount;
import se2203b.assignments.repo.UserAccountRepository;

import java.util.List;

@Service
public class UserAccountService {
    private final UserAccountRepository repo;

    public UserAccountService(UserAccountRepository repo) {
        this.repo = repo;
    }

    public boolean isInitialized() {
        List<UserAccount> users = repo.findByRole(Role.MASTER);

        return !users.isEmpty();
    }

    public int businessManagersCount(){
        return repo.findByRole(Role.BUSINESS_MANAGER).size();
    }
    public int networkAdministratorsCount(){
        return repo.findByRole(Role.NETWORK_ADMINISTRATOR).size();
    }
    public int lineOfBusinessesCount(){
        return repo.findByRole(Role.LINE_OF_BUSINESS_EXECUTIVE).size();
    }
    public void createMaster(String username, String rawPassword) {
        String u = username.trim().toLowerCase();

        int masters = repo.countByRole(Role.MASTER);

            if (masters > 0) {
                throw new IllegalStateException("Already initialized");
            }

            int dupe = repo.countByUsername(u);
            if (dupe > 0) {
                throw new IllegalStateException("Username exists");
            }

            String hash = PasswordUtil.hashPassword(rawPassword);

            UserAccount master = new UserAccount(u, Role.MASTER, hash, true);
            repo.save(master);
        }

    public UserAccount authenticate(String username, String rawPassword) {
        String u = username.trim().toLowerCase();

        UserAccount user = repo.findByUsername(u);

        if (user == null) {
            return null; // 🔥 prevents crash
        }

        if (!PasswordUtil.verify(rawPassword, user.getPasswordHash())) {
            return null;
        }

        return user;
    }

    public void changePassword(String username, String currentRawPassword, String newRawPassword) {
        String u = username.trim().toLowerCase();
        UserAccount user = repo.findByUsername(u);


            // verify current password during password change
            if (!PasswordUtil.verify(currentRawPassword, user.getPasswordHash())) {

                throw new IllegalArgumentException("Current password incorrect");
            }

            user.setPasswordHash(PasswordUtil.hashPassword(newRawPassword));
            user.setMustChangePassword(false);

            repo.save(user);
        }

    public void createUser(String username, String rawPassword, Role role) {
        String u = username.trim().toLowerCase();

        int users = repo.countByRole(role);

        if (users > 0) {
            throw new IllegalStateException("Already initialized");
        }

        int dupe = repo.countByUsername(u);
        if (dupe > 0) {
            throw new IllegalStateException("Username exists");
        }

        String hash = PasswordUtil.hashPassword(rawPassword);

        UserAccount user = new UserAccount(u, role, hash, true);
        repo.save(user);
    }
}
