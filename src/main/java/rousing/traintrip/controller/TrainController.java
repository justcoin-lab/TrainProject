package rousing.traintrip.controller;

import lombok.RequiredArgsConstructor;
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

/**
 * 기차여행 정보와 관련된 뷰를 처리하는 컨트롤러입니다.
 */
@Controller
@RequestMapping("/trains")
@RequiredArgsConstructor
public class TrainController {

    private final TrainService trainService;
    private final UserService userService;

    /**
     * 기차여행 상세 정보 페이지를 제공합니다.
     *
     * @param id        기차여행 ID
     * @param model     모델 객체
     * @param principal 현재 인증된 사용자
     * @return 뷰 이름
     */
    @GetMapping("/{id}")
    public String getTrainDetail(@PathVariable Long id, Model model, Principal principal) {
        // 현재 사용자 정보 확인 (인증된 경우에만)
        Long userId = null;
        if (principal != null) {
            User user = userService.getCurrentUser(principal.getName());
            userId = user.getId();
        }

        // 기차여행 정보 조회 (북마크 상태 포함)
        TrainDetailDto train = trainService.getTrainById(id, userId);
        model.addAttribute("train", train);
        
        return "train/detail";
    }
}
