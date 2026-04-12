package com.sparta.msa.lesson.domain.agent.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.ArrayList;
import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class MultiAgentResult {

    String problem;

    @Builder.Default
    List<AgentExecution> executions = new ArrayList<>();

    String finalOutput;

    public void addExecution(AgentExecution execution) {
        this.executions.add(execution);
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @FieldDefaults(level = AccessLevel.PRIVATE)
    public static class AgentExecution {

        String agentName;
        String taskDescription;
        String output;
    }

}