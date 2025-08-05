package com.chssr.atelem.repositories;

import com.chssr.atelem.models.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
}
