package br.pucpr.maisrolev2.rest.hosts;

import br.pucpr.maisrolev2.lib.exception.ExceptionHandlers;
import br.pucpr.maisrolev2.lib.exception.NotFoundException;
import br.pucpr.maisrolev2.lib.security.JWT;
import br.pucpr.maisrolev2.rest.hosts.requests.HostLoginRequest;
import br.pucpr.maisrolev2.rest.hosts.responses.HostLoginResponse;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/hosts")
public class HostController {
    private final HostService service;
    private final ExceptionHandlers exceptionHandlers;
    private final JWT jwt;

    public HostController(HostService service, ExceptionHandlers exceptionHandlers, JWT jwt) {this.service = service;
        this.exceptionHandlers = exceptionHandlers;
        this.jwt = jwt;
    }

    @GetMapping("{id}")
    @Transactional
    public ResponseEntity<Object> searchHost(@PathVariable(value = "id") Long id){
        try {
            return ResponseEntity.ok(service.getHost(id));
        } catch (NotFoundException e) {
            return exceptionHandlers.handleNotFoundException(e);
        }
    }
    @GetMapping("/all")
    @Transactional
    public ResponseEntity<Object> showAllHosts() {
        try {
            return ResponseEntity.ok(service.getAllHosts());
        } catch (NotFoundException e) {
            return exceptionHandlers.handleNotFoundException(e);
        }
    }

    @PostMapping("/register")
    @Transactional
    public ResponseEntity<Object> add(@Valid @RequestBody Host host) {
            service.add(host);
            return new ResponseEntity<>(host, HttpStatus.CREATED);
    }


    @PostMapping("/login")
    public ResponseEntity<HostLoginResponse> login(@RequestBody HostLoginRequest req) {
            var host = service.logHost(req.getEmail(), req.getPassword());
            var token = jwt.createHostToken(host);

            return ResponseEntity.ok( new HostLoginResponse(token, host));
    }


}
