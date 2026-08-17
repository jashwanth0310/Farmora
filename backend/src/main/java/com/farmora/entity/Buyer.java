package com.farmora.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "buyers")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Buyer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    private String businessName;
    private String businessType; // Restaurant, Hotel, Retailer, Wholesaler, Institution

    private String deliveryLocation;
    private Double deliveryLatitude;
    private Double deliveryLongitude;

    @Builder.Default
    private Boolean verified = false;
}
