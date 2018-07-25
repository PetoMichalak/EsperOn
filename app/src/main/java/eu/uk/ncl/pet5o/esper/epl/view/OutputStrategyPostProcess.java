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

import eu.uk.ncl.pet5o.esper.client.EventBean;
import eu.uk.ncl.pet5o.esper.client.annotation.AuditEnum;
import eu.uk.ncl.pet5o.esper.collection.UniformPair;
import eu.uk.ncl.pet5o.esper.core.context.util.AgentInstanceContext;
import eu.uk.ncl.pet5o.esper.core.service.UpdateDispatchView;
import eu.uk.ncl.pet5o.esper.epl.expression.core.ExprEvaluatorContext;
import eu.uk.ncl.pet5o.esper.epl.spec.SelectClauseStreamSelectorEnum;
import eu.uk.ncl.pet5o.esper.epl.table.mgmt.TableStateInstance;
import eu.uk.ncl.pet5o.esper.epl.table.strategy.ExprTableEvalLockUtil;
import eu.uk.ncl.pet5o.esper.event.NaturalEventBean;
import eu.uk.ncl.pet5o.esper.util.AuditPath;

/**
 * An output strategy that handles routing (insert-into) and stream selection.
 */
public class OutputStrategyPostProcess {
    private final OutputStrategyPostProcessFactory parent;
    private final AgentInstanceContext agentInstanceContext;
    private final TableStateInstance tableStateInstance;
    private final boolean audit;

    public OutputStrategyPostProcess(OutputStrategyPostProcessFactory parent, AgentInstanceContext agentInstanceContext, TableStateInstance tableStateInstance) {
        this.parent = parent;
        this.agentInstanceContext = agentInstanceContext;
        this.tableStateInstance = tableStateInstance;
        this.audit = AuditEnum.INSERT.getAudit(agentInstanceContext.getAnnotations()) != null;
    }

    public void output(boolean forceUpdate, UniformPair<EventBean[]> result, UpdateDispatchView finalView) {
        EventBean[] newEvents = result != null ? result.getFirst() : null;
        EventBean[] oldEvents = result != null ? result.getSecond() : null;

        // route first
        if (parent.isRoute()) {
            if ((newEvents != null) && (parent.getInsertIntoStreamSelector().isSelectsIStream())) {
                route(newEvents, agentInstanceContext);
            }

            if ((oldEvents != null) && (parent.getInsertIntoStreamSelector().isSelectsRStream())) {
                route(oldEvents, agentInstanceContext);
            }
        }

        // discard one side of results
        if (parent.getSelectStreamDirEnum() == SelectClauseStreamSelectorEnum.RSTREAM_ONLY) {
            newEvents = oldEvents;
            oldEvents = null;
        } else if (parent.getSelectStreamDirEnum() == SelectClauseStreamSelectorEnum.ISTREAM_ONLY) {
            oldEvents = null;   // since the insert-into may require rstream
        }

        // dispatch
        if (newEvents != null || oldEvents != null) {
            finalView.newResult(new UniformPair<EventBean[]>(newEvents, oldEvents));
        } else if (forceUpdate) {
            finalView.newResult(new UniformPair<EventBean[]>(null, null));
        }
    }

    private void route(EventBean[] events, ExprEvaluatorContext exprEvaluatorContext) {
        for (EventBean routed : events) {
            if (routed instanceof NaturalEventBean) {
                NaturalEventBean natural = (NaturalEventBean) routed;
                if (audit) {
                    AuditPath.auditInsertInto(agentInstanceContext.getEngineURI(), agentInstanceContext.getStatementName(), natural.getOptionalSynthetic());
                }
                if (tableStateInstance != null) {
                    tableStateInstance.addEventUnadorned(natural.getOptionalSynthetic());
                } else {
                    parent.getInternalEventRouter().route(natural.getOptionalSynthetic(), parent.getEpStatementHandle(), agentInstanceContext.getStatementContext().getInternalEventEngineRouteDest(), exprEvaluatorContext, parent.isAddToFront());
                }
            } else {
                if (audit) {
                    AuditPath.auditInsertInto(agentInstanceContext.getEngineURI(), agentInstanceContext.getStatementName(), routed);
                }
                if (tableStateInstance != null) {
                    ExprTableEvalLockUtil.obtainLockUnless(tableStateInstance.getTableLevelRWLock().writeLock(), exprEvaluatorContext);
                    tableStateInstance.addEventUnadorned(routed);
                } else {
                    parent.getInternalEventRouter().route(routed, parent.getEpStatementHandle(), agentInstanceContext.getStatementContext().getInternalEventEngineRouteDest(), exprEvaluatorContext, parent.isAddToFront());
                }
            }
        }
    }
}
