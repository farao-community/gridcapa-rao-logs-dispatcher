/*
 * Copyright (c) 2022, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package com.farao_community.farao.gridcapa.job_launcher;

import com.farao_community.farao.gridcapa.task_manager.api.TaskLogEventUpdate;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import reactor.core.publisher.Flux;

import static org.junit.jupiter.api.Assertions.*;


/**
 * @author Mohamed Benrejeb {@literal <mohamed.ben-rejeb at rte-france.com>}
 */
@SpringBootTest
class RaoLogsDispatcherServiceTest {

    @Autowired
    RaoLogsDispatcherService raoLogsDispatcherService;

    @Test
    void dispatchingTest() {
        String logEvent = """
                {
                  "clientAppId": "client-app-id",
                  "computationId": "54",
                  "gridcapaTaskId": "1fdda469-53e9-4d63-a533-b935cffdd2f6",
                  "timestamp": "2021-12-30T17:31:33.030+01:00",
                  "level": "INFO",
                  "message": "Hello from RAO-RUNNER",
                  "serviceName": "RAO-RUNNER"\s
                }""";
        Flux<TaskLogEventUpdate> taskLogEventUpdateFlux = raoLogsDispatcherService.dispatchRaoEvents().apply(Flux.just(logEvent));
        assertTrue(taskLogEventUpdateFlux.toIterable().iterator().hasNext());
        assertEquals("Hello from RAO-RUNNER", taskLogEventUpdateFlux.blockFirst().getMessage());
    }

    @Test
    void checkThatRaoLogsParsedCorrectly() {
        String logEvent = """
                {
                  "clientAppId": "cse-idcc-runner",
                  "computationId": "54",
                  "gridcapaTaskId": "1fdda469-53e9-4d63-a533-b935cffdd2f6",
                  "timestamp": "2021-12-30T17:31:33.030+01:00",
                  "level": "INFO",
                  "message": "Hello from RAO-RUNNER",
                  "serviceName": "RAO-RUNNER"\s
                }""";

        RaoRunnerLogsModel raoRunnerLogsModel = raoLogsDispatcherService.parseLog(logEvent);
        assertEquals("2021-12-30T17:31:33.030+01:00", raoRunnerLogsModel.getTimestamp());
        assertEquals("cse-idcc-runner", raoRunnerLogsModel.getClientAppId());
    }

    @Test
    void checkThatNoExceptionIsThrownIfRaoLogsDoesNotMatchExpectedModel() {
        String logEvent = """
                {
                  "clientAppId": "cse-idcc-runner",
                  "UnKnownField": "1fdda469-53e9-4d63-a533-b935cffdd2f6",
                }""";
        assertDoesNotThrow(() -> raoLogsDispatcherService.parseLog(logEvent));
    }

    @Test
    void checkThatEventIsIgnoredIfRaoLogsDoesNotMatchExpectedModel() {
        String logEvent = """
                {
                  "clientAppId": "cse-idcc-runner",
                  "UnKnownField": "1fdda469-53e9-4d63-a533-b935cffdd2f6",
                }""";
        Flux<TaskLogEventUpdate> taskLogEventUpdateFlux = raoLogsDispatcherService.dispatchRaoEvents().apply(Flux.just(logEvent));
        assertFalse(taskLogEventUpdateFlux.toIterable().iterator().hasNext());
    }
}
