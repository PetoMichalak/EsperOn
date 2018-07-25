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
import com.espertech.esper.epl.expression.core.ExprEvaluatorContext;
import com.espertech.esper.event.EventBeanUtility;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;

/**
 * Represents a subselect in an expression tree.
 */
public class SubselectEvalStrategyRowUnfilteredUnselected implements SubselectEvalStrategyRow {

    private static final Logger log = LoggerFactory.getLogger(SubselectEvalStrategyRowUnfilteredUnselected.class);

    public final static SubselectEvalStrategyRowUnfilteredUnselected INSTANCE = new SubselectEvalStrategyRowUnfilteredUnselected();

    public SubselectEvalStrategyRowUnfilteredUnselected() {
    }

    // No filter and no select-clause: return underlying event
    public Object evaluate(com.espertech.esper.client.EventBean[] eventsPerStream, boolean newData, Collection<EventBean> matchingEvents, ExprEvaluatorContext exprEvaluatorContext,
                           ExprSubselectRowNode parent) {
        if (matchingEvents.size() > 1) {
            log.warn(parent.getMultirowMessage());
            return null;
        }
        return EventBeanUtility.getNonemptyFirstEventUnderlying(matchingEvents);
    }

    // No filter and no select-clause: return matching events
    public Collection<EventBean> evaluateGetCollEvents(com.espertech.esper.client.EventBean[] eventsPerStream, boolean newData, Collection<EventBean> matchingEvents, ExprEvaluatorContext context, ExprSubselectRowNode parent) {
        return matchingEvents;
    }

    // No filter and no select-clause: no value can be determined
    public Collection evaluateGetCollScalar(com.espertech.esper.client.EventBean[] eventsPerStream, boolean isNewData, Collection<EventBean> matchingEvents, ExprEvaluatorContext context, ExprSubselectRowNode parent) {
        return null;
    }

    // No filter and no select-clause: no value can be determined
    public Object[] typableEvaluate(com.espertech.esper.client.EventBean[] eventsPerStream, boolean newData, Collection<EventBean> matchingEvents, ExprEvaluatorContext exprEvaluatorContext, ExprSubselectRowNode parent) {
        return null;
    }

    public Object[][] typableEvaluateMultirow(com.espertech.esper.client.EventBean[] eventsPerStream, boolean newData, Collection<EventBean> matchingEvents, ExprEvaluatorContext exprEvaluatorContext, ExprSubselectRowNode parent) {
        return null;
    }

    public com.espertech.esper.client.EventBean evaluateGetEventBean(com.espertech.esper.client.EventBean[] eventsPerStream, boolean newData, Collection<EventBean> matchingEvents, ExprEvaluatorContext exprEvaluatorContext, ExprSubselectRowNode parent) {
        return null;    // this actually only applies to when there is a select-clause
    }
}
