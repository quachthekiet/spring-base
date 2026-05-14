
package com.quachthekiet.base.config;

import java.util.HashSet;
import java.util.Set;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.quachthekiet.base.model.Permission;
import com.quachthekiet.base.model.Role;
import com.quachthekiet.base.model.User;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;

@Configuration
public class DataInitializer implements CommandLineRunner {

    @PersistenceContext
    private EntityManager em;

    private final PasswordEncoder passwordEncoder;

    public DataInitializer(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        // --- create permissions if not exists ---
        Permission pRead = findOrCreatePermission("USER_READ", "GET", "Read users");
        Permission pWrite = findOrCreatePermission("USER_WRITE", "POST", "Write users");

        // --- create roles if not exists (use simple names Admin/User) ---
        Role adminRole = findOrCreateRole("Admin", Set.of(pRead, pWrite));
        Role userRole = findOrCreateRole("User", Set.of(pRead));

        // --- create admin user if not exists ---
        Long adminCount = em.createQuery("select count(u) from User u where u.email = :email", Long.class)
                .setParameter("email", "admin@example.com")
                .getSingleResult();

        if (adminCount == 0) {
            User admin = new User();
            admin.setEmail("admin@example.com");
            admin.setPassword(passwordEncoder.encode("admin123"));
            Set<Role> adminRoles = new HashSet<>();
            adminRoles.add(adminRole);
            admin.setRoles(adminRoles);
            em.persist(admin);
        }

        // --- create example regular user if not exists ---
        Long userCount = em.createQuery("select count(u) from User u where u.email = :email", Long.class)
                .setParameter("email", "user@example.com")
                .getSingleResult();

        if (userCount == 0) {
            User user = new User();
            user.setEmail("user@example.com");
            user.setPassword(passwordEncoder.encode("user123"));
            Set<Role> roles = new HashSet<>();
            roles.add(userRole);
            user.setRoles(roles);
            em.persist(user);
        }
    }

    private Permission findOrCreatePermission(String name, String method, String description) {
        Long count = em.createQuery("select count(p) from Permission p where p.name = :name", Long.class)
                .setParameter("name", name)
                .getSingleResult();
        if (count != null && count > 0) {
            return em.createQuery("select p from Permission p where p.name = :name", Permission.class)
                    .setParameter("name", name)
                    .getSingleResult();
        }
        Permission p = Permission.builder().name(name).method(method).description(description).build();
        em.persist(p);
        return p;
    }

    private Role findOrCreateRole(String name, Set<Permission> permissions) {
        Long count = em.createQuery("select count(r) from Role r where r.name = :name", Long.class)
                .setParameter("name", name)
                .getSingleResult();
        if (count != null && count > 0) {
            return em.createQuery("select r from Role r where r.name = :name", Role.class)
                    .setParameter("name", name)
                    .getSingleResult();
        }
        Role r = new Role();
        // Role has no setter methods generated (only @Getter on class) so set fields
        // via constructor/reflection
        // But Role currently only has getters; to avoid reflection, persist a new Role
        // entity via native query
        r = new Role();
        // Use em.persist with setting via field access is not possible without setters;
        // use a JPQL insert via native SQL
        em.createNativeQuery("INSERT INTO role (name) VALUES (?)")
                .setParameter(1, name)
                .executeUpdate();

        // reload role
        Role created = em.createQuery("select r from Role r where r.name = :name", Role.class)
                .setParameter("name", name)
                .getSingleResult();

        // attach permissions (role_permissions join table)
        for (Permission p : permissions) {
            em.createNativeQuery("INSERT IGNORE INTO role_permissions (role_id, permission_id) VALUES (?, ?)")
                    .setParameter(1, created.getId())
                    .setParameter(2, p.getId())
                    .executeUpdate();
        }

        return created;
    }

}
