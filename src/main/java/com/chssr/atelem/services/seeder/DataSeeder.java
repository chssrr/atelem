package com.chssr.atelem.services.seeder;

import com.chssr.atelem.models.Cabinet;
import com.chssr.atelem.models.Device;
import com.chssr.atelem.repositories.CabinetRepository;
import com.chssr.atelem.repositories.DeviceRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;
import java.util.Random;

@Component
public class DataSeeder {

    private final CabinetRepository cabinetRepository;
    private final DeviceRepository deviceRepository;

    private final Random random = new Random();

    public DataSeeder(CabinetRepository cabinetRepository, DeviceRepository deviceRepository) {
        this.cabinetRepository = cabinetRepository;
        this.deviceRepository = deviceRepository;
    }

    @PostConstruct
    public void seed() {
        seedCabinets();
        seedDevices();
    }

    private void seedCabinets() {
        if (cabinetRepository.count() == 0) {
            String[] statuses = {"active", "in_repair", "decommissioned", "in_stock"};
            String[] locations = {"Цокольный этаж", "Первый этаж", "Второй этаж", "Склад", "Офис"};
            for (int i = 1; i <= 50; i++) {
                Cabinet cabinet = new Cabinet();
                cabinet.setName("Test Cabinet " + i);
                cabinet.setSerialNumber("CAB-" + String.format("%04d", i));
                cabinet.setManufacturer("Manufacturer " + ((i % 10) + 1));
                // Разные даты покупки в диапазоне 1-500 дней назад
                cabinet.setPurchaseDate(LocalDate.now().minusDays(random.nextInt(500) + 1));
                // Случайный статус из массива
                cabinet.setStatus(statuses[random.nextInt(statuses.length)]);
                // Случайное расположение из массива
                cabinet.setLocation(locations[random.nextInt(locations.length)]);
                cabinetRepository.save(cabinet);
            }
            System.out.println("Seeded 50 cabinets");
        }
    }

    private void seedDevices() {
        if (deviceRepository.count() == 0) {
            List<Cabinet> cabinets = cabinetRepository.findAll();
            String[] statuses = {"in_use", "in_stock", "maintenance", "retired"};
            for (int i = 1; i <= 200; i++) {
                Device device = new Device();
                device.setSerialNumber("D-" + String.format("%05d", i));
                device.setManufacturer("Device Maker " + ((i % 15) + 1));
                // Даты покупки 1-1000 дней назад
                device.setPurchaseDate(LocalDate.now().minusDays(random.nextInt(1000) + 1));
                device.setStatus(statuses[random.nextInt(statuses.length)]);
                // Случайное распределение устройств по шкафам, иногда без шкафа
                if (random.nextDouble() < 0.9) { // 90% с шкафом
                    device.setCabinet(cabinets.get(random.nextInt(cabinets.size())));
                } else {
                    device.setCabinet(null); // 10% без шкафа
                }
                deviceRepository.save(device);
            }
            System.out.println("Seeded 200 devices");
        }
    }
}
