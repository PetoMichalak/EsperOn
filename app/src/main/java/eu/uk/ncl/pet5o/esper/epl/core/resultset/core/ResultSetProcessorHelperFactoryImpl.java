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
package eu.uk.ncl.pet5o.esper.epl.core.resultset.core;

import eu.uk.ncl.pet5o.esper.core.context.util.AgentInstanceContext;
import eu.uk.ncl.pet5o.esper.core.service.StatementContext;
import eu.uk.ncl.pet5o.esper.epl.agg.service.common.AggregationGroupByRollupDesc;
import eu.uk.ncl.pet5o.esper.epl.core.resultset.agggrouped.*;
import eu.uk.ncl.pet5o.esper.epl.core.resultset.grouped.ResultSetProcessorGroupedOutputAllGroupReps;
import eu.uk.ncl.pet5o.esper.epl.core.resultset.grouped.ResultSetProcessorGroupedOutputAllGroupRepsImpl;
import eu.uk.ncl.pet5o.esper.epl.core.resultset.grouped.ResultSetProcessorGroupedOutputFirstHelper;
import eu.uk.ncl.pet5o.esper.epl.core.resultset.grouped.ResultSetProcessorGroupedOutputFirstHelperImpl;
import eu.uk.ncl.pet5o.esper.epl.core.resultset.handthru.*;
import eu.uk.ncl.pet5o.esper.epl.core.resultset.rowforall.*;
import eu.uk.ncl.pet5o.esper.epl.core.resultset.rowperevent.ResultSetProcessorRowPerEvent;
import eu.uk.ncl.pet5o.esper.epl.core.resultset.rowperevent.ResultSetProcessorRowPerEventOutputAllHelper;
import eu.uk.ncl.pet5o.esper.epl.core.resultset.rowperevent.ResultSetProcessorRowPerEventOutputAllHelperImpl;
import eu.uk.ncl.pet5o.esper.epl.core.resultset.rowperevent.ResultSetProcessorRowPerEventOutputLastHelperImpl;
import eu.uk.ncl.pet5o.esper.epl.core.resultset.rowpergroup.*;
import eu.uk.ncl.pet5o.esper.epl.core.resultset.rowpergrouprollup.*;
import eu.uk.ncl.pet5o.esper.epl.expression.core.ExprNode;
import eu.uk.ncl.pet5o.esper.epl.expression.core.ExprValidationException;
import eu.uk.ncl.pet5o.esper.epl.expression.time.ExprTimePeriod;
import eu.uk.ncl.pet5o.esper.epl.spec.OnTriggerSetAssignment;
import eu.uk.ncl.pet5o.esper.epl.variable.VariableMetaData;
import eu.uk.ncl.pet5o.esper.epl.view.*;

import java.util.List;

public class ResultSetProcessorHelperFactoryImpl implements ResultSetProcessorHelperFactory {

    public ResultSetProcessorSimpleOutputLastHelper makeRSSimpleOutputLast(ResultSetProcessorSimple simple, AgentInstanceContext agentInstanceContext, int numStreams) {
        return new ResultSetProcessorSimpleOutputLastHelperImpl(simple);
    }

    public ResultSetProcessorSimpleOutputAllHelper makeRSSimpleOutputAll(ResultSetProcessorSimple simple, AgentInstanceContext agentInstanceContext, int numStreams) {
        return new ResultSetProcessorSimpleOutputAllHelperImpl(simple);
    }

    public OutputProcessViewConditionDeltaSet makeOutputConditionChangeSet(boolean isJoin, AgentInstanceContext agentInstanceContext) {
        return new OutputProcessViewConditionDeltaSetImpl(isJoin);
    }

    public OutputConditionFactory makeOutputConditionTime(ExprTimePeriod timePeriodExpr, boolean isStartConditionOnCreation) {
        return new OutputConditionTimeFactory(timePeriodExpr, isStartConditionOnCreation);
    }

    public OutputConditionFactory makeOutputConditionExpression(ExprNode whenExpressionNode, List<OnTriggerSetAssignment> thenExpressions, StatementContext statementContext, ExprNode andAfterTerminateExpr, List<OnTriggerSetAssignment> andAfterTerminateThenExpressions, boolean isStartConditionOnCreation) throws ExprValidationException {
        return new OutputConditionExpressionFactory(whenExpressionNode, thenExpressions, statementContext, andAfterTerminateExpr, andAfterTerminateThenExpressions, isStartConditionOnCreation);
    }

    public OutputConditionFactory makeOutputConditionCrontab(List<ExprNode> crontabAtSchedule, StatementContext statementContext, boolean isStartConditionOnCreation) throws ExprValidationException {
        return new OutputConditionCrontabFactory(crontabAtSchedule, statementContext, isStartConditionOnCreation);
    }

    public OutputConditionFactory makeOutputConditionCount(int rate, VariableMetaData variableMetaData, StatementContext statementContext) {
        return new OutputConditionCountFactory(rate, variableMetaData);
    }

    public OutputProcessViewAfterState makeOutputConditionAfter(Long afterConditionTime, Integer afterConditionNumberOfEvents, boolean afterConditionSatisfied, AgentInstanceContext agentInstanceContext) {
        if (afterConditionSatisfied) {
            return OutputProcessViewAfterStateNone.INSTANCE;
        }
        return new OutputProcessViewAfterStateImpl(afterConditionTime, afterConditionNumberOfEvents);
    }

    public ResultSetProcessorRowPerEventOutputLastHelperImpl makeRSRowPerEventOutputLast(ResultSetProcessorRowPerEvent processor, AgentInstanceContext agentInstanceContext) {
        return new ResultSetProcessorRowPerEventOutputLastHelperImpl(processor);
    }

    public ResultSetProcessorRowPerEventOutputAllHelper makeRSRowPerEventOutputAll(ResultSetProcessorRowPerEvent processor, AgentInstanceContext agentInstanceContext) {
        return new ResultSetProcessorRowPerEventOutputAllHelperImpl(processor);
    }

    public ResultSetProcessorRowForAllOutputLastHelper makeRSRowForAllOutputLast(ResultSetProcessorRowForAll processor, AgentInstanceContext agentInstanceContext) {
        return new ResultSetProcessorRowForAllOutputLastHelperImpl(processor);
    }

    public ResultSetProcessorRowForAllOutputAllHelper makeRSRowForAllOutputAll(ResultSetProcessorRowForAll processor, AgentInstanceContext agentInstanceContext) {
        return new ResultSetProcessorRowForAllOutputAllHelperImpl(processor);
    }

    public ResultSetProcessorGroupedOutputAllGroupReps makeRSGroupedOutputAllNoOpt(AgentInstanceContext agentInstanceContext, Class[] groupKeyTypes, int numStreams) {
        return new ResultSetProcessorGroupedOutputAllGroupRepsImpl();
    }

    public ResultSetProcessorRowPerGroupOutputAllHelper makeRSRowPerGroupOutputAllOpt(AgentInstanceContext agentInstanceContext, ResultSetProcessorRowPerGroup resultSetProcessorRowPerGroup, Class[] groupKeyTypes, int numStreams) {
        return new ResultSetProcessorRowPerGroupOutputAllHelperImpl(resultSetProcessorRowPerGroup);
    }

    public ResultSetProcessorRowPerGroupOutputLastHelper makeRSRowPerGroupOutputLastOpt(AgentInstanceContext agentInstanceContext, ResultSetProcessorRowPerGroup resultSetProcessorRowPerGroup, Class[] groupKeyTypes, int numStreams) {
        return new ResultSetProcessorRowPerGroupOutputLastHelperImpl(resultSetProcessorRowPerGroup);
    }

    public ResultSetProcessorGroupedOutputFirstHelper makeRSGroupedOutputFirst(AgentInstanceContext agentInstanceContext, Class[] groupKeyTypes, OutputConditionPolledFactory optionalOutputFirstConditionFactory, AggregationGroupByRollupDesc optionalGroupByRollupDesc, int optionalRollupLevel) {
        return new ResultSetProcessorGroupedOutputFirstHelperImpl();
    }

    public ResultSetProcessorRowPerGroupUnboundHelper makeRSRowPerGroupUnboundGroupRep(AgentInstanceContext agentInstanceContext, Class[] groupKeyTypes) {
        return new ResultSetProcessorRowPerGroupUnboundHelperImpl();
    }

    public ResultSetProcessorAggregateGroupedOutputAllHelper makeRSAggregateGroupedOutputAll(AgentInstanceContext agentInstanceContext, ResultSetProcessorAggregateGrouped processor, Class[] groupKeyTypes, int numStreams) {
        return new ResultSetProcessorAggregateGroupedOutputAllHelperImpl(processor);
    }

    public ResultSetProcessorAggregateGroupedOutputLastHelper makeRSAggregateGroupedOutputLastOpt(AgentInstanceContext agentInstanceContext, ResultSetProcessorAggregateGrouped resultSetProcessorAggregateGrouped, Class[] groupKeyTypes, int numStreams) {
        return new ResultSetProcessorAggregateGroupedOutputLastHelperImpl(resultSetProcessorAggregateGrouped);
    }

    public ResultSetProcessorRowPerGroupRollupOutputLastHelper makeRSRowPerGroupRollupLast(AgentInstanceContext agentInstanceContext, ResultSetProcessorRowPerGroupRollup resultSetProcessorRowPerGroupRollup, Class[] groupKeyTypes, int numStreams) {
        return new ResultSetProcessorRowPerGroupRollupOutputLastHelperImpl(resultSetProcessorRowPerGroupRollup, resultSetProcessorRowPerGroupRollup.getGroupByRollupDesc().getLevels().length);
    }

    public ResultSetProcessorRowPerGroupRollupOutputAllHelper makeRSRowPerGroupRollupAll(AgentInstanceContext agentInstanceContext, ResultSetProcessorRowPerGroupRollup resultSetProcessorRowPerGroupRollup, Class[] groupKeyTypes, int numStreams) {
        return new ResultSetProcessorRowPerGroupRollupOutputAllHelperImpl(resultSetProcessorRowPerGroupRollup, resultSetProcessorRowPerGroupRollup.getGroupByRollupDesc().getLevels().length);
    }

    public ResultSetProcessorRowPerGroupRollupUnboundHelper makeRSRowPerGroupRollupSnapshotUnbound(AgentInstanceContext agentInstanceContext, ResultSetProcessorRowPerGroupRollup resultSetProcessorRowPerGroupRollup, Class[] groupKeyTypes, int numStreams) {
        int levelCount = resultSetProcessorRowPerGroupRollup.getGroupByRollupDesc().getLevels().length;
        return new ResultSetProcessorRowPerGroupRollupUnboundHelperImpl(levelCount);
    }
}
