package rousing.traintrip.domain;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Train extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name; // 기차여행 이름

    @Column(nullable = false, columnDefinition = "TEXT")
    private String description; // 기차 여행 설명

    @Column(nullable = false)
    private String imageUrl; // 이미지 URL

    @Column(nullable = false)
    private String operatingDays; // 운행일 정보
    
    @Column(nullable = false)
    private String fare; // 요금 정보

    @Column(nullable = false)
    private String routeImageUrl; // 노선도 이미지 URL

    @Column(nullable = false)
    private String bookingUrl; // 예약 URL

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "region_id")
    private Region region;

    @OneToMany(mappedBy = "train", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Bookmark> bookmarks = new ArrayList<>();

    @Builder
    public Train(String name, String description, String imageUrl,
                 String operatingDays, String fare, String routeImageUrl,
                 String bookingUrl, Region region) {
        this.name = name;
        this.description = description;
        this.imageUrl = imageUrl;
        this.operatingDays = operatingDays;
        this.fare = fare;
        this.routeImageUrl = routeImageUrl;
        this.bookingUrl = bookingUrl;
        this.region = region;
    }
    
    // 기차 정보 업데이트 메서드
    public void update(String name, String description, String imageUrl,
                      String operatingDays, String fare, String routeImageUrl,
                      String bookingUrl, Region region) {
        this.name = name;
        this.description = description;
        this.imageUrl = imageUrl;
        this.operatingDays = operatingDays;
        this.fare = fare;
        this.routeImageUrl = routeImageUrl;
        this.bookingUrl = bookingUrl;
        this.region = region;
    }
}