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

public class SubselectEvalStrategyNRRelOpAnyWGroupBy extends SubselectEvalStrategyNRRelOpBase {

    private final ExprEvaluator havingEval;

    public SubselectEvalStrategyNRRelOpAnyWGroupBy(ExprEvaluator valueEval, ExprEvaluator selectEval, boolean resultWhenNoMatchingEvents, RelationalOpEnum.Computer computer, ExprEvaluator havingEval) {
        super(valueEval, selectEval, resultWhenNoMatchingEvents, computer);
        this.havingEval = havingEval;
    }

    protected Object evaluateInternal(Object leftResult, eu.uk.ncl.pet5o.esper.client.EventBean[] events, boolean isNewData, Collection<EventBean> matchingEvents, ExprEvaluatorContext exprEvaluatorContext, AggregationService aggregationServiceAnyPartition) {
        AggregationService aggregationService = aggregationServiceAnyPartition.getContextPartitionAggregationService(exprEvaluatorContext.getAgentInstanceId());
        Collection<Object> groupKeys = aggregationService.getGroupKeys(exprEvaluatorContext);
        boolean hasNonNullRow = false;
        boolean hasRows = false;

        for (Object groupKey : groupKeys) {
            aggregationService.setCurrentAccess(groupKey, exprEvaluatorContext.getAgentInstanceId(), null);

            if (havingEval != null) {
                Boolean pass = (Boolean) havingEval.evaluate(events, true, exprEvaluatorContext);
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
