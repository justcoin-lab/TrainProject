package rousing.traintrip.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import rousing.traintrip.domain.User;
import rousing.traintrip.dto.UserRegisterDto;
import rousing.traintrip.dto.UserSummaryDto;
import rousing.traintrip.exception.BadRequestException;
import rousing.traintrip.exception.DuplicateResourceException;
import rousing.traintrip.exception.ResourceNotFoundException;
import rousing.traintrip.mapper.UserMapper;
import rousing.traintrip.repository.UserRepository;

import java.util.List;

/**
 * 사용자 관리를 담당하는 서비스 클래스입니다.
 */
@Service
@Transactional
@RequiredArgsConstructor
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;

    /**
     * Spring Security 인증을 위한 사용자 정보를 로드합니다.
     *
     * @param username 사용자명
     * @return 인증 정보
     * @throws UsernameNotFoundException 사용자를 찾을 수 없는 경우
     */
    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = findUserByUsername(username);

        List<GrantedAuthority> authorities = List.of(
                new SimpleGrantedAuthority("ROLE_" + user.getRole().name())
        );

        return new org.springframework.security.core.userdetails.User(
                user.getUsername(),
                user.getPassword(),
                authorities
        );
    }

    /**
     * 사용자명으로 사용자를 찾습니다.
     *
     * @param username 사용자명
     * @return 사용자 엔티티
     * @throws UsernameNotFoundException 사용자를 찾을 수 없는 경우
     */
    @Transactional(readOnly = true)
    private User findUserByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다: " + username));
    }

    /**
     * 새로운 사용자를 등록합니다.
     *
     * @param dto 사용자 등록 DTO
     * @return 등록된 사용자 요약 정보
     */
    @Transactional
    public UserSummaryDto register(UserRegisterDto dto) {
        validateNewUser(dto);

        String encodedPassword = passwordEncoder.encode(dto.getPassword());
        User user = userMapper.toEntity(dto, encodedPassword);
        
        User savedUser = userRepository.save(user);
        return userMapper.toSummaryDto(savedUser);
    }

    /**
     * 새 사용자 등록 시 유효성을 검증합니다.
     *
     * @param dto 사용자 등록 DTO
     * @throws DuplicateResourceException 사용자명 또는 이메일이 이미 존재하는 경우
     */
    private void validateNewUser(UserRegisterDto dto) {
        if (userRepository.existsByUsername(dto.getUsername())) {
            throw new DuplicateResourceException("User", "username", dto.getUsername());
        }

        if (userRepository.existsByEmail(dto.getEmail())) {
            throw new DuplicateResourceException("User", "email", dto.getEmail());
        }
    }

    /**
     * 사용자명으로 현재 사용자 정보를 조회합니다.
     *
     * @param username 사용자명
     * @return 사용자 엔티티
     */
    @Transactional(readOnly = true)
    public User getCurrentUser(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User", "username", username));
    }

    /**
     * 모든 사용자 목록을 요약 정보로 조회합니다.
     *
     * @return 사용자 요약 정보 목록
     */
    @Transactional(readOnly = true)
    public List<UserSummaryDto> getAllUsers() {
        List<User> users = userRepository.findAll();
        return userMapper.toSummaryDtoList(users);
    }

    /**
     * ID로 사용자를 조회합니다.
     *
     * @param id 사용자 ID
     * @return 사용자 엔티티
     */
    @Transactional(readOnly = true)
    public User getUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", id));
    }

    /**
     * 사용자 역할을 업데이트합니다.
     *
     * @param id   사용자 ID
     * @param role 새 역할
     * @return 업데이트된 사용자 요약 정보
     */
    @Transactional
    public UserSummaryDto updateUserRole(Long id, User.Role role) {
        User user = getUserById(id);
        user.updateRole(role);
        User updatedUser = userRepository.save(user);
        return userMapper.toSummaryDto(updatedUser);
    }

    /**
     * 사용자를 삭제합니다.
     *
     * @param id 사용자 ID
     */
    @Transactional
    public void deleteUser(Long id) {
        if (!userRepository.existsById(id)) {
            throw new ResourceNotFoundException("User", "id", id);
        }
        userRepository.deleteById(id);
    }

    /**
     * 닉네임을 변경합니다.
     *
     * @param userId      사용자 ID
     * @param newNickname 새 닉네임
     * @return 업데이트된 사용자 요약 정보
     */
    @Transactional
    public UserSummaryDto updateNickname(Long userId, String newNickname) {
        User user = getUserById(userId);
        user.updateNickname(newNickname);
        User updatedUser = userRepository.save(user);
        return userMapper.toSummaryDto(updatedUser);
    }

    /**
     * 비밀번호를 변경합니다.
     *
     * @param userId          사용자 ID
     * @param currentPassword 현재 비밀번호
     * @param newPassword     새 비밀번호
     * @return 업데이트된 사용자 요약 정보
     */
    @Transactional
    public UserSummaryDto updatePassword(Long userId, String currentPassword, String newPassword) {
        User user = getUserById(userId);

        // 현재 비밀번호 확인
        if (!passwordEncoder.matches(currentPassword, user.getPassword())) {
            throw new BadRequestException("현재 비밀번호가 일치하지 않습니다.");
        }

        // 새 비밀번호 암호화 후 저장
        user.updatePassword(passwordEncoder.encode(newPassword));
        User updatedUser = userRepository.save(user);
        return userMapper.toSummaryDto(updatedUser);
    }
}
