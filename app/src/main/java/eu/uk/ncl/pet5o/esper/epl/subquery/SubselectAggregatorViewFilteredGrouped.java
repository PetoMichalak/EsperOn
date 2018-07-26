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
package eu.uk.ncl.pet5o.esper.epl.subquery;

import eu.uk.ncl.pet5o.esper.client.EventBean;
import eu.uk.ncl.pet5o.esper.epl.agg.service.common.AggregationService;
import eu.uk.ncl.pet5o.esper.epl.expression.core.ExprEvaluator;
import eu.uk.ncl.pet5o.esper.epl.expression.core.ExprEvaluatorContext;
import eu.uk.ncl.pet5o.esper.epl.expression.core.ExprNode;
import eu.uk.ncl.pet5o.esper.metrics.instrumentation.InstrumentationHelper;

public class SubselectAggregatorViewFilteredGrouped extends SubselectAggregatorViewBase {
    private final ExprNode filterExprNode;

    public SubselectAggregatorViewFilteredGrouped(AggregationService aggregationService, ExprEvaluator optionalFilterExpr, ExprEvaluatorContext exprEvaluatorContext, ExprEvaluator[] groupKeys, ExprNode filterExprNode) {
        super(aggregationService, optionalFilterExpr, exprEvaluatorContext, groupKeys);
        this.filterExprNode = filterExprNode;
    }

    public void update(EventBean[] newData, EventBean[] oldData) {
        if (InstrumentationHelper.ENABLED) {
            InstrumentationHelper.get().qSubselectAggregation(filterExprNode);
        }
        if (newData != null) {
            for (EventBean theEvent : newData) {
                eventsPerStream[0] = theEvent;
                boolean isPass = filter(true);
                if (isPass) {
                    Object groupKey = generateGroupKey(true);
                    aggregationService.applyEnter(eventsPerStream, groupKey, exprEvaluatorContext);
                }
            }
        }

        if (oldData != null) {
            for (EventBean theEvent : oldData) {
                eventsPerStream[0] = theEvent;
                boolean isPass = filter(false);
                if (isPass) {
                    Object groupKey = generateGroupKey(false);
                    aggregationService.applyLeave(eventsPerStream, groupKey, exprEvaluatorContext);
                }
            }
        }
        if (InstrumentationHelper.ENABLED) {
            InstrumentationHelper.get().aSubselectAggregation();
        }
    }
}
