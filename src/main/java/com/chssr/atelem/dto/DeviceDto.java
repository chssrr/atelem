package com.chssr.atelem.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;


@Getter
@Setter
public class DeviceDto {

    private Long id;
    private String serialNumber;
    private String manufacturer;
    private LocalDate purchaseDate;
    private String status;
    private Long cabinetId;
}
