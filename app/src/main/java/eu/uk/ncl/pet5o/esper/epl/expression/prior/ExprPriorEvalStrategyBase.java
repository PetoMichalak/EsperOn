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
package eu.uk.ncl.pet5o.esper.epl.expression.prior;

import eu.uk.ncl.pet5o.esper.epl.expression.core.ExprEvaluator;
import eu.uk.ncl.pet5o.esper.epl.expression.core.ExprEvaluatorContext;

/**
 * Represents the 'prior' prior event function in an expression node tree.
 */
public abstract class ExprPriorEvalStrategyBase implements ExprPriorEvalStrategy {
    public abstract eu.uk.ncl.pet5o.esper.client.EventBean getSubstituteEvent(eu.uk.ncl.pet5o.esper.client.EventBean originalEvent, boolean isNewData, int constantIndexNumber);

    public Object evaluate(eu.uk.ncl.pet5o.esper.client.EventBean[] eventsPerStream, boolean isNewData, ExprEvaluatorContext exprEvaluatorContext, int streamNumber, ExprEvaluator evaluator, int constantIndexNumber) {
        eu.uk.ncl.pet5o.esper.client.EventBean originalEvent = eventsPerStream[streamNumber];

        eu.uk.ncl.pet5o.esper.client.EventBean substituteEvent = getSubstituteEvent(originalEvent, isNewData, constantIndexNumber);

        // Substitute original event with prior event, evaluate inner expression
        eventsPerStream[streamNumber] = substituteEvent;
        Object evalResult = evaluator.evaluate(eventsPerStream, isNewData, exprEvaluatorContext);
        eventsPerStream[streamNumber] = originalEvent;

        return evalResult;
    }
}
