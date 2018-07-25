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
package eu.uk.ncl.pet5o.esper.metrics.instrumentation;

import eu.uk.ncl.pet5o.esper.client.EPStatementState;
import eu.uk.ncl.pet5o.esper.client.EventBean;
import eu.uk.ncl.pet5o.esper.collection.MultiKey;
import eu.uk.ncl.pet5o.esper.collection.Pair;
import eu.uk.ncl.pet5o.esper.collection.UniformPair;
import eu.uk.ncl.pet5o.esper.core.context.util.AgentInstanceContext;
import eu.uk.ncl.pet5o.esper.core.context.util.ContextDescriptor;
import eu.uk.ncl.pet5o.esper.core.context.util.EPStatementAgentInstanceHandle;
import eu.uk.ncl.pet5o.esper.core.service.EPStatementHandle;
import eu.uk.ncl.pet5o.esper.core.service.InternalEventRouterEntry;
import eu.uk.ncl.pet5o.esper.epl.agg.access.AggregationState;
import eu.uk.ncl.pet5o.esper.epl.agg.aggregator.AggregationMethod;
import eu.uk.ncl.pet5o.esper.epl.core.orderby.OrderByElementEval;
import eu.uk.ncl.pet5o.esper.epl.expression.baseagg.ExprAggregateNode;
import eu.uk.ncl.pet5o.esper.epl.expression.baseagg.ExprAggregateNodeBase;
import eu.uk.ncl.pet5o.esper.epl.expression.core.*;
import eu.uk.ncl.pet5o.esper.epl.expression.dot.ExprDotEval;
import eu.uk.ncl.pet5o.esper.epl.expression.dot.ExprDotNode;
import eu.uk.ncl.pet5o.esper.epl.expression.funcs.*;
import eu.uk.ncl.pet5o.esper.epl.expression.ops.*;
import eu.uk.ncl.pet5o.esper.epl.expression.prev.ExprPreviousNode;
import eu.uk.ncl.pet5o.esper.epl.expression.prior.ExprPriorNode;
import eu.uk.ncl.pet5o.esper.epl.expression.subquery.ExprSubselectNode;
import eu.uk.ncl.pet5o.esper.epl.expression.time.ExprTimePeriodImpl;
import eu.uk.ncl.pet5o.esper.epl.expression.time.ExprTimestampNode;
import eu.uk.ncl.pet5o.esper.epl.join.exec.base.JoinExecTableLookupStrategy;
import eu.uk.ncl.pet5o.esper.epl.join.table.EventTable;
import eu.uk.ncl.pet5o.esper.epl.lookup.SubordTableLookupStrategy;
import eu.uk.ncl.pet5o.esper.epl.lookup.SubordWMatchExprLookupStrategyType;
import eu.uk.ncl.pet5o.esper.epl.named.NamedWindowConsumerView;
import eu.uk.ncl.pet5o.esper.epl.named.NamedWindowDeltaData;
import eu.uk.ncl.pet5o.esper.epl.rettype.EPType;
import eu.uk.ncl.pet5o.esper.epl.spec.ExpressionDeclItem;
import eu.uk.ncl.pet5o.esper.epl.spec.OnTriggerType;
import eu.uk.ncl.pet5o.esper.epl.updatehelper.EventBeanUpdateItem;
import eu.uk.ncl.pet5o.esper.filter.*;
import eu.uk.ncl.pet5o.esper.filterspec.ExprNodeAdapterBase;
import eu.uk.ncl.pet5o.esper.filterspec.FilterValueSet;
import eu.uk.ncl.pet5o.esper.filterspec.MatchedEventMap;
import eu.uk.ncl.pet5o.esper.pattern.*;
import eu.uk.ncl.pet5o.esper.rowregex.RegexNFAState;
import eu.uk.ncl.pet5o.esper.rowregex.RegexNFAStateEntry;
import eu.uk.ncl.pet5o.esper.rowregex.RegexPartitionState;
import eu.uk.ncl.pet5o.esper.schedule.ScheduleHandle;
import eu.uk.ncl.pet5o.esper.type.BitWiseOpEnum;
import eu.uk.ncl.pet5o.esper.view.View;

import java.lang.reflect.Method;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public interface Instrumentation {
    void qStimulantEvent(eu.uk.ncl.pet5o.esper.client.EventBean eventBean, String engineURI);

    void aStimulantEvent();

    void qStimulantTime(long currentTime, String engineURI);

    void aStimulantTime();

    void qEvent(eu.uk.ncl.pet5o.esper.client.EventBean eventBean, String engineURI, boolean providedBySendEvent);

    void aEvent();

    void qEventCP(eu.uk.ncl.pet5o.esper.client.EventBean theEvent, EPStatementAgentInstanceHandle handle, long engineTime);

    void aEventCP();

    void qTime(long engineTime, String engineURI);

    void aTime();

    void qTimeCP(EPStatementAgentInstanceHandle handle, long engineTime);

    void aTimeCP();

    void qNamedWindowDispatch(String engineURI);

    void aNamedWindowDispatch();

    void qNamedWindowCPSingle(String engineURI, List<NamedWindowConsumerView> value, eu.uk.ncl.pet5o.esper.client.EventBean[] newData, eu.uk.ncl.pet5o.esper.client.EventBean[] oldData, EPStatementAgentInstanceHandle handle, long time);

    void aNamedWindowCPSingle();

    void qNamedWindowCPMulti(String engineURI, Map<NamedWindowConsumerView, NamedWindowDeltaData> deltaPerConsumer, EPStatementAgentInstanceHandle handle, long time);

    void aNamedWindowCPMulti();

    void qRegEx(eu.uk.ncl.pet5o.esper.client.EventBean newEvent, RegexPartitionState partitionState);

    void aRegEx(RegexPartitionState partitionState, List<RegexNFAStateEntry> endStates, List<RegexNFAStateEntry> terminationStates);

    void qRegExState(RegexNFAStateEntry currentState, LinkedHashMap<String, Pair<Integer, Boolean>> variableStreams, int[] multimatchStreamNumToVariable);

    void aRegExState(List<RegexNFAStateEntry> next, LinkedHashMap<String, Pair<Integer, Boolean>> variableStreams, int[] multimatchStreamNumToVariable);

    void qRegExStateStart(RegexNFAState startState, LinkedHashMap<String, Pair<Integer, Boolean>> variableStreams, int[] multimatchStreamNumToVariable);

    void aRegExStateStart(List<RegexNFAStateEntry> nextStates, LinkedHashMap<String, Pair<Integer, Boolean>> variableStreams, int[] multimatchStreamNumToVariable);

    void qRegExPartition(ExprNode[] partitionExpressionNodes);

    void aRegExPartition(boolean exists, RegexPartitionState state);

    void qRegIntervalValue(ExprNode exprNode);

    void aRegIntervalValue(long result);

    void qRegIntervalState(RegexNFAStateEntry endState, LinkedHashMap<String, Pair<Integer, Boolean>> variableStreams, int[] multimatchStreamNumToVariable, long engineTime);

    void aRegIntervalState(boolean scheduled);

    void qRegOut(eu.uk.ncl.pet5o.esper.client.EventBean[] outBeans);

    void aRegOut();

    void qRegMeasure(RegexNFAStateEntry endState, LinkedHashMap<String, Pair<Integer, Boolean>> variableStreams, int[] multimatchStreamNumToVariable);

    void aRegMeasure(eu.uk.ncl.pet5o.esper.client.EventBean outBean);

    void qRegExScheduledEval();

    void aRegExScheduledEval();

    void qExprBool(ExprNode exprNode, eu.uk.ncl.pet5o.esper.client.EventBean[] eventsPerStream);

    void aExprBool(Boolean result);

    void qExprValue(ExprNode exprNode, eu.uk.ncl.pet5o.esper.client.EventBean[] eventsPerStream);

    void aExprValue(Object result);

    void qExprEquals(ExprNode exprNode);

    void aExprEquals(Boolean result);

    void qExprAnd(ExprNode exprNode);

    void aExprAnd(Boolean result);

    void qExprLike(ExprNode exprNode);

    void aExprLike(Boolean result);

    void qExprBitwise(ExprNode exprNode, BitWiseOpEnum bitWiseOpEnum);

    void aExprBitwise(Object result);

    void qExprMath(ExprMathNode exprMathNode, String op);

    void aExprMath(Object result);

    void qExprRegexp(ExprRegexpNode exprRegexpNode);

    void aExprRegexp(Boolean result);

    void qExprIdent(String fullUnresolvedName);

    void aExprIdent(Object result);

    void qExprTypeof();

    void aExprTypeof(String typeName);

    void qExprOr(ExprOrNode exprOrNode);

    void aExprOr(Boolean result);

    void qExprIn(ExprInNodeImpl exprInNode);

    void aExprIn(Boolean result);

    void qExprCoalesce(ExprCoalesceNode exprCoalesceNode);

    void aExprCoalesce(Object value);

    void qExprConcat(ExprConcatNode exprConcatNode);

    void aExprConcat(String result);

    void qaExprConst(Object result);

    void qaExprTimestamp(ExprTimestampNode exprTimestampNode, long value);

    void qExprBetween(ExprBetweenNodeImpl exprBetweenNode);

    void aExprBetween(Boolean result);

    void qExprCast(ExprCastNode exprCastNode);

    void aExprCast(Object result);

    void qExprCase(ExprCaseNode exprCaseNode);

    void aExprCase(Object result);

    void qExprArray(ExprArrayNode exprArrayNode);

    void aExprArray(Object result);

    void qExprEqualsAnyOrAll(ExprEqualsAllAnyNode exprEqualsAllAnyNode);

    void aExprEqualsAnyOrAll(Boolean result);

    void qExprMinMaxRow(ExprMinMaxRowNode exprMinMaxRowNode);

    void aExprMinMaxRow(Object result);

    void qExprNew(ExprNewStructNode exprNewNode);

    void aExprNew(Map<String, Object> props);

    void qExprNot(ExprNotNode exprNotNode);

    void aExprNot(Boolean result);

    void qExprPropExists(ExprPropertyExistsNode exprPropertyExistsNode);

    void aExprPropExists(boolean exists);

    void qExprRelOpAnyOrAll(ExprRelationalOpAllAnyNode exprRelationalOpAllAnyNode, String op);

    void aExprRelOpAnyOrAll(Boolean result);

    void qExprRelOp(ExprRelationalOpNodeImpl exprRelationalOpNode, String op);

    void aExprRelOp(Boolean result);

    void qExprStreamUnd(ExprStreamUnderlyingNodeImpl exprStreamUnderlyingNode);

    void aExprStreamUnd(Object result);

    void qExprStreamUndSelectClause(ExprStreamUnderlyingNode undNode);

    void aExprStreamUndSelectClause(eu.uk.ncl.pet5o.esper.client.EventBean event);

    void qExprIs(ExprEqualsNodeImpl exprNode);

    void aExprIs(boolean result);

    void qExprVariable(ExprVariableNode exprVariableNode);

    void aExprVariable(Object value);

    void qExprTimePeriod(ExprTimePeriodImpl exprTimePeriod);

    void aExprTimePeriod(Object result);

    void qExprInstanceof(ExprInstanceofNode exprInstanceofNode);

    void aExprInstanceof(Boolean result);

    void qExprContextProp(ExprContextPropertyNodeImpl exprContextPropertyNode);

    void aExprContextProp(Object result);

    void qExprPlugInSingleRow(Method method);

    void aExprPlugInSingleRow(Object result);

    void qaExprAggValue(ExprAggregateNodeBase exprAggregateNodeBase, Object value);

    void qExprSubselect(ExprSubselectNode exprSubselectNode);

    void aExprSubselect(Object result);

    void qExprDot(ExprDotNode exprDotNode);

    void aExprDot(Object result);

    void qExprDotChain(EPType targetTypeInfo, Object target, ExprDotEval[] evalUnpacking);

    void aExprDotChain();

    void qExprDotChainElement(int num, ExprDotEval methodEval);

    void aExprDotChainElement(EPType typeInfo, Object result);

    void qaExprIStream(ExprIStreamNode exprIStreamNode, boolean newData);

    void qExprDeclared(ExpressionDeclItem parent);

    void aExprDeclared(Object value);

    void qExprPrev(ExprPreviousNode exprPreviousNode, boolean newData);

    void aExprPrev(Object result);

    void qExprPrior(ExprPriorNode exprPriorNode);

    void aExprPrior(Object result);

    void qExprStreamUndMethod(ExprDotNode exprDotEvalStreamMethod);

    void aExprStreamUndMethod(Object result);

    void qExprStreamEventMethod(ExprDotNode exprDotNode);

    void aExprStreamEventMethod(Object result);

    void qExprTableSubproperty(ExprNode exprNode, String tableName, String subpropName);

    void aExprTableSubproperty(Object result);

    void qExprTableTop(ExprNode exprNode, String tableName);

    void aExprTableTop(Object result);

    void qExprTableSubpropAccessor(ExprNode exprNode, String tableName, String subpropName, ExprAggregateNode aggregationExpression);

    void aExprTableSubpropAccessor(Object result);

    void qScheduleAdd(long currentTime, long afterMSec, ScheduleHandle handle, long slot);

    void aScheduleAdd();

    void qScheduleRemove(ScheduleHandle handle, long slot);

    void aScheduleRemove();

    void qScheduleEval(long currentTime);

    void aScheduleEval(Collection<ScheduleHandle> handles);

    void qPatternAndEvaluateTrue(EvalAndNode evalAndNode, MatchedEventMap passUp);

    void aPatternAndEvaluateTrue(boolean quitted);

    void qPatternAndQuit(EvalAndNode evalAndNode);

    void aPatternAndQuit();

    void qPatternAndEvaluateFalse(EvalAndNode evalAndNode);

    void aPatternAndEvaluateFalse();

    void qPatternAndStart(EvalAndNode evalAndNode, MatchedEventMap beginState);

    void aPatternAndStart();

    void qPatternFollowedByEvaluateTrue(EvalFollowedByNode evalFollowedByNode, MatchedEventMap matchEvent, Integer index);

    void aPatternFollowedByEvaluateTrue(boolean quitted);

    void qPatternFollowedByQuit(EvalFollowedByNode evalFollowedByNode);

    void aPatternFollowedByQuit();

    void qPatternFollowedByEvalFalse(EvalFollowedByNode evalFollowedByNode);

    void aPatternFollowedByEvalFalse();

    void qPatternFollowedByStart(EvalFollowedByNode evalFollowedByNode, MatchedEventMap beginState);

    void aPatternFollowedByStart();

    void qPatternOrEvaluateTrue(EvalOrNode evalOrNode, MatchedEventMap matchEvent);

    void aPatternOrEvaluateTrue(boolean quitted);

    void qPatternOrEvalFalse(EvalOrNode evalOrNode);

    void aPatternOrEvalFalse();

    void qPatternOrQuit(EvalOrNode evalOrNode);

    void aPatternOrQuit();

    void aPatternOrStart();

    void qPatternOrStart(EvalOrNode evalOrNode, MatchedEventMap beginState);

    void qPatternFilterMatch(EvalFilterNode filterNode, eu.uk.ncl.pet5o.esper.client.EventBean theEvent);

    void aPatternFilterMatch(boolean quitted);

    void qPatternFilterStart(EvalFilterNode evalFilterNode, MatchedEventMap beginState);

    void aPatternFilterStart();

    void qPatternFilterQuit(EvalFilterNode evalFilterNode, MatchedEventMap beginState);

    void aPatternFilterQuit();

    void qPatternRootEvaluateTrue(MatchedEventMap matchEvent);

    void aPatternRootEvaluateTrue(boolean quitted);

    void qPatternRootStart(MatchedEventMap root);

    void aPatternRootStart();

    void qPatternRootQuit();

    void aPatternRootQuit();

    void qPatternRootEvalFalse();

    void aPatternRootEvalFalse();

    void qPatternEveryEvaluateTrue(EvalEveryNode evalEveryNode, MatchedEventMap matchEvent);

    void aPatternEveryEvaluateTrue();

    void qPatternEveryStart(EvalEveryNode evalEveryNode, MatchedEventMap beginState);

    void aPatternEveryStart();

    void qPatternEveryEvalFalse(EvalEveryNode evalEveryNode);

    void aPatternEveryEvalFalse();

    void qPatternEveryQuit(EvalEveryNode evalEveryNode);

    void aPatternEveryQuit();

    void qPatternEveryDistinctEvaluateTrue(EvalEveryDistinctNode everyDistinctNode, MatchedEventMap matchEvent);

    void aPatternEveryDistinctEvaluateTrue(Set<Object> keysFromNodeNoExpire, LinkedHashMap<Object, Long> keysFromNodeExpire, Object matchEventKey, boolean haveSeenThis);

    void qPatternEveryDistinctQuit(EvalEveryDistinctNode everyNode);

    void aPatternEveryDistinctQuit();

    void qPatternEveryDistinctEvalFalse(EvalEveryDistinctNode everyNode);

    void aPatternEveryDistinctEvalFalse();

    void qPatternEveryDistinctStart(EvalEveryDistinctNode everyNode, MatchedEventMap beginState);

    void aPatternEveryDistinctStart();

    void qPatternGuardEvaluateTrue(EvalGuardNode evalGuardNode, MatchedEventMap matchEvent);

    void aPatternGuardEvaluateTrue(boolean quitted);

    void qPatternGuardStart(EvalGuardNode evalGuardNode, MatchedEventMap beginState);

    void aPatternGuardStart();

    void qPatternGuardQuit(EvalGuardNode evalGuardNode);

    void aPatternGuardQuit();

    void qPatternGuardGuardQuit(EvalGuardNode evalGuardNode);

    void aPatternGuardGuardQuit();

    void qPatternGuardScheduledEval();

    void aPatternGuardScheduledEval();

    void qPatternMatchUntilEvaluateTrue(EvalMatchUntilNode evalMatchUntilNode, MatchedEventMap matchEvent, boolean matchFromUntil);

    void aPatternMatchUntilEvaluateTrue(boolean quitted);

    void qPatternMatchUntilStart(EvalMatchUntilNode evalMatchUntilNode, MatchedEventMap beginState);

    void aPatternMatchUntilStart();

    void qPatternMatchUntilEvalFalse(EvalMatchUntilNode evalMatchUntilNode, boolean matchFromUntil);

    void aPatternMatchUntilEvalFalse();

    void qPatternMatchUntilQuit(EvalMatchUntilNode evalMatchUntilNode);

    void aPatternMatchUntilQuit();

    void qPatternNotEvaluateTrue(EvalNotNode evalNotNode, MatchedEventMap matchEvent);

    void aPatternNotEvaluateTrue(boolean quitted);

    void aPatternNotQuit();

    void qPatternNotQuit(EvalNotNode evalNotNode);

    void qPatternNotStart(EvalNotNode evalNotNode, MatchedEventMap beginState);

    void aPatternNotStart();

    void qPatternNotEvalFalse(EvalNotNode evalNotNode);

    void aPatternNotEvalFalse();

    void qPatternObserverEvaluateTrue(EvalObserverNode evalObserverNode, MatchedEventMap matchEvent);

    void aPatternObserverEvaluateTrue();

    void qPatternObserverStart(EvalObserverNode evalObserverNode, MatchedEventMap beginState);

    void aPatternObserverStart();

    void qPatternObserverQuit(EvalObserverNode evalObserverNode);

    void aPatternObserverQuit();

    void qPatternObserverScheduledEval();

    void aPatternObserverScheduledEval();

    void qContextPartitionAllocate(AgentInstanceContext agentInstanceContext);

    void aContextPartitionAllocate();

    void qContextPartitionDestroy(AgentInstanceContext agentInstanceContext);

    void aContextPartitionDestroy();

    void qContextScheduledEval(ContextDescriptor contextDescriptor);

    void aContextScheduledEval();

    void qOutputProcessNonBuffered(eu.uk.ncl.pet5o.esper.client.EventBean[] newData, eu.uk.ncl.pet5o.esper.client.EventBean[] oldData);

    void aOutputProcessNonBuffered();

    void qOutputProcessNonBufferedJoin(Set<MultiKey<EventBean>> newEvents, Set<MultiKey<EventBean>> oldEvents);

    void aOutputProcessNonBufferedJoin();

    void qOutputProcessWCondition(eu.uk.ncl.pet5o.esper.client.EventBean[] newData, eu.uk.ncl.pet5o.esper.client.EventBean[] oldData);

    void aOutputProcessWCondition(boolean buffered);

    void qOutputProcessWConditionJoin(Set<MultiKey<EventBean>> newEvents, Set<MultiKey<EventBean>> oldEvents);

    void aOutputProcessWConditionJoin(boolean buffered);

    void qOutputRateConditionUpdate(int newDataLength, int oldDataLength);

    void aOutputRateConditionUpdate();

    void qOutputRateConditionOutputNow();

    void aOutputRateConditionOutputNow(boolean generate);

    void qOutputRateConditionScheduledEval();

    void aOutputRateConditionScheduledEval();

    void qResultSetProcessSimple();

    void aResultSetProcessSimple(eu.uk.ncl.pet5o.esper.client.EventBean[] selectNewEvents, eu.uk.ncl.pet5o.esper.client.EventBean[] selectOldEvents);

    void qResultSetProcessUngroupedFullyAgg();

    void aResultSetProcessUngroupedFullyAgg(eu.uk.ncl.pet5o.esper.client.EventBean[] selectNewEvents, eu.uk.ncl.pet5o.esper.client.EventBean[] selectOldEvents);

    void qResultSetProcessUngroupedNonfullyAgg();

    void aResultSetProcessUngroupedNonfullyAgg(eu.uk.ncl.pet5o.esper.client.EventBean[] selectNewEvents, eu.uk.ncl.pet5o.esper.client.EventBean[] selectOldEvents);

    void qResultSetProcessGroupedRowPerGroup();

    void aResultSetProcessGroupedRowPerGroup(eu.uk.ncl.pet5o.esper.client.EventBean[] selectNewEvents, eu.uk.ncl.pet5o.esper.client.EventBean[] selectOldEvents);

    void aResultSetProcessGroupedRowPerGroup(UniformPair<eu.uk.ncl.pet5o.esper.client.EventBean[]> pair);

    void qResultSetProcessGroupedRowPerEvent();

    void aResultSetProcessGroupedRowPerEvent(eu.uk.ncl.pet5o.esper.client.EventBean[] selectNewEvents, eu.uk.ncl.pet5o.esper.client.EventBean[] selectOldEvents);

    void aResultSetProcessGroupedRowPerEvent(UniformPair<eu.uk.ncl.pet5o.esper.client.EventBean[]> pair);

    void qResultSetProcessComputeGroupKeys(boolean enter, ExprNode[] groupKeyNodeExpressions, eu.uk.ncl.pet5o.esper.client.EventBean[] eventsPerStream);

    void aResultSetProcessComputeGroupKeys(boolean enter, Object groupKeysPerEvent);

    void qAggregationUngroupedApplyEnterLeave(boolean enter, int numAggregators, int numAccessStates);

    void aAggregationUngroupedApplyEnterLeave(boolean enter);

    void qAggregationGroupedApplyEnterLeave(boolean enter, int numAggregators, int numAccessStates, Object groupKey);

    void aAggregationGroupedApplyEnterLeave(boolean enter);

    void qAggregationGroupedRollupEvalParam(boolean enter, int length);

    void aAggregationGroupedRollupEvalParam(Object result);

    void qAggNoAccessEnterLeave(boolean enter, int index, AggregationMethod aggregationMethod, ExprNode aggExpr);

    void aAggNoAccessEnterLeave(boolean enter, int index, AggregationMethod aggregationMethod);

    void qAggAccessEnterLeave(boolean enter, int index, AggregationState state, ExprNode aggExpr);

    void aAggAccessEnterLeave(boolean enter, int index, AggregationState state);

    void qSelectClause(eu.uk.ncl.pet5o.esper.client.EventBean[] eventsPerStream, boolean newData, boolean synthesize, ExprEvaluatorContext exprEvaluatorContext);

    void aSelectClause(boolean newData, eu.uk.ncl.pet5o.esper.client.EventBean event, Object[] subscriberParameters);

    void qViewProcessIRStream(View view, String viewName, eu.uk.ncl.pet5o.esper.client.EventBean[] newData, eu.uk.ncl.pet5o.esper.client.EventBean[] oldData);

    void aViewProcessIRStream();

    void qViewScheduledEval(View view, String viewName);

    void aViewScheduledEval();

    void qViewIndicate(View view, String viewName, eu.uk.ncl.pet5o.esper.client.EventBean[] newData, eu.uk.ncl.pet5o.esper.client.EventBean[] oldData);

    void aViewIndicate();

    void qSubselectAggregation(ExprNode optionalFilterExprNode);

    void aSubselectAggregation();

    void qFilterActivationSubselect(String eventTypeName, ExprSubselectNode subselectNode);

    void aFilterActivationSubselect();

    void qFilterActivationStream(String eventTypeName, int streamNumber);

    void aFilterActivationStream();

    void qFilterActivationNamedWindowInsert(String namedWindowName);

    void aFilterActivationNamedWindowInsert();

    void qFilterActivationOnTrigger(String eventTypeName);

    void aFilterActivationOnTrigger();

    void qRouteBetweenStmt(eu.uk.ncl.pet5o.esper.client.EventBean theEvent, EPStatementHandle epStatementHandle, boolean addToFront);

    void aRouteBetweenStmt();

    void qIndexAddRemove(EventTable eventTable, eu.uk.ncl.pet5o.esper.client.EventBean[] newData, eu.uk.ncl.pet5o.esper.client.EventBean[] oldData);

    void aIndexAddRemove();

    void qIndexAdd(EventTable eventTable, eu.uk.ncl.pet5o.esper.client.EventBean[] addEvents);

    void aIndexAdd();

    void qIndexRemove(EventTable eventTable, eu.uk.ncl.pet5o.esper.client.EventBean[] removeEvents);

    void aIndexRemove();

    void qIndexSubordLookup(SubordTableLookupStrategy subordTableLookupStrategy, EventTable optionalEventIndex, int[] keyStreamNums);

    void aIndexSubordLookup(Collection<EventBean> events, Object keys);

    void qIndexJoinLookup(JoinExecTableLookupStrategy strategy, EventTable index);

    void aIndexJoinLookup(Set<EventBean> result, Object keys);

    void qFilter(eu.uk.ncl.pet5o.esper.client.EventBean theEvent);

    void aFilter(Collection<FilterHandle> matches);

    void qFilterHandleSetIndexes(List<FilterParamIndexBase> indizes);

    void aFilterHandleSetIndexes();

    void qaFilterHandleSetCallbacks(Set<FilterHandle> callbackSet);

    void qFilterReverseIndex(FilterParamIndexLookupableBase filterParamIndex, Object propertyValue);

    void aFilterReverseIndex(Boolean match);

    void qFilterBoolean(FilterParamIndexBooleanExpr filterParamIndexBooleanExpr);

    void aFilterBoolean();

    void qFilterBooleanExpr(int num, Map.Entry<ExprNodeAdapterBase, EventEvaluator> evals);

    void aFilterBooleanExpr(boolean result);

    void qFilterAdd(FilterValueSet filterValueSet, FilterHandle filterCallback);

    void aFilterAdd();

    void qFilterRemove(FilterHandle filterCallback, EventTypeIndexBuilderValueIndexesPair pair);

    void aFilterRemove();

    void qWhereClauseFilter(ExprNode exprNode, eu.uk.ncl.pet5o.esper.client.EventBean[] newData, eu.uk.ncl.pet5o.esper.client.EventBean[] oldData);

    void aWhereClauseFilter(eu.uk.ncl.pet5o.esper.client.EventBean[] filteredNewData, eu.uk.ncl.pet5o.esper.client.EventBean[] filteredOldData);

    void qWhereClauseFilterEval(int num, eu.uk.ncl.pet5o.esper.client.EventBean event, boolean newData);

    void aWhereClauseFilterEval(Boolean pass);

    void qWhereClauseIR(eu.uk.ncl.pet5o.esper.client.EventBean[] filteredNewData, eu.uk.ncl.pet5o.esper.client.EventBean[] filteredOldData);

    void aWhereClauseIR();

    void qHavingClause(eu.uk.ncl.pet5o.esper.client.EventBean[] eventsPerStream);

    void aHavingClause(Boolean pass);

    void qOrderBy(eu.uk.ncl.pet5o.esper.client.EventBean[] evalEventsPerStream, OrderByElementEval[] orderBy);

    void aOrderBy(Object values);

    void qJoinDispatch(eu.uk.ncl.pet5o.esper.client.EventBean[][] newDataPerStream, eu.uk.ncl.pet5o.esper.client.EventBean[][] oldDataPerStream);

    void aJoinDispatch();

    void qJoinExexStrategy();

    void aJoinExecStrategy(UniformPair<Set<MultiKey<EventBean>>> joinSet);

    void qJoinExecFilter();

    void aJoinExecFilter(Set<MultiKey<EventBean>> newEvents, Set<MultiKey<EventBean>> oldEvents);

    void qJoinExecProcess(UniformPair<Set<MultiKey<EventBean>>> joinSet);

    void aJoinExecProcess();

    void qJoinCompositionStreamToWin();

    void aJoinCompositionStreamToWin(Set<MultiKey<EventBean>> newResults);

    void qJoinCompositionWinToWin();

    void aJoinCompositionWinToWin(Set<MultiKey<EventBean>> newResults, Set<MultiKey<EventBean>> oldResults);

    void qJoinCompositionHistorical();

    void aJoinCompositionHistorical(Set<MultiKey<EventBean>> newResults, Set<MultiKey<EventBean>> oldResults);

    void qJoinCompositionStepUpdIndex(int stream, eu.uk.ncl.pet5o.esper.client.EventBean[] added, eu.uk.ncl.pet5o.esper.client.EventBean[] removed);

    void aJoinCompositionStepUpdIndex();

    void qJoinCompositionQueryStrategy(boolean insert, int streamNum, eu.uk.ncl.pet5o.esper.client.EventBean[] events);

    void aJoinCompositionQueryStrategy();

    void qInfraTriggeredLookup(SubordWMatchExprLookupStrategyType lookupStrategy);

    void aInfraTriggeredLookup(eu.uk.ncl.pet5o.esper.client.EventBean[] result);

    void qInfraOnAction(OnTriggerType triggerType, eu.uk.ncl.pet5o.esper.client.EventBean[] triggerEvents, eu.uk.ncl.pet5o.esper.client.EventBean[] matchingEvents);

    void aInfraOnAction();

    void qInfraUpdate(eu.uk.ncl.pet5o.esper.client.EventBean beforeUpdate, eu.uk.ncl.pet5o.esper.client.EventBean[] eventsPerStream, int length, boolean copy);

    void aInfraUpdate(eu.uk.ncl.pet5o.esper.client.EventBean afterUpdate);

    void qInfraUpdateRHSExpr(int index, EventBeanUpdateItem updateItem);

    void aInfraUpdateRHSExpr(Object result);

    void qInfraMergeWhenThens(boolean matched, eu.uk.ncl.pet5o.esper.client.EventBean triggerEvent, int numWhenThens);

    void aInfraMergeWhenThens(boolean matched);

    void qInfraMergeWhenThenItem(boolean matched, int count);

    void aInfraMergeWhenThenItem(boolean matched, boolean actionsApplied);

    void qInfraMergeWhenThenActions(int numActions);

    void aInfraMergeWhenThenActions();

    void qInfraMergeWhenThenActionItem(int count, String actionName);

    void aInfraMergeWhenThenActionItem(boolean applies);

    void qEngineManagementStmtCompileStart(String engineURI, int statementId, String statementName, String epl, long engineTime);

    void aEngineManagementStmtCompileStart(boolean success, String message);

    void qaEngineManagementStmtStarted(String engineURI, int statementId, String statementName, String epl, long engineTime);

    void qEngineManagementStmtStop(EPStatementState targetState, String engineURI, int statementId, String statementName, String epl, long engineTime);

    void aEngineManagementStmtStop();

    void qaStatementResultExecute(UniformPair<eu.uk.ncl.pet5o.esper.client.EventBean[]> events, int statementId, String statementName, int agentInstanceId, long threadId);

    void qSplitStream(boolean all, eu.uk.ncl.pet5o.esper.client.EventBean theEvent, ExprEvaluator[] whereClauses);

    void aSplitStream(boolean all, boolean handled);

    void qSplitStreamWhere(int index);

    void aSplitStreamWhere(Boolean pass);

    void qSplitStreamRoute(int index);

    void aSplitStreamRoute();

    void qUpdateIStream(InternalEventRouterEntry[] entries);

    void aUpdateIStream(eu.uk.ncl.pet5o.esper.client.EventBean finalEvent, boolean haveCloned);

    void qUpdateIStreamApply(int index, InternalEventRouterEntry entry);

    void aUpdateIStreamApply(eu.uk.ncl.pet5o.esper.client.EventBean updated, boolean applied);

    void qUpdateIStreamApplyWhere();

    void aUpdateIStreamApplyWhere(Boolean result);

    void qUpdateIStreamApplyAssignments(InternalEventRouterEntry entry);

    void aUpdateIStreamApplyAssignments(Object[] values);

    void qUpdateIStreamApplyAssignmentItem(int index);

    void aUpdateIStreamApplyAssignmentItem(Object value);

    void qHistoricalScheduledEval();

    void aHistoricalScheduledEval();

    void qTableAddEvent(eu.uk.ncl.pet5o.esper.client.EventBean theEvent);

    void aTableAddEvent();

    void qTableDeleteEvent(eu.uk.ncl.pet5o.esper.client.EventBean theEvent);

    void aTableDeleteEvent();

    void qaTableUpdatedEvent(eu.uk.ncl.pet5o.esper.client.EventBean theEvent);

    void qaTableUpdatedEventWKeyBefore(eu.uk.ncl.pet5o.esper.client.EventBean theEvent);

    void qaTableUpdatedEventWKeyAfter(eu.uk.ncl.pet5o.esper.client.EventBean theEvent);
}

