/*
 * Copyright (c) 2022, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package com.farao_community.farao.gridcapa.job_launcher;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;

import java.util.function.Function;

import com.farao_community.farao.gridcapa.task_manager.api.TaskLogEventUpdate;
import reactor.core.publisher.Flux;

/**
 * @author Mohamed Benrejeb {@literal <mohamed.ben-rejeb at rte-france.com>}
 */
@Service
public class RaoLogsDispatcherService {

    private static final Logger LOGGER = LoggerFactory.getLogger(RaoLogsDispatcherService.class);

    ObjectMapper objectMapper = new ObjectMapper();

    @Value("${client.name}")
    private String clientName;

    @Bean
    public Function<Flux<String>, Flux<TaskLogEventUpdate>> dispatchRaoEvents() {
        return f -> f
            .onErrorContinue((t, r) -> LOGGER.error(t.getMessage(), t))
            .map(this::parseLog)
            .filter(raoRunnerLogsModel -> raoRunnerLogsModel.getClientAppId().equals(clientName))
            .map(log -> new TaskLogEventUpdate(log.getGridcapaTaskId(), log.getTimestamp(), log.getLevel(), log.getMessage(), log.getServiceName()));
    }

    RaoRunnerLogsModel parseLog(String logEventString) {
        try {
            return objectMapper.readValue(logEventString, RaoRunnerLogsModel.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("parsing exception occurred while reading log event", e);
        }
    }

}
