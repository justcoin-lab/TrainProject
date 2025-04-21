package rousing.traintrip.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import rousing.traintrip.dto.CountryDto;
import rousing.traintrip.dto.RegionDto;
import rousing.traintrip.service.CountryService;
import rousing.traintrip.service.RegionService;
import rousing.traintrip.service.TrainService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequiredArgsConstructor
public class HomeController {
    private final CountryService countryService;
    private final RegionService regionService;
    private final TrainService trainService;

    @GetMapping("/")
    public String home(Model model) {
        List<CountryDto> countries = countryService.getAllCountries();
        model.addAttribute("countries", countries);

        // 한국과 일본의 모든 지역과 기차여행 정보를 가져옵니다.
        Map<String, List<Object>> countriesData = new HashMap<>();

        for (CountryDto country : countries) {
            List<Object> regionWithTrains = new ArrayList<>();
            List<RegionDto> regions = regionService.getRegionsByCountryId(country.getId());

            for (RegionDto region : regions) {
                Map<String, Object> regionData = new HashMap<>();
                regionData.put("region", region);
                regionData.put("trains", trainService.getTrainsByRegionId(region.getId()));
                regionWithTrains.add(regionData);
            }

            countriesData.put(country.getName(), regionWithTrains);
        }

        model.addAttribute("countriesData", countriesData);
        return "index";
    }
}
