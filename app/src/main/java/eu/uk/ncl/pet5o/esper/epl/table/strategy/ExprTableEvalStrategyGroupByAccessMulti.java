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
import eu.uk.ncl.pet5o.esper.collection.MultiKeyUntyped;
import eu.uk.ncl.pet5o.esper.epl.agg.access.AggregationAccessorSlotPair;
import eu.uk.ncl.pet5o.esper.epl.expression.core.ExprEvaluator;
import eu.uk.ncl.pet5o.esper.epl.expression.core.ExprEvaluatorContext;

import java.util.Collection;

public class ExprTableEvalStrategyGroupByAccessMulti extends ExprTableEvalStrategyGroupByAccessBase {

    private final ExprEvaluator[] groupExpr;

    public ExprTableEvalStrategyGroupByAccessMulti(TableAndLockProviderGrouped provider, AggregationAccessorSlotPair pair, ExprEvaluator[] groupExpr) {
        super(provider, pair);
        this.groupExpr = groupExpr;
    }

    public Object evaluate(eu.uk.ncl.pet5o.esper.client.EventBean[] eventsPerStream, boolean isNewData, ExprEvaluatorContext exprEvaluatorContext) {
        Object group = getKey(groupExpr, eventsPerStream, isNewData, exprEvaluatorContext);
        return evaluateInternal(group, eventsPerStream, isNewData, exprEvaluatorContext);
    }

    public Collection<EventBean> evaluateGetROCollectionEvents(eu.uk.ncl.pet5o.esper.client.EventBean[] eventsPerStream, boolean isNewData, ExprEvaluatorContext context) {
        Object group = getKey(groupExpr, eventsPerStream, isNewData, context);
        return evaluateGetROCollectionEventsInternal(group, eventsPerStream, isNewData, context);
    }

    public eu.uk.ncl.pet5o.esper.client.EventBean evaluateGetEventBean(eu.uk.ncl.pet5o.esper.client.EventBean[] eventsPerStream, boolean isNewData, ExprEvaluatorContext context) {
        Object group = getKey(groupExpr, eventsPerStream, isNewData, context);
        return evaluateGetEventBeanInternal(group, eventsPerStream, isNewData, context);
    }

    public Collection evaluateGetROCollectionScalar(eu.uk.ncl.pet5o.esper.client.EventBean[] eventsPerStream, boolean isNewData, ExprEvaluatorContext context) {
        Object group = getKey(groupExpr, eventsPerStream, isNewData, context);
        return evaluateGetROCollectionScalarInternal(group, eventsPerStream, isNewData, context);
    }

    protected static MultiKeyUntyped getKey(ExprEvaluator[] evaluators, eu.uk.ncl.pet5o.esper.client.EventBean[] eventsPerStream, boolean isNewData, ExprEvaluatorContext context) {
        Object[] keys = new Object[evaluators.length];
        for (int i = 0; i < evaluators.length; i++) {
            keys[i] = evaluators[i].evaluate(eventsPerStream, isNewData, context);
        }
        return new MultiKeyUntyped(keys);
    }
}
