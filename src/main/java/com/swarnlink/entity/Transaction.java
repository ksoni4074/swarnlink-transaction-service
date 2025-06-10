package com.swarnlink.entity;

import com.swarnlink.entity.enums.TransactionDirection;
import com.swarnlink.entity.enums.TransactionType;
import com.swarnlink.entity.enums.UnitType;
import jakarta.annotation.Nonnull;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "transactions")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Transaction extends AuditEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Nonnull
    private Long userId;

    @ManyToOne(optional = false)
    private Party party;

    @Enumerated(EnumType.STRING)
    private TransactionType type;

    @Enumerated(EnumType.STRING)
    private TransactionDirection direction;

    @Column(nullable = false)
    private BigDecimal totalAmount;

    @Enumerated(EnumType.STRING)
    private UnitType unit;

    private boolean isSettled = false;

    @Column(nullable = false)
    private LocalDate tentativeCloseDate;

    private String description;
}