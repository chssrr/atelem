package com.chssr.atelem.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
public class CabinetDto {

    private Long id;
    private String name;
    private String serialNumber;
    private String manufacturer;
    private LocalDate purchaseDate;
    private String status;
    private String location;
    private List<DeviceDto> devices;
}
