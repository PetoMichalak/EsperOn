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
package eu.uk.ncl.pet5o.esper.epl.agg.access;

import com.espertech.esper.epl.expression.core.ExprEvaluatorContext;

/**
 * Implementation of access function for single-stream (not joins).
 */
public class AggregationStateSortedJoinWFilter extends AggregationStateSortedJoin {

    public AggregationStateSortedJoinWFilter(AggregationStateSortedSpec spec) {
        super(spec);
    }

    @Override
    public void applyEnter(com.espertech.esper.client.EventBean[] eventsPerStream, ExprEvaluatorContext exprEvaluatorContext) {
        com.espertech.esper.client.EventBean theEvent = eventsPerStream[spec.getStreamId()];
        if (theEvent == null) {
            return;
        }
        Boolean pass = (Boolean) spec.getOptionalFilter().evaluate(eventsPerStream, true, exprEvaluatorContext);
        if (pass != null && pass) {
            referenceAdd(theEvent, eventsPerStream, exprEvaluatorContext);
        }
    }

    @Override
    public void applyLeave(com.espertech.esper.client.EventBean[] eventsPerStream, ExprEvaluatorContext exprEvaluatorContext) {
        com.espertech.esper.client.EventBean theEvent = eventsPerStream[spec.getStreamId()];
        if (theEvent == null) {
            return;
        }
        Boolean pass = (Boolean) spec.getOptionalFilter().evaluate(eventsPerStream, false, exprEvaluatorContext);
        if (pass != null && pass) {
            dereferenceRemove(theEvent, eventsPerStream, exprEvaluatorContext);
        }
    }
}
