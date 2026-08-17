package com.substring.springbootapp.config;

import com.substring.springbootapp.entity.Role;
import com.substring.springbootapp.entity.User;
import com.substring.springbootapp.repository.UserRepository;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * Idempotently seeds the fixed set of sysadmin accounts on every startup.
 * Existing accounts are left untouched other than ensuring the SYSADMIN role is present.
 */
@Component
public class SysAdminSeeder implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(SysAdminSeeder.class);

    private record SeedAccount(String email, String password, String fullName) {
    }

    private static final List<SeedAccount> SYSADMIN_ACCOUNTS = List.of(
            new SeedAccount("aysombath@gmail.com", "@Sombath@1999", "Ay Sombath"),
            new SeedAccount("henrique.santana127@altmail.kr", "@admin@123", "Henrique Santana"),
            new SeedAccount("rith.magnificent.9@gmail.com", "@admin@123", "Rith Magnificent")
    );

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public SysAdminSeeder(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        for (SeedAccount account : SYSADMIN_ACCOUNTS) {
            userRepository.findByEmailIgnoreCase(account.email())
                    .ifPresentOrElse(
                            existing -> ensureSysAdmin(existing),
                            () -> createSysAdmin(account));
        }
    }

    private void ensureSysAdmin(User existing) {
        if (!existing.getRoles().contains(Role.SYSADMIN)) {
            existing.getRoles().add(Role.SYSADMIN);
            userRepository.save(existing);
            log.info("Granted SYSADMIN role to existing account: {}", existing.getEmail());
        }
    }

    private void createSysAdmin(SeedAccount account) {
        User user = new User();
        user.setEmail(account.email());
        user.setFullName(account.fullName());
        user.setPassword(passwordEncoder.encode(account.password()));
        user.setEnabled(true);
        user.setRoles(new java.util.HashSet<>(java.util.Set.of(Role.SYSADMIN)));
        userRepository.save(user);
        log.info("Seeded SYSADMIN account: {}", account.email());
    }
}
