/*
 * Copyright (c) 2022, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package com.farao_community.farao.gridcapa.job_launcher;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author Mohamed Benrejeb {@literal <mohamed.ben-rejeb at rte-france.com>}
 */
class RaoRunnerLogsModelTest {

    @Test
    void testRaoLogsModel() {
        RaoRunnerLogsModel raoRunnerLogsModel =  new RaoRunnerLogsModel("process-id", "rao-computation-id", "client-id", "WARN", "ts", "message", "service-name", "prefix");
        assertEquals("process-id", raoRunnerLogsModel.getGridcapaTaskId());
        assertEquals("rao-computation-id", raoRunnerLogsModel.getComputationId());
        assertEquals("client-id", raoRunnerLogsModel.getClientAppId());
        assertEquals("WARN", raoRunnerLogsModel.getLevel());
        assertEquals("ts", raoRunnerLogsModel.getTimestamp());
        assertEquals("message", raoRunnerLogsModel.getMessage());
        assertEquals("service-name", raoRunnerLogsModel.getServiceName());
        assertEquals("prefix", raoRunnerLogsModel.getEventPrefix());
    }

    @Test
    void testRaoLogsModelWithoutEventPrefix() {
        RaoRunnerLogsModel raoRunnerLogsModel =  new RaoRunnerLogsModel("process-id", "rao-computation-id", "client-id", "WARN", "ts", "message", "service-name");
        assertEquals("process-id", raoRunnerLogsModel.getGridcapaTaskId());
        assertEquals("rao-computation-id", raoRunnerLogsModel.getComputationId());
        assertEquals("client-id", raoRunnerLogsModel.getClientAppId());
        assertEquals("WARN", raoRunnerLogsModel.getLevel());
        assertEquals("ts", raoRunnerLogsModel.getTimestamp());
        assertEquals("message", raoRunnerLogsModel.getMessage());
        assertEquals("service-name", raoRunnerLogsModel.getServiceName());
        assertNull(raoRunnerLogsModel.getEventPrefix());
    }
}
