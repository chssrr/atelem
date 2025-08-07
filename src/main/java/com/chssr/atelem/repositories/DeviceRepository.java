package com.chssr.atelem.repositories;

import com.chssr.atelem.models.Device;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DeviceRepository extends JpaRepository<Device, Long> {
}
