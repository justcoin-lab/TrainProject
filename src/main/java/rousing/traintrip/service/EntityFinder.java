package rousing.traintrip.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import rousing.traintrip.domain.Country;
import rousing.traintrip.domain.Region;
import rousing.traintrip.domain.Train;
import rousing.traintrip.domain.User;
import rousing.traintrip.exception.ResourceNotFoundException;
import rousing.traintrip.repository.CountryRepository;
import rousing.traintrip.repository.RegionRepository;
import rousing.traintrip.repository.TrainRepository;
import rousing.traintrip.repository.UserRepository;

/**
 * 엔티티 조회를 중앙화한 유틸리티 컴포넌트입니다.
 * 여러 서비스에서 공통적으로 사용되는 엔티티 조회 로직을 통합 관리합니다.
 */
@Component
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class EntityFinder {
    
    private final UserRepository userRepository;
    private final TrainRepository trainRepository;
    private final RegionRepository regionRepository;
    private final CountryRepository countryRepository;
    
    /**
     * ID로 사용자를 조회합니다.
     * 
     * @param id 사용자 ID
     * @return 사용자 엔티티
     * @throws ResourceNotFoundException 사용자를 찾을 수 없는 경우
     */
    public User findUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", id));
    }
    
    /**
     * 사용자명으로 사용자를 조회합니다.
     * 
     * @param username 사용자명
     * @return 사용자 엔티티
     * @throws ResourceNotFoundException 사용자를 찾을 수 없는 경우
     */
    public User findUserByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User", "username", username));
    }
    
    /**
     * ID로 기차여행을 조회합니다.
     * 
     * @param id 기차여행 ID
     * @return 기차여행 엔티티
     * @throws ResourceNotFoundException 기차여행을 찾을 수 없는 경우
     */
    public Train findTrainById(Long id) {
        return trainRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Train", "id", id));
    }
    
    /**
     * ID로 지역을 조회합니다.
     * 
     * @param id 지역 ID
     * @return 지역 엔티티
     * @throws ResourceNotFoundException 지역을 찾을 수 없는 경우
     */
    public Region findRegionById(Long id) {
        return regionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Region", "id", id));
    }
    
    /**
     * ID로 국가를 조회합니다.
     * 
     * @param id 국가 ID
     * @return 국가 엔티티
     * @throws ResourceNotFoundException 국가를 찾을 수 없는 경우
     */
    public Country findCountryById(Long id) {
        return countryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Country", "id", id));
    }
    
    /**
     * 이름으로 국가를 조회합니다.
     * 
     * @param name 국가 이름
     * @return 국가 엔티티
     * @throws ResourceNotFoundException 국가를 찾을 수 없는 경우
     */
    public Country findCountryByName(String name) {
        return countryRepository.findByName(name)
                .orElseThrow(() -> new ResourceNotFoundException("Country", "name", name));
    }
}
