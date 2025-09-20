package com.skthon.sixthsensebe.domain.healtharray.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.skthon.sixthsensebe.domain.healtharray.dto.request.GptRequest;
import com.skthon.sixthsensebe.domain.healtharray.exception.GptErrorCode;
import com.skthon.sixthsensebe.domain.jobposting.entity.JobPosting;
import com.skthon.sixthsensebe.domain.jobposting.exception.JobPostingErrorCode;
import com.skthon.sixthsensebe.domain.jobposting.repository.JobPostingRepository;
import com.skthon.sixthsensebe.domain.user.entity.Health;
import com.skthon.sixthsensebe.global.config.GptConfig;
import com.skthon.sixthsensebe.global.exception.CustomException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

@Slf4j
@Service
@RequiredArgsConstructor
public class GptService {
  private final JobPostingRepository jobPostingRepository;
  private final ObjectMapper objectMapper;
  private final RestTemplate restTemplate;
  private final GptConfig gptConfig;


  // DTO 로 변환
    public List<GptRequest> getAllJobRequestsForGpt() {
      List<JobPosting> postings = jobPostingRepository.findAll();
      return postings.stream()
          .map(this::toGptRequest)
          .collect(Collectors.toList());
    }
    // 개별 변환
    private GptRequest toGptRequest(JobPosting jobPosting) {
      return GptRequest.builder()
          .id(jobPosting.getId())
          .jobCategory(jobPosting.getJobCategory().name())
          .detailJobCategories(
              jobPosting.getDetailJobCategory()
                  .stream()
                  .map(Enum::name)
                  .collect(Collectors.toList())
          )
          .build();
    }

    // 프롬프트 생성 메소드
    public List<Map<String, String>> createPrompt(GptRequest gptRequest, List<Health> userHealthList) {
      // Health enum 리스트를 문자열로 변환
      String healthStr = userHealthList.stream()
          .map(Enum::name)
          .collect(Collectors.joining(", "));

      // 상세 직무 문자열
      String detailJobs = String.join(", ", gptRequest.getDetailJobCategories());

      // GPT 메시지 content
      String content = String.format("""
                사용자의 건강정보: %s

                채용공고 정보:
                - ID: %d
                - 직무 카테고리: %s
                - 상세 직무: %s

                요청사항:
                사용자의 건강정보를 고려하여 가장 적합한 채용공고 하나의 ID만 숫자로 반환하세요.
                """, healthStr, gptRequest.getId(), gptRequest.getJobCategory(), detailJobs);

      // GPT API messages 포맷
      return List.of(
          Map.of("role", "system", "content", "당신은 50대 구직자 맞춤 채용 추천 도우미입니다."),
          Map.of("role", "user", "content", content)
      );
    }


  // gpt에게 요청하고 추천 채용 공고 id 반환받는 메소드
  public Integer callGptAPI(GptRequest gptRequest, List<Map<String, String>> prompts) {
    // requestBody
    Map<String, Object> requestBody = new HashMap<>();
    requestBody.put("model", gptConfig.getModel());
    requestBody.put("messages", prompts);
    requestBody.put("temperature", 0.0); // 추천 ID 하나만 뽑으므로 창의성 낮게

    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);
    headers.setBearerAuth(gptConfig.getSecretKey());

    HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(requestBody, headers);

    try {
      log.info("[GptService] GPT API 요청 시도 : jobId={}", gptRequest.getId());
      ResponseEntity<Map> response = restTemplate.postForEntity(
          gptConfig.getUrl(), requestEntity, Map.class);

      if (response.getStatusCode() != HttpStatus.OK) {
        log.error("[GptService] GPT API 응답 실패 : jobId={}, httpStatus={}",
            gptRequest.getId(), response.getStatusCode());
        throw new CustomException(GptErrorCode.GPT_API_CALL_FAILED);
      }

      Map<String, Object> responseBody = response.getBody();
      List<Map<String, Object>> choices = objectMapper.convertValue(
          responseBody.get("choices"),
          new TypeReference<List<Map<String, Object>>>() {}
      );

      if (choices == null || choices.isEmpty()) {
        log.warn("[GptService] GPT API 응답 성공했으나 choices 비어있음 : jobId={}", gptRequest.getId());
        throw new CustomException(GptErrorCode.GPT_EMPTY_RESPONSE);
      }

      // choices[0].message.content 에서 추천 ID 추출
      Map<String, Object> firstChoice = choices.get(0);
      Map<String, String> message = (Map<String, String>) firstChoice.get("message");
      String content = message.get("content");

      if (content == null || content.isBlank()) {
        log.warn("[GptService] GPT 응답에 content 없음 : jobId={}", gptRequest.getId());
        throw new CustomException(GptErrorCode.GPT_EMPTY_RESPONSE);
      }

      // 숫자만 추출
      String onlyDigits = content.replaceAll("[^0-9]", "");
      if (onlyDigits.isEmpty()) {
        log.error("[GptService] GPT 응답에서 숫자 추출 실패 : content={}", content);
        throw new CustomException(GptErrorCode.GPT_INVALID_PROMPT);
      }

      Integer recommendedId = Integer.parseInt(onlyDigits);
      log.info("[GptService] GPT 추천 채용공고 ID: {}", recommendedId);

      return recommendedId;

    } catch (HttpClientErrorException e) {
      log.error("[GptService] GPT API 클라이언트 오류 : {}", e.getResponseBodyAsString(), e);
      throw new CustomException(GptErrorCode.GPT_INVALID_PROMPT);

    } catch (ResourceAccessException e) {
      log.error("[GptService] GPT API 타임아웃 : {}", e.getMessage(), e);
      throw new CustomException(GptErrorCode.GPT_TIMEOUT);

    } catch (Exception e) {
      log.error("[GptService] GPT API 호출 실패 : {}", e.getMessage(), e);
      throw new CustomException(GptErrorCode.GPT_API_CALL_FAILED);
    }
  }


  public Integer gptFlow(List<Health> userHealthList){
    // 1. DB에서 모든 공고 조회
    List<GptRequest> allJobs = getAllJobRequestsForGpt();
    if (allJobs.isEmpty()) {
      log.warn("채용공고가 없습니다.");
      throw new CustomException(JobPostingErrorCode.JOB_POSTING_NOT_FOUND);
    }

    // 2. GPT 프롬프트 생성
    // 전체 공고를 GPT에게 보내기보다는, 테스트용으로 첫 공고 하나만 넣을 수도 있음
    // 실제 서비스에서는 allJobs 리스트를 문자열로 만들어 보내면 됩니다.
    GptRequest firstJob = allJobs.get(0); // 단순화
    List<Map<String, String>> prompts = createPrompt(firstJob, userHealthList);

    // 3. GPT API 호출
    Integer recommendedJobId = callGptAPI(firstJob, prompts);

    // 4. 추천 ID 반환
    return recommendedJobId;

  }

}
