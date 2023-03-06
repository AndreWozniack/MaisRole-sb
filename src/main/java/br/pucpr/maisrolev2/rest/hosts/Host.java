package br.pucpr.maisrolev2.rest.hosts;

import br.pucpr.maisrolev2.rest.hosts.Agenda.Agenda;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Host {
    @Id @GeneratedValue
    private Long id;
    private String password;

    @NotBlank
    private String hostName;

    @Enumerated(EnumType.STRING)
    @ElementCollection
    private Set<HostType> hostTypes;

    @OneToOne(cascade = CascadeType.ALL)
    private Contact contact;

    @OneToOne(cascade = CascadeType.ALL)
    private Address address;


    private Agenda agenda;

    //private Agenda agenda;
    //private Event event

}
