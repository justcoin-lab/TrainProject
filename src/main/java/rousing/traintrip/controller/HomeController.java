package rousing.traintrip.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import rousing.traintrip.dto.CountryDto;
import rousing.traintrip.service.DataStructureService;

import java.util.List;
import java.util.Map;

/**
 * 홈 화면 관련 요청을 처리하는 컨트롤러입니다.
 */
@Controller
@RequiredArgsConstructor
public class HomeController {
    
    private final DataStructureService dataStructureService;

    /**
     * 홈 페이지를 제공합니다.
     *
     * @param model 모델 객체
     * @return 뷰 이름
     */
    @GetMapping("/")
    public String home(Model model) {
        // 국가 목록 제공
        List<CountryDto> countries = dataStructureService.getAllCountries();
        model.addAttribute("countries", countries);

        // 국가별 지역 및 기차여행 정보 제공
        Map<String, List<Map<String, Object>>> countriesData = dataStructureService.getCountryRegionTrainHierarchy();
        model.addAttribute("countriesData", countriesData);
        
        return "index";
    }
}