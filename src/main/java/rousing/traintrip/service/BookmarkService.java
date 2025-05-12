package rousing.traintrip.service;

import rousing.traintrip.exception.ResourceNotFoundException;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import rousing.traintrip.domain.Bookmark;
import rousing.traintrip.domain.Train;
import rousing.traintrip.domain.User;
import rousing.traintrip.dto.BookmarkDto;
import rousing.traintrip.repository.BookmarkRepository;
import rousing.traintrip.repository.TrainRepository;
import rousing.traintrip.repository.UserRepository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class BookmarkService {
    private final BookmarkRepository bookmarkRepository;
    private final UserRepository userRepository;
    private final TrainRepository trainRepository;

    // 특정 사용자의 모든 북마크 정보를 조회합니다.
    @Transactional(readOnly = true)
    public List<BookmarkDto> getBookmarksByUserId(Long userId) {
        return bookmarkRepository.findByUserId(userId).stream()
                .map(BookmarkDto::fromEntity)
                .collect(Collectors.toList());
    }

    // 사용자의 기차여행 북마크 상태
    // 이미 북마크가 존재하면 삭제하고, 없으면 새로 생성합니다.
    @Transactional
    public void toggleBookmark(Long userId, Long trainId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));

        Train train = trainRepository.findById(trainId)
                .orElseThrow(() -> new ResourceNotFoundException("Train", "id", trainId));

        Optional<Bookmark> existingBookmark = bookmarkRepository.findByUserIdAndTrainId(userId, trainId);

        if (existingBookmark.isPresent()) {
            bookmarkRepository.delete(existingBookmark.get());
        } else {
            Bookmark bookmark = new Bookmark(user, train);
            bookmarkRepository.save(bookmark);
        }
    }
}
