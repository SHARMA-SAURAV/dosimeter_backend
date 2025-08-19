package com.cdac.dosimeter_visualization.model;

//package com.cdac.dosimeter_visualization.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = jakarta.persistence.GenerationType.IDENTITY)
    private int id;
    private String name;
    private String password;
    private String email;
    //    private String role; // e.g., "USER", "ADMIN"
//    private boolean enabled; // Indicates if the user account is active
    private String sex;
    private String phoneNo;
    @JsonIgnore
    @OneToMany(mappedBy = "user")
    private List<DosimeterAssignment> assignments;

}
