package com.skthon.sixthsensebe;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing
@SpringBootApplication
public class SixthsenseBeApplication {

  public static void main(String[] args) {
    SpringApplication.run(SixthsenseBeApplication.class, args);
  }

}
