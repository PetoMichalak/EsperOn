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
import eu.uk.ncl.pet5o.esper.epl.core.resultset.agggrouped.ResultSetProcessorAggregateGrouped;
import eu.uk.ncl.pet5o.esper.epl.core.resultset.agggrouped.ResultSetProcessorAggregateGroupedOutputAllHelper;
import eu.uk.ncl.pet5o.esper.epl.core.resultset.agggrouped.ResultSetProcessorAggregateGroupedOutputLastHelper;
import eu.uk.ncl.pet5o.esper.epl.core.resultset.grouped.ResultSetProcessorGroupedOutputAllGroupReps;
import eu.uk.ncl.pet5o.esper.epl.core.resultset.grouped.ResultSetProcessorGroupedOutputFirstHelper;
import eu.uk.ncl.pet5o.esper.epl.core.resultset.handthru.ResultSetProcessorSimple;
import eu.uk.ncl.pet5o.esper.epl.core.resultset.handthru.ResultSetProcessorSimpleOutputAllHelper;
import eu.uk.ncl.pet5o.esper.epl.core.resultset.handthru.ResultSetProcessorSimpleOutputLastHelper;
import eu.uk.ncl.pet5o.esper.epl.core.resultset.rowforall.ResultSetProcessorRowForAll;
import eu.uk.ncl.pet5o.esper.epl.core.resultset.rowforall.ResultSetProcessorRowForAllOutputAllHelper;
import eu.uk.ncl.pet5o.esper.epl.core.resultset.rowforall.ResultSetProcessorRowForAllOutputLastHelper;
import eu.uk.ncl.pet5o.esper.epl.core.resultset.rowperevent.ResultSetProcessorRowPerEvent;
import eu.uk.ncl.pet5o.esper.epl.core.resultset.rowperevent.ResultSetProcessorRowPerEventOutputAllHelper;
import eu.uk.ncl.pet5o.esper.epl.core.resultset.rowperevent.ResultSetProcessorRowPerEventOutputLastHelper;
import eu.uk.ncl.pet5o.esper.epl.core.resultset.rowpergroup.ResultSetProcessorRowPerGroup;
import eu.uk.ncl.pet5o.esper.epl.core.resultset.rowpergroup.ResultSetProcessorRowPerGroupOutputAllHelper;
import eu.uk.ncl.pet5o.esper.epl.core.resultset.rowpergroup.ResultSetProcessorRowPerGroupOutputLastHelper;
import eu.uk.ncl.pet5o.esper.epl.core.resultset.rowpergroup.ResultSetProcessorRowPerGroupUnboundHelper;
import eu.uk.ncl.pet5o.esper.epl.core.resultset.rowpergrouprollup.ResultSetProcessorRowPerGroupRollup;
import eu.uk.ncl.pet5o.esper.epl.core.resultset.rowpergrouprollup.ResultSetProcessorRowPerGroupRollupOutputAllHelper;
import eu.uk.ncl.pet5o.esper.epl.core.resultset.rowpergrouprollup.ResultSetProcessorRowPerGroupRollupOutputLastHelper;
import eu.uk.ncl.pet5o.esper.epl.core.resultset.rowpergrouprollup.ResultSetProcessorRowPerGroupRollupUnboundHelper;
import eu.uk.ncl.pet5o.esper.epl.expression.core.ExprNode;
import eu.uk.ncl.pet5o.esper.epl.expression.core.ExprValidationException;
import eu.uk.ncl.pet5o.esper.epl.expression.time.ExprTimePeriod;
import eu.uk.ncl.pet5o.esper.epl.spec.OnTriggerSetAssignment;
import eu.uk.ncl.pet5o.esper.epl.variable.VariableMetaData;
import eu.uk.ncl.pet5o.esper.epl.view.OutputConditionFactory;
import eu.uk.ncl.pet5o.esper.epl.view.OutputConditionPolledFactory;
import eu.uk.ncl.pet5o.esper.epl.view.OutputProcessViewAfterState;
import eu.uk.ncl.pet5o.esper.epl.view.OutputProcessViewConditionDeltaSet;

import java.util.List;

public interface ResultSetProcessorHelperFactory {
    OutputProcessViewConditionDeltaSet makeOutputConditionChangeSet(boolean isJoin, AgentInstanceContext agentInstanceContext);

    OutputConditionFactory makeOutputConditionTime(ExprTimePeriod timePeriodExpr, boolean isStartConditionOnCreation);

    OutputConditionFactory makeOutputConditionExpression(ExprNode whenExpressionNode, List<OnTriggerSetAssignment> thenExpressions, StatementContext statementContext, ExprNode andAfterTerminateExpr, List<OnTriggerSetAssignment> andAfterTerminateThenExpressions, boolean isStartConditionOnCreation) throws ExprValidationException;

    OutputConditionFactory makeOutputConditionCrontab(List<ExprNode> crontabAtSchedule, StatementContext statementContext, boolean isStartConditionOnCreation) throws ExprValidationException;

    OutputConditionFactory makeOutputConditionCount(int rate, VariableMetaData variableMetaData, StatementContext statementContext);

    OutputProcessViewAfterState makeOutputConditionAfter(Long afterConditionTime, Integer afterConditionNumberOfEvents, boolean afterConditionSatisfied, AgentInstanceContext agentInstanceContext);

    ResultSetProcessorSimpleOutputLastHelper makeRSSimpleOutputLast(ResultSetProcessorSimple simple, AgentInstanceContext agentInstanceContext, int numStreams);

    ResultSetProcessorSimpleOutputAllHelper makeRSSimpleOutputAll(ResultSetProcessorSimple simple, AgentInstanceContext agentInstanceContext, int numStreams);

    ResultSetProcessorRowPerEventOutputLastHelper makeRSRowPerEventOutputLast(ResultSetProcessorRowPerEvent processor, AgentInstanceContext agentInstanceContext);

    ResultSetProcessorRowPerEventOutputAllHelper makeRSRowPerEventOutputAll(ResultSetProcessorRowPerEvent processor, AgentInstanceContext agentInstanceContext);

    ResultSetProcessorRowForAllOutputLastHelper makeRSRowForAllOutputLast(ResultSetProcessorRowForAll processor, AgentInstanceContext agentInstanceContext);

    ResultSetProcessorRowForAllOutputAllHelper makeRSRowForAllOutputAll(ResultSetProcessorRowForAll processor, AgentInstanceContext agentInstanceContext);

    ResultSetProcessorGroupedOutputAllGroupReps makeRSGroupedOutputAllNoOpt(AgentInstanceContext agentInstanceContext, Class[] groupKeyTypes, int numStreams);

    ResultSetProcessorRowPerGroupOutputAllHelper makeRSRowPerGroupOutputAllOpt(AgentInstanceContext agentInstanceContext, ResultSetProcessorRowPerGroup resultSetProcessorRowPerGroup, Class[] groupKeyTypes, int numStreams);

    ResultSetProcessorRowPerGroupOutputLastHelper makeRSRowPerGroupOutputLastOpt(AgentInstanceContext agentInstanceContext, ResultSetProcessorRowPerGroup resultSetProcessorRowPerGroup, Class[] groupKeyTypes, int numStreams);

    ResultSetProcessorRowPerGroupUnboundHelper makeRSRowPerGroupUnboundGroupRep(AgentInstanceContext agentInstanceContext, Class[] groupKeyTypes);

    ResultSetProcessorAggregateGroupedOutputAllHelper makeRSAggregateGroupedOutputAll(AgentInstanceContext agentInstanceContext, ResultSetProcessorAggregateGrouped resultSetProcessorAggregateGrouped, Class[] groupKeyTypes, int numStreams);

    ResultSetProcessorAggregateGroupedOutputLastHelper makeRSAggregateGroupedOutputLastOpt(AgentInstanceContext agentInstanceContext, ResultSetProcessorAggregateGrouped resultSetProcessorAggregateGrouped, Class[] groupKeyTypes, int numStreams);

    ResultSetProcessorGroupedOutputFirstHelper makeRSGroupedOutputFirst(AgentInstanceContext agentInstanceContext, Class[] groupKeyTypes, OutputConditionPolledFactory optionalOutputFirstConditionFactory, AggregationGroupByRollupDesc optionalGroupByRollupDesc, int optionalRollupLevel);

    ResultSetProcessorRowPerGroupRollupOutputLastHelper makeRSRowPerGroupRollupLast(AgentInstanceContext agentInstanceContext, ResultSetProcessorRowPerGroupRollup resultSetProcessorRowPerGroupRollup, Class[] groupKeyTypes, int numStreams);

    ResultSetProcessorRowPerGroupRollupOutputAllHelper makeRSRowPerGroupRollupAll(AgentInstanceContext agentInstanceContext, ResultSetProcessorRowPerGroupRollup resultSetProcessorRowPerGroupRollup, Class[] groupKeyTypes, int numStreams);

    ResultSetProcessorRowPerGroupRollupUnboundHelper makeRSRowPerGroupRollupSnapshotUnbound(AgentInstanceContext agentInstanceContext, ResultSetProcessorRowPerGroupRollup resultSetProcessorRowPerGroupRollup, Class[] groupKeyTypes, int numStreams);
}
