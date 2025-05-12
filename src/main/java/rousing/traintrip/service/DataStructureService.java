package rousing.traintrip.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import rousing.traintrip.dto.CountryDto;
import rousing.traintrip.dto.RegionDto;
import rousing.traintrip.dto.TrainSummaryDto;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 데이터 구조화 서비스
 * 컨트롤러에서 공통적으로 사용하는 데이터 구조화 기능을 제공합니다.
 * HierarchyService와 통합되었습니다.
 */
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class DataStructureService {

    private final CountryService countryService;
    private final RegionService regionService;
    private final TrainService trainService;

    /**
     * 국가별 지역 및 기차여행 데이터를 계층적으로 구성합니다.
     * 
     * @return 국가 이름을 키로 하고 지역 및 기차여행 목록을 값으로 가지는 Map
     */
    public Map<String, List<Map<String, Object>>> getCountryRegionTrainHierarchy() {
        List<CountryDto> countries = countryService.getAllCountries();
        Map<String, List<Map<String, Object>>> result = new HashMap<>();

        for (CountryDto country : countries) {
            List<Map<String, Object>> regionDataList = buildRegionDataList(country.getId());
            result.put(country.getName(), regionDataList);
        }

        return result;
    }
    
    /**
     * 지역별 데이터 목록을 구성합니다.
     * 
     * @param countryId 국가 ID
     * @return 지역 데이터 목록
     */
    private List<Map<String, Object>> buildRegionDataList(Long countryId) {
        List<RegionDto> regions = regionService.getRegionsByCountryId(countryId);
        List<Map<String, Object>> regionDataList = new ArrayList<>();
        
        for (RegionDto region : regions) {
            Map<String, Object> regionData = new HashMap<>();
            regionData.put("region", region);
            regionData.put("trains", trainService.getTrainsByRegionId(region.getId()));
            regionDataList.add(regionData);
        }
        
        return regionDataList;
    }
    
    /**
     * 국가별 지역 및 기차여행 데이터를 계층적으로 구성하고, 지역 데이터의 타입을 Map으로 통일합니다.
     * 주로 관리자 페이지에서 사용합니다.
     * 
     * @return 국가 이름을 키로 하고 지역 및 기차여행 목록을 값으로 가지는 Map
     */
    public Map<String, List<Map<String, Object>>> getCountriesRegionsTrainsForAdmin() {
        List<CountryDto> countries = countryService.getAllCountries();
        Map<String, List<Map<String, Object>>> result = new HashMap<>();

        for (CountryDto country : countries) {
            List<Map<String, Object>> regionDataList = new ArrayList<>();
            
            // 해당 국가에 속한 모든 지역 정보 가져오기
            List<RegionDto> regions = country.getRegions();
            if (regions != null && !regions.isEmpty()) {
                for (RegionDto region : regions) {
                    Map<String, Object> regionData = new HashMap<>();
                    regionData.put("region", region);
                    regionData.put("trains", trainService.getTrainsByRegionId(region.getId()));
                    regionDataList.add(regionData);
                }
            }
            
            result.put(country.getName(), regionDataList);
        }

        return result;
    }
    
    /**
     * 모든 국가 목록을 조회합니다.
     * @return 국가 목록
     */
    public List<CountryDto> getAllCountries() {
        return countryService.getAllCountries();
    }
    
    /**
     * 특정 지역에 속한 기차여행 목록을 조회합니다.
     * @param regionId 지역 ID
     * @return 기차여행 목록
     */
    public List<TrainSummaryDto> getTrainsByRegionId(Long regionId) {
        return trainService.getTrainsByRegionId(regionId);
    }
    
    /**
     * 특정 국가에 속한 모든 지역 목록을 조회합니다.
     * @param countryId 국가 ID
     * @return 지역 목록
     */
    public List<RegionDto> getRegionsByCountryId(Long countryId) {
        return regionService.getRegionsByCountryId(countryId);
    }
}