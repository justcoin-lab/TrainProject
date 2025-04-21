package rousing.traintrip.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import rousing.traintrip.domain.Bookmark;

import java.util.List;
import java.util.Optional;

public interface BookmarkRepository extends JpaRepository<Bookmark, Long> {
    List<Bookmark> findByUserId(Long userId);
    Optional<Bookmark> findByUserIdAndTrainId(Long userId, Long trainId);
    boolean existsByUserIdAndTrainId(Long userId, Long trainId);
}
