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
import eu.uk.ncl.pet5o.esper.event.EventBeanUtility;

import java.util.Collection;

public class SubselectEvalStrategyNRExistsWGroupByWHaving implements SubselectEvalStrategyNR {

    private final ExprEvaluator havingEval;

    public SubselectEvalStrategyNRExistsWGroupByWHaving(ExprEvaluator havingEval) {
        this.havingEval = havingEval;
    }

    public Object evaluate(eu.uk.ncl.pet5o.esper.client.EventBean[] eventsPerStream, boolean isNewData, Collection<EventBean> matchingEvents, ExprEvaluatorContext exprEvaluatorContext, AggregationService aggregationServiceAnyPartition) {
        if (matchingEvents == null || matchingEvents.size() == 0) {
            return false;
        }
        AggregationService aggregationService = aggregationServiceAnyPartition.getContextPartitionAggregationService(exprEvaluatorContext.getAgentInstanceId());
        Collection<Object> groupKeys = aggregationService.getGroupKeys(exprEvaluatorContext);
        eu.uk.ncl.pet5o.esper.client.EventBean[] events = EventBeanUtility.allocatePerStreamShift(eventsPerStream);
        for (Object groupKey : groupKeys) {
            aggregationService.setCurrentAccess(groupKey, exprEvaluatorContext.getAgentInstanceId(), null);

            Boolean pass = (Boolean) havingEval.evaluate(events, true, exprEvaluatorContext);
            if ((pass == null) || (!pass)) {
                continue;
            }
            return true;
        }
        return false;
    }
}
