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
package eu.uk.ncl.pet5o.esper.epl.named;

import eu.uk.ncl.pet5o.esper.client.ConfigurationEngineDefaults;
import eu.uk.ncl.pet5o.esper.client.EventType;
import eu.uk.ncl.pet5o.esper.core.context.util.EPStatementAgentInstanceHandle;
import eu.uk.ncl.pet5o.esper.core.service.StatementContext;
import eu.uk.ncl.pet5o.esper.core.service.StatementResultService;
import eu.uk.ncl.pet5o.esper.epl.metric.MetricReportingService;
import eu.uk.ncl.pet5o.esper.event.vaevent.ValueAddEventProcessor;
import eu.uk.ncl.pet5o.esper.timer.TimeSourceService;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Service to manage named window dispatches, locks and processors on an engine level.
 */
public interface NamedWindowDispatchService {
    NamedWindowProcessor createProcessor(String name, NamedWindowMgmtServiceImpl namedWindowMgmtService, NamedWindowDispatchService namedWindowDispatchService, String contextName, EventType eventType, StatementResultService statementResultService, ValueAddEventProcessor revisionProcessor, String eplExpression, String statementName, boolean isPrioritized, boolean isEnableSubqueryIndexShare, boolean enableQueryPlanLog, MetricReportingService metricReportingService, boolean isBatchingDataWindow, boolean isVirtualDataWindow, Set<String> optionalUniqueKeyProps, String eventTypeAsName, StatementContext statementContextCreateWindow);

    NamedWindowTailView createTailView(EventType eventType, NamedWindowMgmtService namedWindowMgmtService, NamedWindowDispatchService namedWindowDispatchService, StatementResultService statementResultService, ValueAddEventProcessor revisionProcessor, boolean prioritized, boolean parentBatchWindow, String contextName, TimeSourceService timeSourceService, ConfigurationEngineDefaults.Threading threadingConfig);

    /**
     * Dispatch events of the insert and remove stream of named windows to consumers, as part of the
     * main event processing or dispatch loop.
     *
     * @return send events to consuming statements
     */
    boolean dispatch();

    /**
     * For use to add a result of a named window that must be dispatched to consuming views.
     *
     * @param delta        is the result to dispatch
     * @param consumers    is the destination of the dispatch, a map of statements to one or more consuming views
     * @param latchFactory latch factory
     */
    void addDispatch(NamedWindowConsumerLatchFactory latchFactory, NamedWindowDeltaData delta, Map<EPStatementAgentInstanceHandle, List<NamedWindowConsumerView>> consumers);

    /**
     * Destroy service.
     */
    void destroy();
}
