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
package eu.uk.ncl.pet5o.esper.epl.view;

import eu.uk.ncl.pet5o.esper.core.context.util.AgentInstanceContext;
import eu.uk.ncl.pet5o.esper.core.service.StatementContext;
import eu.uk.ncl.pet5o.esper.epl.core.resultset.core.ResultSetProcessorHelperFactory;
import eu.uk.ncl.pet5o.esper.epl.expression.core.ExprValidationException;
import eu.uk.ncl.pet5o.esper.epl.spec.OutputLimitLimitType;
import eu.uk.ncl.pet5o.esper.epl.spec.OutputLimitSpec;

/**
 * An output condition that is satisfied at the first event
 * of either a time-based or count-based batch.
 */
public class OutputConditionFirstFactory implements OutputConditionFactory {
    private final OutputConditionFactory innerConditionFactory;

    public OutputConditionFirstFactory(OutputLimitSpec outputLimitSpec, StatementContext statementContext, boolean isGrouped, boolean isWithHavingClause, ResultSetProcessorHelperFactory resultSetProcessorHelperFactory)
            throws ExprValidationException {
        OutputLimitSpec innerSpec = new OutputLimitSpec(outputLimitSpec.getRate(), outputLimitSpec.getVariableName(), outputLimitSpec.getRateType(), OutputLimitLimitType.DEFAULT, outputLimitSpec.getWhenExpressionNode(), outputLimitSpec.getThenExpressions(), outputLimitSpec.getCrontabAtSchedule(), outputLimitSpec.getTimePeriodExpr(), outputLimitSpec.getAfterTimePeriodExpr(), outputLimitSpec.getAfterNumberOfEvents(), outputLimitSpec.isAndAfterTerminate(), outputLimitSpec.getAndAfterTerminateExpr(), outputLimitSpec.getAndAfterTerminateThenExpressions());
        this.innerConditionFactory = OutputConditionFactoryFactory.createCondition(innerSpec, statementContext, isGrouped, isWithHavingClause, false, resultSetProcessorHelperFactory);
    }

    public OutputCondition make(AgentInstanceContext agentInstanceContext, OutputCallback outputCallback) {
        return new OutputConditionFirst(outputCallback, agentInstanceContext, innerConditionFactory);
    }
}
