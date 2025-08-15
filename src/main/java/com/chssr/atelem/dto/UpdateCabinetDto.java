package com.chssr.atelem.dto;

import lombok.Data;
import java.time.LocalDate;
import java.util.List;



@Data
public class UpdateCabinetDto {
    private Long id;
    private String name;
    private String serialNumber;
    private String manufacturer;
    private LocalDate purchaseDate;
    private String status;
    private String location;
    private List<Long> deviceIds;
}
