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

public class AIRegistryMatchRecognizePreviousSingle implements AIRegistryMatchRecognizePrevious, RegexExprPreviousEvalStrategy {

    private RegexExprPreviousEvalStrategy strategy;

    public void assignService(int num, RegexExprPreviousEvalStrategy value) {
        this.strategy = value;
    }

    public void deassignService(int num) {
        this.strategy = null;
    }

    public int getAgentInstanceCount() {
        return strategy == null ? 0 : 1;
    }

    public RegexPartitionStateRandomAccess getAccess(ExprEvaluatorContext exprEvaluatorContext) {
        return strategy.getAccess(exprEvaluatorContext);
    }
}
