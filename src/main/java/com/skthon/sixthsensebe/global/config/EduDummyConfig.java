package com.skthon.sixthsensebe.global.config;

import com.skthon.sixthsensebe.domain.education.entity.Education;
import com.skthon.sixthsensebe.domain.education.repository.EducationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;


@Slf4j
@RequiredArgsConstructor
@Configuration
public class EduDummyConfig {

  @Bean
  public CommandLineRunner initData(EducationRepository educationRepository) {
    return args -> {

      // 데이터 중복 저장 방지
      if (educationRepository.count() > 0) {
        log.info("Education 데이터가 이미 존재합니다. 초기화를 건너뜁니다.");
        return;
      }

      educationRepository.saveAll(List.of(
          Education.builder()
              .title("간병사")
              .summary("요양병원, 재활센터, 요양원 같은 간병서비스 분야에서 수요가 높아 다양한 돌봄 현장에서 활용되는 자격증")
              .description("간병인으로서 가져야 할 자세와 간병에 대한 기본적 지식과 간병사의 자질, 환자에 대한 최상의 서비스를 제공하기 위한 기술을 갖추게 해주는 자격증")
              .requirement("16세 이상 누구나")
              .competentAuthority("보건복지부")
              .issuingAuthority("대한자격개발검정원")
              .homepageUrl("https://korea-kca.com/home/curriculum/info.php?item=648")
              .build(),

          Education.builder()
              .title("생활지원사")
              .summary("노인복지관, 재가복지센터 등 지자체 돌봄 서비스 현장에서 활동할 수 있는 자격증")
              .description("노인돌봄에 관한 이해를 바탕으로 노인돌봄이 필요한 관련기관에서 일상생활이 어려운 취약계층 노인들에게 안전, 건강, 가사, 일상생활 등의 돌봄 서비스를 습득하는 자격증")
              .requirement("16세 이상 누구나")
              .competentAuthority("보건복지부")
              .issuingAuthority("한국자격검정평가진흥원")
              .homepageUrl("https://korea-kca.com/home/curriculum/info.php?item=668")
              .build(),

          Education.builder()
              .title("실버케어지도사")
              .summary("고령층의 건강 관리, 여가 프로그램, 치매 예방 활동 등을 기획·지도하는 직종에 활용")
              .description("관련기관에서 노인분들에게 필요한 돌봄 서비스(안전, 건강, 가사)를 통해 일상생활에 필요한 기본적인 서비스를 습득하는 자격증")
              .requirement("16세 이상 누구나")
              .competentAuthority("보건복지부")
              .issuingAuthority("한국자격검정평가진흥원")
              .homepageUrl("https://korea-kca.com/home/curriculum/info.php?item=773")
              .build()
      ));
    };
  }
}
