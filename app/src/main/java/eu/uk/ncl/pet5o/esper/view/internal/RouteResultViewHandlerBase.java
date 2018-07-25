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
package eu.uk.ncl.pet5o.esper.view.internal;

import eu.uk.ncl.pet5o.esper.client.EventBean;
import eu.uk.ncl.pet5o.esper.client.annotation.AuditEnum;
import eu.uk.ncl.pet5o.esper.collection.UniformPair;
import eu.uk.ncl.pet5o.esper.core.context.util.AgentInstanceContext;
import eu.uk.ncl.pet5o.esper.core.service.EPStatementHandle;
import eu.uk.ncl.pet5o.esper.core.service.InternalEventRouter;
import eu.uk.ncl.pet5o.esper.core.start.EPStatementStartMethodOnTriggerItem;
import eu.uk.ncl.pet5o.esper.epl.core.resultset.core.ResultSetProcessor;
import eu.uk.ncl.pet5o.esper.epl.expression.core.ExprEvaluator;
import eu.uk.ncl.pet5o.esper.epl.expression.core.ExprEvaluatorContext;
import eu.uk.ncl.pet5o.esper.epl.table.mgmt.TableStateInstance;
import eu.uk.ncl.pet5o.esper.metrics.instrumentation.InstrumentationHelper;
import eu.uk.ncl.pet5o.esper.util.AuditPath;

public abstract class RouteResultViewHandlerBase implements RouteResultViewHandler {
    protected final InternalEventRouter internalEventRouter;
    private final TableStateInstance[] tableStateInstances;
    protected final EPStatementStartMethodOnTriggerItem[] items;
    protected final EPStatementHandle epStatementHandle;
    protected final ResultSetProcessor[] processors;
    protected final ExprEvaluator[] whereClauses;
    protected final EventBean[] eventsPerStream = new EventBean[1];
    protected final AgentInstanceContext agentInstanceContext;
    protected final boolean audit;

    public RouteResultViewHandlerBase(EPStatementHandle epStatementHandle, InternalEventRouter internalEventRouter, TableStateInstance[] tableStateInstances, EPStatementStartMethodOnTriggerItem[] items, ResultSetProcessor[] processors, ExprEvaluator[] whereClauses, AgentInstanceContext agentInstanceContext) {
        this.internalEventRouter = internalEventRouter;
        this.tableStateInstances = tableStateInstances;
        this.items = items;
        this.epStatementHandle = epStatementHandle;
        this.processors = processors;
        this.whereClauses = whereClauses;
        this.agentInstanceContext = agentInstanceContext;
        this.audit = AuditEnum.INSERT.getAudit(agentInstanceContext.getAnnotations()) != null;
    }

    boolean checkWhereClauseCurrentEvent(int index, ExprEvaluatorContext exprEvaluatorContext) {
        boolean pass = true;

        if (whereClauses[index] != null) {
            if (InstrumentationHelper.ENABLED) {
                InstrumentationHelper.get().qSplitStreamWhere(index);
            }
            Boolean passEvent = (Boolean) whereClauses[index].evaluate(eventsPerStream, true, exprEvaluatorContext);
            if ((passEvent == null) || (!passEvent)) {
                pass = false;
            }
            if (InstrumentationHelper.ENABLED) {
                InstrumentationHelper.get().aSplitStreamWhere(pass);
            }
        }

        return pass;
    }

    boolean mayRouteCurrentEvent(int index, ExprEvaluatorContext exprEvaluatorContext) {
        if (InstrumentationHelper.ENABLED) {
            InstrumentationHelper.get().qSplitStreamRoute(index);
        }
        UniformPair<EventBean[]> result = processors[index].processViewResult(eventsPerStream, null, false);
        boolean routed = false;
        if ((result != null) && (result.getFirst() != null) && (result.getFirst().length > 0)) {
            route(result.getFirst()[0], index, exprEvaluatorContext);
            routed = true;
        }
        if (InstrumentationHelper.ENABLED) {
            InstrumentationHelper.get().aSplitStreamRoute();
        }
        return routed;
    }

    private void route(EventBean routed, int index, ExprEvaluatorContext exprEvaluatorContext) {
        if (audit) {
            AuditPath.auditInsertInto(agentInstanceContext.getEngineURI(), agentInstanceContext.getStatementName(), routed);
        }
        TableStateInstance tableStateInstance = tableStateInstances[index];
        if (tableStateInstance != null) {
            tableStateInstance.addEventUnadorned(routed);
        } else {
            boolean isNamedWindowInsert = items[index].isNamedWindowInsert();
            internalEventRouter.route(routed, epStatementHandle, agentInstanceContext.getStatementContext().getInternalEventEngineRouteDest(), exprEvaluatorContext, isNamedWindowInsert);
        }
    }
}
