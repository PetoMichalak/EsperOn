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

import eu.uk.ncl.pet5o.esper.epl.expression.core.ExprEvaluator;
import eu.uk.ncl.pet5o.esper.epl.expression.core.ExprEvaluatorContext;

/**
 * Implementation of access function for joins.
 */
public class AggregationStateLinearJoinWFilter extends AggregationStateLinearJoinImpl {
    private ExprEvaluator filterEval;

    public AggregationStateLinearJoinWFilter(int streamId, ExprEvaluator filterEval) {
        super(streamId);
        this.filterEval = filterEval;
    }

    @Override
    public void applyEnter(eu.uk.ncl.pet5o.esper.client.EventBean[] eventsPerStream, ExprEvaluatorContext exprEvaluatorContext) {
        eu.uk.ncl.pet5o.esper.client.EventBean theEvent = eventsPerStream[streamId];
        if (theEvent == null) {
            return;
        }
        Boolean pass = (Boolean) filterEval.evaluate(eventsPerStream, true, exprEvaluatorContext);
        if (pass != null && pass) {
            super.addEvent(theEvent);
        }
    }

    @Override
    public void applyLeave(eu.uk.ncl.pet5o.esper.client.EventBean[] eventsPerStream, ExprEvaluatorContext exprEvaluatorContext) {
        eu.uk.ncl.pet5o.esper.client.EventBean theEvent = eventsPerStream[streamId];
        if (theEvent == null) {
            return;
        }
        Boolean pass = (Boolean) filterEval.evaluate(eventsPerStream, true, exprEvaluatorContext);
        if (pass != null && pass) {
            super.removeEvent(theEvent);
        }
    }
}
