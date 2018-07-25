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
import eu.uk.ncl.pet5o.esper.epl.expression.core.ExprEvaluator;
import eu.uk.ncl.pet5o.esper.epl.expression.core.ExprEvaluatorContext;
import eu.uk.ncl.pet5o.esper.util.SimpleNumberCoercer;

import java.util.Collection;

/**
 * Represents a in-subselect evaluation strategy.
 */
public class SubselectEvalStrategyNREqualsInUnfiltered extends SubselectEvalStrategyNREqualsInBase {
    public SubselectEvalStrategyNREqualsInUnfiltered(ExprEvaluator valueEval, ExprEvaluator selectEval, boolean notIn, SimpleNumberCoercer coercer) {
        super(valueEval, selectEval, notIn, coercer);
    }

    protected Object evaluateInternal(Object leftResult, eu.uk.ncl.pet5o.esper.client.EventBean[] events, boolean isNewData, Collection<EventBean> matchingEvents, ExprEvaluatorContext exprEvaluatorContext, AggregationService aggregationService) {
        if (leftResult == null) {
            return null;
        }

        // Evaluate each select until we have a match
        boolean hasNullRow = false;
        for (eu.uk.ncl.pet5o.esper.client.EventBean theEvent : matchingEvents) {
            events[0] = theEvent;

            Object rightResult;
            if (selectEval != null) {
                rightResult = selectEval.evaluate(events, true, exprEvaluatorContext);
            } else {
                rightResult = events[0].getUnderlying();
            }

            if (rightResult != null) {
                if (coercer == null) {
                    if (leftResult.equals(rightResult)) {
                        return !isNotIn;
                    }
                } else {
                    Number left = coercer.coerceBoxed((Number) leftResult);
                    Number right = coercer.coerceBoxed((Number) rightResult);
                    if (left.equals(right)) {
                        return !isNotIn;
                    }
                }
            } else {
                hasNullRow = true;
            }
        }

        if (hasNullRow) {
            return null;
        }
        return isNotIn;
    }
}
