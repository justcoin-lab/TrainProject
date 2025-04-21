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
    private String name;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false)
    private String imageUrl;

    @Column(nullable = false)
    private String operatingDays;

    @Column(nullable = false)
    private String fare;

    @Column(nullable = false)
    private String routeImageUrl;

    @Column(nullable = false)
    private String bookingUrl;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "region_id")
    private Region region;

    @OneToMany(mappedBy = "train", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Bookmark> bookmarks = new ArrayList<>();

    public Train(String name, String description, String imageUrl, String operatingDays,
                 String fare, String routeImageUrl, String bookingUrl, Region region) {
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