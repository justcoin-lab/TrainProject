package rousing.traintrip.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import rousing.traintrip.community.dto.BoardDTO;
import rousing.traintrip.community.service.BoardService;
import rousing.traintrip.domain.User;
import rousing.traintrip.dto.*;
import rousing.traintrip.mapper.UserMapper;
import rousing.traintrip.service.*;

import java.util.List;
import java.util.Map;

/**
 * 관리자 기능을 처리하는 컨트롤러입니다.
 */
@Slf4j
@Controller
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {
    private final TrainService trainService;
    private final RegionService regionService;
    private final CountryService countryService;
    private final UserService userService;
    private final DataStructureService dataStructureService;
    private final BoardService boardService;
    private final UserMapper userMapper;

    /**
     * 관리자 대시보드 페이지를 제공합니다.
     *
     * @return 뷰 이름
     */
    @GetMapping
    public String adminDashboard() {
        return "admin/index";
    }

    /**
     * 기차여행 관리 페이지를 제공합니다.
     *
     * @param model 모델 객체
     * @return 뷰 이름
     */
    @GetMapping("/trains")
    public String listTrains(Model model) {
        // 모든 기차여행 정보 조회
        List<TrainSummaryDto> trains = trainService.getAllTrains();
        model.addAttribute("trains", trains);

        // 국가 목록 제공
        List<CountryDto> countries = countryService.getAllCountries();
        model.addAttribute("countries", countries);

        // 국가별 지역 및 기차여행 정보 제공
        Map<String, List<Map<String, Object>>> countriesData = dataStructureService.getCountriesRegionsTrainsForAdmin();
        model.addAttribute("countriesData", countriesData);
        
        return "admin/trains";
    }

    /**
     * 새 기차여행 등록 페이지를 제공합니다.
     *
     * @param regionId 지역 ID (선택적)
     * @param model    모델 객체
     * @return 뷰 이름
     */
    @GetMapping("/trains/new")
    public String newTrainForm(@RequestParam(required = false) Long regionId, Model model) {
        // 기본 DTO 생성
        TrainDto trainDto = new TrainDto();
        
        // 지역 ID가 제공된 경우 해당 지역을 미리 선택
        if (regionId != null) {
            trainDto.setRegionId(regionId);
        }
        
        // 국가 정보 조회
        List<CountryDto> countries = countryService.getAllCountries();
        
        // 뷰에 필요한 데이터 추가
        model.addAttribute("train", trainDto);
        model.addAttribute("countries", countries);
        model.addAttribute("preSelectedRegionId", regionId);
        
        return "admin/train-form";
    }

    /**
     * 기차여행 수정 페이지를 제공합니다.
     *
     * @param id    기차여행 ID
     * @param model 모델 객체
     * @return 뷰 이름
     */
    @GetMapping("/trains/edit/{id}")
    public String editTrainForm(@PathVariable Long id, Model model) {
        // 기존 기차여행 정보 조회
        TrainDetailDto train = trainService.getTrainById(id, null);
        
        // 폼 데이터로 변환
        TrainDto trainDto = TrainDto.builder()
                .id(train.getId())
                .name(train.getName())
                .description(train.getDescription())
                .imageUrl(train.getImageUrl())
                .operatingDays(train.getOperatingDays())
                .fare(train.getFare())
                .routeImageUrl(train.getRouteImageUrl())
                .bookingUrl(train.getBookingUrl())
                .siteUrl(train.getSiteUrl())
                .regionId(train.getRegionId())
                .build();

        // 국가 정보 조회
        List<CountryDto> countries = countryService.getAllCountries();
        
        model.addAttribute("train", trainDto);
        model.addAttribute("countries", countries);
        
        return "admin/train-form";
    }

    /**
     * 기차여행 저장 요청을 처리합니다.
     *
     * @param trainDto       기차여행 DTO
     * @param result         유효성 검사 결과
     * @param imageFile      이미지 파일
     * @param routeImageFile 노선 이미지 파일
     * @param model          모델 객체
     * @param redirectAttributes 리다이렉트 속성
     * @return 뷰 이름
     */
    @PostMapping("/trains")
    public String saveTrain(@Valid TrainDto trainDto, BindingResult result, 
                           @RequestParam(required = false) MultipartFile imageFile,
                           @RequestParam(required = false) MultipartFile routeImageFile,
                           Model model, RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            // 유효성 검사 오류 발생 시 폼 다시 보이기
            model.addAttribute("countries", countryService.getAllCountries());
            return "admin/train-form";
        }
        
        try {
            // TrainFormDto로 변환
            TrainFormDto formDto = TrainFormDto.builder()
                    .trainDto(trainDto)
                    .imageFile(imageFile)
                    .routeImageFile(routeImageFile)
                    .build();
            
            // 기차여행 저장 또는 업데이트 처리
            if (trainDto.getId() == null) {
                trainService.createTrain(formDto);
                redirectAttributes.addFlashAttribute("message", "기차여행이 성공적으로 등록되었습니다.");
            } else {
                trainService.updateTrain(formDto);
                redirectAttributes.addFlashAttribute("message", "기차여행이 성공적으로 수정되었습니다.");
            }
            
            return "redirect:/admin/trains";
        } catch (Exception e) {
            // 오류 처리
            log.error("기차여행 저장 중 오류 발생: {}", e.getMessage());
            model.addAttribute("errorMessage", "오류 발생: " + e.getMessage());
            model.addAttribute("countries", countryService.getAllCountries());
            return "admin/train-form";
        }
    }

    /**
     * 기차여행 삭제 요청을 처리합니다.
     *
     * @param id 기차여행 ID
     * @param redirectAttributes 리다이렉트 속성
     * @return 뷰 이름
     */
    @PostMapping("/trains/delete/{id}")
    public String deleteTrain(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            trainService.deleteTrain(id);
            redirectAttributes.addFlashAttribute("message", "기차여행이 성공적으로 삭제되었습니다.");
        } catch (Exception e) {
            log.error("기차여행 삭제 중 오류 발생: {}", e.getMessage());
            redirectAttributes.addFlashAttribute("error", "삭제 중 오류 발생: " + e.getMessage());
        }
        return "redirect:/admin/trains";
    }

    /**
     * 사용자 관리 페이지를 제공합니다.
     *
     * @param model 모델 객체
     * @return 뷰 이름
     */
    @GetMapping("/users")
    public String listUsers(Model model) {
        List<UserSummaryDto> users = userService.getAllUsers();
        model.addAttribute("users", users);
        return "admin/users";
    }

    /**
     * 사용자 상세 정보 페이지를 제공합니다.
     *
     * @param id    사용자 ID
     * @param model 모델 객체
     * @return 뷰 이름
     */
    @GetMapping("/users/{id}")
    public String viewUser(@PathVariable Long id, Model model) {
        User user = userService.getUserById(id);
        model.addAttribute("user", userMapper.toSummaryDto(user));
        model.addAttribute("roles", User.Role.values());
        return "admin/user-detail";
    }

    /**
     * 사용자 역할 업데이트 요청을 처리합니다.
     *
     * @param id                사용자 ID
     * @param role              새 역할
     * @param redirectAttributes 리다이렉트 속성
     * @return 뷰 이름
     */
    @PostMapping("/users/{id}/role")
    public String updateUserRole(@PathVariable Long id, @RequestParam User.Role role, 
                                RedirectAttributes redirectAttributes) {
        try {
            userService.updateUserRole(id, role);
            redirectAttributes.addFlashAttribute("message", "사용자 역할이 성공적으로 변경되었습니다.");
        } catch (Exception e) {
            log.error("사용자 역할 변경 중 오류 발생: {}", e.getMessage());
            redirectAttributes.addFlashAttribute("error", "역할 변경 중 오류 발생: " + e.getMessage());
        }
        return "redirect:/admin/users/" + id;
    }

    /**
     * 사용자 삭제 요청을 처리합니다.
     *
     * @param id                사용자 ID
     * @param redirectAttributes 리다이렉트 속성
     * @return 뷰 이름
     */
    @PostMapping("/users/delete/{id}")
    public String deleteUser(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            userService.deleteUser(id);
            redirectAttributes.addFlashAttribute("message", "사용자가 성공적으로 삭제되었습니다.");
        } catch (Exception e) {
            log.error("사용자 삭제 중 오류 발생: {}", e.getMessage());
            redirectAttributes.addFlashAttribute("error", "삭제 중 오류 발생: " + e.getMessage());
        }
        return "redirect:/admin/users";
    }
    
    /**
     * 게시글 관리 페이지를 제공합니다.
     *
     * @param pageable  페이징 정보
     * @param searchType 검색 타입
     * @param keyword   검색어
     * @param model     모델 객체
     * @return 뷰 이름
     */
    @GetMapping("/community/posts")
    public String manageCommunityPosts(
            @PageableDefault(sort = "id", direction = Sort.Direction.DESC, size = 10) Pageable pageable,
            @RequestParam(required = false) String searchType,
            @RequestParam(required = false) String keyword,
            Model model) {
        
        Page<BoardDTO> boardPage;
        
        // 검색 조건에 따라 게시글 목록 조회
        if (keyword != null && !keyword.isEmpty()) {
            switch (searchType) {
                case "title":
                    boardPage = boardService.findByTitleContaining(keyword, pageable);
                    break;
                case "content":
                    boardPage = boardService.findByContentContaining(keyword, pageable);
                    break;
                case "writer":
                    boardPage = boardService.findByWriterPaging(keyword, pageable);
                    break;
                default:
                    boardPage = boardService.findAll(pageable);
                    break;
            }
        } else {
            boardPage = boardService.findAll(pageable);
        }
        
        model.addAttribute("boardPage", boardPage);
        model.addAttribute("searchType", searchType);
        model.addAttribute("keyword", keyword);
        
        return "admin/community/posts";
    }
    
    /**
     * 게시글 상세 정보 페이지를 제공합니다.
     *
     * @param id    게시글 ID
     * @param model 모델 객체
     * @return 뷰 이름
     */
    @GetMapping("/community/posts/{id}")
    public String viewPost(@PathVariable Long id, Model model) {
        BoardDTO post = boardService.findById(id);
        model.addAttribute("post", post);
        return "admin/community/post-detail";
    }
    
    /**
     * 게시글 삭제 요청을 처리합니다.
     *
     * @param id                게시글 ID
     * @param redirectAttributes 리다이렉트 속성
     * @return 뷰 이름
     */
    @PostMapping("/community/posts/delete/{id}")
    public String deletePost(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            boardService.delete(id);
            redirectAttributes.addFlashAttribute("message", "게시글이 성공적으로 삭제되었습니다.");
        } catch (Exception e) {
            log.error("게시글 삭제 중 오류 발생: {}", e.getMessage());
            redirectAttributes.addFlashAttribute("error", "삭제 중 오류 발생: " + e.getMessage());
        }
        return "redirect:/admin/community/posts";
    }
}