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

import eu.uk.ncl.pet5o.esper.collection.ArrayWrap;
import eu.uk.ncl.pet5o.esper.epl.expression.core.ExprEvaluator;
import eu.uk.ncl.pet5o.esper.epl.expression.core.ExprEvaluatorContext;
import eu.uk.ncl.pet5o.esper.epl.expression.prior.ExprPriorEvalStrategy;

public class AIRegistryPriorMultiPerm implements AIRegistryPrior, ExprPriorEvalStrategy {

    private final ArrayWrap<ExprPriorEvalStrategy> strategies;
    private int count;

    public AIRegistryPriorMultiPerm() {
        strategies = new ArrayWrap<ExprPriorEvalStrategy>(ExprPriorEvalStrategy.class, 10);
    }

    public void assignService(int num, ExprPriorEvalStrategy value) {
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

    public Object evaluate(eu.uk.ncl.pet5o.esper.client.EventBean[] eventsPerStream, boolean isNewData, ExprEvaluatorContext exprEvaluatorContext, int streamNumber, ExprEvaluator evaluator, int constantIndexNumber) {
        int agentInstanceId = exprEvaluatorContext.getAgentInstanceId();
        ExprPriorEvalStrategy strategy = strategies.getArray()[agentInstanceId];
        return strategy.evaluate(eventsPerStream, isNewData, exprEvaluatorContext, streamNumber, evaluator, constantIndexNumber);
    }
}
