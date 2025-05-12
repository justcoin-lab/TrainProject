package rousing.traintrip.mapper;

import org.springframework.stereotype.Component;
import rousing.traintrip.domain.User;
import rousing.traintrip.dto.UserRegisterDto;
import rousing.traintrip.dto.UserSummaryDto;

import java.util.List;
import java.util.stream.Collectors;

/**
 * User 엔티티와 DTO 간의 변환을 담당하는 매퍼 클래스입니다.
 */
@Component
public class UserMapper {

    /**
     * User 엔티티를 UserSummaryDto로 변환합니다.
     *
     * @param user 사용자 엔티티
     * @return 사용자 요약 DTO
     */
    public UserSummaryDto toSummaryDto(User user) {
        return UserSummaryDto.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .nickname(user.getNickname())
                .role(user.getRole().name())
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .build();
    }

    /**
     * User 엔티티 리스트를 UserSummaryDto 리스트로 변환합니다.
     *
     * @param users 사용자 엔티티 리스트
     * @return 사용자 요약 DTO 리스트
     */
    public List<UserSummaryDto> toSummaryDtoList(List<User> users) {
        return users.stream()
                .map(this::toSummaryDto)
                .collect(Collectors.toList());
    }

    /**
     * UserRegisterDto로부터 User 엔티티를 생성합니다.
     * 비밀번호 암호화는 서비스 레이어에서 처리됩니다.
     *
     * @param dto           사용자 등록 DTO
     * @param encodedPassword 암호화된 비밀번호
     * @return 사용자 엔티티
     */
    public User toEntity(UserRegisterDto dto, String encodedPassword) {
        return User.builder()
                .username(dto.getUsername())
                .password(encodedPassword)
                .email(dto.getEmail())
                .nickname(dto.getUsername())  // 기본 닉네임은 사용자명으로 설정
                .role(User.Role.USER)         // 기본 역할은 USER
                .build();
    }
}
