package rousing.traintrip.domain;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Country extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name; // 국가 이름

    @OneToMany(mappedBy = "country", cascade = CascadeType.ALL)
    private List<Region> regions = new ArrayList<>();

    @Builder
    public Country(String name) {

        this.name = name;
    }
}
