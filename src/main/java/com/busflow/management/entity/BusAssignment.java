package com.busflow.management.entity;

import com.busflow.management.dto.IncomeResponseDTO;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BusAssignment extends BaseEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "bus_id", nullable = false)
    private Bus bus;

    @ManyToOne
    @JoinColumn(name = "assigned_by_id")
    private User assignedBy; // The owner who assigned

    @Column(nullable = false)
    private Boolean isActive = true; //can revoke the access

    private LocalDateTime assignedDate;
    private LocalDateTime revokedDate;
}
