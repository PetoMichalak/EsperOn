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
import eu.uk.ncl.pet5o.esper.epl.agg.access.AggregationAccessorSlotPair;
import eu.uk.ncl.pet5o.esper.epl.expression.core.ExprEvaluatorContext;
import eu.uk.ncl.pet5o.esper.epl.expression.table.ExprTableAccessEvalStrategy;
import eu.uk.ncl.pet5o.esper.event.ObjectArrayBackedEventBean;

import java.util.Collection;

public abstract class ExprTableEvalStrategyGroupByAccessBase extends ExprTableEvalStrategyGroupByBase implements ExprTableAccessEvalStrategy {

    private final AggregationAccessorSlotPair pair;

    protected ExprTableEvalStrategyGroupByAccessBase(TableAndLockProviderGrouped provider, AggregationAccessorSlotPair pair) {
        super(provider);
        this.pair = pair;
    }

    protected Object evaluateInternal(Object group, eu.uk.ncl.pet5o.esper.client.EventBean[] eventsPerStream, boolean isNewData, ExprEvaluatorContext context) {
        ObjectArrayBackedEventBean row = lockTableReadAndGet(group, context);
        if (row == null) {
            return null;
        }
        return ExprTableEvalStrategyUtil.evalAccessorGetValue(ExprTableEvalStrategyUtil.getRow(row), pair, eventsPerStream, isNewData, context);
    }

    public Object[] evaluateTypableSingle(eu.uk.ncl.pet5o.esper.client.EventBean[] eventsPerStream, boolean isNewData, ExprEvaluatorContext context) {
        throw new IllegalStateException("Not typable");
    }

    protected Collection<EventBean> evaluateGetROCollectionEventsInternal(Object group, eu.uk.ncl.pet5o.esper.client.EventBean[] eventsPerStream, boolean isNewData, ExprEvaluatorContext context) {
        ObjectArrayBackedEventBean row = lockTableReadAndGet(group, context);
        if (row == null) {
            return null;
        }
        return ExprTableEvalStrategyUtil.evalGetROCollectionEvents(ExprTableEvalStrategyUtil.getRow(row), pair, eventsPerStream, isNewData, context);
    }

    protected eu.uk.ncl.pet5o.esper.client.EventBean evaluateGetEventBeanInternal(Object group, eu.uk.ncl.pet5o.esper.client.EventBean[] eventsPerStream, boolean isNewData, ExprEvaluatorContext context) {
        ObjectArrayBackedEventBean row = lockTableReadAndGet(group, context);
        if (row == null) {
            return null;
        }
        return ExprTableEvalStrategyUtil.evalGetEventBean(ExprTableEvalStrategyUtil.getRow(row), pair, eventsPerStream, isNewData, context);
    }

    protected Collection evaluateGetROCollectionScalarInternal(Object group, eu.uk.ncl.pet5o.esper.client.EventBean[] eventsPerStream, boolean isNewData, ExprEvaluatorContext context) {
        ObjectArrayBackedEventBean row = lockTableReadAndGet(group, context);
        if (row == null) {
            return null;
        }
        return ExprTableEvalStrategyUtil.evalGetROCollectionScalar(ExprTableEvalStrategyUtil.getRow(row), pair, eventsPerStream, isNewData, context);
    }
}
