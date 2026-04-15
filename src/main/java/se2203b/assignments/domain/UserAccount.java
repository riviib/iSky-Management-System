package se2203b.assignments.domain;

import jakarta.persistence.*;

@Entity
public class UserAccount {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 64)
    private String username;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 40)
    private Role role;

    @Column(nullable = false, length = 220)
    private String passwordHash;

    @Column(nullable = false)
    private boolean mustChangePassword;

    protected UserAccount() {}

    public UserAccount(String username, Role role, String passwordHash, boolean mustChangePassword) {
        this.username = username;
        this.role = role;
        this.passwordHash = passwordHash;
        this.mustChangePassword = mustChangePassword;
    }

    public Long getId() { return id; }
    public String getUsername() { return username; }
    public Role getRole() { return role; }
    public String getPasswordHash() { return passwordHash; }
    public boolean isMustChangePassword() { return mustChangePassword; }

    public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }
    public void setMustChangePassword(boolean mustChangePassword) { this.mustChangePassword = mustChangePassword; }
}
