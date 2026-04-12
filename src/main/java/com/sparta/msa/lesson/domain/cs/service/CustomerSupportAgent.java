package com.sparta.msa.lesson.domain.cs.service;


import com.sparta.msa.lesson.domain.cs.dto.response.CustomerSupportResponse;
import com.sparta.msa.lesson.domain.cs.tools.CustomerSupportTools;
import com.sparta.msa.lesson.global.constants.enums.CustomerPriority;
import com.sparta.msa.lesson.global.constants.enums.CustomerSentiment;
import com.sparta.msa.lesson.global.exception.DomainException;
import com.sparta.msa.lesson.global.exception.DomainExceptionCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class CustomerSupportAgent {

    private final ChatClient.Builder chatClientBuilder;
    private final CustomerSupportTools customerSupportTools;

    private static final String BASE_SYSTEM_PROMPT = """
      당신은 전문적이고 친절한 고객 지원 에이전트입니다.
      고객의 요청을 정확히 이해하고 도구(주문조회, 환불, FAQ, 추천, 배송추적)를 활용하여 문제를 해결하세요.
      """;

    /**
     * 1. 표준 요청 처리 기본 ReAct 패턴으로 문제를 해결합니다.
     */
    public String handleCustomerRequest(String customerId, String message) {
        log.info("[Standard Agent] 요청 처리 시작: {}", customerId);

        try {
            return chatClientBuilder.build().prompt()
                    .system(BASE_SYSTEM_PROMPT)
                    .tools(customerSupportTools)
                    .user(formatUserMessage(customerId, message))
                    .call()
                    .content();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new DomainException(DomainExceptionCode.AI_RESPONSE_ERROR);
        }
    }

    /**
     * 2. 우선순위 기반 응대 고객 등급에 따라 페르소나와 후속 조치를 차별화합니다.
     */
    public CustomerSupportResponse handlePrioritizedRequest(
            String customerId, String message, CustomerPriority priority) {
        log.info("[Priority Agent] 등급별 대응: {} (등급: {})", customerId, priority);

        try {
            String personaPrompt = switch (priority) {
                case VIP -> "당신은 VIP 전담 매니저입니다. 최상의 예우를 갖추고 전용 혜택을 강조하세요.";
                case URGENT -> "당신은 긴급 대응 전문가입니다. 신속하고 간결한 해결책 제시에 집중하세요.";
                case NORMAL -> BASE_SYSTEM_PROMPT;
            };

            String response = chatClientBuilder.build().prompt()
                    .system(personaPrompt)
                    .tools(customerSupportTools)
                    .user(formatUserMessage(customerId, message))
                    .call()
                    .content();

            return CustomerSupportResponse.builder()
                    .customerId(customerId)
                    .request(message)
                    .response(response)
                    .priority(priority)
                    .followUpAction(performFollowUp(customerId, priority))
                    .build();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new DomainException(DomainExceptionCode.AI_RESPONSE_ERROR);
        }
    }

    /**
     * 3. 감정 분석 기반 대응 분노를 감지하면 자동으로 상급자에게 보고(Escalation)합니다.
     */
    public String handleCustomerRequestWithSentiment(String customerId, String message) {
        log.info("[Sentiment Agent] 감정 분석 시작: {}", customerId);

        try {
            CustomerSentiment sentiment = analyzeSentiment(message);
            log.info("[Sentiment Agent] 분석된 감정: {}", sentiment);

            String emotionalPrompt = switch (sentiment) {
                case ANGRY -> "고객이 매우 화가 났습니다. 진심 어린 사과를 우선시하고 즉각적인 보상안을 검토하세요.";
                case FRUSTRATED -> "고객이 불편을 겪고 있습니다. 깊은 공감을 표현하며 해결 과정을 친절히 안내하세요.";
                default -> BASE_SYSTEM_PROMPT;
            };

            String response = chatClientBuilder.build().prompt()
                    .system(emotionalPrompt)
                    .tools(customerSupportTools)
                    .user(formatUserMessage(customerId, message))
                    .call()
                    .content();

            if (sentiment == CustomerSentiment.ANGRY) {
                escalateToSupervisor(customerId, message, response);
            }

            return response;
        } catch (DomainException e) {
            throw e;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new DomainException(DomainExceptionCode.AI_RESPONSE_ERROR);
        }
    }

    /**
     * 4. 다국어 지원 글로벌 고객을 위해 지정된 언어로 페르소나를 전환합니다.
     */
    public String handleMultilingualRequest(String customerId, String message, String language) {
        log.info("[Multilingual Agent] 언어: {}, 고객ID: {}", language, customerId);

        try {
            String langPrompt = "당신은 %s 전문 상담원입니다. 모든 답변은 반드시 %s로 작성하세요."
                    .formatted(language, language);

            return chatClientBuilder.build().prompt()
                    .system(langPrompt)
                    .tools(customerSupportTools)
                    .user(formatUserMessage(customerId, message))
                    .call()
                    .content();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new DomainException(DomainExceptionCode.AI_RESPONSE_ERROR);
        }
    }

    // --- [Helper Methods] ---

    private String formatUserMessage(String customerId, String message) {
        return "고객ID: %s\n요청내용: %s".formatted(customerId, message);
    }

    private CustomerSentiment analyzeSentiment(String text) {
        String sentimentStr = chatClientBuilder.build().prompt()
                .system("고객 메시지를 HAPPY, NEUTRAL, FRUSTRATED, ANGRY 중 하나로 분류하세요. 단어만 응답하세요.")
                .user(text)
                .call()
                .content()
                .trim()
                .toUpperCase();

        try {
            return CustomerSentiment.valueOf(sentimentStr);
        } catch (Exception e) {
            log.warn("[Sentiment] 파싱 실패, NEUTRAL로 기본 처리: {}", sentimentStr);
            return CustomerSentiment.NEUTRAL;
        }
    }

    private String performFollowUp(String customerId, CustomerPriority priority) {
        return "시스템 기록 완료: [" + priority.getDescription() + "] 등급에 따른 프로토콜 적용";
    }

    private void escalateToSupervisor(String customerId, String message, String response) {
        log.error("[에스컬레이션] 상급자 개입 필요 - 고객: {}, 요청: {}", customerId, message);
    }

}