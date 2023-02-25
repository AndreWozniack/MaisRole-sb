package br.pucpr.maisrolev2.rest.users;

import br.pucpr.maisrolev2.rest.Role;
import br.pucpr.maisrolev2.rest.reviews.Review;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.Set;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class User {
    @Id
    @GeneratedValue
    private Long id;
    @Column(unique = true, nullable = false)
    @Size(min = 5, max = 30)
    private String username;

    @Size(min = 8)
    private String password;
    @Enumerated(EnumType.STRING)
    @ElementCollection
    private Set<Role> roles;
    @OneToOne(cascade = CascadeType.ALL)
    @NotNull
    @JsonProperty
    private UserPersonalData personalData;
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private Set<Review> reviews = new HashSet<>();
}