package br.pucpr.maisrolev2.rest.hosts;

import jakarta.persistence.*;
import lombok.Data;


@Entity @Data
public class Address {
    @Id @GeneratedValue
    private Long id;
    private String street;
    private String number;
    private String CEP;
    private String district;
    private String city;
    private String state;

}
