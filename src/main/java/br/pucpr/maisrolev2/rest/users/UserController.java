package br.pucpr.maisrolev2.rest.users;

import br.pucpr.maisrolev2.lib.exception.AlreadyExistsException;
import br.pucpr.maisrolev2.lib.exception.ExceptionHandlers;
import br.pucpr.maisrolev2.lib.exception.NotFoundException;
import br.pucpr.maisrolev2.lib.exception.UnauthorizedException;
import br.pucpr.maisrolev2.rest.reviews.Review;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.annotation.security.PermitAll;
import jakarta.annotation.security.RolesAllowed;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users")
public class UserController {
    private final UserService service;
    private final ExceptionHandlers exceptionHandler;
    public UserController(UserService service, ExceptionHandlers exceptionHandler) {
        this.service = service;
        this.exceptionHandler = exceptionHandler;
    }

    @GetMapping("{id}")
    @Transactional
    public ResponseEntity<Object> searchUser(@PathVariable(value = "id") Long id) {
        try {
            return ResponseEntity.ok(service.getUser(id));
        } catch (NotFoundException e) {
            return exceptionHandler.handleNotFoundException(e);
        }
    }

    @GetMapping("/all")
    @Transactional
    public ResponseEntity<Object> showAllUsers() {
        try {
            return ResponseEntity.ok(service.getAllUsers());
        } catch (NotFoundException e) {
            return exceptionHandler.handleNotFoundException(e);
        }
    }

    @GetMapping("{id}/reviews")
    @Transactional
    @RolesAllowed("USER")
    @SecurityRequirement(name = "AuthServer")
    public ResponseEntity<Object> getAllReviews(@PathVariable("id") Long id) {
        try {
            return ResponseEntity.ok(service.getReviewsByUser(id));
        } catch (NotFoundException e) {
            return exceptionHandler.handleNotFoundException(e);
        }
    }

    @PostMapping("/register")
    @PermitAll
    public ResponseEntity<Object> registerUser(@RequestBody User user) {
        try {
            service.add(user);
            return new ResponseEntity<>(user, HttpStatus.CREATED);
        } catch (AlreadyExistsException e) {
            return exceptionHandler.handleAlreadyExistsException(e);
        }
    }

    @PostMapping("/login")
    @PermitAll
    public ResponseEntity<Object> login(@RequestBody String username, @RequestBody String password) {
        try {
            return ResponseEntity.ok(service.logUser(username, password));
        } catch (UnauthorizedException e) {
            return exceptionHandler.handleUnauthorizedException(e);
        }
    }

    @PutMapping("/update")
    @Transactional
    @RolesAllowed("USER")
    @SecurityRequirement(name = "AuthServer")
    public void update(@Valid @RequestBody User user) {
        service.update(user.getId(), user);
    }

    @DeleteMapping("{id}")
    @Transactional
    @RolesAllowed("USER")
    @SecurityRequirement(name = "AuthServer")
    public void delete(@PathVariable("id") Long id) {
        service.deleteUser(id);
    }
}