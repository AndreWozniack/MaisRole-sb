package br.pucpr.maisrolev2.rest.users;

import br.pucpr.maisrolev2.lib.exception.NotFoundException;
import br.pucpr.maisrolev2.rest.reviews.Review;
import br.pucpr.maisrolev2.rest.reviews.ReviewRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {
    @Autowired
    private final UserRepository userRepository;
    @Autowired
    private final ReviewRepository reviewRepository;
    public UserService(UserRepository userRepository, ReviewRepository reviewRepository) {
        this.userRepository = userRepository;
        this.reviewRepository = reviewRepository;
    }

    public User getUser(Long id) {
        return userRepository.findById(id).orElseThrow(() -> new NotFoundException(id));
    }

    public List<User> getAllUsers() {
        List<User> users = userRepository.findAll();
        if (users.isEmpty()) throw new NotFoundException("No users registered");
        return users;
    }

    public List<Review> getReviewsByUser(Long id) {
        List<Review> reviews = reviewRepository.findAllByUserId(id);

        if (reviews.isEmpty()) throw new NotFoundException(id, "User has no reviews posted.");
        return reviews;
    }

    @Transactional
    public User add(User user) {return userRepository.save(user);}


    @Transactional
    public void update(Long id, User user) {
        var existingUser = userRepository.findById(id);
        if (existingUser.isPresent()) {
            var updated = existingUser.get();
            updated.setUsername(user.getUsername());
            updated.setPassword(user.getPassword());
            updated.setRoles(user.getRoles());
            updated.setPersonalData(user.getPersonalData());
            updated.setReviews(user.getReviews());

            userRepository.save(updated);
        }
    }



    @Transactional
    public void deleteUser(Long id) {
        if (!userRepository.existsById(id)) throw new NotFoundException(id);
        userRepository.deleteById(id);
    }
}
