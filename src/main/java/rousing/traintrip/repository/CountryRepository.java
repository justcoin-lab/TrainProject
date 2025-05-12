package rousing.traintrip.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import rousing.traintrip.domain.Country;

import java.util.Optional;

public interface CountryRepository extends JpaRepository<Country, Long> {
    Optional<Country> findByName(String name);
}
