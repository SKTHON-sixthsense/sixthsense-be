package com.skthon.sixthsensebe.domain.jobposting.controller;

import com.skthon.sixthsensebe.domain.jobposting.dto.request.CreateJobPostingRequest;
import com.skthon.sixthsensebe.domain.jobposting.dto.response.JobPostingResponse;
import com.skthon.sixthsensebe.domain.jobposting.entity.EmploymentType;
import com.skthon.sixthsensebe.domain.jobposting.entity.jobcategory.DetailJobCategory;
import com.skthon.sixthsensebe.domain.jobposting.entity.jobcategory.JobCategory;
import com.skthon.sixthsensebe.domain.jobposting.entity.jobcategory.detail.*;
import com.skthon.sixthsensebe.domain.jobposting.service.JobPostingService;
import com.skthon.sixthsensebe.global.response.BaseResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
@Tag(name = "JobPosting", description = "채용공고 등록 API")
public class JobPostingController {

  private final JobPostingService jobPostingService;

  @PostMapping(value = "/jobposting", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  @Operation(summary = "채용 공고 등록", description = "채용공고를 등록하고 등록한 정보를 반환")
  public ResponseEntity<BaseResponse<JobPostingResponse>> createJobPosting(
      @Parameter(content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE))
      @RequestPart(value = "jobposting") @Valid CreateJobPostingRequest request,
      @Parameter(description = "업체 상세 요강 이미지",
          content = @Content(mediaType = MediaType.MULTIPART_FORM_DATA_VALUE))
      @RequestPart(value = "image") MultipartFile file,
      @Parameter(description = "고용 형태") @RequestParam("employment") EmploymentType employmentType,
      @Parameter(description = "모집 직종 대분류") @RequestParam("jobcategory") JobCategory jobCategory,
      @Parameter(description = "모집 직종 세부분류") @RequestParam("detailjobcategory") List<DetailJobCategory> detailJobCategory) {

    JobPostingResponse response = jobPostingService.createJobPosting(request, file, employmentType, jobCategory, detailJobCategory);

    return ResponseEntity.ok(BaseResponse.success("채용공고가 성공적으로 등록되었습니다.", response));

  }

  @GetMapping("/jobpostings")
  @Operation(summary = "채용 공고 전체 조회", description = "등록된 모든 채용공고를 조회합니다")
  public ResponseEntity<BaseResponse<List<JobPostingResponse>>> getAllJobPostings() {
    List<JobPostingResponse> responses = jobPostingService.getAllJobPostings();
    return ResponseEntity.ok(BaseResponse.success("채용공고 목록을 성공적으로 조회했습니다.", responses));
  }

  @GetMapping("/jobposting/{id}")
  @Operation(summary = "채용 공고 단일 조회", description = "ID로 특정 채용공고를 조회합니다")
  public ResponseEntity<BaseResponse<JobPostingResponse>> getJobPosting(@PathVariable Long id) {
    JobPostingResponse response = jobPostingService.getJobPosting(id);
    return ResponseEntity.ok(BaseResponse.success("채용공고를 성공적으로 조회했습니다.", response));
  }

  @GetMapping("/jobcategories")
  @Operation(summary = "직종 대분류 목록 조회", description = "모든 직종 대분류를 조회합니다")
  public ResponseEntity<BaseResponse<List<String>>> getJobCategories() {
    List<String> categories = List.of(
        JobCategory.FACILITY_MANAGEMENT.getValue(),
        JobCategory.FOOD_SERVICE.getValue(),
        JobCategory.DELIVERY_TRANSPORT.getValue(),
        JobCategory.SALES.getValue(),
        JobCategory.CARE_SERVICE.getValue(),
        JobCategory.DAY_LABOR.getValue()
    );
    return ResponseEntity.ok(BaseResponse.success("직종 대분류 목록을 조회했습니다.", categories));
  }

  @GetMapping("/jobcategories/{category}/details")
  @Operation(summary = "직종 세부분류 조회", description = "선택한 대분류에 해당하는 세부분류를 조회합니다")
  public ResponseEntity<BaseResponse<List<String>>> getDetailJobCategories(@PathVariable String category) {
    List<String> detailCategories;

    switch (category) {
      case "시설관리 및 운영":
        detailCategories = List.of(
            FacilityManagementCategory.SECURITY_SAFETY.getValue(),
            FacilityManagementCategory.PARKING_MANAGEMENT.getValue(),
            FacilityManagementCategory.CLEANING_BEAUTY.getValue(),
            FacilityManagementCategory.STORE_MANAGEMENT.getValue()
        );
        break;
      case "요식업":
        detailCategories = List.of(
            FoodServiceCategory.DISHWASHER.getValue(),
            FoodServiceCategory.KITCHEN_ASSISTANT.getValue(),
            FoodServiceCategory.HALL_SERVICE.getValue(),
            FoodServiceCategory.COFFEE_PREPARATION.getValue()
        );
        break;
      case "운전 및 배송":
        detailCategories = List.of(
            DeliveryTransportCategory.PARCEL_LOADING.getValue(),
            DeliveryTransportCategory.FREIGHT_DRIVER.getValue(),
            DeliveryTransportCategory.DELIVERY_DRIVER.getValue(),
            DeliveryTransportCategory.SUBSTITUTE_DRIVER.getValue()
        );
        break;
      case "판매직":
        detailCategories = List.of(
            SalesCategory.CASHIER.getValue(),
            SalesCategory.PRODUCT_DISPLAY.getValue(),
            SalesCategory.TASTING_GUIDE.getValue(),
            SalesCategory.SALES_ASSISTANT.getValue()
        );
        break;
      case "가사 및 돌봄":
        detailCategories = List.of(
            CareServiceCategory.CARE_WORKER.getValue(),
            CareServiceCategory.CAREGIVER.getValue(),
            CareServiceCategory.FLYER_DISTRIBUTOR.getValue(),
            CareServiceCategory.BABYSITTER.getValue()
        );
        break;
      case "일용직":
        detailCategories = List.of(
            DayLaborCategory.CONSTRUCTION_ASSISTANT.getValue(),
            DayLaborCategory.PACKAGING_INSPECTOR.getValue(),
            DayLaborCategory.TASTING_GUIDE_DAY.getValue(),
            DayLaborCategory.FARM_WORK.getValue()
        );
        break;
      default:
        return ResponseEntity.badRequest().body(BaseResponse.error(400, "잘못된 카테고리입니다."));
    }

    return ResponseEntity.ok(BaseResponse.success("세부 카테고리 목록을 조회했습니다.", detailCategories));
  }
}
