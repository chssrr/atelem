package com.chssr.atelem.mappers;

import com.chssr.atelem.dto.CabinetDto;
import com.chssr.atelem.dto.DeviceDto;
import com.chssr.atelem.models.Cabinet;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class CabinetMapper {

    private final DeviceMapper deviceMapper;

    public CabinetMapper(DeviceMapper deviceMapper) {
        this.deviceMapper = deviceMapper;
    }

    // Entity -> DTO
    public CabinetDto toDto(Cabinet cabinet) {
        if (cabinet == null) return null;

        CabinetDto dto = new CabinetDto();
        dto.setId(cabinet.getId());
        dto.setName(cabinet.getName());
        dto.setSerialNumber(cabinet.getSerialNumber());
        dto.setManufacturer(cabinet.getManufacturer());
        dto.setPurchaseDate(cabinet.getPurchaseDate());
        dto.setStatus(cabinet.getStatus());
        dto.setLocation(cabinet.getLocation());

        if (cabinet.getDevices() != null) {
            List<DeviceDto> devicesDto = cabinet.getDevices().stream()
                    .map(deviceMapper::toDto)
                    .collect(Collectors.toList());
            dto.setDevices(devicesDto);
        }
        return dto;
    }

    // DTO -> Entity (если нужно, например для PUT/POST) - можно без устройств или с вашей логикой
    public Cabinet fromDto(CabinetDto dto) {
        if (dto == null) return null;

        Cabinet cabinet = new Cabinet();
        cabinet.setId(dto.getId());
        cabinet.setName(dto.getName());
        cabinet.setSerialNumber(dto.getSerialNumber());
        cabinet.setManufacturer(dto.getManufacturer());
        cabinet.setPurchaseDate(dto.getPurchaseDate());
        cabinet.setStatus(dto.getStatus());
        cabinet.setLocation(dto.getLocation());
        // Обычно список devices отдельно обновляется, здесь не устанавливаем их

        return cabinet;
    }
}
