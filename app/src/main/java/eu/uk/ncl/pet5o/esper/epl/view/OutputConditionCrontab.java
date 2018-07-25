/*
 ***************************************************************************************
 *  Copyright (C) 2006 EsperTech, Inc. All rights reserved.                            *
 *  http://www.espertech.com/esper                                                     *
 *  http://www.espertech.com                                                           *
 *  ---------------------------------------------------------------------------------- *
 *  The software in this package is published under the terms of the GPL license       *
 *  a copy of which has been included with this distribution in the license.txt file.  *
 ***************************************************************************************
 */
package eu.uk.ncl.pet5o.esper.epl.view;

import eu.uk.ncl.pet5o.esper.core.context.util.AgentInstanceContext;
import eu.uk.ncl.pet5o.esper.core.service.EPStatementHandleCallback;
import eu.uk.ncl.pet5o.esper.core.service.EngineLevelExtensionServicesContext;
import eu.uk.ncl.pet5o.esper.epl.core.engineimport.EngineImportService;
import eu.uk.ncl.pet5o.esper.metrics.instrumentation.InstrumentationHelper;
import eu.uk.ncl.pet5o.esper.schedule.ScheduleComputeHelper;
import eu.uk.ncl.pet5o.esper.schedule.ScheduleHandleCallback;
import eu.uk.ncl.pet5o.esper.schedule.ScheduleSpec;
import eu.uk.ncl.pet5o.esper.schedule.SchedulingService;
import eu.uk.ncl.pet5o.esper.util.ExecutionPathDebugLog;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Output condition handling crontab-at schedule output.
 */
public final class OutputConditionCrontab extends OutputConditionBase implements OutputCondition {
    private static final boolean DO_OUTPUT = true;
    private static final boolean FORCE_UPDATE = true;

    private final AgentInstanceContext context;
    private final ScheduleSpec scheduleSpec;

    private final long scheduleSlot;
    private Long currentReferencePoint;
    private boolean isCallbackScheduled;

    public OutputConditionCrontab(OutputCallback outputCallback, AgentInstanceContext context, boolean isStartConditionOnCreation, ScheduleSpec scheduleSpec) {
        super(outputCallback);
        this.context = context;
        this.scheduleSpec = scheduleSpec;
        scheduleSlot = context.getStatementContext().getScheduleBucket().allocateSlot();
        if (isStartConditionOnCreation) {
            updateOutputCondition(0, 0);
        }
    }

    public final void updateOutputCondition(int newEventsCount, int oldEventsCount) {
        if ((ExecutionPathDebugLog.isDebugEnabled) && (log.isDebugEnabled())) {
            log.debug(".updateOutputCondition, " +
                    "  newEventsCount==" + newEventsCount +
                    "  oldEventsCount==" + oldEventsCount);
        }

        if (currentReferencePoint == null) {
            currentReferencePoint = context.getStatementContext().getSchedulingService().getTime();
        }

        // Schedule the next callback if there is none currently scheduled
        if (!isCallbackScheduled) {
            scheduleCallback();
        }
    }

    public final String toString() {
        return this.getClass().getName() +
                " spec=" + scheduleSpec;
    }

    private void scheduleCallback() {
        isCallbackScheduled = true;
        long current = context.getStatementContext().getSchedulingService().getTime();

        if ((ExecutionPathDebugLog.isDebugEnabled) && (log.isDebugEnabled())) {
            log.debug(".scheduleCallback Scheduled new callback for " +
                    " now=" + current +
                    " currentReferencePoint=" + currentReferencePoint +
                    " spec=" + scheduleSpec);
        }

        ScheduleHandleCallback callback = new ScheduleHandleCallback() {
            public void scheduledTrigger(EngineLevelExtensionServicesContext extensionServicesContext) {
                if (InstrumentationHelper.ENABLED) {
                    InstrumentationHelper.get().qOutputRateConditionScheduledEval();
                }
                OutputConditionCrontab.this.isCallbackScheduled = false;
                OutputConditionCrontab.this.outputCallback.continueOutputProcessing(DO_OUTPUT, FORCE_UPDATE);
                scheduleCallback();
                if (InstrumentationHelper.ENABLED) {
                    InstrumentationHelper.get().aOutputRateConditionScheduledEval();
                }
            }
        };
        EPStatementHandleCallback handle = new EPStatementHandleCallback(context.getEpStatementAgentInstanceHandle(), callback);
        SchedulingService schedulingService = context.getStatementContext().getSchedulingService();
        EngineImportService engineImportService = context.getStatementContext().getEngineImportService();
        long nextScheduledTime = ScheduleComputeHelper.computeDeltaNextOccurance(scheduleSpec, schedulingService.getTime(), engineImportService.getTimeZone(), engineImportService.getTimeAbacus());
        schedulingService.add(nextScheduledTime, handle, scheduleSlot);
    }

    public void terminated() {
        outputCallback.continueOutputProcessing(true, true);
    }

    public void stop() {
        // no action required
    }

    private static final Logger log = LoggerFactory.getLogger(OutputConditionCrontab.class);
}
