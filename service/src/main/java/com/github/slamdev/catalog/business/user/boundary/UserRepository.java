package com.github.slamdev.catalog.business.user.boundary;

import com.github.slamdev.catalog.business.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    // Interface is implemented in runtime by Spring Data
}
