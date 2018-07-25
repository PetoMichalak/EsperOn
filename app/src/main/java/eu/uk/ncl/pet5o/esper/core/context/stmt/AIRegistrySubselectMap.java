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
import eu.uk.ncl.pet5o.esper.epl.expression.core.ExprEvaluatorContext;
import eu.uk.ncl.pet5o.esper.epl.expression.subquery.ExprSubselectStrategy;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class AIRegistrySubselectMap implements AIRegistrySubselect, ExprSubselectStrategy {

    private final Map<Integer, ExprSubselectStrategy> strategies;

    public AIRegistrySubselectMap() {
        strategies = new HashMap<Integer, ExprSubselectStrategy>();
    }

    public void assignService(int num, ExprSubselectStrategy subselectStrategy) {
        strategies.put(num, subselectStrategy);
    }

    public void deassignService(int num) {
        strategies.remove(num);
    }

    public Collection<EventBean> evaluateMatching(eu.uk.ncl.pet5o.esper.client.EventBean[] eventsPerStream, ExprEvaluatorContext exprEvaluatorContext) {
        int agentInstanceId = exprEvaluatorContext.getAgentInstanceId();
        ExprSubselectStrategy strategy = strategies.get(agentInstanceId);
        return strategy.evaluateMatching(eventsPerStream, exprEvaluatorContext);
    }

    public int getAgentInstanceCount() {
        return strategies.size();
    }
}
