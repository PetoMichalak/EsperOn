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

import eu.uk.ncl.pet5o.esper.epl.expression.core.ExprEvaluator;
import eu.uk.ncl.pet5o.esper.epl.expression.core.ExprEvaluatorContext;
import eu.uk.ncl.pet5o.esper.epl.expression.prior.ExprPriorEvalStrategy;

public class AIRegistryPriorSingle implements AIRegistryPrior, ExprPriorEvalStrategy {

    private ExprPriorEvalStrategy strategy;

    public AIRegistryPriorSingle() {
    }

    public void assignService(int num, ExprPriorEvalStrategy value) {
        strategy = value;
    }

    public void deassignService(int num) {
        strategy = null;
    }

    public int getAgentInstanceCount() {
        return strategy == null ? 0 : 1;
    }

    public Object evaluate(eu.uk.ncl.pet5o.esper.client.EventBean[] eventsPerStream, boolean isNewData, ExprEvaluatorContext exprEvaluatorContext, int streamNumber, ExprEvaluator evaluator, int constantIndexNumber) {
        return strategy.evaluate(eventsPerStream, isNewData, exprEvaluatorContext, streamNumber, evaluator, constantIndexNumber);
    }
}
