package rousing.traintrip;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.security.crypto.password.PasswordEncoder;
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

	@Bean
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
			Country korea = new Country("한국");
			Country japan = new Country("일본");

			countryRepository.saveAll(List.of(korea, japan));

			// 한국 지역 정보 초기화
			Region capital = new Region("수도권", korea);
			Region gangwon = new Region("강원도", korea);
			Region chungcheong = new Region("충청도", korea);
			Region gyeongsang = new Region("경상도", korea);
			Region jeolla = new Region("전라도", korea);
			Region jeju = new Region("제주도", korea);

			regionRepository.saveAll(List.of(capital, gangwon, chungcheong, gyeongsang, jeolla, jeju));

			// 일본 지역 정보 초기화
			Region hokkaido = new Region("홋카이도", japan);
			Region tohoku = new Region("도호쿠", japan);
			Region hokurikuShinetsu = new Region("호쿠리쿠신에쓰", japan);
			Region kanto = new Region("간토", japan);
			Region tokai = new Region("도카이", japan);
			Region kansai = new Region("간사이", japan);
			Region chugoku = new Region("주고쿠", japan);
			Region shikoku = new Region("시고쿠", japan);
			Region kyushu = new Region("규슈", japan);

			regionRepository.saveAll(List.of(hokkaido, tohoku, hokurikuShinetsu, kanto,
					tokai, kansai, chugoku, shikoku, kyushu));

			// 한국 기차여행 정보 초기화
			Train v_train = new Train(
					"V-트레인",
					"아름다운 협곡을 따라 달리는 V자 형태의 협곡철길을 달리는 열차로, 철도 폐선지를 활용한 관광열차입니다.",
					"/images/trains/v-train.jpg",
					"매일 (연중무휴)",
					"성인 15,000원 / 어린이 7,500원",
					"/images/routes/v-train-route.jpg",
					"https://www.letskorail.com/",
					gangwon
			);

			Train o_train = new Train(
					"O-트레인",
					"강원도의 깊은 산골과 바다를 동시에 여행할 수 있는 순환형 여행열차입니다.",
					"/images/trains/o-train.jpg",
					"금, 토, 일 및 공휴일",
					"성인 18,000원 / 어린이 9,000원",
					"/images/routes/o-train-route.jpg",
					"https://www.letskorail.com/",
					gangwon
			);

			Train sea_train = new Train(
					"바다열차",
					"강원도 동해안의 아름다운 해안선을 따라 달리는 열차로, 창문 너머로 눈부신 동해의 풍경을 감상할 수 있습니다.",
					"/images/trains/sea-train.jpg",
					"화~일 (월요일 휴무)",
					"성인 19,000원 / 어린이 9,500원",
					"/images/routes/sea-train-route.jpg",
					"https://www.letskorail.com/",
					gangwon
			);

			Train s_train = new Train(
					"S-트레인",
					"남도해양열차로 불리며 전라남도의 아름다운 해안선과 섬을 따라 달리는 열차입니다.",
					"/images/trains/s-train.jpg",
					"수~일 (월, 화 휴무)",
					"성인 18,000원 / 어린이 9,000원",
					"/images/routes/s-train-route.jpg",
					"https://www.letskorail.com/",
					jeolla
			);

			trainRepository.saveAll(List.of(v_train, o_train, sea_train, s_train));

			// 일본 기차여행 정보 초기화
			Train joyful_train = new Train(
					"조이풀 트레인",
					"홋카이도의 웅장한 자연을 즐기는 테마 열차로, 계절마다 다른 매력을 느낄 수 있습니다.",
					"/images/trains/joyful-train.jpg",
					"토, 일 및 공휴일",
					"성인 10,000엔 / 어린이 5,000엔",
					"/images/routes/joyful-train-route.jpg",
					"https://www.jreast.co.jp/",
					hokkaido
			);

			Train shikishima = new Train(
					"시키시마",
					"도호쿠 지방을 순회하는 최고급 크루즈 기차로, 럭셔리한 인테리어와 일류 셰프의 요리를 즐길 수 있습니다.",
					"/images/trains/shikishima.jpg",
					"주 2회 운행 (예약 필수)",
					"1인 250,000엔~",
					"/images/routes/shikishima-route.jpg",
					"https://www.jreast.co.jp/",
					tohoku
			);

			Train resort_shirakami = new Train(
					"리조트 시라카미",
					"아오모리와 아키타를 잇는 리조트 열차로, 세계자연유산인 시라카미 산맥과 일본해의 절경을 감상할 수 있습니다.",
					"/images/trains/resort-shirakami.jpg",
					"매일 (연중무휴)",
					"성인 12,000엔 / 어린이 6,000엔",
					"/images/routes/resort-shirakami-route.jpg",
					"https://www.jreast.co.jp/",
					tohoku
			);

			trainRepository.saveAll(List.of(joyful_train, shikishima, resort_shirakami));
		};
	}
}
