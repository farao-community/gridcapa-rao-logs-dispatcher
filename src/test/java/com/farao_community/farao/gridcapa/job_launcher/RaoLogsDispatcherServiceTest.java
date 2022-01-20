/*
 * Copyright (c) 2022, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package com.farao_community.farao.gridcapa.job_launcher;

import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.ThrowingSupplier;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;


/**
 * @author Mohamed Benrejeb {@literal <mohamed.ben-rejeb at rte-france.com>}
 */
@SpringBootTest
class RaoLogsDispatcherServiceTest {

    @Autowired
    RaoLogsDispatcherService raoLogsDispatcherService;

    @Test
    void checkThatRaoLogsAreConvertedAndSentWithoutException() {
        String logEvent = "{\n" +
            "  \"clientAppId\": \"cse-idcc-runner\",\n" +
            "  \"computationId\": \"54\",\n" +
            "  \"gridcapaTaskId\": \"1fdda469-53e9-4d63-a533-b935cffdd2f6\",\n" +
            "  \"timestamp\": \"2021-12-30T17:31:33.030+01:00\",\n" +
            "  \"level\": \"INFO\",\n" +
            "  \"message\": \"Hello from RAO-RUNNER\",\n" +
            "  \"serviceName\": \"RAO-RUNNER\" \n" +
            "}";

        raoLogsDispatcherService.dispatchRaoLogsEvents().accept(logEvent);
        assertDoesNotThrow((ThrowingSupplier<Exception>) Exception::new);
    }

    @Test
    void checkThatExceptionIsThrownIfRaoLogsDoesNotMatchExpectedModel() {
        String logEvent = "{\n" +
            "  \"clientAppId\": \"cse-idcc-runner\",\n" +
            "  \"UnKnownField\": \"1fdda469-53e9-4d63-a533-b935cffdd2f6\",\n" +
            "}";
        Logger logger = (Logger) LoggerFactory.getLogger(RaoLogsDispatcherService.class);
        ListAppender<ILoggingEvent> listAppender = new ListAppender<>();
        listAppender.start();
        logger.addAppender(listAppender);
        raoLogsDispatcherService.dispatchRaoLogsEvents().accept(logEvent);
        assertEquals("com.fasterxml.jackson.core.JsonParseException", listAppender.list.get(0).getThrowableProxy().getClassName());
        assertEquals("WARN", listAppender.list.get(0).getLevel().toString());
    }
}
