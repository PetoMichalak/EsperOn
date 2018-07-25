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
import eu.uk.ncl.pet5o.esper.client.EventType;
import eu.uk.ncl.pet5o.esper.core.context.util.AgentInstanceContext;
import eu.uk.ncl.pet5o.esper.core.service.EPStatementHandle;
import eu.uk.ncl.pet5o.esper.core.service.InternalEventRouter;
import eu.uk.ncl.pet5o.esper.core.start.EPStatementStartMethodOnTriggerItem;
import eu.uk.ncl.pet5o.esper.epl.core.resultset.core.ResultSetProcessor;
import eu.uk.ncl.pet5o.esper.epl.expression.core.ExprEvaluator;
import eu.uk.ncl.pet5o.esper.epl.expression.core.ExprEvaluatorContext;
import eu.uk.ncl.pet5o.esper.epl.table.mgmt.TableStateInstance;
import eu.uk.ncl.pet5o.esper.util.CollectionUtil;
import eu.uk.ncl.pet5o.esper.view.ViewSupport;

import java.util.Iterator;

/**
 * View for processing split-stream syntax.
 */
public class RouteResultView extends ViewSupport {
    private final EventType eventType;
    private RouteResultViewHandler handler;
    private ExprEvaluatorContext exprEvaluatorContext;

    public RouteResultView(boolean isFirst,
                           EventType eventType,
                           EPStatementHandle epStatementHandle,
                           InternalEventRouter internalEventRouter,
                           TableStateInstance[] tableStateInstances,
                           EPStatementStartMethodOnTriggerItem[] items,
                           ResultSetProcessor[] processors,
                           ExprEvaluator[] whereClauses,
                           AgentInstanceContext agentInstanceContext) {
        if (whereClauses.length != processors.length) {
            throw new IllegalArgumentException("Number of where-clauses and processors does not match");
        }

        this.exprEvaluatorContext = agentInstanceContext;
        this.eventType = eventType;
        if (isFirst) {
            handler = new RouteResultViewHandlerFirst(epStatementHandle, internalEventRouter, tableStateInstances, items, processors, whereClauses, agentInstanceContext);
        } else {
            handler = new RouteResultViewHandlerAll(epStatementHandle, internalEventRouter, tableStateInstances, items, processors, whereClauses, agentInstanceContext);
        }
    }

    public void update(EventBean[] newData, EventBean[] oldData) {
        if (newData == null) {
            return;
        }

        for (EventBean bean : newData) {
            boolean isHandled = handler.handle(bean, exprEvaluatorContext);

            if (!isHandled) {
                updateChildren(new EventBean[]{bean}, null);
            }
        }
    }

    public EventType getEventType() {
        return eventType;
    }

    public Iterator<EventBean> iterator() {
        return CollectionUtil.NULL_EVENT_ITERATOR;
    }
}
