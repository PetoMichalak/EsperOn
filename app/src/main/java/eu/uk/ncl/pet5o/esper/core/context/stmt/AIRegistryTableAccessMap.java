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

import com.espertech.esper.client.EventBean;
import com.espertech.esper.epl.expression.core.ExprEvaluatorContext;
import com.espertech.esper.epl.expression.table.ExprTableAccessEvalStrategy;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class AIRegistryTableAccessMap implements AIRegistryTableAccess, ExprTableAccessEvalStrategy {

    private final Map<Integer, ExprTableAccessEvalStrategy> strategies;

    public AIRegistryTableAccessMap() {
        strategies = new HashMap<Integer, ExprTableAccessEvalStrategy>();
    }

    public void assignService(int num, ExprTableAccessEvalStrategy value) {
        strategies.put(num, value);
    }

    public void deassignService(int num) {
        strategies.remove(num);
    }

    public int getAgentInstanceCount() {
        return strategies.size();
    }

    public Object evaluate(com.espertech.esper.client.EventBean[] eventsPerStream, boolean isNewData, ExprEvaluatorContext context) {
        return getStrategy(context).evaluate(eventsPerStream, isNewData, context);
    }

    public Object[] evaluateTypableSingle(com.espertech.esper.client.EventBean[] eventsPerStream, boolean isNewData, ExprEvaluatorContext context) {
        return getStrategy(context).evaluateTypableSingle(eventsPerStream, isNewData, context);
    }

    public Collection<EventBean> evaluateGetROCollectionEvents(com.espertech.esper.client.EventBean[] eventsPerStream, boolean isNewData, ExprEvaluatorContext context) {
        return getStrategy(context).evaluateGetROCollectionEvents(eventsPerStream, isNewData, context);
    }

    public com.espertech.esper.client.EventBean evaluateGetEventBean(com.espertech.esper.client.EventBean[] eventsPerStream, boolean isNewData, ExprEvaluatorContext context) {
        return getStrategy(context).evaluateGetEventBean(eventsPerStream, isNewData, context);
    }

    public Collection evaluateGetROCollectionScalar(com.espertech.esper.client.EventBean[] eventsPerStream, boolean isNewData, ExprEvaluatorContext context) {
        return getStrategy(context).evaluateGetROCollectionScalar(eventsPerStream, isNewData, context);
    }

    private ExprTableAccessEvalStrategy getStrategy(ExprEvaluatorContext context) {
        return strategies.get(context.getAgentInstanceId());
    }
}
