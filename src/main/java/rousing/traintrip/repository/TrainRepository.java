package rousing.traintrip.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import rousing.traintrip.domain.Train;

import java.util.List;

public interface TrainRepository extends JpaRepository<Train, Long> {
    List<Train> findByRegionId(Long regionId);
}