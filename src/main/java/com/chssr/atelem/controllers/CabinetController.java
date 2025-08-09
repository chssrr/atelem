package com.chssr.atelem.controllers;

import com.chssr.atelem.dto.CabinetDto;
import com.chssr.atelem.models.Cabinet;
import com.chssr.atelem.services.CabinetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/cabinets")
public class CabinetController {

    private final CabinetService cabinetService;

    @Autowired
    public CabinetController(CabinetService cabinetService) {
        this.cabinetService = cabinetService;
    }


    @GetMapping
    public Page<CabinetDto> getCabinets(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String location,
            @PageableDefault(size = 10, page = 0) Pageable pageable) {

        return cabinetService.searchCabinets(name, status, location, pageable);
    }

/*
    @GetMapping
    public ResponseEntity<List<CabinetDto>> findAll() {
        List<CabinetDto> cabinetsDto = cabinetService.findAll();
        return ResponseEntity.ok(cabinetsDto);
    }
*/

    @GetMapping("/{id}")
    public ResponseEntity<CabinetDto> findById(@PathVariable Long id) {
        CabinetDto cabinetDto = cabinetService.findById(id);
        return ResponseEntity.ok(cabinetDto);
    }

    @PostMapping
    public ResponseEntity<CabinetDto> create(@RequestBody CabinetDto cabinetDto) {
        CabinetDto savedDto = cabinetService.save(cabinetDto);
        return new  ResponseEntity<>(savedDto, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<CabinetDto> update(@PathVariable Long id, @RequestBody CabinetDto cabinetDto) {
        cabinetDto.setId(id);
        CabinetDto updatedDto = cabinetService.save(cabinetDto);
        return ResponseEntity.ok(cabinetDto);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        cabinetService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
