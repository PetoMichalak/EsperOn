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
import eu.uk.ncl.pet5o.esper.epl.expression.core.ExprEvaluatorContext;
import eu.uk.ncl.pet5o.esper.rowregex.RegexExprPreviousEvalStrategy;
import eu.uk.ncl.pet5o.esper.rowregex.RegexPartitionStateRandomAccess;

public class AIRegistryMatchRecognizePreviousMultiPerm implements AIRegistryMatchRecognizePrevious, RegexExprPreviousEvalStrategy {

    private final ArrayWrap<RegexExprPreviousEvalStrategy> strategies;
    private int count;

    public AIRegistryMatchRecognizePreviousMultiPerm() {
        strategies = new ArrayWrap<RegexExprPreviousEvalStrategy>(RegexExprPreviousEvalStrategy.class, 10);
    }

    public void assignService(int num, RegexExprPreviousEvalStrategy value) {
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

    public RegexPartitionStateRandomAccess getAccess(ExprEvaluatorContext exprEvaluatorContext) {
        int agentInstanceId = exprEvaluatorContext.getAgentInstanceId();
        RegexExprPreviousEvalStrategy strategy = strategies.getArray()[agentInstanceId];
        return strategy.getAccess(exprEvaluatorContext);
    }
}
