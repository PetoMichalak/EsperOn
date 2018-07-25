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
package eu.uk.ncl.pet5o.esper.epl.expression.subquery;

import eu.uk.ncl.pet5o.esper.client.EventBean;
import eu.uk.ncl.pet5o.esper.epl.agg.service.common.AggregationService;
import eu.uk.ncl.pet5o.esper.epl.expression.core.ExprEvaluatorContext;

import java.util.ArrayDeque;
import java.util.Collection;
import java.util.Map;

/**
 * Represents a subselect in an expression tree.
 */
public class SubselectEvalStrategyRowUnfilteredSelectedGroupedNoHaving extends SubselectEvalStrategyRowUnfilteredSelected
        implements SubselectEvalStrategyRow {

    public Object evaluate(eu.uk.ncl.pet5o.esper.client.EventBean[] eventsPerStream, boolean newData, Collection<EventBean> matchingEvents, ExprEvaluatorContext exprEvaluatorContext, ExprSubselectRowNode parent) {

        Collection<Object> groupKeys = parent.getSubselectAggregationService().getGroupKeys(exprEvaluatorContext);
        if (groupKeys.isEmpty() || groupKeys.size() > 1) {
            return null;
        }
        parent.getSubselectAggregationService().setCurrentAccess(groupKeys.iterator().next(), exprEvaluatorContext.getAgentInstanceId(), null);

        return super.evaluate(eventsPerStream, newData, matchingEvents, exprEvaluatorContext, parent);
    }

    public Collection evaluateGetCollScalar(eu.uk.ncl.pet5o.esper.client.EventBean[] eventsPerStream, boolean isNewData, Collection<EventBean> matchingEvents, ExprEvaluatorContext context, ExprSubselectRowNode parent) {
        return null;
    }

    public eu.uk.ncl.pet5o.esper.client.EventBean evaluateGetEventBean(eu.uk.ncl.pet5o.esper.client.EventBean[] eventsPerStream, boolean newData, Collection<EventBean> matchingEvents, ExprEvaluatorContext context, ExprSubselectRowNode parent) {
        return null;
    }

    public Collection<EventBean> evaluateGetCollEvents(eu.uk.ncl.pet5o.esper.client.EventBean[] eventsPerStream, boolean newData, Collection<EventBean> matchingEvents, ExprEvaluatorContext context, ExprSubselectRowNode parent) {
        AggregationService aggregationService = parent.getSubselectAggregationService().getContextPartitionAggregationService(context.getAgentInstanceId());
        Collection<Object> groupKeys = aggregationService.getGroupKeys(context);
        if (groupKeys.isEmpty()) {
            return null;
        }
        Collection<EventBean> events = new ArrayDeque<EventBean>(groupKeys.size());
        for (Object groupKey : groupKeys) {
            aggregationService.setCurrentAccess(groupKey, context.getAgentInstanceId(), null);
            Map<String, Object> row = parent.evaluateRow(null, true, context);
            eu.uk.ncl.pet5o.esper.client.EventBean event = parent.subselectMultirowType.getEventAdapterService().adapterForTypedMap(row, parent.subselectMultirowType.getEventType());
            events.add(event);
        }
        return events;
    }
}
