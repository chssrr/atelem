package com.chssr.atelem.mappers;

import com.chssr.atelem.dto.DeviceDto;
import com.chssr.atelem.models.Cabinet;
import com.chssr.atelem.models.Device;
import com.chssr.atelem.repositories.CabinetRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Component;

@Component
public class DeviceMapper {

    private final CabinetRepository cabinetRepository;

    public DeviceMapper(CabinetRepository cabinetRepository) {
        this.cabinetRepository = cabinetRepository;
    }

    // Entity -> DTO
    public DeviceDto toDto(Device device) {
        if (device == null) return null;

        DeviceDto dto = new DeviceDto();
        dto.setId(device.getId());
        dto.setSerialNumber(device.getSerialNumber());
        dto.setManufacturer(device.getManufacturer());
        dto.setPurchaseDate(device.getPurchaseDate());
        dto.setStatus(device.getStatus());

        if (device.getCabinet() != null) {
            dto.setCabinetId(device.getCabinet().getId());
        }
        return dto;
    }

    // DTO -> Entity
    public Device fromDto(DeviceDto dto) {
        if (dto == null) return null;

        Device device = new Device();
        device.setId(dto.getId());
        device.setSerialNumber(dto.getSerialNumber());
        device.setManufacturer(dto.getManufacturer());
        device.setPurchaseDate(dto.getPurchaseDate());
        device.setStatus(dto.getStatus());

        if (dto.getCabinetId() != null) {
            Cabinet cabinet = cabinetRepository.findById(dto.getCabinetId())
                    .orElseThrow(() -> new EntityNotFoundException("Cabinet not found with id " + dto.getCabinetId()));
            device.setCabinet(cabinet);
        } else {
            device.setCabinet(null);
        }
        return device;
    }
}
