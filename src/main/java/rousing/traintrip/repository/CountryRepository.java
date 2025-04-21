package rousing.traintrip.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import rousing.traintrip.domain.Country;

public interface CountryRepository extends JpaRepository<Country, Long> {
}
