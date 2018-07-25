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

import com.espertech.esper.client.EventBean;
import com.espertech.esper.client.EventPropertyGetter;
import com.espertech.esper.epl.expression.core.ExprEvaluatorContext;
import com.espertech.esper.epl.expression.core.ExprIdentNode;
import com.espertech.esper.epl.expression.core.ExprIdentNodeEvaluator;
import com.espertech.esper.event.EventBeanUtility;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class SubselectEvalStrategyRowUnfilteredSelected implements SubselectEvalStrategyRow {

    private static final Logger log = LoggerFactory.getLogger(SubselectEvalStrategyRowUnfilteredSelected.class);

    // No filter and with select clause
    public Object evaluate(com.espertech.esper.client.EventBean[] eventsPerStream, boolean newData, Collection<EventBean> matchingEvents, ExprEvaluatorContext exprEvaluatorContext,
                           ExprSubselectRowNode parent) {
        if (matchingEvents.size() > 1) {
            log.warn(parent.getMultirowMessage());
            return null;
        }

        com.espertech.esper.client.EventBean[] events = EventBeanUtility.allocatePerStreamShift(eventsPerStream);
        events[0] = EventBeanUtility.getNonemptyFirstEvent(matchingEvents);

        Object result;
        if (parent.selectClauseEvaluator.length == 1) {
            result = parent.selectClauseEvaluator[0].evaluate(events, true, exprEvaluatorContext);
        } else {
            // we are returning a Map here, not object-array, preferring the self-describing structure
            result = parent.evaluateRow(events, true, exprEvaluatorContext);
        }
        return result;
    }

    // No filter and with select clause
    public Collection<EventBean> evaluateGetCollEvents(com.espertech.esper.client.EventBean[] eventsPerStream, boolean newData, Collection<EventBean> matchingEvents, ExprEvaluatorContext context, ExprSubselectRowNode parent) {
        if (matchingEvents.size() == 0) {
            return Collections.emptyList();
        }

        // when selecting a single property in the select clause that provides a fragment
        if (parent.subselectMultirowType == null) {
            Collection<EventBean> events = new ArrayDeque<EventBean>(matchingEvents.size());
            ExprIdentNodeEvaluator eval = ((ExprIdentNode) parent.selectClause[0]).getExprEvaluatorIdent();
            EventPropertyGetter getter = eval.getGetter();
            for (com.espertech.esper.client.EventBean subselectEvent : matchingEvents) {
                Object fragment = getter.getFragment(subselectEvent);
                if (fragment == null) {
                    continue;
                }
                events.add((com.espertech.esper.client.EventBean) fragment);
            }
            return events;
        }

        // when selecting a combined output row that contains multiple fields
        Collection<EventBean> events = new ArrayDeque<EventBean>(matchingEvents.size());
        com.espertech.esper.client.EventBean[] eventsPerStreamEval = EventBeanUtility.allocatePerStreamShift(eventsPerStream);
        for (com.espertech.esper.client.EventBean subselectEvent : matchingEvents) {
            eventsPerStreamEval[0] = subselectEvent;
            Map<String, Object> row = parent.evaluateRow(eventsPerStreamEval, true, context);
            com.espertech.esper.client.EventBean event = parent.subselectMultirowType.getEventAdapterService().adapterForTypedMap(row, parent.subselectMultirowType.getEventType());
            events.add(event);
        }
        return events;
    }

    // No filter and with select clause
    public Collection evaluateGetCollScalar(com.espertech.esper.client.EventBean[] eventsPerStream, boolean isNewData, Collection<EventBean> matchingEvents, ExprEvaluatorContext context, ExprSubselectRowNode parent) {
        List<Object> result = new ArrayList<Object>();
        com.espertech.esper.client.EventBean[] events = EventBeanUtility.allocatePerStreamShift(eventsPerStream);
        for (com.espertech.esper.client.EventBean subselectEvent : matchingEvents) {
            events[0] = subselectEvent;
            result.add(parent.selectClauseEvaluator[0].evaluate(events, isNewData, context));
        }
        return result;
    }

    // No filter and with select clause
    public Object[] typableEvaluate(com.espertech.esper.client.EventBean[] eventsPerStream, boolean isNewData, Collection<EventBean> matchingEvents, ExprEvaluatorContext exprEvaluatorContext, ExprSubselectRowNode parent) {
        // take the first match only
        com.espertech.esper.client.EventBean[] events = EventBeanUtility.allocatePerStreamShift(eventsPerStream);
        events[0] = EventBeanUtility.getNonemptyFirstEvent(matchingEvents);
        Object[] results = new Object[parent.selectClauseEvaluator.length];
        for (int i = 0; i < results.length; i++) {
            results[i] = parent.selectClauseEvaluator[i].evaluate(events, isNewData, exprEvaluatorContext);
        }
        return results;
    }

    // No filter and with select clause
    public Object[][] typableEvaluateMultirow(com.espertech.esper.client.EventBean[] eventsPerStream, boolean isNewData, Collection<EventBean> matchingEvents, ExprEvaluatorContext exprEvaluatorContext, ExprSubselectRowNode parent) {
        Object[][] rows = new Object[matchingEvents.size()][];
        int index = -1;
        com.espertech.esper.client.EventBean[] events = EventBeanUtility.allocatePerStreamShift(eventsPerStream);
        for (com.espertech.esper.client.EventBean matchingEvent : matchingEvents) {
            index++;
            events[0] = matchingEvent;
            Object[] results = new Object[parent.selectClauseEvaluator.length];
            for (int i = 0; i < results.length; i++) {
                results[i] = parent.selectClauseEvaluator[i].evaluate(events, isNewData, exprEvaluatorContext);
            }
            rows[index] = results;
        }
        return rows;
    }

    // No filter and with select clause
    public com.espertech.esper.client.EventBean evaluateGetEventBean(com.espertech.esper.client.EventBean[] eventsPerStream, boolean newData, Collection<EventBean> matchingEvents, ExprEvaluatorContext context, ExprSubselectRowNode parent) {
        com.espertech.esper.client.EventBean[] events = EventBeanUtility.allocatePerStreamShift(eventsPerStream);
        events[0] = EventBeanUtility.getNonemptyFirstEvent(matchingEvents);
        Map<String, Object> row = parent.evaluateRow(events, true, context);
        return parent.subselectMultirowType.getEventAdapterService().adapterForTypedMap(row, parent.subselectMultirowType.getEventType());
    }
}
