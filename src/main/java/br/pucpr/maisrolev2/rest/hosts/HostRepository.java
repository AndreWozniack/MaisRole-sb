package br.pucpr.maisrolev2.rest.hosts;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface HostRepository extends JpaRepository<Host, Long> {
    Optional<Host> findHostById(Long id);
    Optional<Host> findHostByContact_Email(String email);
}
