package rousing.traintrip.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import rousing.traintrip.domain.User;
import rousing.traintrip.dto.CountryDto;
import rousing.traintrip.dto.RegionDto;
import rousing.traintrip.dto.TrainDetailDto;
import rousing.traintrip.dto.TrainSummaryDto;
import rousing.traintrip.dto.TrainUpsertDto;
import rousing.traintrip.service.CountryService;
import rousing.traintrip.service.RegionService;
import rousing.traintrip.service.TrainService;
import rousing.traintrip.service.UserService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {
    private final TrainService trainService;
    private final RegionService regionService;
    private final CountryService countryService;
    private final UserService userService;
    
    // 관리자 대시보드
    @GetMapping
    public String adminDashboard() {
        return "admin/index";
    }

    // 모든 기차여행 목록 불러오기 (지역별로 분류)
    @GetMapping("/trains")
    public String listTrains(Model model) {
        List<TrainSummaryDto> trains = trainService.getAllTrains();
        model.addAttribute("trains", trains);
        
        // 국가별, 지역별로 기차여행 데이터 구성
        List<CountryDto> countries = countryService.getAllCountries();
        model.addAttribute("countries", countries);

        // 각 국가의 모든 지역과 기차여행 정보를 가져옵니다.
        Map<String, List<Map<String, Object>>> countriesData = new HashMap<>();

        for (CountryDto country : countries) {
            List<Map<String, Object>> regionWithTrains = new ArrayList<>();
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
        return "admin/trains";
    }

    @GetMapping("/trains/new")
    public String newTrainForm(Model model) {
        model.addAttribute("train", new TrainUpsertDto());
        model.addAttribute("countries", countryService.getAllCountries());
        return "admin/train-form";
    }

    // 기차여행 수정
    @GetMapping("/trains/edit/{id}")
    public String editTrainForm(@PathVariable Long id, Model model) {
        TrainDetailDto train = trainService.getTrainById(id, null);
        TrainUpsertDto upsertDto = new TrainUpsertDto(
                train.getId(),
                train.getName(),
                train.getDescription(),
                train.getImageUrl(),
                train.getOperatingDays(),
                train.getFare(),
                train.getRouteImageUrl(),
                train.getBookingUrl(),
                train.getRegionId()
        );

        model.addAttribute("train", upsertDto);
        model.addAttribute("countries", countryService.getAllCountries());
        return "admin/train-form";
    }

    @PostMapping("/trains")
    public String saveTrain(@Valid TrainUpsertDto trainDto, BindingResult result) {
        if (result.hasErrors()) {
            return "admin/train-form";
        }

        if (trainDto.getId() == null) {
            trainService.createTrain(trainDto);
        } else {
            trainService.updateTrain(trainDto);
        }

        return "redirect:/admin/trains";
    }

    @PostMapping("/trains/delete/{id}")
    public String deleteTrain(@PathVariable Long id) {
        trainService.deleteTrain(id);
        return "redirect:/admin/trains";
    }
    
    // ===== 회원 관리 기능 =====
    
    // 모든 회원 목록 불러오기
    @GetMapping("/users")
    public String listUsers(Model model) {
        List<User> users = userService.getAllUsers();
        model.addAttribute("users", users);
        return "admin/users";
    }
    
    // 회원 상세 정보 보기
    @GetMapping("/users/{id}")
    public String viewUser(@PathVariable Long id, Model model) {
        User user = userService.getUserById(id);
        model.addAttribute("user", user);
        model.addAttribute("roles", User.Role.values());
        return "admin/user-detail";
    }
    
    // 회원 역할 업데이트
    @PostMapping("/users/{id}/role")
    public String updateUserRole(@PathVariable Long id, @RequestParam User.Role role) {
        userService.updateUserRole(id, role);
        return "redirect:/admin/users/" + id;
    }
    
    // 회원 삭제
    @PostMapping("/users/delete/{id}")
    public String deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return "redirect:/admin/users";
    }
}