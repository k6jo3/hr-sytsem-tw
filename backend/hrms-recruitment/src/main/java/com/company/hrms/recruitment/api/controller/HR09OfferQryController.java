package com.company.hrms.recruitment.api.controller;

import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.company.hrms.common.annotation.CurrentUser;
import com.company.hrms.common.controller.QueryBaseController;
import com.company.hrms.common.model.JWTModel;
import com.company.hrms.recruitment.application.dto.offer.OfferResponse;
import com.company.hrms.recruitment.application.dto.offer.OfferSearchDto;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * Offer 查詢 Controller
 */
@RestController
@RequestMapping("/api/v1/recruitment/offers")
@Tag(name = "HR09-Offer", description = "Offer 查詢")
public class HR09OfferQryController extends QueryBaseController {

    @GetMapping
    @Operation(summary = "查詢 Offer 列表", operationId = "getOffers")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "查詢成功", content = @Content(schema = @Schema(implementation = Page.class)))
    })
    public ResponseEntity<Object> getOffers(
            @ParameterObject OfferSearchDto searchDto,
            @Parameter(hidden = true) @CurrentUser JWTModel currentUser) throws Exception {
        return ResponseEntity.ok(getResponse(searchDto, currentUser));
    }

    @GetMapping("/{id}")
    @Operation(summary = "查詢 Offer 詳情", operationId = "getOffer")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "查詢成功", content = @Content(schema = @Schema(implementation = OfferResponse.class))),
            @ApiResponse(responseCode = "404", description = "Offer 不存在")
    })
    public ResponseEntity<OfferResponse> getOffer(
            @Parameter(description = "Offer ID") @PathVariable String id,
            @Parameter(hidden = true) @CurrentUser JWTModel currentUser) throws Exception {
        return ResponseEntity.ok(getResponse(null, currentUser, id));
    }
}
