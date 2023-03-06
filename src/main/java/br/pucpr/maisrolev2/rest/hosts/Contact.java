package br.pucpr.maisrolev2.rest.hosts;

import jakarta.persistence.*;
import lombok.Data;


@Entity @Data
public class Contact {
    @Id @GeneratedValue
    private Long id;
    private String insta;
    private String face;
    private String mobile;
    private String phone;
    private String email;

}
