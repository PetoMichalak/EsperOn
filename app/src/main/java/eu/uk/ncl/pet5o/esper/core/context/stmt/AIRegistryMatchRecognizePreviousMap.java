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

import eu.uk.ncl.pet5o.esper.epl.expression.core.ExprEvaluatorContext;
import eu.uk.ncl.pet5o.esper.rowregex.RegexExprPreviousEvalStrategy;
import eu.uk.ncl.pet5o.esper.rowregex.RegexPartitionStateRandomAccess;

import java.util.HashMap;
import java.util.Map;

public class AIRegistryMatchRecognizePreviousMap implements AIRegistryMatchRecognizePrevious, RegexExprPreviousEvalStrategy {

    private final Map<Integer, RegexExprPreviousEvalStrategy> strategies;

    public AIRegistryMatchRecognizePreviousMap() {
        strategies = new HashMap<Integer, RegexExprPreviousEvalStrategy>();
    }

    public void assignService(int num, RegexExprPreviousEvalStrategy value) {
        strategies.put(num, value);
    }

    public void deassignService(int num) {
        strategies.remove(num);
    }

    public int getAgentInstanceCount() {
        return strategies.size();
    }

    public RegexPartitionStateRandomAccess getAccess(ExprEvaluatorContext exprEvaluatorContext) {
        int agentInstanceId = exprEvaluatorContext.getAgentInstanceId();
        RegexExprPreviousEvalStrategy strategy = strategies.get(agentInstanceId);
        return strategy.getAccess(exprEvaluatorContext);
    }
}
