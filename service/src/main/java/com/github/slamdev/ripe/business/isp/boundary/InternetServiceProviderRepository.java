package com.github.slamdev.ripe.business.isp.boundary;

import com.github.slamdev.ripe.business.isp.entity.InternetServiceProvider;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InternetServiceProviderRepository extends JpaRepository<InternetServiceProvider, Long> {
    // Interface is implemented in runtime by Spring Data
}
