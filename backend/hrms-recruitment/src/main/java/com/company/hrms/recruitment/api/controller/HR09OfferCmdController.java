package com.company.hrms.recruitment.api.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.company.hrms.common.annotation.CurrentUser;
import com.company.hrms.common.controller.CommandBaseController;
import com.company.hrms.common.model.JWTModel;
import com.company.hrms.recruitment.application.dto.offer.CreateOfferRequest;
import com.company.hrms.recruitment.application.dto.offer.ExtendOfferRequest;
import com.company.hrms.recruitment.application.dto.offer.OfferResponse;
import com.company.hrms.recruitment.application.dto.offer.RejectOfferRequest;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

/**
 * Offer 指令 Controller
 */
@RestController
@RequestMapping("/api/v1/recruitment/offers")
@Tag(name = "HR09-Offer", description = "Offer 管理")
public class HR09OfferCmdController extends CommandBaseController {

        @PostMapping
        @Operation(summary = "建立 Offer", operationId = "createOffer")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "建立成功", content = @Content(schema = @Schema(implementation = OfferResponse.class))),
                        @ApiResponse(responseCode = "400", description = "請求參數錯誤"),
                        @ApiResponse(responseCode = "404", description = "應徵者不存在")
        })
        public ResponseEntity<OfferResponse> createOffer(
                        @Valid @RequestBody CreateOfferRequest request,
                        @Parameter(hidden = true) @CurrentUser JWTModel currentUser) throws Exception {
                return ResponseEntity.ok(execCommand(request, currentUser));
        }

        @PostMapping("/{id}/accept")
        @Operation(summary = "接受 Offer", operationId = "acceptOffer")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "接受成功", content = @Content(schema = @Schema(implementation = OfferResponse.class))),
                        @ApiResponse(responseCode = "400", description = "無法接受"),
                        @ApiResponse(responseCode = "404", description = "Offer 不存在")
        })
        public ResponseEntity<OfferResponse> acceptOffer(
                        @Parameter(description = "Offer ID") @PathVariable String id,
                        @Parameter(hidden = true) @CurrentUser JWTModel currentUser) throws Exception {
                return ResponseEntity.ok(execCommand(null, currentUser, id));
        }

        @PostMapping("/{id}/reject")
        @Operation(summary = "拒絕 Offer", operationId = "rejectOffer")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "拒絕成功", content = @Content(schema = @Schema(implementation = OfferResponse.class))),
                        @ApiResponse(responseCode = "400", description = "無法拒絕"),
                        @ApiResponse(responseCode = "404", description = "Offer 不存在")
        })
        public ResponseEntity<OfferResponse> rejectOffer(
                        @Parameter(description = "Offer ID") @PathVariable String id,
                        @RequestBody RejectOfferRequest request,
                        @Parameter(hidden = true) @CurrentUser JWTModel currentUser) throws Exception {
                return ResponseEntity.ok(execCommand(request, currentUser, id));
        }

        @PostMapping("/{id}/withdraw")
        @Operation(summary = "撤回 Offer", operationId = "withdrawOffer")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "撤回成功", content = @Content(schema = @Schema(implementation = OfferResponse.class))),
                        @ApiResponse(responseCode = "400", description = "無法撤回"),
                        @ApiResponse(responseCode = "404", description = "Offer 不存在")
        })
        public ResponseEntity<OfferResponse> withdrawOffer(
                        @Parameter(description = "Offer ID") @PathVariable String id,
                        @Parameter(hidden = true) @CurrentUser JWTModel currentUser) throws Exception {
                return ResponseEntity.ok(execCommand(null, currentUser, id));
        }

        @PutMapping("/{id}/extend")
        @Operation(summary = "延長 Offer 到期日", operationId = "extendOffer")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "延長成功", content = @Content(schema = @Schema(implementation = OfferResponse.class))),
                        @ApiResponse(responseCode = "400", description = "無法延長"),
                        @ApiResponse(responseCode = "404", description = "Offer 不存在")
        })
        public ResponseEntity<OfferResponse> extendOffer(
                        @Parameter(description = "Offer ID") @PathVariable String id,
                        @Valid @RequestBody ExtendOfferRequest request,
                        @Parameter(hidden = true) @CurrentUser JWTModel currentUser) throws Exception {
                return ResponseEntity.ok(execCommand(request, currentUser, id));
        }
}
