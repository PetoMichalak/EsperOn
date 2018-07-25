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
package eu.uk.ncl.pet5o.esper.core.context.stmt;

import eu.uk.ncl.pet5o.esper.client.EventBean;
import eu.uk.ncl.pet5o.esper.collection.ArrayWrap;
import eu.uk.ncl.pet5o.esper.epl.expression.core.ExprEvaluatorContext;
import eu.uk.ncl.pet5o.esper.epl.expression.table.ExprTableAccessEvalStrategy;

import java.util.Collection;

public class AIRegistryTableAccessMultiPerm implements AIRegistryTableAccess, ExprTableAccessEvalStrategy {

    private final ArrayWrap<ExprTableAccessEvalStrategy> strategies;
    private int count;

    public AIRegistryTableAccessMultiPerm() {
        strategies = new ArrayWrap<ExprTableAccessEvalStrategy>(ExprTableAccessEvalStrategy.class, 10);
    }

    public void assignService(int num, ExprTableAccessEvalStrategy value) {
        AIRegistryUtil.checkExpand(num, strategies);
        strategies.getArray()[num] = value;
        count++;
    }

    public void deassignService(int num) {
        strategies.getArray()[num] = null;
        count--;
    }

    public int getAgentInstanceCount() {
        return count;
    }

    public Object evaluate(eu.uk.ncl.pet5o.esper.client.EventBean[] eventsPerStream, boolean isNewData, ExprEvaluatorContext context) {
        return getStrategy(context).evaluate(eventsPerStream, isNewData, context);
    }

    public Object[] evaluateTypableSingle(eu.uk.ncl.pet5o.esper.client.EventBean[] eventsPerStream, boolean isNewData, ExprEvaluatorContext context) {
        return getStrategy(context).evaluateTypableSingle(eventsPerStream, isNewData, context);
    }

    public Collection<EventBean> evaluateGetROCollectionEvents(eu.uk.ncl.pet5o.esper.client.EventBean[] eventsPerStream, boolean isNewData, ExprEvaluatorContext context) {
        return getStrategy(context).evaluateGetROCollectionEvents(eventsPerStream, isNewData, context);
    }

    public eu.uk.ncl.pet5o.esper.client.EventBean evaluateGetEventBean(eu.uk.ncl.pet5o.esper.client.EventBean[] eventsPerStream, boolean isNewData, ExprEvaluatorContext context) {
        return getStrategy(context).evaluateGetEventBean(eventsPerStream, isNewData, context);
    }

    public Collection evaluateGetROCollectionScalar(eu.uk.ncl.pet5o.esper.client.EventBean[] eventsPerStream, boolean isNewData, ExprEvaluatorContext context) {
        return getStrategy(context).evaluateGetROCollectionScalar(eventsPerStream, isNewData, context);
    }

    private ExprTableAccessEvalStrategy getStrategy(ExprEvaluatorContext context) {
        return strategies.getArray()[context.getAgentInstanceId()];
    }
}
