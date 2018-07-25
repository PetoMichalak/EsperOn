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
import eu.uk.ncl.pet5o.esper.epl.expression.core.ExprEvaluatorContext;
import eu.uk.ncl.pet5o.esper.event.EventBeanUtility;
import eu.uk.ncl.pet5o.esper.util.CollectionUtil;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

public class SubselectEvalStrategyRowFilteredSelected implements SubselectEvalStrategyRow {

    // Filter and Select
    public Object evaluate(eu.uk.ncl.pet5o.esper.client.EventBean[] eventsPerStream, boolean newData, Collection<EventBean> matchingEvents, ExprEvaluatorContext exprEvaluatorContext, ExprSubselectRowNode parent) {
        eu.uk.ncl.pet5o.esper.client.EventBean[] eventsZeroBased = EventBeanUtility.allocatePerStreamShift(eventsPerStream);
        eu.uk.ncl.pet5o.esper.client.EventBean subSelectResult = ExprSubselectRowNodeUtility.evaluateFilterExpectSingleMatch(eventsZeroBased, newData, matchingEvents, exprEvaluatorContext, parent);
        if (subSelectResult == null) {
            return null;
        }

        eventsZeroBased[0] = subSelectResult;
        Object result;
        if (parent.selectClauseEvaluator.length == 1) {
            result = parent.selectClauseEvaluator[0].evaluate(eventsZeroBased, true, exprEvaluatorContext);
        } else {
            // we are returning a Map here, not object-array, preferring the self-describing structure
            result = parent.evaluateRow(eventsZeroBased, true, exprEvaluatorContext);
        }

        return result;
    }

    // Filter and Select
    public Collection<EventBean> evaluateGetCollEvents(eu.uk.ncl.pet5o.esper.client.EventBean[] eventsPerStream, boolean newData, Collection<EventBean> matchingEvents, ExprEvaluatorContext context, ExprSubselectRowNode parent) {
        return null;
    }

    // Filter and Select
    public Collection evaluateGetCollScalar(eu.uk.ncl.pet5o.esper.client.EventBean[] eventsPerStream, boolean isNewData, Collection<EventBean> matchingEvents, ExprEvaluatorContext context, ExprSubselectRowNode parent) {
        List<Object> result = new ArrayList<Object>();
        eu.uk.ncl.pet5o.esper.client.EventBean[] events = EventBeanUtility.allocatePerStreamShift(eventsPerStream);
        for (eu.uk.ncl.pet5o.esper.client.EventBean subselectEvent : matchingEvents) {
            events[0] = subselectEvent;
            Boolean pass = (Boolean) parent.filterExpr.evaluate(events, true, context);
            if ((pass != null) && pass) {
                result.add(parent.selectClauseEvaluator[0].evaluate(events, isNewData, context));
            }
        }
        return result;
    }

    // Filter and Select
    public Object[] typableEvaluate(eu.uk.ncl.pet5o.esper.client.EventBean[] eventsPerStream, boolean isNewData, Collection<EventBean> matchingEvents, ExprEvaluatorContext exprEvaluatorContext, ExprSubselectRowNode parent) {
        eu.uk.ncl.pet5o.esper.client.EventBean[] events = EventBeanUtility.allocatePerStreamShift(eventsPerStream);
        eu.uk.ncl.pet5o.esper.client.EventBean subSelectResult = ExprSubselectRowNodeUtility.evaluateFilterExpectSingleMatch(events, isNewData, matchingEvents, exprEvaluatorContext, parent);
        if (subSelectResult == null) {
            return null;
        }

        events[0] = subSelectResult;
        Object[] results = new Object[parent.selectClauseEvaluator.length];
        for (int i = 0; i < results.length; i++) {
            results[i] = parent.selectClauseEvaluator[i].evaluate(events, isNewData, exprEvaluatorContext);
        }
        return results;
    }

    public Object[][] typableEvaluateMultirow(eu.uk.ncl.pet5o.esper.client.EventBean[] eventsPerStream, boolean newData, Collection<EventBean> matchingEvents, ExprEvaluatorContext exprEvaluatorContext, ExprSubselectRowNode parent) {
        Object[][] rows = new Object[matchingEvents.size()][];
        int index = -1;
        eu.uk.ncl.pet5o.esper.client.EventBean[] events = EventBeanUtility.allocatePerStreamShift(eventsPerStream);
        for (eu.uk.ncl.pet5o.esper.client.EventBean matchingEvent : matchingEvents) {
            events[0] = matchingEvent;

            Boolean pass = (Boolean) parent.filterExpr.evaluate(events, newData, exprEvaluatorContext);
            if ((pass != null) && pass) {
                index++;
                Object[] results = new Object[parent.selectClauseEvaluator.length];
                for (int i = 0; i < results.length; i++) {
                    results[i] = parent.selectClauseEvaluator[i].evaluate(events, newData, exprEvaluatorContext);
                }
                rows[index] = results;
            }
        }
        if (index == rows.length - 1) {
            return rows;
        }
        if (index == -1) {
            return CollectionUtil.OBJECTARRAYARRAY_EMPTY;
        }
        int access = index + 1;
        Object[][] rowArray = new Object[access][];
        System.arraycopy(rows, 0, rowArray, 0, rowArray.length);
        return rowArray;
    }

    // Filter and Select
    public eu.uk.ncl.pet5o.esper.client.EventBean evaluateGetEventBean(eu.uk.ncl.pet5o.esper.client.EventBean[] eventsPerStream, boolean isNewData, Collection<EventBean> matchingEvents, ExprEvaluatorContext context, ExprSubselectRowNode parent) {
        eu.uk.ncl.pet5o.esper.client.EventBean[] events = EventBeanUtility.allocatePerStreamShift(eventsPerStream);
        eu.uk.ncl.pet5o.esper.client.EventBean subSelectResult = ExprSubselectRowNodeUtility.evaluateFilterExpectSingleMatch(events, isNewData, matchingEvents, context, parent);
        if (subSelectResult == null) {
            return null;
        }
        Map<String, Object> row = parent.evaluateRow(events, true, context);
        return parent.subselectMultirowType.getEventAdapterService().adapterForTypedMap(row, parent.subselectMultirowType.getEventType());
    }
}
