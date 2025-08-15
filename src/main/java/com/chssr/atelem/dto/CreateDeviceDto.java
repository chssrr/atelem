package com.chssr.atelem.dto;


import lombok.Data;

import java.time.LocalDate;

@Data
public class CreateDeviceDto {

    private String serialNumber;
    private String manufacturer;
    private LocalDate purchaseDate;
    private String status;
    private Long cabinetId;
}
