package com.chssr.atelem.services.specifications;

import com.chssr.atelem.models.Device;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

public class DeviceSpecifications {

    public static Specification<Device> filterByParams(String serialNumber, String manufacturer, String status) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (serialNumber != null && !serialNumber.isEmpty()) {
                predicates.add(criteriaBuilder.like(
                        criteriaBuilder.lower(root.get("serialNumber")),
                        "%" + serialNumber.toLowerCase() + "%"));
            }

            if (manufacturer != null && !manufacturer.isEmpty()) {
                predicates.add(criteriaBuilder.like(
                        criteriaBuilder.lower(root.get("manufacturer")),
                        "%" + manufacturer.toLowerCase() + "%"));
            }

            if (status != null && !status.isEmpty()) {
                predicates.add(criteriaBuilder.equal(root.get("status"), status));
            }



            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}
