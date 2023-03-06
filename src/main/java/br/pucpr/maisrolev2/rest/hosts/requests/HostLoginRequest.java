package br.pucpr.maisrolev2.rest.hosts.requests;

import lombok.Data;

@Data
public class HostLoginRequest {
    private String email;
    private String password;
}
