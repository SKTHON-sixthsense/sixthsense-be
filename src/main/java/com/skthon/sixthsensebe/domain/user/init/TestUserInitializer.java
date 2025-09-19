package com.skthon.sixthsensebe.domain.user.init;

import com.skthon.sixthsensebe.domain.user.entity.Role;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class TestUserInitializer implements ApplicationRunner {

  @PersistenceContext
  private final EntityManager em;

  private final PasswordEncoder passwordEncoder;

  @Override
  @Transactional
  public void run(ApplicationArguments args) {

    // AUTO_INCREMENT 초기화 (선택)
    em.createNativeQuery("ALTER TABLE users AUTO_INCREMENT = 1").executeUpdate();

    createOrUpdateUser(1L, "worker", "worker1234!", "박명수", Role.WORKER);
    createOrUpdateUser(2L, "boss", "boss1234!", "유재석", Role.OWNER);
  }

  private void createOrUpdateUser(Long id, String username, String rawPw, String name, Role role) {
    String encodedPw = passwordEncoder.encode(rawPw);

    em.createNativeQuery(
            "INSERT INTO users (id, username, password, name, role, created_at, modified_at) " +
                "VALUES (:id, :username, :password, :name, :role, NOW(), NOW()) " +
                "ON DUPLICATE KEY UPDATE " +
                "username = VALUES(username), " +
                "password = VALUES(password), " +
                "name = VALUES(name), " +
                "role = VALUES(role), " +
                "modified_at = NOW()"
        )
        .setParameter("id", id)
        .setParameter("username", username)
        .setParameter("password", encodedPw)
        .setParameter("name", name)
        .setParameter("role", role.name())
        .executeUpdate();
  }
}
