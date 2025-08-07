package com.chssr.atelem.models;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@Entity
@Table(name = "devices")
public class Device {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String serialNumber;

    private String manufacturer;

    private LocalDate purchaseDate;

    private String status;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "cabinet_id")
    private Cabinet cabinet;
}
