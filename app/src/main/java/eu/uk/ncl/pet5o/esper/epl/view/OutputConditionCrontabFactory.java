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
import eu.uk.ncl.pet5o.esper.epl.expression.core.ExprEvaluator;
import eu.uk.ncl.pet5o.esper.epl.expression.core.ExprNode;
import eu.uk.ncl.pet5o.esper.epl.expression.core.ExprNodeOrigin;
import eu.uk.ncl.pet5o.esper.epl.expression.core.ExprValidationException;
import eu.uk.ncl.pet5o.esper.epl.util.EPLScheduleExpressionUtil;
import eu.uk.ncl.pet5o.esper.schedule.ScheduleSpec;

import java.util.List;

/**
 * Output condition handling crontab-at schedule output.
 */
public class OutputConditionCrontabFactory implements OutputConditionFactory {
    protected final ExprEvaluator[] scheduleSpecEvaluators;
    protected final boolean isStartConditionOnCreation;

    public OutputConditionCrontabFactory(List<ExprNode> scheduleSpecExpressionList,
                                         StatementContext statementContext,
                                         boolean isStartConditionOnCreation)
            throws ExprValidationException {
        this.scheduleSpecEvaluators = EPLScheduleExpressionUtil.crontabScheduleValidate(ExprNodeOrigin.OUTPUTLIMIT, scheduleSpecExpressionList, statementContext, false);
        this.isStartConditionOnCreation = isStartConditionOnCreation;
    }

    public OutputCondition make(AgentInstanceContext agentInstanceContext, OutputCallback outputCallback) {
        ScheduleSpec scheduleSpec = EPLScheduleExpressionUtil.crontabScheduleBuild(scheduleSpecEvaluators, agentInstanceContext);
        return new OutputConditionCrontab(outputCallback, agentInstanceContext, isStartConditionOnCreation, scheduleSpec);
    }
}
