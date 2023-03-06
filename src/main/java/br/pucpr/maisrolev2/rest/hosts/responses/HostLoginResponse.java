package br.pucpr.maisrolev2.rest.hosts.responses;

import br.pucpr.maisrolev2.rest.hosts.Host;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data @AllArgsConstructor
public class HostLoginResponse {
    private String token;
    private String email;
    private String name;
    private String password;

    public HostLoginResponse(String token, Host host){
        this.email = host.getContact().getEmail();
        this.name = host.getHostName();
        this.password = host.getPassword();
        this.token = token;
    }
}
