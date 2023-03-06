package br.pucpr.maisrolev2.rest.users;

import br.pucpr.maisrolev2.lib.exception.*;
import br.pucpr.maisrolev2.lib.security.JWT;
import br.pucpr.maisrolev2.rest.reviews.Review;
import br.pucpr.maisrolev2.rest.users.requests.UserLoginRequest;
import br.pucpr.maisrolev2.rest.users.responses.UserLoginResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.annotation.security.PermitAll;
import jakarta.annotation.security.RolesAllowed;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import jakarta.validation.constraints.PositiveOrZero;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/users")
public class UserController {
    private final UserService service;
    private final ExceptionHandlers exceptionHandler;
    private JWT jwt;
    public UserController(UserService service, ExceptionHandlers exceptionHandler, JWT jwt) {
        this.jwt = jwt;
        this.service = service;
        this.exceptionHandler = exceptionHandler;
    }

    @GetMapping("/{id}")
    @Transactional
    @Operation(
            summary = "Get user by ID",
            description = "Retrieves a user in the database using an unique ID.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "User found and retrieved.",
                            content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = User.class))}
                    ),
                    @ApiResponse(responseCode = "400", description = "Must inform a valid ID."),
                    @ApiResponse(responseCode = "404", description = "User not found.")
            }
    )
    public ResponseEntity<User> searchUser(@PathVariable(value = "id") @PositiveOrZero Long id) {
        return ResponseEntity.ok(service.getUser(id));
    }

    @GetMapping("/all")
    @Transactional
    @Operation(
            summary = "Get all user registered",
            description = "Retrieves an array with all registered users.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "All users found and retrieved.",
                            content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = User.class))}
                    ),
                    @ApiResponse(responseCode = "404", description = "No users found.")
            }
    )
    public ResponseEntity<List<User>> showAllUsers() {
        return ResponseEntity.ok(service.getAllUsers());
    }

    @GetMapping("/me/reviews")
    @Transactional
    @RolesAllowed({"USER", "ADMIN"})
    @SecurityRequirement(name = "AuthServer")
    @Operation(
            summary = "Get all reviews by user",
            description = "Retrieves an array of reviews made by user currently logged in.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "All reviews found and retrieved.",
                            content = {@Content(mediaType = "application/json",
                                    schema = @Schema(implementation = User.class))}
                    ),
                    @ApiResponse(responseCode = "400", description = "Error in token processing."),
                    @ApiResponse(responseCode = "401", description = "Must log in as user."),
                    @ApiResponse(responseCode = "404", description = "User not found")
            }
    )
    public ResponseEntity<List<Review>> getAllReviews(Authentication auth) {
        var reviews = service.getReviewsByUser((Long) auth.getCredentials());
        return ResponseEntity.ok(reviews);
    }

    @PostMapping("/register")
    @PermitAll
    @Operation(
            summary = "Add a user to database.",
            description = "Registers a new user with unique email and username.",
            responses = {
                    @ApiResponse(responseCode = "201", description = "User created successfully.",
                            content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = User.class))}),
                    @ApiResponse(responseCode = "400", description = "Invalid information provided."),
                    @ApiResponse(responseCode = "403", description = "User already logged in.")
            }
    )
    public ResponseEntity<Object> registerUser(@Valid @RequestBody User user, BindingResult bindingResult) {
        try {
            if (bindingResult.hasErrors()) {
                throw new MethodArgumentNotValidException(new MethodParameter(
                        service.getClass().getDeclaredMethod("add", User.class), 0),
                        bindingResult);
            }
            service.add(user);
            return new ResponseEntity<>(user, HttpStatus.CREATED);
        } catch (MethodArgumentNotValidException e) {
            return exceptionHandler.handleValidationException(e);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }
    @PostMapping("/login")
    @PermitAll
    @Operation(
            summary = "Logs user",
            description = "Logs the user using username and password.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "User logged in.",
                            content = {@Content(mediaType = "application/json",
                                    schema = @Schema(implementation = UserLoginRequest.class))}
                    ),
                    @ApiResponse(responseCode = "400", description = "Invalid data provided"),
                    @ApiResponse(responseCode = "401", description = "Incorrect username or password."),
                    @ApiResponse(responseCode = "403", description = "User already logged in."),
                    @ApiResponse(responseCode = "404", description = "Username does not exist.")
            }
    )
    public ResponseEntity<UserLoginResponse> login(@RequestBody UserLoginRequest req) {
        var user = service.logUser(req.getUsername(), req.getPassword());
        var token = jwt.createUserToken(user);

        return ResponseEntity.ok(new UserLoginResponse(token, user));
    }

    @PutMapping("/me/update")
    @Transactional
    @RolesAllowed({"USER", "ADMIN"})
    @SecurityRequirement(name = "AuthServer")
    @Operation(
            summary = "Updates user information.",
            description = "Receives new user data and updates current data with provided information.",
            responses = {
                    @ApiResponse(responseCode = "201", description = "User information updated.",
                            content = {@Content(mediaType = "application/json",
                                    schema = @Schema(implementation = User.class))}
                    ),
                    @ApiResponse(responseCode = "400", description = "Invalid data provided"),
                    @ApiResponse(responseCode = "401", description = "Must be logged as user."),
            }
    )
    public ResponseEntity<Object> update(@Valid @RequestBody User user, BindingResult bindingResult) {
        try {
            if (bindingResult.hasErrors()) {
                throw new MethodArgumentNotValidException(new MethodParameter(
                        service.getClass().getDeclaredMethod("add", User.class), 0),
                        bindingResult);
            }
            service.update(user.getId(), user);
            return new ResponseEntity<>(service.getUser(user.getId()), HttpStatus.CREATED);

        } catch (MethodArgumentNotValidException e) {
            return exceptionHandler.handleValidationException(e);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }

    @DeleteMapping("/me/delete")
    @Transactional
    @RolesAllowed({"USER", "ADMIN"})
    @SecurityRequirement(name = "AuthServer")
    @Operation(
            summary = "Deletes current user",
            description = "Deletes the account of currently logged in user.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "User deleted successfully."),
                    @ApiResponse(responseCode = "401", description = "Must be logged as user."),
            }

    )
    public ResponseEntity<Void> delete(@PathVariable("id") Long id) {
        service.deleteUser(id);
        return ResponseEntity.ok().build();
    }
}