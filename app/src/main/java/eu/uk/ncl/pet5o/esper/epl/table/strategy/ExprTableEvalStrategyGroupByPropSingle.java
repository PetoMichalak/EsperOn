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

import com.espertech.esper.client.EventBean;
import com.espertech.esper.epl.expression.core.ExprEnumerationGivenEvent;
import com.espertech.esper.epl.expression.core.ExprEvaluator;
import com.espertech.esper.epl.expression.core.ExprEvaluatorContext;

import java.util.Collection;

public class ExprTableEvalStrategyGroupByPropSingle extends ExprTableEvalStrategyGroupByPropBase {

    private final ExprEvaluator groupExpr;

    public ExprTableEvalStrategyGroupByPropSingle(TableAndLockProviderGrouped provider, int propertyIndex, ExprEnumerationGivenEvent optionalEnumEval, ExprEvaluator groupExpr) {
        super(provider, propertyIndex, optionalEnumEval);
        this.groupExpr = groupExpr;
    }

    public Object evaluate(com.espertech.esper.client.EventBean[] eventsPerStream, boolean isNewData, ExprEvaluatorContext exprEvaluatorContext) {
        Object groupKey = groupExpr.evaluate(eventsPerStream, isNewData, exprEvaluatorContext);
        return evaluateInternal(groupKey, exprEvaluatorContext);
    }

    public Collection<EventBean> evaluateGetROCollectionEvents(com.espertech.esper.client.EventBean[] eventsPerStream, boolean isNewData, ExprEvaluatorContext context) {
        Object groupKey = groupExpr.evaluate(eventsPerStream, isNewData, context);
        return evaluateGetROCollectionEventsInternal(groupKey, context);
    }

    public com.espertech.esper.client.EventBean evaluateGetEventBean(com.espertech.esper.client.EventBean[] eventsPerStream, boolean isNewData, ExprEvaluatorContext context) {
        Object groupKey = groupExpr.evaluate(eventsPerStream, isNewData, context);
        return evaluateGetEventBeanInternal(groupKey, context);
    }

    public Collection evaluateGetROCollectionScalar(com.espertech.esper.client.EventBean[] eventsPerStream, boolean isNewData, ExprEvaluatorContext context) {
        Object groupKey = groupExpr.evaluate(eventsPerStream, isNewData, context);
        return evaluateGetROCollectionScalarInternal(groupKey, context);
    }
}
