package com.swarnlink.entity;

import jakarta.annotation.Nonnull;
import jakarta.persistence.*;
import lombok.*;
import java.io.Serializable;

@Entity
@Table(name = "parties")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Party extends AuditEntity implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Nonnull
    private Long userId;

    private String name;

    @Column(nullable = false)
    private String mobileNumber;

    private String address;
}