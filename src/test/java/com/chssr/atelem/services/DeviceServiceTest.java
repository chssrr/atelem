package com.chssr.atelem.services;

import com.chssr.atelem.dto.DeviceDto;
import com.chssr.atelem.mappers.DeviceMapper;
import com.chssr.atelem.models.Cabinet;
import com.chssr.atelem.models.Device;
import com.chssr.atelem.repositories.CabinetRepository;
import com.chssr.atelem.repositories.DeviceRepository;
import com.chssr.atelem.services.specifications.DeviceSpecifications;
import jakarta.persistence.EntityNotFoundException;
import jdk.jfr.Name;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class DeviceServiceTest {


    @Mock
    private DeviceRepository deviceRepository;

    @Mock
    private DeviceMapper deviceMapper;

    @Mock
    private CabinetRepository cabinetRepository;

    @InjectMocks
    private DeviceService deviceService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);  // Инициализация моков
    }


    @Test
    @DisplayName("searchDevices: возвращает страницу с устройствами при непустом результате")
    void testSearchDevices_ReturnsPageWithResults() {
        String serialNumber = "SN123";
        String manufacturer = "MakerX";
        String status = "active";

        Pageable pageable = PageRequest.of(0, 10);

        Device device = new Device();
        device.setId(1L);

        DeviceDto deviceDto = new DeviceDto();
        deviceDto.setId(1L);

        Page<Device> devicePage = new PageImpl<>(List.of(device), pageable, 1);

        when(deviceRepository.findAll(any(Specification.class), eq(pageable))).thenReturn(devicePage);
        when(deviceMapper.toDto(device)).thenReturn(deviceDto);

        Page<DeviceDto> result = deviceService.searchDevices(serialNumber, manufacturer, status, pageable);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals(deviceDto.getId(), result.getContent().get(0).getId());

        verify(deviceRepository).findAll(any(Specification.class), eq(pageable));
        verify(deviceMapper).toDto(device);
    }

    @Test
    @DisplayName("searchDevices: возвращает пустую страницу при отсутствии результатов")
    void testSearchDevices_ReturnsEmptyPage() {
        String serialNumber = "NoSuchSN";
        String manufacturer = "NoMaker";
        String status = "inactive";

        Pageable pageable = PageRequest.of(0, 10);

        Page<Device> emptyPage = new PageImpl<>(List.of(), pageable, 0);

        when(deviceRepository.findAll(any(Specification.class), eq(pageable))).thenReturn(emptyPage);

        Page<DeviceDto> result = deviceService.searchDevices(serialNumber, manufacturer, status, pageable);

        assertNotNull(result);
        assertEquals(0, result.getTotalElements());
        assertTrue(result.getContent().isEmpty());

        verify(deviceRepository).findAll(any(Specification.class), eq(pageable));
        verify(deviceMapper, never()).toDto(any());
    }

    @Test
    @DisplayName("searchDevices: корректно обрабатывает null-параметры фильтра")
    void testSearchDevices_WithNullFilterParams() {
        Pageable pageable = PageRequest.of(0, 10);

        Device device = new Device();
        device.setId(2L);

        DeviceDto deviceDto = new DeviceDto();
        deviceDto.setId(2L);

        Page<Device> devicePage = new PageImpl<>(List.of(device), pageable, 1);

        when(deviceRepository.findAll(any(Specification.class), eq(pageable))).thenReturn(devicePage);
        when(deviceMapper.toDto(device)).thenReturn(deviceDto);

        // Вызываем метод с null-параметрами
        Page<DeviceDto> result = deviceService.searchDevices(null, null, null, pageable);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals(deviceDto.getId(), result.getContent().get(0).getId());

        verify(deviceRepository).findAll(any(Specification.class), eq(pageable));
        verify(deviceMapper).toDto(device);
    }

    // Можно добавить тест на случай, если репозиторий выбрасывает исключение (опционально)
    @Test
    @DisplayName("searchDevices: выбрасывает RuntimeException при ошибке репозитория")
    void testSearchDevices_RepositoryThrows() {
        String serialNumber = "SN";
        String manufacturer = "Maker";
        String status = "active";

        Pageable pageable = PageRequest.of(0, 10);

        when(deviceRepository.findAll(any(Specification.class), eq(pageable)))
                .thenThrow(new RuntimeException("DB error"));

        RuntimeException ex = assertThrows(RuntimeException.class, () -> {
            deviceService.searchDevices(serialNumber, manufacturer, status, pageable);
        });

        assertTrue(ex.getMessage().contains("DB error"));

        verify(deviceRepository).findAll(any(Specification.class), eq(pageable));
        verify(deviceMapper, never()).toDto(any());
    }




    @Test
    @DisplayName("findById: возвращает DeviceDto, когда устройство найдено")
    void testFindById_WhenDeviceExists_ReturnsDeviceDto() {
        Long id = 1L;
        Device device = new Device();
        device.setId(id);

        DeviceDto deviceDto = new DeviceDto();
        deviceDto.setId(id);

        when(deviceRepository.findById(id)).thenReturn(Optional.of(device));
        when(deviceMapper.toDto(device)).thenReturn(deviceDto);

        DeviceDto result = deviceService.findById(id);

        assertNotNull(result);
        assertEquals(id, result.getId());

        verify(deviceRepository, times(1)).findById(id);
        verify(deviceMapper, times(1)).toDto(device);
    }


    @Test
    @DisplayName("findById: выбрасывает EntityNotFoundException, когда устройство не найдено")
    void testFindById_WhenDeviceNotFound_ThrowsException() {
        Long id = 1L;
        when(deviceRepository.findById(id)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> {
            deviceService.findById(id);
        });

        assertTrue(exception.getMessage().contains("Device not found with id " + id));
        verify(deviceRepository, times(1)).findById(id);
    }

    @Test
    @DisplayName("findEntityById: returns Device, when device is found")
    void testFindEntityById_WhenDeviceExists_returnsDevice() {
        Long id = 1L;
        Device device = new Device();
        device.setId(id);
        when(deviceRepository.findById(id)).thenReturn(Optional.of(device));

        Optional<Device> returnedDevice = deviceRepository.findById(id);

        assertTrue(returnedDevice.isPresent());
        assertEquals(id, returnedDevice.get().getId());
    }




    @Test
    @DisplayName("save: сохраняет новое устройство и возвращает DeviceDto")
    void testSave_NewDevice_Success() {
        // Входящий DTO
        DeviceDto deviceDto = new DeviceDto();
        deviceDto.setSerialNumber("SN123");

        // Сущность, которую создаст маппер
        Device deviceEntity = new Device();
        deviceEntity.setSerialNumber("SN123");

        // Сущность, которую вернёт репозиторий после сохранения (например, с id)
        Device savedDevice = new Device();
        savedDevice.setId(1L);
        savedDevice.setSerialNumber("SN123");

        // DTO, которую нужно вернуть из метода, преобразованную маппером
        DeviceDto savedDto = new DeviceDto();
        savedDto.setId(1L);
        savedDto.setSerialNumber("SN123");

        // Моки
        when(deviceMapper.fromDto(deviceDto)).thenReturn(deviceEntity);
        when(deviceRepository.save(deviceEntity)).thenReturn(savedDevice);
        when(deviceMapper.toDto(savedDevice)).thenReturn(savedDto);

        // Вызов тестируемого метода
        DeviceDto result = deviceService.save(deviceDto);

        // Проверки
        assertNotNull(result, "Результат не должен быть null");
        assertEquals(1L, result.getId(), "ID сохраненного устройства должен быть 1");
        assertEquals("SN123", result.getSerialNumber(), "SerialNumber должен совпадать");

        // Верификация вызовов
        verify(deviceMapper).fromDto(deviceDto);
        verify(deviceRepository).save(deviceEntity);
        verify(deviceMapper).toDto(savedDevice);
    }


    @Test
    @DisplayName("update: успешно обновляет устройство с существующим кабинетом")
    void testUpdate_ExistingDeviceWithCabinet_Success() {
        Long id = 1L;

        DeviceDto updateDto = new DeviceDto();
        updateDto.setSerialNumber("SNUpdated");
        updateDto.setManufacturer("MakerUpdated");
        updateDto.setPurchaseDate(LocalDate.of(2023,1,1));
        updateDto.setStatus("in_use");
        updateDto.setCabinetId(10L);

        Device existingDevice = new Device();
        existingDevice.setId(id);

        Cabinet cabinet = new Cabinet();
        cabinet.setId(10L);

        Device updatedDevice = new Device();
        updatedDevice.setId(id);
        updatedDevice.setSerialNumber("SNUpdated");
        updatedDevice.setManufacturer("MakerUpdated");
        updatedDevice.setPurchaseDate(LocalDate.of(2023,1,1));
        updatedDevice.setStatus("in_use");
        updatedDevice.setCabinet(cabinet);

        DeviceDto updatedDto = new DeviceDto();
        updatedDto.setId(id);
        updatedDto.setSerialNumber("SNUpdated");

        when(deviceRepository.findById(id)).thenReturn(Optional.of(existingDevice));
        when(cabinetRepository.findById(10L)).thenReturn(Optional.of(cabinet));
        when(deviceRepository.save(existingDevice)).thenReturn(updatedDevice);
        when(deviceMapper.toDto(updatedDevice)).thenReturn(updatedDto);

        DeviceDto result = deviceService.update(id, updateDto);

        assertNotNull(result);
        assertEquals(id, result.getId());
        assertEquals("SNUpdated", result.getSerialNumber());

        verify(deviceRepository).findById(id);
        verify(cabinetRepository).findById(10L);
        verify(deviceRepository).save(existingDevice);
        verify(deviceMapper).toDto(updatedDevice);
    }

    @Test
    @DisplayName("update: выбрасывает EntityNotFoundException, когда устройство не найдено")
    void testUpdate_DeviceNotFound_ThrowsException() {
        Long id = 1L;
        DeviceDto updateDto = new DeviceDto();

        when(deviceRepository.findById(id)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> {
            deviceService.update(id, updateDto);
        });

        assertTrue(exception.getMessage().contains("Device not found with id " + id));
    }

    @Test
    @DisplayName("update: выбрасывает EntityNotFoundException, когда кабинет не найден")
    void testUpdate_CabinetNotFound_ThrowsException() {
        Long id = 1L;

        DeviceDto updateDto = new DeviceDto();
        updateDto.setCabinetId(10L);

        Device existingDevice = new Device();
        existingDevice.setId(id);

        when(deviceRepository.findById(id)).thenReturn(Optional.of(existingDevice));
        when(cabinetRepository.findById(10L)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> {
            deviceService.update(id, updateDto);
        });

        assertTrue(exception.getMessage().contains("Cabinet not found with id 10"));
    }


    @Test
    @DisplayName("deleteById: удаляет устройство и обновляет кабинет, если устройство связано с кабинетом")
    void testDeleteById_WhenDeviceHasCabinet_RemovesDeviceFromCabinetAndDeletes() {
        Long deviceId = 1L;

        Device device = new Device();
        device.setId(deviceId);

        Cabinet cabinet = new Cabinet();
        List<Device> devicesList = new ArrayList<>();
        devicesList.add(device);
        cabinet.setId(10L);
        cabinet.setDevices(devicesList);

        device.setCabinet(cabinet);

        when(deviceRepository.findById(deviceId)).thenReturn(Optional.of(device));
        doNothing().when(deviceRepository).delete(device);

        deviceService.deleteById(deviceId);

        // Проверяем, что устройство удалено из кабинета
        assertFalse(cabinet.getDevices().contains(device), "Device should be removed from cabinet devices");
        assertNull(device.getCabinet(), "Device's cabinet reference should be null");

        verify(deviceRepository).findById(deviceId);
        verify(deviceRepository).delete(device);
    }

    @Test
    @DisplayName("deleteById: удаляет устройство без кабинета")
    void testDeleteById_WhenDeviceHasNoCabinet_DeletesDevice() {
        Long deviceId = 2L;

        Device device = new Device();
        device.setId(deviceId);
        device.setCabinet(null);

        when(deviceRepository.findById(deviceId)).thenReturn(Optional.of(device));
        doNothing().when(deviceRepository).delete(device);

        deviceService.deleteById(deviceId);

        // У устройства нет кабинета, просто вызываем удаление
        verify(deviceRepository).findById(deviceId);
        verify(deviceRepository).delete(device);
    }

    @Test
    @DisplayName("deleteById: выбрасывает EntityNotFoundException, если устройство не найдено")
    void testDeleteById_WhenDeviceNotFound_ThrowsException() {
        Long deviceId = 3L;
        when(deviceRepository.findById(deviceId)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> {
            deviceService.deleteById(deviceId);
        });

        assertTrue(exception.getMessage().contains("Device not found with id " + deviceId));
        verify(deviceRepository).findById(deviceId);
        verify(deviceRepository, never()).delete(any(Device.class));
    }



}
