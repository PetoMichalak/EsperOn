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
package eu.uk.ncl.pet5o.esper.epl.table.strategy;

import eu.uk.ncl.pet5o.esper.client.EventBean;
import eu.uk.ncl.pet5o.esper.epl.expression.core.ExprEnumerationGivenEvent;
import eu.uk.ncl.pet5o.esper.epl.expression.core.ExprEvaluator;
import eu.uk.ncl.pet5o.esper.epl.expression.core.ExprEvaluatorContext;

import java.util.Collection;

public class ExprTableEvalStrategyGroupByPropMulti extends ExprTableEvalStrategyGroupByPropBase {

    private final ExprEvaluator[] groupExpr;

    public ExprTableEvalStrategyGroupByPropMulti(TableAndLockProviderGrouped provider, int propertyIndex, ExprEnumerationGivenEvent optionalEnumEval, ExprEvaluator[] groupExpr) {
        super(provider, propertyIndex, optionalEnumEval);
        this.groupExpr = groupExpr;
    }

    public Object evaluate(eu.uk.ncl.pet5o.esper.client.EventBean[] eventsPerStream, boolean isNewData, ExprEvaluatorContext exprEvaluatorContext) {
        Object groupKey = ExprTableEvalStrategyGroupByAccessMulti.getKey(groupExpr, eventsPerStream, isNewData, exprEvaluatorContext);
        return evaluateInternal(groupKey, exprEvaluatorContext);
    }

    public Collection<EventBean> evaluateGetROCollectionEvents(eu.uk.ncl.pet5o.esper.client.EventBean[] eventsPerStream, boolean isNewData, ExprEvaluatorContext context) {
        Object groupKey = ExprTableEvalStrategyGroupByAccessMulti.getKey(groupExpr, eventsPerStream, isNewData, context);
        return evaluateGetROCollectionEventsInternal(groupKey, context);
    }

    public eu.uk.ncl.pet5o.esper.client.EventBean evaluateGetEventBean(eu.uk.ncl.pet5o.esper.client.EventBean[] eventsPerStream, boolean isNewData, ExprEvaluatorContext context) {
        Object groupKey = ExprTableEvalStrategyGroupByAccessMulti.getKey(groupExpr, eventsPerStream, isNewData, context);
        return evaluateGetEventBeanInternal(groupKey, context);
    }

    public Collection evaluateGetROCollectionScalar(eu.uk.ncl.pet5o.esper.client.EventBean[] eventsPerStream, boolean isNewData, ExprEvaluatorContext context) {
        Object groupKey = ExprTableEvalStrategyGroupByAccessMulti.getKey(groupExpr, eventsPerStream, isNewData, context);
        return evaluateGetROCollectionScalarInternal(groupKey, context);
    }
}
