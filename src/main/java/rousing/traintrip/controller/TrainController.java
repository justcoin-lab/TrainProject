package rousing.traintrip.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import rousing.traintrip.domain.User;
import rousing.traintrip.dto.TrainDetailDto;
import rousing.traintrip.service.TrainService;
import rousing.traintrip.service.UserService;

import java.security.Principal;

@Controller
@RequestMapping("/trains")
@RequiredArgsConstructor
public class TrainController {

    @Autowired private final TrainService trainService;
    @Autowired private final UserService userService;

    @GetMapping("/{id}")
    public String getTrainDetail(@PathVariable Long id, Model model, Principal principal) {
        Long userId = null;
        if (principal != null) {
            User user = userService.getCurrentUser(principal.getName());
            userId = user.getId();
        }

        TrainDetailDto train = trainService.getTrainById(id, userId);
        model.addAttribute("train", train);
        return "train/detail";
    }
}