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
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;

import java.util.function.Consumer;

import com.farao_community.farao.gridcapa.task_manager.api.TaskLogEventUpdate;

/**
 * @author Mohamed Benrejeb {@literal <mohamed.ben-rejeb at rte-france.com>}
 */
@Service
public class RaoLogsDispatcherService {

    private static final Logger LOGGER = LoggerFactory.getLogger(RaoLogsDispatcherService.class);

    @Value("${client.name}")
    private String clientName;

    private static final String DESTINATION_LOGS_BINDING = "dispatch-logs";

    private final StreamBridge streamBridge;

    public RaoLogsDispatcherService(StreamBridge streamBridge) {
        this.streamBridge = streamBridge;
    }

    @Bean
    public Consumer<String> dispatchRaoLogsEvents() {
        return logEventString -> {
            try {
                ObjectMapper objectMapper = new ObjectMapper();
                RaoRunnerLogsModel raoLog = objectMapper.readValue(logEventString, RaoRunnerLogsModel.class);
                if (raoLog.getClientAppId().equals(clientName)) {

                    streamBridge.send(DESTINATION_LOGS_BINDING, convertToTaskManagerEventModel(raoLog));
                }
            } catch (JsonProcessingException e) {
                LOGGER.warn("parsing exception occurred while reading log event", e);
            }
        };
    }

    private TaskLogEventUpdate convertToTaskManagerEventModel(RaoRunnerLogsModel raoLog) {
        return new TaskLogEventUpdate(raoLog.getGridcapaTaskId(),
            raoLog.getTimestamp(),
            raoLog.getLevel(),
            raoLog.getMessage(),
            raoLog.getServiceName());
    }
}
