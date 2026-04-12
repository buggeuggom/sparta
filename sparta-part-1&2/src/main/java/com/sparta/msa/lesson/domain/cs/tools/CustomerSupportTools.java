package com.sparta.msa.lesson.domain.cs.tools;
import com.sparta.msa.lesson.domain.cs.dto.tool.FAQSearchRequest;
import com.sparta.msa.lesson.domain.cs.dto.tool.FAQSearchResponse;
import com.sparta.msa.lesson.domain.cs.dto.tool.FAQSearchResponse.FAQItem;
import com.sparta.msa.lesson.domain.cs.dto.tool.OrderQueryRequest;
import com.sparta.msa.lesson.domain.cs.dto.tool.OrderQueryResponse;
import com.sparta.msa.lesson.domain.cs.dto.tool.ProductRecommendationRequest;
import com.sparta.msa.lesson.domain.cs.dto.tool.ProductRecommendationResponse;
import com.sparta.msa.lesson.domain.cs.dto.tool.ProductRecommendationResponse.ProductItem;
import com.sparta.msa.lesson.domain.cs.dto.tool.RefundRequest;
import com.sparta.msa.lesson.domain.cs.dto.tool.RefundResponse;
import com.sparta.msa.lesson.domain.cs.dto.tool.ShipmentTrackingRequest;
import com.sparta.msa.lesson.domain.cs.dto.tool.ShipmentTrackingResponse;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class CustomerSupportTools {

    private static final DateTimeFormatter FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    // --- [주문 및 배송 관련 도구] ---

    @Tool(description = "주문 번호(ID)를 통해 주문 상태, 상품 목록, 결제 금액 등 상세 정보를 조회합니다.")
    public OrderQueryResponse getOrderInfo(OrderQueryRequest request) {
        log.info("[Tool] 주문 조회 실행: ID={}", request.getOrderId());

        // 존재하지 않는 주문 번호 처리
        if (request.getOrderId() == null || !request.getOrderId().startsWith("ORD-")) {
            log.warn("[Tool] 주문 조회 실패: {} 존재하지 않음", request.getOrderId());
            return null;
        }

        return OrderQueryResponse.builder()
                .orderId(request.getOrderId())
                .status("배송 중")
                .items(List.of("무선 이어폰", "충전 케이블"))
                .totalPrice(89000.0)
                .build();
    }

    @Tool(description = "주문의 현재 배송 위치와 상태(배송 중, 배송 완료 등)를 실시간으로 추적합니다.")
    public ShipmentTrackingResponse trackShipment(ShipmentTrackingRequest request) {
        log.info("[Tool] 배송 추적 실행: {}", request.getOrderId());

        return ShipmentTrackingResponse.builder()
                .orderId(request.getOrderId())
                .status("배송 중")
                .currentLocation("서울 물류센터")
                .lastUpdated(LocalDateTime.now().format(FORMATTER))
                .build();
    }

    // --- [고객 서비스 처리 도구] ---

    @Tool(description = "특정 주문에 대한 환불 요청을 처리합니다. 환불 사유가 반드시 포함되어야 합니다.")
    public RefundResponse processRefund(RefundRequest request) {
        log.info("[Tool] 환불 처리 요청: 주문ID={}, 사유={}", request.getOrderId(), request.getReason());

        try {
            // TODO: 실제 환불 처리 로직 연동
            return RefundResponse.builder()
                    .success(true)
                    .message("성공적으로 환불이 승인되었습니다.")
                    .build();
        } catch (Exception e) {
            log.error("[Tool] 환불 처리 실패: ", e);
            return RefundResponse.builder()
                    .success(false)
                    .message("환불 실패 사유: " + e.getMessage())
                    .build();
        }
    }

    @Tool(description = "배송, 반품, 교환 정책 등 자주 묻는 질문(FAQ) 데이터베이스에서 정보를 검색합니다.")
    public FAQSearchResponse searchFAQ(FAQSearchRequest request) {
        log.info("[Tool] FAQ 검색 실행: {}", request.getKeyword());

        List<FAQItem> faqs = List.of(
                FAQItem.builder().question("배송").answer("평균 2-3일 내에 도착합니다.").build(),
                FAQItem.builder().question("반품").answer("단순 변심의 경우 수령 후 7일 이내 신청 가능합니다.").build(),
                FAQItem.builder().question("교환").answer("제품 불량 시 100% 무상 교환을 보장합니다.").build()
        );

        return FAQSearchResponse.builder()
                .faqs(faqs)
                .build();
    }

    // --- [마케팅 및 추천 도구] ---

    @Tool(description = "고객의 구매 이력이나 관심 카테고리를 기반으로 맞춤형 상품을 추천합니다.")
    public ProductRecommendationResponse recommendProducts(ProductRecommendationRequest request) {
        log.info("[Tool] 상품 추천 실행: 고객ID={}, 카테고리={}", request.getCustomerId(), request.getCategory());

        List<ProductItem> recommendations = List.of(
                ProductItem.builder().productId("P001").name("인기 급상승 상품").price(29900.0).build(),
                ProductItem.builder().productId("P002").name("당신을 위한 맞춤 상품").price(39900.0).build()
        );

        return ProductRecommendationResponse.builder()
                .recommendations(recommendations)
                .build();
    }

}