package com.chssr.atelem.controllers;

import com.chssr.atelem.dto.CabinetDto;
import com.chssr.atelem.dto.DeviceDto;
import com.chssr.atelem.models.Device;
import com.chssr.atelem.services.DeviceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Devices", description = "Управление приборами")
@RestController
@RequestMapping("/api/devices")
public class DeviceController {

    private final DeviceService deviceService;

    @Autowired
    public DeviceController(DeviceService deviceService) {
        this.deviceService = deviceService;
    }


    @Operation(
            summary = "Получить список приборов с фильтрацией и пагинацией",
            description = "Позволяет получать приборы с параметрами поиска по серийному номеру, производителю, и статусу",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Успешный ответ с данными",
                            content = @Content(schema = @Schema(implementation = CabinetDto.class))),
                    @ApiResponse(responseCode = "400", description = "Неверные параметры запроса"),
                    @ApiResponse(responseCode = "500", description = "Внутренняя ошибка сервера")
            }
    )
    @GetMapping
    public Page<DeviceDto> findDevices(
                @Parameter(description = "Фильтр по серийному номеру")
            @RequestParam(required = false) String serialNumber,
                @Parameter(description = "Фильтр по производителю")
            @RequestParam(required = false) String manufacturer,
                @Parameter(description = "Фильтр по статусу")
            @RequestParam(required = false) String status,
                @Parameter(description = "Параметры пагинации и сортировки")
            @PageableDefault(size = 10, page = 0) Pageable pageable) {

        return deviceService.searchDevices(serialNumber, manufacturer, status, pageable);
    }
/*

    @GetMapping
    public ResponseEntity<List<DeviceDto>> findAll() {
        List<DeviceDto> devicesDto = deviceService.findAll();
        return ResponseEntity.ok(devicesDto);
    }
*/
    @Operation(
            summary = "Получить прибор по ID",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Прибор найден",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = DeviceDto.class))),
                    @ApiResponse(responseCode = "404", description = "Прибор с таким ID не найден")
            }
    )
    @GetMapping("/{id}")
    public ResponseEntity<DeviceDto> findById(@Parameter(description = "ID прибора", required = true)
                                                  @PathVariable Long id) {
        DeviceDto deviceDto = deviceService.findById(id);
        return ResponseEntity.ok(deviceDto);
    }



    @Operation(
            summary = "Создать новый прибор",
            responses = {
                    @ApiResponse(responseCode = "201", description = "Прибор успешно создан",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = DeviceDto.class))),
                    @ApiResponse(responseCode = "400", description = "Некорректные данные запроса")
            }
    )
    @PostMapping
    public ResponseEntity<DeviceDto> create(@Parameter(description = "Объект прибора для создания", required = true)
                                                @RequestBody DeviceDto deviceDto) {
        DeviceDto savedDto = deviceService.save(deviceDto);
        return new ResponseEntity<>(savedDto, HttpStatus.CREATED);
    }



    @Operation(
            summary = "Обновить существующий прибор",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Прибор успешно обновлен",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = DeviceDto.class))),
                    @ApiResponse(responseCode = "400", description = "Некорректные данные запроса"),
                    @ApiResponse(responseCode = "404", description = "Прибор с таким ID не найден")
            }
    )
    @PutMapping("/{id}")
    public ResponseEntity<DeviceDto> update(@Parameter(description = "ID прибора для обновления", required = true)
                                                @PathVariable Long id,
                                            @Parameter(description = "Обновленные данные прибора", required = true)
                                                @RequestBody DeviceDto deviceDto) {
        deviceDto.setId(id);
        DeviceDto updatedDto = deviceService.save(deviceDto);
        return ResponseEntity.ok(updatedDto);
    }



    @Operation(
            summary = "Удалить прибор по ID",
            responses = {
                    @ApiResponse(responseCode = "204", description = "Прибор успешно удален"),
                    @ApiResponse(responseCode = "404", description = "Прибор с таким ID не найден")
            }
    )
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@Parameter(description = "ID прибора для удаления", required = true)
                                           @PathVariable Long id) {
        deviceService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
