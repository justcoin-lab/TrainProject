package rousing.traintrip.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import rousing.traintrip.domain.Region;

import java.util.List;

public interface RegionRepository extends JpaRepository<Region, Long> {
    List<Region> findByCountryId(Long countryId);
}