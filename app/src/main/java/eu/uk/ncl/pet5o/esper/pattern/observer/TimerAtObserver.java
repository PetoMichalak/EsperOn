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
package eu.uk.ncl.pet5o.esper.pattern.observer;

import eu.uk.ncl.pet5o.esper.core.service.EPStatementHandleCallback;
import eu.uk.ncl.pet5o.esper.core.service.EngineLevelExtensionServicesContext;
import eu.uk.ncl.pet5o.esper.epl.core.engineimport.EngineImportService;
import eu.uk.ncl.pet5o.esper.filterspec.MatchedEventMap;
import eu.uk.ncl.pet5o.esper.metrics.instrumentation.InstrumentationHelper;
import eu.uk.ncl.pet5o.esper.schedule.ScheduleComputeHelper;
import eu.uk.ncl.pet5o.esper.schedule.ScheduleHandleCallback;
import eu.uk.ncl.pet5o.esper.schedule.ScheduleSpec;
import eu.uk.ncl.pet5o.esper.schedule.SchedulingService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Observer implementation for indicating that a certain time arrived, similar to "crontab".
 */
public class TimerAtObserver implements EventObserver, ScheduleHandleCallback {
    private final ScheduleSpec scheduleSpec;
    private final long scheduleSlot;
    private final MatchedEventMap beginState;
    private final ObserverEventEvaluator observerEventEvaluator;

    private boolean isTimerActive = false;
    private EPStatementHandleCallback scheduleHandle;

    /**
     * Ctor.
     *
     * @param scheduleSpec           - specification containing the crontab schedule
     * @param beginState             - start state
     * @param observerEventEvaluator - receiver for events
     */
    public TimerAtObserver(ScheduleSpec scheduleSpec, MatchedEventMap beginState, ObserverEventEvaluator observerEventEvaluator) {
        this.scheduleSpec = scheduleSpec;
        this.beginState = beginState;
        this.observerEventEvaluator = observerEventEvaluator;
        this.scheduleSlot = observerEventEvaluator.getContext().getPatternContext().getScheduleBucket().allocateSlot();
    }

    public MatchedEventMap getBeginState() {
        return beginState;
    }

    public final void scheduledTrigger(EngineLevelExtensionServicesContext engineLevelExtensionServicesContext) {
        if (InstrumentationHelper.ENABLED) {
            InstrumentationHelper.get().qPatternObserverScheduledEval();
        }
        observerEventEvaluator.observerEvaluateTrue(beginState, true);
        isTimerActive = false;
        if (InstrumentationHelper.ENABLED) {
            InstrumentationHelper.get().aPatternObserverScheduledEval();
        }
    }

    public void startObserve() {
        if (isTimerActive) {
            throw new IllegalStateException("Timer already active");
        }

        scheduleHandle = new EPStatementHandleCallback(observerEventEvaluator.getContext().getAgentInstanceContext().getEpStatementAgentInstanceHandle(), this);
        SchedulingService schedulingService = observerEventEvaluator.getContext().getPatternContext().getSchedulingService();
        EngineImportService engineImportService = observerEventEvaluator.getContext().getStatementContext().getEngineImportService();
        long nextScheduledTime = ScheduleComputeHelper.computeDeltaNextOccurance(scheduleSpec, schedulingService.getTime(), engineImportService.getTimeZone(), engineImportService.getTimeAbacus());
        schedulingService.add(nextScheduledTime, scheduleHandle, scheduleSlot);
        isTimerActive = true;
    }

    public void stopObserve() {
        if (isTimerActive) {
            observerEventEvaluator.getContext().getPatternContext().getSchedulingService().remove(scheduleHandle, scheduleSlot);
            isTimerActive = false;
            scheduleHandle = null;
        }
    }

    public void accept(EventObserverVisitor visitor) {
        visitor.visitObserver(beginState, 2, scheduleSlot, scheduleSpec);
    }

    private static final Logger log = LoggerFactory.getLogger(TimerAtObserver.class);
}
