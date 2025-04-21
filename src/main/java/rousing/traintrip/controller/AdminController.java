package rousing.traintrip.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import rousing.traintrip.dto.TrainDetailDto;
import rousing.traintrip.dto.TrainSummaryDto;
import rousing.traintrip.dto.TrainUpsertDto;
import rousing.traintrip.service.CountryService;
import rousing.traintrip.service.RegionService;
import rousing.traintrip.service.TrainService;

import java.util.List;

@Controller
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {
    private final TrainService trainService;
    private final RegionService regionService;
    private final CountryService countryService;

    // 모든 기차여행 목록 불러오기
    @GetMapping("/trains")
    public String listTrains(Model model) {
        List<TrainSummaryDto> trains = trainService.getAllTrains();
        model.addAttribute("trains", trains);
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

    @DeleteMapping("/trains/{id}")
    public String deleteTrain(@PathVariable Long id) {
        trainService.deleteTrain(id);
        return "redirect:/admin/trains";
    }
}