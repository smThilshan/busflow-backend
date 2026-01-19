package com.busflow.management.entity;

import com.busflow.management.enums.Role;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Table(name = "users")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String username;

    @Column(nullable = false)
    private String password;

    @Enumerated(EnumType.STRING)
    private Role role;

//    @ManyToOne
//    @JoinColumn(name = "bus_id")
//    private Bus bus; // Only for CONDUCTOR

    @OneToMany(mappedBy = "user")
    private List<BusAssignment>  BusAssignments;


    @OneToMany(mappedBy = "owner")
    private List<Bus> ownedBuses; // If OWNER role
}
