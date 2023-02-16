package br.pucpr.maisrolev2.rest.reviews;

import br.pucpr.maisrolev2.rest.users.User;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

@Entity
@NamedQuery(
        name="Review.findAllByUserId",
        query="SELECT r FROM Review r" +
                " JOIN r.user u" +
                " WHERE u = :id" +
                " ORDER BY r.id"
)
public class Review {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
    private LocalDateTime postDate = LocalDateTime.now();
    @NotNull
    private Integer rating;
    private String text;

}
