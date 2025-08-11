package com.chssr.atelem.services;

import com.chssr.atelem.dto.CabinetDto;
import com.chssr.atelem.mappers.CabinetMapper;
import com.chssr.atelem.models.Cabinet;
import com.chssr.atelem.models.Device;
import com.chssr.atelem.repositories.CabinetRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

public class CabinetServiceTests {

    @Mock
    private CabinetMapper cabinetMapper;

    @Mock
    private CabinetRepository cabinetRepository;

    @InjectMocks
    private CabinetService cabinetService;


    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }



    @Test
    @DisplayName("searchCabinets: return Page of cabinets if found")
    void testSearchCabinets_ReturnsPageWithResults() {
        String name = "Cab1";
        String status = "active";
        String location = "Room101";

        Pageable pageable = PageRequest.of(0, 10);

        Cabinet cabinet = new Cabinet();
        cabinet.setId(1L);

        CabinetDto cabinetDto = new CabinetDto();
        cabinetDto.setId(1L);

        Page<Cabinet> cabinetPage = new PageImpl<>(List.of(cabinet), pageable, 1);

        when(cabinetRepository.findAll(any(Specification.class), eq(pageable))).thenReturn(cabinetPage);
        when(cabinetMapper.toDto(cabinet)).thenReturn(cabinetDto);

        Page<CabinetDto> result = cabinetService.searchCabinets(name, status, location, pageable);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals(cabinetDto.getId(), result.getContent().get(0).getId());

        verify(cabinetRepository).findAll(any(Specification.class), eq(pageable));
        verify(cabinetMapper).toDto(cabinet);
    }



    @Test
    @DisplayName("searchCabinets: returns empty Page if not found")
    void testSearchCabinets_ReturnsEmptyPage() {
        String name = "NotFound";
        String status = "inactive";
        String location = "NoWhere";

        Pageable pageable = PageRequest.of(0, 10);

        Page<Cabinet> emptyPage = new PageImpl<>(List.of(), pageable, 0);

        when(cabinetRepository.findAll(any(Specification.class), eq(pageable))).thenReturn(emptyPage);

        Page<CabinetDto> result = cabinetService.searchCabinets(name, status, location, pageable);

        assertNotNull(result);
        assertEquals(0, result.getTotalElements());
        assertTrue(result.getContent().isEmpty());

        verify(cabinetRepository).findAll(any(Specification.class), eq(pageable));
        verify(cabinetMapper, never()).toDto(any());
    }



    @Test
    @DisplayName("searchCabinets: correctly handles null filter parameters")
    void testSearchCabinets_WithNullFilterParams() {
        Pageable pageable = PageRequest.of(0, 10);

        Cabinet cabinet = new Cabinet();
        cabinet.setId(2L);

        CabinetDto cabinetDto = new CabinetDto();
        cabinetDto.setId(2L);

        Page<Cabinet> cabinetPage = new PageImpl<>(List.of(cabinet), pageable, 1);

        when(cabinetRepository.findAll(any(Specification.class), eq(pageable))).thenReturn(cabinetPage);
        when(cabinetMapper.toDto(cabinet)).thenReturn(cabinetDto);

        Page<CabinetDto> result = cabinetService.searchCabinets(null, null, null, pageable);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals(cabinetDto.getId(), result.getContent().get(0).getId());

        verify(cabinetRepository).findAll(any(Specification.class), eq(pageable));
        verify(cabinetMapper).toDto(cabinet);
    }



    @Test
    @DisplayName("searchCabinets: throws RuntimeException when exception in repository")
    void testSearchCabinets_RepositoryThrows() {
        String name = "Cab1";
        String status = "active";
        String location = "Room101";

        Pageable pageable = PageRequest.of(0, 10);

        when(cabinetRepository.findAll(any(Specification.class), eq(pageable)))
                .thenThrow(new RuntimeException("DB error"));

        RuntimeException ex = assertThrows(RuntimeException.class, () -> {
            cabinetService.searchCabinets(name, status, location, pageable);
        });

        assertTrue(ex.getMessage().contains("DB error"));

        verify(cabinetRepository).findAll(any(Specification.class), eq(pageable));
        verify(cabinetMapper, never()).toDto(any());
    }



    @Test
    @DisplayName("findById: returns DTO when cabinet exists")
    void testFindById_Found() {
        Cabinet cabinet = new Cabinet();
        cabinet.setId(10L);

        CabinetDto dto = new CabinetDto();
        dto.setId(10L);

        when(cabinetRepository.findById(10L)).thenReturn(Optional.of(cabinet));
        when(cabinetMapper.toDto(cabinet)).thenReturn(dto);

        CabinetDto result = cabinetService.findById(10L);

        assertNotNull(result);
        assertEquals(10L, result.getId());

        verify(cabinetRepository).findById(10L);
        verify(cabinetMapper).toDto(cabinet);
    }




    @Test
    @DisplayName("findById: throws EntityNotFoundException when cabinet does not exist")
    void testFindById_NotFound() {
        when(cabinetRepository.findById(999L)).thenReturn(Optional.empty());

        EntityNotFoundException ex = assertThrows(EntityNotFoundException.class, () -> {
            cabinetService.findById(999L);
        });

        assertTrue(ex.getMessage().contains("Cabinet not found"));
        verify(cabinetRepository).findById(999L);
        verify(cabinetMapper, never()).toDto(any());
    }



    @Test
    @DisplayName("save: should set cabinet reference to all devices and return saved DTO")
    void testSave_WithDevices() {
        // given
        CabinetDto inputDto = new CabinetDto();
        Cabinet cabinet = new Cabinet();

        Device device1 = new Device();
        Device device2 = new Device();
        cabinet.setDevices(List.of(device1, device2));

        CabinetDto savedDto = new CabinetDto();
        savedDto.setId(1L);

        when(cabinetMapper.fromDto(inputDto)).thenReturn(cabinet);
        when(cabinetRepository.save(cabinet)).thenReturn(cabinet);
        when(cabinetMapper.toDto(cabinet)).thenReturn(savedDto);

        // when
        CabinetDto result = cabinetService.save(inputDto);

        // then
        assertNotNull(result);
        assertEquals(1L, result.getId());
        // verify devices got cabinet reference
        assertEquals(cabinet, device1.getCabinet());
        assertEquals(cabinet, device2.getCabinet());

        verify(cabinetMapper).fromDto(inputDto);
        verify(cabinetRepository).save(cabinet);
        verify(cabinetMapper).toDto(cabinet);
    }


    @Test
    @DisplayName("save: should work when cabinet has no devices")
    void testSave_WithoutDevices() {
        // given
        CabinetDto inputDto = new CabinetDto();
        Cabinet cabinet = new Cabinet();
        cabinet.setDevices(List.of()); // empty list

        CabinetDto savedDto = new CabinetDto();
        savedDto.setId(2L);

        when(cabinetMapper.fromDto(inputDto)).thenReturn(cabinet);
        when(cabinetRepository.save(cabinet)).thenReturn(cabinet);
        when(cabinetMapper.toDto(cabinet)).thenReturn(savedDto);

        // when
        CabinetDto result = cabinetService.save(inputDto);

        // then
        assertNotNull(result);
        assertEquals(2L, result.getId());
        verify(cabinetMapper).fromDto(inputDto);
        verify(cabinetRepository).save(cabinet);
        verify(cabinetMapper).toDto(cabinet);
    }



    @Test
    @DisplayName("update: should update existing cabinet and return updated DTO")
    void testUpdate_Found() {
        // given
        Long id = 3L;
        Cabinet existing = new Cabinet();
        existing.setId(id);

        CabinetDto dto = new CabinetDto();
        dto.setName("NewName");
        dto.setSerialNumber("SN123");
        dto.setManufacturer("MakerX");
        dto.setPurchaseDate(LocalDate.now());
        dto.setStatus("Available");
        dto.setLocation("Room101");

        Cabinet updatedEntity = new Cabinet();
        updatedEntity.setId(id);

        CabinetDto updatedDto = new CabinetDto();
        updatedDto.setId(id);

        when(cabinetRepository.findById(id)).thenReturn(Optional.of(existing));
        when(cabinetRepository.save(existing)).thenReturn(updatedEntity);
        when(cabinetMapper.toDto(updatedEntity)).thenReturn(updatedDto);

        // when
        CabinetDto result = cabinetService.update(id, dto);

        // then
        assertNotNull(result);
        assertEquals(id, result.getId());
        assertEquals("NewName", existing.getName());
        assertEquals("SN123", existing.getSerialNumber());
        assertEquals("MakerX", existing.getManufacturer());
        assertEquals(dto.getPurchaseDate(), existing.getPurchaseDate());
        assertEquals("Available", existing.getStatus());
        assertEquals("Room101", existing.getLocation());

        verify(cabinetRepository).findById(id);
        verify(cabinetRepository).save(existing);
        verify(cabinetMapper).toDto(updatedEntity);
    }



    @Test
    @DisplayName("update: should throw EntityNotFoundException when cabinet does not exist")
    void testUpdate_NotFound() {
        // given
        Long id = 999L;
        CabinetDto dto = new CabinetDto();
        when(cabinetRepository.findById(id)).thenReturn(Optional.empty());

        // when / then
        EntityNotFoundException ex = assertThrows(EntityNotFoundException.class, () -> {
            cabinetService.update(id, dto);
        });

        assertTrue(ex.getMessage().contains("Cabinet not found"));
        verify(cabinetRepository).findById(id);
        verify(cabinetRepository, never()).save(any());
        verify(cabinetMapper, never()).toDto(any());
    }



    @Test
    @DisplayName("deleteById: should remove cabinet reference from all devices, clear device list, and delete cabinet")
    void testDeleteById_Found() {
        // given
        Long id = 5L;
        Cabinet cabinet = new Cabinet();

        Device device1 = new Device();
        device1.setCabinet(cabinet);
        Device device2 = new Device();
        device2.setCabinet(cabinet);

        cabinet.setDevices(new ArrayList<>(List.of(device1, device2)));

        when(cabinetRepository.findById(id)).thenReturn(Optional.of(cabinet));

        // when
        cabinetService.deleteById(id);

        // then
        assertNull(device1.getCabinet());
        assertNull(device2.getCabinet());
        assertTrue(cabinet.getDevices().isEmpty());

        verify(cabinetRepository).findById(id);
        verify(cabinetRepository).delete(cabinet);
    }



    @Test
    @DisplayName("deleteById: should throw EntityNotFoundException when cabinet does not exist")
    void testDeleteById_NotFound() {
        // given
        Long id = 999L;
        when(cabinetRepository.findById(id)).thenReturn(Optional.empty());

        // when / then
        EntityNotFoundException ex = assertThrows(EntityNotFoundException.class, () -> {
            cabinetService.deleteById(id);
        });

        assertTrue(ex.getMessage().contains("Cabinet not found"));
        verify(cabinetRepository).findById(id);
        verify(cabinetRepository, never()).delete(any(Specification.class));
    }



}
