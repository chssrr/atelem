package com.chssr.atelem.repositories;

import com.chssr.atelem.models.Cabinet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface CabinetRepository extends JpaRepository<Cabinet, Long>, JpaSpecificationExecutor<Cabinet> {
}
