package com.chssr.atelem.dto;

import lombok.Data;
import java.time.LocalDate;
import java.util.List;

@Data
public class CreateCabinetDto {

    private String name;
    private String serialNumber;
    private String manufacturer;
    private LocalDate purchaseDate;
    private String status;
    private String location;
    private List<CreateDeviceDto> devices;
}







