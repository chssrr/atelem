package com.chssr.atelem.services;

import com.chssr.atelem.dto.CabinetDto;
import com.chssr.atelem.mappers.CabinetMapper;
import com.chssr.atelem.models.Cabinet;
import com.chssr.atelem.models.Device;
import com.chssr.atelem.repositories.CabinetRepository;
import com.chssr.atelem.services.specifications.CabinetSpecifications;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class CabinetService {

    private final CabinetRepository cabinetRepository;
    private final CabinetMapper cabinetMapper;

    @Autowired
    public CabinetService(CabinetRepository cabinetRepository, CabinetMapper cabinetMapper) {
        this.cabinetRepository = cabinetRepository;
        this.cabinetMapper = cabinetMapper;
    }


    public Page<CabinetDto> searchCabinets(String name, String status, String location, Pageable pageable) {
        Specification<Cabinet> spec = CabinetSpecifications.filterByParams(name, status, location);
        Page<Cabinet> page = cabinetRepository.findAll(spec, pageable);
        return page.map(cabinetMapper::toDto);
    }

    public List<CabinetDto> findAll() {
        return cabinetRepository.findAll().stream()
                .map(cabinetMapper::toDto)
                .collect(Collectors.toList());
    }

    public CabinetDto findById(Long id) {
        Cabinet cabinet = cabinetRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Cabinet not found with id " + id));
        return cabinetMapper.toDto(cabinet);
    }

    private Cabinet findEntityById(Long id) {
        return cabinetRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Cabinet not found with id " + id));
    }

    public CabinetDto save(CabinetDto cabinetDto) {
        Cabinet cabinet = cabinetMapper.fromDto(cabinetDto);

        for (Device device: cabinet.getDevices()) {
            device.setCabinet(cabinet);
        }
        cabinetRepository.save(cabinet);
        return cabinetMapper.toDto(cabinet);
    }


    public CabinetDto update(Long id, CabinetDto cabinetDto) {
        Cabinet existing = cabinetRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Cabinet not found with id " + id));

        existing.setName(cabinetDto.getName());
        existing.setSerialNumber(cabinetDto.getSerialNumber());
        existing.setManufacturer(cabinetDto.getManufacturer());
        existing.setPurchaseDate(cabinetDto.getPurchaseDate());
        existing.setStatus(cabinetDto.getStatus());
        existing.setLocation(cabinetDto.getLocation());
        // Обычно devices не обновляем здесь — для устройств отдельный API/логика

        Cabinet updated = cabinetRepository.save(existing);
        return cabinetMapper.toDto(updated);
    }

    public void deleteById(Long id) {
        Cabinet cabinet = findEntityById(id);

        for (Device device: cabinet.getDevices()) {
            device.setCabinet(null);
        }

        cabinet.getDevices().clear();

        cabinetRepository.delete(cabinet);


    }

}
