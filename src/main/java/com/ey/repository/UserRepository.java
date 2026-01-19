
package com.ey.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ey.entity.Role;
import com.ey.entity.User;

public interface UserRepository extends JpaRepository<User, Long> {
	Optional<User> findByEmail(String email);
	boolean existsByRolesContaining(Role role);

	boolean existsByEmail(String email);
}