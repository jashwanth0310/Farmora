package com.farmora.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "farmers")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Farmer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    private String village;
    private String district;
    private String state;
    private Double farmSize;

    @Builder.Default
    private Boolean verified = false;

    @Builder.Default
    private Double reliabilityScore = 90.0;
}
