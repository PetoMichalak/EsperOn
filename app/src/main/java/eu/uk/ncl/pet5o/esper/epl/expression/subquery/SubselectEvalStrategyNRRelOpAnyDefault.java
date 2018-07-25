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
import eu.uk.ncl.pet5o.esper.type.RelationalOpEnum;

import java.util.Collection;

public class SubselectEvalStrategyNRRelOpAnyDefault extends SubselectEvalStrategyNRRelOpBase {

    private final ExprEvaluator filterEval;

    public SubselectEvalStrategyNRRelOpAnyDefault(ExprEvaluator valueEval, ExprEvaluator selectEval, boolean resultWhenNoMatchingEvents, RelationalOpEnum.Computer computer, ExprEvaluator filterEval) {
        super(valueEval, selectEval, resultWhenNoMatchingEvents, computer);
        this.filterEval = filterEval;
    }

    protected Object evaluateInternal(Object leftResult, eu.uk.ncl.pet5o.esper.client.EventBean[] events, boolean isNewData, Collection<EventBean> matchingEvents, ExprEvaluatorContext exprEvaluatorContext, AggregationService aggregationService) {
        boolean hasNonNullRow = false;
        boolean hasRows = false;
        for (eu.uk.ncl.pet5o.esper.client.EventBean subselectEvent : matchingEvents) {
            events[0] = subselectEvent;

            // Eval filter expression
            if (filterEval != null) {
                Boolean pass = (Boolean) filterEval.evaluate(events, true, exprEvaluatorContext);
                if ((pass == null) || (!pass)) {
                    continue;
                }
            }
            hasRows = true;

            Object valueRight;
            if (selectEval != null) {
                valueRight = selectEval.evaluate(events, true, exprEvaluatorContext);
            } else {
                valueRight = events[0].getUnderlying();
            }

            if (valueRight != null) {
                hasNonNullRow = true;
            }

            if ((leftResult != null) && (valueRight != null)) {
                if (computer.compare(leftResult, valueRight)) {
                    return true;
                }
            }
        }

        if (!hasRows) {
            return false;
        }
        if ((!hasNonNullRow) || (leftResult == null)) {
            return null;
        }
        return false;
    }
}
