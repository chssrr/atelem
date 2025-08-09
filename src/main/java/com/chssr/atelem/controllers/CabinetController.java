package com.chssr.atelem.controllers;

import com.chssr.atelem.dto.CabinetDto;
import com.chssr.atelem.models.Cabinet;
import com.chssr.atelem.services.CabinetService;
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
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Cabinets", description = "Управление шкафами")
@RestController
@RequestMapping("/api/cabinets")
public class CabinetController {

    private final CabinetService cabinetService;

    @Autowired
    public CabinetController(CabinetService cabinetService) {
        this.cabinetService = cabinetService;
    }



    @Operation(
            summary = "Получить список шкафов с фильтрацией и пагинацией",
            description = "Позволяет получать шкафы с параметрами поиска по имени, статусу и локации с пагинацией",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Успешный ответ с данными",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = CabinetDto.class))),
                    @ApiResponse(responseCode = "400", description = "Неверные параметры запроса"),
                    @ApiResponse(responseCode = "500", description = "Ошибка сервера")
            }
    )
    @GetMapping
    public Page<CabinetDto> getCabinets(
            @Parameter(description = "Фильтр по имени шкафа")
                @RequestParam(required = false) String name,
            @Parameter(description = "Фильтр по статусу шкафа")
                @RequestParam(required = false) String status,
            @Parameter(description = "Фильтр по локации шкафа")
                @RequestParam(required = false) String location,
            @Parameter(description = "Параметры пагинации и сортировки")
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




    @Operation(
            summary = "Получить шкаф по ID",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Шкаф найден",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = CabinetDto.class))),
                    @ApiResponse(responseCode = "404", description = "Шкаф с таким ID не найден")
            }
    )
    @GetMapping("/{id}")
    public ResponseEntity<CabinetDto> findById(@Parameter(description = "ID шкафа", required = true)
                                                   @PathVariable Long id) {
        CabinetDto cabinetDto = cabinetService.findById(id);
        return ResponseEntity.ok(cabinetDto);
    }




    @Operation(
            summary = "Создать новый шкаф",
            responses = {
                    @ApiResponse(responseCode = "201", description = "Шкаф успешно создан",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = CabinetDto.class))),
                    @ApiResponse(responseCode = "400", description = "Некорректные данные запроса")
            }
    )
    @PostMapping
    public ResponseEntity<CabinetDto> create(@Parameter(description = "Объект шкафа для создания", required = true)
                                                 @RequestBody CabinetDto cabinetDto) {
        CabinetDto savedDto = cabinetService.save(cabinetDto);
        return new  ResponseEntity<>(savedDto, HttpStatus.CREATED);
    }






    @Operation(
            summary = "Обновить существующий шкаф",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Шкаф успешно обновлён",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = CabinetDto.class))),
                    @ApiResponse(responseCode = "400", description = "Некорректные данные запроса"),
                    @ApiResponse(responseCode = "404", description = "Шкаф с таким ID не найден")
            }
    )
    @PutMapping("/{id}")
    public ResponseEntity<CabinetDto> update(@Parameter(description = "ID шкафа для обновления", required = true)
                                                 @PathVariable Long id,
                                             @Parameter(description = "Обновлённые данные шкафа", required = true)
                                                 @RequestBody CabinetDto cabinetDto) {
        cabinetDto.setId(id);
        CabinetDto updatedDto = cabinetService.save(cabinetDto);
        return ResponseEntity.ok(cabinetDto);
    }




    @Operation(
            summary = "Удалить шкаф по ID",
            responses = {
                    @ApiResponse(responseCode = "204", description = "Шкаф успешно удалён"),
                    @ApiResponse(responseCode = "404", description = "Шкаф с таким ID не найден")
            }
    )
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@Parameter(description = "ID шкафа для удаления", required = true)
                                           @PathVariable Long id) {
        cabinetService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
