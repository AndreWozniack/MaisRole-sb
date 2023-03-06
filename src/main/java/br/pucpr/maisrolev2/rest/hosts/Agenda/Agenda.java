package br.pucpr.maisrolev2.rest.hosts.Agenda;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.Data;

import java.util.Set;

@Entity @Data
public class Agenda {
    @Id @GeneratedValue
    private Long id;

    private Set<WeekDays> weekDaysSet;
}
