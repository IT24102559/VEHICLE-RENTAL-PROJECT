package org.example.rentalsytsem.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Table(name = "vehicles")
public class vehicle {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "brand_name", length = 50, nullable = false)
    @JsonProperty("brandName")
    private String brandName;

    @Column(name = "model_name", length = 50, nullable = false)
    @JsonProperty("modelName")
    private String modelName;

    @Column(name = "vehicle_type", length = 50, nullable = false)
    @JsonProperty("vehicleType")
    private String vehicleType;

    @Column(name = "is_available")
    @JsonProperty("available")
    private boolean isAvailable;

    @Column(name = "rental_price_per_day", nullable = false)
    @JsonProperty("rentalPricePerDay")
    private double rentalPricePerDay;

    @Column(name = "image_url", length = 255)
    @JsonProperty("imageUrl")
    private String imageUrl;

    @Column(name = "status", length = 32)
    @JsonProperty("status")
    private String status; // e.g. PENDING, APPROVED, REJECTED

}
