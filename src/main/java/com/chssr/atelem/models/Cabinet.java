package com.chssr.atelem.models;


import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import javax.xml.crypto.dsig.spec.XSLTTransformParameterSpec;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "cabinets")
public class Cabinet {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private String serialNumber;

    private String manufacturer;

    private LocalDate purchaseDate;

    private String status;

    private String location;

    @OneToMany(mappedBy = "cabinet", cascade = CascadeType.PERSIST, orphanRemoval = false, fetch = FetchType.EAGER)
    private List<Device> devices = new ArrayList<>();


    public void addDevice(Device device) {
        devices.add(device);
        device.setCabinet(this);
    }

    public void removeDevice(Device device) {
        devices.remove(device);
        device.setCabinet(null);
    }

}
