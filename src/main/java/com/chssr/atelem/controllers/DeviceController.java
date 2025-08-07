package com.chssr.atelem.controllers;

import com.chssr.atelem.dto.DeviceDto;
import com.chssr.atelem.models.Device;
import com.chssr.atelem.services.DeviceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/devices")
public class DeviceController {

    private final DeviceService deviceService;

    @Autowired
    public DeviceController(DeviceService deviceService) {
        this.deviceService = deviceService;
    }

    @GetMapping
    public ResponseEntity<List<DeviceDto>> findAll() {
        List<DeviceDto> devicesDto = deviceService.findAll();
        return ResponseEntity.ok(devicesDto);
    }

    @GetMapping("/{id}")
    public ResponseEntity<DeviceDto> findById(@PathVariable Long id) {
        DeviceDto deviceDto = deviceService.findById(id);
        return ResponseEntity.ok(deviceDto);
    }

    @PostMapping
    public ResponseEntity<DeviceDto> create(@RequestBody DeviceDto deviceDto) {
        DeviceDto savedDto = deviceService.save(deviceDto);
        return new ResponseEntity<>(savedDto, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<DeviceDto> update(@PathVariable Long id, @RequestBody DeviceDto deviceDto) {
        deviceDto.setId(id);
        DeviceDto updatedDto = deviceService.save(deviceDto);
        return ResponseEntity.ok(updatedDto);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        deviceService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
