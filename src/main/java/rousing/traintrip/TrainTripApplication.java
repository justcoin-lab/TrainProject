package rousing.traintrip;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import rousing.traintrip.domain.Country;
import rousing.traintrip.domain.Region;
import rousing.traintrip.domain.Train;
import rousing.traintrip.domain.User;
import rousing.traintrip.repository.CountryRepository;
import rousing.traintrip.repository.RegionRepository;
import rousing.traintrip.repository.TrainRepository;
import rousing.traintrip.repository.UserRepository;

import java.util.List;

@SpringBootApplication
@EnableJpaAuditing
public class TrainTripApplication {
	public static void main(String[] args) {
		SpringApplication.run(TrainTripApplication.class, args);
	}

	/*@Bean
	public CommandLineRunner dataLoader(
			CountryRepository countryRepository,
			RegionRepository regionRepository,
			TrainRepository trainRepository,
			UserRepository userRepository,
			PasswordEncoder passwordEncoder
	) {
		return args -> {
			// 관리자 계정 생성
			if (!userRepository.existsByUsername("admin")) {
				User admin = User.builder()
				.username("admin")
				.password(passwordEncoder.encode("admin"))
				.email("admin@traintrips.com")
				.nickname("관리자")
                .role(User.Role.ADMIN)
                .build();
				userRepository.save(admin);
			}

			// 국가 정보 초기화
			if (countryRepository.count() == 0) {
				Country korea = Country.builder().name("한국").build();
				Country japan = Country.builder().name("일본").build();

				countryRepository.saveAll(List.of(korea, japan));

				// 한국 지역 정보 초기화
				Region nationwide = Region.builder().name("전국").country(korea).build();
				Region gangwon = Region.builder().name("강원도").country(korea).build();
				Region chungcheong = Region.builder().name("충청도").country(korea).build();
				Region gyeongsang = Region.builder().name("경상도").country(korea).build();
				Region jeolla = Region.builder().name("전라도").country(korea).build();

				regionRepository.saveAll(List.of(nationwide, gangwon, chungcheong, gyeongsang, jeolla));

				// 일본 지역 정보 초기화
				Region hokkaido = Region.builder().name("홋카이도").country(japan).build();
				Region tohoku = Region.builder().name("도호쿠").country(japan).build();
				Region hokurikuShinetsu = Region.builder().name("호쿠리쿠신에쓰").country(japan).build();
				Region kanto = Region.builder().name("간토").country(japan).build();
				Region tokai = Region.builder().name("도카이").country(japan).build();
				Region kansai = Region.builder().name("간사이").country(japan).build();
				Region chugoku = Region.builder().name("주고쿠").country(japan).build();
				Region shikoku = Region.builder().name("시고쿠").country(japan).build();
				Region kyushu = Region.builder().name("규슈").country(japan).build();

				regionRepository.saveAll(List.of(hokkaido, tohoku, hokurikuShinetsu, kanto,
						tokai, kansai, chugoku, shikoku, kyushu));


			}
		};
	}*/
}