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
public class SubselectEvalStrategyNREqualsInFiltered extends SubselectEvalStrategyNREqualsInBase {
    private final ExprEvaluator filterEval;

    public SubselectEvalStrategyNREqualsInFiltered(ExprEvaluator valueEval, ExprEvaluator selectEval, boolean notIn, SimpleNumberCoercer coercer, ExprEvaluator filterEval) {
        super(valueEval, selectEval, notIn, coercer);
        this.filterEval = filterEval;
    }

    protected Object evaluateInternal(Object leftResult, eu.uk.ncl.pet5o.esper.client.EventBean[] eventsZeroOffset, boolean isNewData, Collection<EventBean> matchingEvents, ExprEvaluatorContext exprEvaluatorContext, AggregationService aggregationService) {
        boolean hasNullRow = false;
        for (eu.uk.ncl.pet5o.esper.client.EventBean subselectEvent : matchingEvents) {
            // Prepare filter expression event list
            eventsZeroOffset[0] = subselectEvent;

            // Eval filter expression
            Boolean pass = (Boolean) filterEval.evaluate(eventsZeroOffset, true, exprEvaluatorContext);
            if ((pass == null) || (!pass)) {
                continue;
            }
            if (leftResult == null) {
                return null;
            }

            Object rightResult;
            if (selectEval != null) {
                rightResult = selectEval.evaluate(eventsZeroOffset, true, exprEvaluatorContext);
            } else {
                rightResult = eventsZeroOffset[0].getUnderlying();
            }

            if (rightResult == null) {
                hasNullRow = true;
            } else {
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
            }
        }

        if (hasNullRow) {
            return null;
        }

        return isNotIn;
    }
}
