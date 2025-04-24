package rousing.traintrip.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import rousing.traintrip.domain.User;
import rousing.traintrip.dto.UserRegisterDto;
import rousing.traintrip.repository.UserRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsService {

    @Autowired private final UserRepository userRepository;
    @Autowired private final PasswordEncoder passwordEncoder;

    /**
     * Spring Security 인증을 위한 사용자 정보를 로드합니다.
     * 사용자명을 받아 해당 사용자의 인증 정보를 반환합니다.
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다: " + username));

        List<GrantedAuthority> authorities = List.of(new SimpleGrantedAuthority("ROLE_" + user.getRole().name()));

        return new org.springframework.security.core.userdetails.User(
                user.getUsername(),
                user.getPassword(),
                authorities
        );
    }

    // 새로운 사용자를 등록합니다.
    @Transactional
    public void register(UserRegisterDto dto) {
        if (userRepository.existsByUsername(dto.getUsername())) {
            throw new IllegalArgumentException("이미 사용 중인 사용자 이름입니다.");
        }

        if (userRepository.existsByEmail(dto.getEmail())) {
            throw new IllegalArgumentException("이미 사용 중인 이메일입니다.");
        }

        User user = User.builder()
                .username(dto.getUsername())
                .password(passwordEncoder.encode(dto.getPassword()))
                .email(dto.getEmail())
                .nickname(dto.getUsername())
                .role(User.Role.USER)
                .build();

        userRepository.save(user);
    }

    // 사용자명으로 현재 사용자 정보를 조회합니다.
    public User getCurrentUser(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다: " + username));
    }
    
    // 모든 사용자 목록을 조회합니다.
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }
    
    // ID로 사용자를 조회합니다.
    public User getUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다: ID=" + id));
    }
    
    // 사용자 역할을 업데이트합니다.
    @Transactional
    public void updateUserRole(Long id, User.Role role) {
        User user = getUserById(id);
        user.updateRole(role);
        userRepository.save(user);
    }
    
    // 사용자를 삭제합니다.
    @Transactional
    public void deleteUser(Long id) {
        if (!userRepository.existsById(id)) {
            throw new IllegalArgumentException("사용자를 찾을 수 없습니다: ID=" + id);
        }
        userRepository.deleteById(id);
    }
}
