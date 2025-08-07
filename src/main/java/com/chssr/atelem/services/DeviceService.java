package com.chssr.atelem.services;

import com.chssr.atelem.dto.DeviceDto;
import com.chssr.atelem.mappers.DeviceMapper;
import com.chssr.atelem.models.Cabinet;
import com.chssr.atelem.models.Device;
import com.chssr.atelem.repositories.CabinetRepository;
import com.chssr.atelem.repositories.DeviceRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class DeviceService {

    private final DeviceRepository deviceRepository;
    private  final CabinetRepository cabinetRepository;
    private final DeviceMapper deviceMapper;

    @Autowired
    public DeviceService(DeviceRepository deviceRepository, CabinetRepository cabinetRepository , DeviceMapper deviceMapper) {
        this.deviceRepository = deviceRepository;
        this.cabinetRepository = cabinetRepository;
        this.deviceMapper = deviceMapper;
    }

    public List<DeviceDto> findAll() {
        return deviceRepository.findAll().stream()
                .map(deviceMapper::toDto)
                .collect(Collectors.toList());
    }

    public DeviceDto findById(Long id) {
        Device device = deviceRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Device not found with id " + id));
        return deviceMapper.toDto(device);
    }

    private Device findEntityById(Long id) {
        return deviceRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Device not found with id " + id));
    }

    public DeviceDto save(DeviceDto deviceDto) {
        Device device = deviceMapper.fromDto(deviceDto);

        Device saved = deviceRepository.save(device);
        return deviceMapper.toDto(saved);
    }

    public DeviceDto update(Long id, DeviceDto deviceDto) {
        Device existing = deviceRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Device not found with id " + id));

        existing.setSerialNumber(deviceDto.getSerialNumber());
        existing.setManufacturer(deviceDto.getManufacturer());
        existing.setPurchaseDate(deviceDto.getPurchaseDate());
        existing.setStatus(deviceDto.getStatus());

        if (deviceDto.getCabinetId() != null) {
            Cabinet cabinet = cabinetRepository.findById(deviceDto.getCabinetId())
                    .orElseThrow(() -> new EntityNotFoundException("Cabinet not found with id " + deviceDto.getCabinetId()));
            existing.setCabinet(cabinet);
        } else {
            existing.setCabinet(null);
        }

        Device updated = deviceRepository.save(existing);
        return deviceMapper.toDto(updated);

    }

    public void deleteById(Long id) {
        Device device = findEntityById(id);
        Cabinet cabinet = device.getCabinet();
        if(cabinet != null) {
            cabinet.getDevices().remove(device);
            device.setCabinet(null);
        }
        deviceRepository.delete(device);
    }


}
