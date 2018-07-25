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
package eu.uk.ncl.pet5o.esper.epl.core.resultset.rowperevent;

import eu.uk.ncl.pet5o.esper.client.EventBean;
import eu.uk.ncl.pet5o.esper.client.EventType;
import eu.uk.ncl.pet5o.esper.codegen.base.CodegenBlock;
import eu.uk.ncl.pet5o.esper.codegen.base.CodegenClassScope;
import eu.uk.ncl.pet5o.esper.codegen.base.CodegenMember;
import eu.uk.ncl.pet5o.esper.codegen.base.CodegenMethodNode;
import eu.uk.ncl.pet5o.esper.codegen.core.CodegenInstanceAux;
import eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionRelational;
import eu.uk.ncl.pet5o.esper.collection.ArrayEventIterator;
import eu.uk.ncl.pet5o.esper.collection.MultiKey;
import eu.uk.ncl.pet5o.esper.collection.UniformPair;
import eu.uk.ncl.pet5o.esper.core.context.util.AgentInstanceContext;
import eu.uk.ncl.pet5o.esper.epl.agg.service.common.AggregationService;
import eu.uk.ncl.pet5o.esper.epl.core.orderby.OrderByProcessor;
import eu.uk.ncl.pet5o.esper.epl.core.resultset.core.ResultSetProcessorHelperFactory;
import eu.uk.ncl.pet5o.esper.epl.core.resultset.core.ResultSetProcessorOutputConditionType;
import eu.uk.ncl.pet5o.esper.epl.core.resultset.core.ResultSetProcessorOutputHelperVisitor;
import eu.uk.ncl.pet5o.esper.epl.core.resultset.core.ResultSetProcessorUtil;
import eu.uk.ncl.pet5o.esper.epl.core.select.SelectExprProcessor;
import eu.uk.ncl.pet5o.esper.epl.expression.core.ExprEvaluator;
import eu.uk.ncl.pet5o.esper.epl.expression.core.ExprEvaluatorContext;
import eu.uk.ncl.pet5o.esper.metrics.instrumentation.InstrumentationHelper;
import eu.uk.ncl.pet5o.esper.util.CollectionUtil;
import eu.uk.ncl.pet5o.esper.view.Viewable;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import static eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionBuilder.*;
import static eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionBuilder.and;
import static eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionBuilder.arrayAtIndex;
import static eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionBuilder.arrayLength;
import static eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionBuilder.cast;
import static eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionBuilder.constant;
import static eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionBuilder.constantFalse;
import static eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionBuilder.constantNull;
import static eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionBuilder.constantTrue;
import static eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionBuilder.equalsNull;
import static eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionBuilder.exprDotMethod;
import static eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionBuilder.localMethod;
import static eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionBuilder.member;
import static eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionBuilder.newArrayByLength;
import static eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionBuilder.newInstance;
import static eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionBuilder.not;
import static eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionBuilder.notEqualsNull;
import static eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionBuilder.op;
import static eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionBuilder.ref;
import static eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionBuilder.relational;
import static eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionBuilder.staticMethod;
import static eu.uk.ncl.pet5o.esper.epl.core.resultset.codegen.ResultSetProcessorCodegenNames.*;
import static eu.uk.ncl.pet5o.esper.epl.core.resultset.codegen.ResultSetProcessorCodegenNames.NAME_VIEWABLE;
import static eu.uk.ncl.pet5o.esper.epl.core.resultset.codegen.ResultSetProcessorCodegenNames.REF_AGENTINSTANCECONTEXT;
import static eu.uk.ncl.pet5o.esper.epl.core.resultset.codegen.ResultSetProcessorCodegenNames.REF_AGGREGATIONSVC;
import static eu.uk.ncl.pet5o.esper.epl.core.resultset.codegen.ResultSetProcessorCodegenNames.REF_ISSYNTHESIZE;
import static eu.uk.ncl.pet5o.esper.epl.core.resultset.codegen.ResultSetProcessorCodegenNames.REF_JOINEVENTSSET;
import static eu.uk.ncl.pet5o.esper.epl.core.resultset.codegen.ResultSetProcessorCodegenNames.REF_JOINSET;
import static eu.uk.ncl.pet5o.esper.epl.core.resultset.codegen.ResultSetProcessorCodegenNames.REF_NEWDATA;
import static eu.uk.ncl.pet5o.esper.epl.core.resultset.codegen.ResultSetProcessorCodegenNames.REF_OLDDATA;
import static eu.uk.ncl.pet5o.esper.epl.core.resultset.codegen.ResultSetProcessorCodegenNames.REF_ORDERBYPROCESSOR;
import static eu.uk.ncl.pet5o.esper.epl.core.resultset.codegen.ResultSetProcessorCodegenNames.REF_RESULTSETVISITOR;
import static eu.uk.ncl.pet5o.esper.epl.core.resultset.codegen.ResultSetProcessorCodegenNames.REF_SELECTEXPRPROCESSOR;
import static eu.uk.ncl.pet5o.esper.epl.core.resultset.codegen.ResultSetProcessorCodegenNames.REF_VIEWABLE;
import static eu.uk.ncl.pet5o.esper.epl.core.resultset.codegen.ResultSetProcessorCodegenNames.REF_VIEWEVENTSLIST;
import static eu.uk.ncl.pet5o.esper.epl.core.resultset.core.ResultSetProcessorUtil.*;
import static eu.uk.ncl.pet5o.esper.epl.core.resultset.core.ResultSetProcessorUtil.METHOD_APPLYAGGJOINRESULT;
import static eu.uk.ncl.pet5o.esper.epl.core.resultset.core.ResultSetProcessorUtil.METHOD_APPLYAGGVIEWRESULT;
import static eu.uk.ncl.pet5o.esper.epl.core.resultset.core.ResultSetProcessorUtil.METHOD_CLEARANDAGGREGATEUNGROUPED;
import static eu.uk.ncl.pet5o.esper.epl.core.resultset.core.ResultSetProcessorUtil.METHOD_GETSELECTEVENTSNOHAVING;
import static eu.uk.ncl.pet5o.esper.epl.core.resultset.core.ResultSetProcessorUtil.METHOD_GETSELECTJOINEVENTSNOHAVING;
import static eu.uk.ncl.pet5o.esper.epl.core.resultset.core.ResultSetProcessorUtil.METHOD_GETSELECTJOINEVENTSNOHAVINGWITHORDERBY;
import static eu.uk.ncl.pet5o.esper.epl.core.resultset.core.ResultSetProcessorUtil.METHOD_ITERATORTODEQUE;
import static eu.uk.ncl.pet5o.esper.epl.core.resultset.core.ResultSetProcessorUtil.METHOD_ORDEROUTGOINGGETITERATOR;
import static eu.uk.ncl.pet5o.esper.epl.core.resultset.core.ResultSetProcessorUtil.METHOD_POPULATESELECTEVENTSNOHAVING;
import static eu.uk.ncl.pet5o.esper.epl.core.resultset.core.ResultSetProcessorUtil.METHOD_POPULATESELECTEVENTSNOHAVINGWITHORDERBY;
import static eu.uk.ncl.pet5o.esper.epl.core.resultset.core.ResultSetProcessorUtil.METHOD_POPULATESELECTJOINEVENTSNOHAVING;
import static eu.uk.ncl.pet5o.esper.epl.core.resultset.core.ResultSetProcessorUtil.METHOD_POPULATESELECTJOINEVENTSNOHAVINGWITHORDERBY;
import static eu.uk.ncl.pet5o.esper.util.CollectionUtil.METHOD_TOARRAYMAYNULL;

/**
 * Result set processor for the case: aggregation functions used in the select clause, and no group-by,
 * and not all of the properties in the select clause are under an aggregation function.
 * <p>
 * This processor does not perform grouping, every event entering and leaving is in the same group.
 * The processor generates one row for each event entering (new event) and one row for each event leaving (old event).
 * Aggregation state is simply one row holding all the state.
 */
public class ResultSetProcessorRowPerEventImpl implements ResultSetProcessorRowPerEvent {
    private final static String NAME_OUTPUTALLUNORDHELPER = "outputAllUnordHelper";
    private final static String NAME_OUTPUTLASTUNORDHELPER = "outputLastUnordHelper";

    private final ResultSetProcessorRowPerEventFactory prototype;
    private final SelectExprProcessor selectExprProcessor;
    private final OrderByProcessor orderByProcessor;
    private final AggregationService aggregationService;
    private ExprEvaluatorContext exprEvaluatorContext;
    private ResultSetProcessorRowPerEventOutputLastHelper outputLastUnordHelper;
    private ResultSetProcessorRowPerEventOutputAllHelper outputAllUnordHelper;

    ResultSetProcessorRowPerEventImpl(ResultSetProcessorRowPerEventFactory prototype, SelectExprProcessor selectExprProcessor, OrderByProcessor orderByProcessor, AggregationService aggregationService, AgentInstanceContext agentInstanceContext) {
        this.prototype = prototype;
        this.selectExprProcessor = selectExprProcessor;
        this.orderByProcessor = orderByProcessor;
        this.aggregationService = aggregationService;
        this.exprEvaluatorContext = agentInstanceContext;
        this.outputLastUnordHelper = prototype.getOutputConditionType() == ResultSetProcessorOutputConditionType.POLICY_LASTALL_UNORDERED && prototype.isOutputLast() ? prototype.getResultSetProcessorHelperFactory().makeRSRowPerEventOutputLast(this, agentInstanceContext) : null;
        this.outputAllUnordHelper = prototype.getOutputConditionType() == ResultSetProcessorOutputConditionType.POLICY_LASTALL_UNORDERED && prototype.isOutputAll() ? prototype.getResultSetProcessorHelperFactory().makeRSRowPerEventOutputAll(this, agentInstanceContext) : null;
    }

    public void setAgentInstanceContext(AgentInstanceContext context) {
        this.exprEvaluatorContext = context;
    }

    public EventType getResultEventType() {
        return prototype.getResultEventType();
    }

    public void applyViewResult(eu.uk.ncl.pet5o.esper.client.EventBean[] newData, eu.uk.ncl.pet5o.esper.client.EventBean[] oldData) {
        eu.uk.ncl.pet5o.esper.client.EventBean[] eventsPerStream = new eu.uk.ncl.pet5o.esper.client.EventBean[1];
        ResultSetProcessorUtil.applyAggViewResult(aggregationService, exprEvaluatorContext, newData, oldData, eventsPerStream);
    }

    public static void applyViewResultCodegen(CodegenMethodNode method) {
        method.getBlock().declareVar(eu.uk.ncl.pet5o.esper.client.EventBean[].class, "eventsPerStream", newArrayByLength(eu.uk.ncl.pet5o.esper.client.EventBean.class, constant(1)))
                .staticMethod(ResultSetProcessorUtil.class, METHOD_APPLYAGGVIEWRESULT, REF_AGGREGATIONSVC, REF_AGENTINSTANCECONTEXT, REF_NEWDATA, REF_OLDDATA, ref("eventsPerStream"));
    }

    public void applyJoinResult(Set<MultiKey<EventBean>> newEvents, Set<MultiKey<EventBean>> oldEvents) {
        ResultSetProcessorUtil.applyAggJoinResult(aggregationService, exprEvaluatorContext, newEvents, oldEvents);
    }

    public static void applyJoinResultCodegen(CodegenMethodNode method) {
        method.getBlock().staticMethod(ResultSetProcessorUtil.class, METHOD_APPLYAGGJOINRESULT, REF_AGGREGATIONSVC, REF_AGENTINSTANCECONTEXT, REF_NEWDATA, REF_OLDDATA);
    }

    public UniformPair<eu.uk.ncl.pet5o.esper.client.EventBean[]> processJoinResult(Set<MultiKey<EventBean>> newEvents, Set<MultiKey<EventBean>> oldEvents, boolean isSynthesize) {
        if (InstrumentationHelper.ENABLED) {
            InstrumentationHelper.get().qResultSetProcessUngroupedNonfullyAgg();
        }
        eu.uk.ncl.pet5o.esper.client.EventBean[] selectOldEvents = null;
        eu.uk.ncl.pet5o.esper.client.EventBean[] selectNewEvents;

        if (prototype.isUnidirectional()) {
            this.clear();
        }

        ResultSetProcessorUtil.applyAggJoinResult(aggregationService, exprEvaluatorContext, newEvents, oldEvents);

        if (prototype.getOptionalHavingNode() == null) {
            if (prototype.isSelectRStream()) {
                if (orderByProcessor == null) {
                    selectOldEvents = ResultSetProcessorUtil.getSelectJoinEventsNoHaving(selectExprProcessor, oldEvents, false, isSynthesize, exprEvaluatorContext);
                } else {
                    selectOldEvents = ResultSetProcessorUtil.getSelectJoinEventsNoHavingWithOrderBy(aggregationService, selectExprProcessor, orderByProcessor, oldEvents, false, isSynthesize, exprEvaluatorContext);
                }
            }

            if (orderByProcessor == null) {
                selectNewEvents = ResultSetProcessorUtil.getSelectJoinEventsNoHaving(selectExprProcessor, newEvents, true, isSynthesize, exprEvaluatorContext);
            } else {
                selectNewEvents = ResultSetProcessorUtil.getSelectJoinEventsNoHavingWithOrderBy(aggregationService, selectExprProcessor, orderByProcessor, newEvents, true, isSynthesize, exprEvaluatorContext);
            }
        } else {
            if (prototype.isSelectRStream()) {
                if (orderByProcessor == null) {
                    selectOldEvents = ResultSetProcessorUtil.getSelectJoinEventsHaving(selectExprProcessor, oldEvents, prototype.getOptionalHavingNode(), false, isSynthesize, exprEvaluatorContext);
                } else {
                    selectOldEvents = ResultSetProcessorUtil.getSelectJoinEventsHavingWithOrderBy(aggregationService, selectExprProcessor, orderByProcessor, oldEvents, prototype.getOptionalHavingNode(), false, isSynthesize, exprEvaluatorContext);
                }
            }

            if (orderByProcessor == null) {
                selectNewEvents = ResultSetProcessorUtil.getSelectJoinEventsHaving(selectExprProcessor, newEvents, prototype.getOptionalHavingNode(), true, isSynthesize, exprEvaluatorContext);
            } else {
                selectNewEvents = ResultSetProcessorUtil.getSelectJoinEventsHavingWithOrderBy(aggregationService, selectExprProcessor, orderByProcessor, newEvents, prototype.getOptionalHavingNode(), true, isSynthesize, exprEvaluatorContext);
            }
        }

        if ((selectNewEvents == null) && (selectOldEvents == null)) {
            if (InstrumentationHelper.ENABLED) {
                InstrumentationHelper.get().aResultSetProcessUngroupedNonfullyAgg(null, null);
            }
            return null;
        }
        if (InstrumentationHelper.ENABLED) {
            InstrumentationHelper.get().aResultSetProcessUngroupedNonfullyAgg(selectNewEvents, selectOldEvents);
        }
        return new UniformPair<>(selectNewEvents, selectOldEvents);
    }

    public static void processJoinResultCodegen(ResultSetProcessorRowPerEventForge forge, CodegenClassScope classScope, CodegenMethodNode method, CodegenInstanceAux instance) {
        method.getBlock().declareVar(eu.uk.ncl.pet5o.esper.client.EventBean[].class, "selectOldEvents", constantNull())
                .declareVarNoInit(eu.uk.ncl.pet5o.esper.client.EventBean[].class, "selectNewEvents");

        if (forge.isUnidirectional()) {
            method.getBlock().exprDotMethod(ref("this"), "clear");
        }
        method.getBlock().staticMethod(ResultSetProcessorUtil.class, METHOD_APPLYAGGJOINRESULT, REF_AGGREGATIONSVC, REF_AGENTINSTANCECONTEXT, REF_NEWDATA, REF_OLDDATA);

        ResultSetProcessorUtil.processJoinResultCodegen(method, classScope, instance, forge.getOptionalHavingNode() != null, forge.isSelectRStream(), forge.isSorting(), true);
    }

    public UniformPair<eu.uk.ncl.pet5o.esper.client.EventBean[]> processViewResult(eu.uk.ncl.pet5o.esper.client.EventBean[] newData, eu.uk.ncl.pet5o.esper.client.EventBean[] oldData, boolean isSynthesize) {
        if (InstrumentationHelper.ENABLED) {
            InstrumentationHelper.get().qResultSetProcessUngroupedNonfullyAgg();
        }
        eu.uk.ncl.pet5o.esper.client.EventBean[] selectOldEvents = null;
        eu.uk.ncl.pet5o.esper.client.EventBean[] selectNewEvents;

        eu.uk.ncl.pet5o.esper.client.EventBean[] eventsPerStream = new eu.uk.ncl.pet5o.esper.client.EventBean[1];
        ResultSetProcessorUtil.applyAggViewResult(aggregationService, exprEvaluatorContext, newData, oldData, eventsPerStream);

        // generate new events using select expressions
        if (prototype.getOptionalHavingNode() == null) {
            if (prototype.isSelectRStream()) {
                if (orderByProcessor == null) {
                    selectOldEvents = ResultSetProcessorUtil.getSelectEventsNoHaving(selectExprProcessor, oldData, false, isSynthesize, exprEvaluatorContext);
                } else {
                    selectOldEvents = ResultSetProcessorUtil.getSelectEventsNoHavingWithOrderBy(aggregationService, selectExprProcessor, orderByProcessor, oldData, false, isSynthesize, exprEvaluatorContext);
                }
            }

            if (orderByProcessor == null) {
                selectNewEvents = ResultSetProcessorUtil.getSelectEventsNoHaving(selectExprProcessor, newData, true, isSynthesize, exprEvaluatorContext);
            } else {
                selectNewEvents = ResultSetProcessorUtil.getSelectEventsNoHavingWithOrderBy(aggregationService, selectExprProcessor, orderByProcessor, newData, true, isSynthesize, exprEvaluatorContext);
            }
        } else {
            if (prototype.isSelectRStream()) {
                if (orderByProcessor == null) {
                    selectOldEvents = ResultSetProcessorUtil.getSelectEventsHaving(selectExprProcessor, oldData, prototype.getOptionalHavingNode(), false, isSynthesize, exprEvaluatorContext);
                } else {
                    selectOldEvents = ResultSetProcessorUtil.getSelectEventsHavingWithOrderBy(aggregationService, selectExprProcessor, orderByProcessor, oldData, prototype.getOptionalHavingNode(), false, isSynthesize, exprEvaluatorContext);
                }
            }

            if (orderByProcessor == null) {
                selectNewEvents = ResultSetProcessorUtil.getSelectEventsHaving(selectExprProcessor, newData, prototype.getOptionalHavingNode(), true, isSynthesize, exprEvaluatorContext);
            } else {
                selectNewEvents = ResultSetProcessorUtil.getSelectEventsHavingWithOrderBy(aggregationService, selectExprProcessor, orderByProcessor, newData, prototype.getOptionalHavingNode(), true, isSynthesize, exprEvaluatorContext);
            }
        }

        if ((selectNewEvents == null) && (selectOldEvents == null)) {
            if (InstrumentationHelper.ENABLED) {
                InstrumentationHelper.get().aResultSetProcessUngroupedNonfullyAgg(null, null);
            }
            return null;
        }

        if (InstrumentationHelper.ENABLED) {
            InstrumentationHelper.get().aResultSetProcessUngroupedNonfullyAgg(selectNewEvents, selectOldEvents);
        }
        return new UniformPair<>(selectNewEvents, selectOldEvents);
    }

    public static void processViewResultCodegen(ResultSetProcessorRowPerEventForge forge, CodegenClassScope classScope, CodegenMethodNode method, CodegenInstanceAux instance) {
        method.getBlock().declareVar(eu.uk.ncl.pet5o.esper.client.EventBean[].class, "selectOldEvents", constantNull())
                .declareVarNoInit(eu.uk.ncl.pet5o.esper.client.EventBean[].class, "selectNewEvents")
                .declareVar(eu.uk.ncl.pet5o.esper.client.EventBean[].class, "eventsPerStream", newArrayByLength(eu.uk.ncl.pet5o.esper.client.EventBean.class, constant(1)))
                .staticMethod(ResultSetProcessorUtil.class, METHOD_APPLYAGGVIEWRESULT, REF_AGGREGATIONSVC, REF_AGENTINSTANCECONTEXT, REF_NEWDATA, REF_OLDDATA, ref("eventsPerStream"));

        ResultSetProcessorUtil.processViewResultCodegen(method, classScope, instance, forge.getOptionalHavingNode() != null, forge.isSelectRStream(), forge.isSorting(), true);
    }

    public Iterator<EventBean> getIterator(Viewable parent) {
        if (!prototype.isHistoricalOnly()) {
            return obtainIterator(parent);
        }

        ResultSetProcessorUtil.clearAndAggregateUngrouped(exprEvaluatorContext, aggregationService, parent);
        ArrayDeque<EventBean> deque = ResultSetProcessorUtil.iteratorToDeque(obtainIterator(parent));
        aggregationService.clearResults(exprEvaluatorContext);
        return deque.iterator();
    }

    public static void getIteratorViewCodegen(ResultSetProcessorRowPerEventForge forge, CodegenClassScope classScope, CodegenMethodNode method) {
        if (!forge.isHistoricalOnly()) {
            method.getBlock().methodReturn(localMethod(obtainIteratorCodegen(forge, classScope, method), REF_VIEWABLE));
            return;
        }

        method.getBlock()
                .staticMethod(ResultSetProcessorUtil.class, METHOD_CLEARANDAGGREGATEUNGROUPED, REF_AGENTINSTANCECONTEXT, REF_AGGREGATIONSVC, REF_VIEWABLE)
                .declareVar(Iterator.class, "iterator", localMethod(obtainIteratorCodegen(forge, classScope, method), REF_VIEWABLE))
                .declareVar(ArrayDeque.class, "deque", staticMethod(ResultSetProcessorUtil.class, METHOD_ITERATORTODEQUE, ref("iterator")))
                .exprDotMethod(REF_AGGREGATIONSVC, "clearResults", REF_AGENTINSTANCECONTEXT)
                .methodReturn(exprDotMethod(ref("deque"), "iterator"));
    }

    private Iterator<EventBean> obtainIterator(Viewable parent) {

        if (orderByProcessor == null) {
            return new ResultSetProcessorRowPerEventIterator(parent.iterator(), this, exprEvaluatorContext);
        }

        // Pull all parent events, generate order keys
        eu.uk.ncl.pet5o.esper.client.EventBean[] eventsPerStream = new eu.uk.ncl.pet5o.esper.client.EventBean[1];
        List<EventBean> outgoingEvents = new ArrayList<>();
        List<Object> orderKeys = new ArrayList<>();

        for (eu.uk.ncl.pet5o.esper.client.EventBean candidate : parent) {
            eventsPerStream[0] = candidate;

            Boolean pass = true;
            if (prototype.getOptionalHavingNode() != null) {
                pass = (Boolean) prototype.getOptionalHavingNode().evaluate(eventsPerStream, true, exprEvaluatorContext);
            }
            if ((pass == null) || (!pass)) {
                continue;
            }

            outgoingEvents.add(selectExprProcessor.process(eventsPerStream, true, true, exprEvaluatorContext));
            orderKeys.add(orderByProcessor.getSortKey(eventsPerStream, true, exprEvaluatorContext));
        }

        return ResultSetProcessorUtil.orderOutgoingGetIterator(outgoingEvents, orderKeys, orderByProcessor, exprEvaluatorContext);
    }

    private static CodegenMethodNode obtainIteratorCodegen(ResultSetProcessorRowPerEventForge forge, CodegenClassScope classScope, CodegenMethodNode parent) {
        CodegenMethodNode iterator = parent.makeChild(Iterator.class, ResultSetProcessorRowPerEventImpl.class, classScope).addParam(Viewable.class, NAME_VIEWABLE);
        if (!forge.isSorting()) {
            iterator.getBlock().methodReturn(newInstance(ResultSetProcessorRowPerEventIterator.class, exprDotMethod(REF_VIEWABLE, "iterator"), ref("this"), REF_AGENTINSTANCECONTEXT));
            return iterator;
        }

        iterator.getBlock().declareVar(eu.uk.ncl.pet5o.esper.client.EventBean[].class, "eventsPerStream", newArrayByLength(eu.uk.ncl.pet5o.esper.client.EventBean.class, constant(1)))
                .declareVar(List.class, "outgoingEvents", newInstance(ArrayList.class))
                .declareVar(List.class, "orderKeys", newInstance(ArrayList.class));

        {
            CodegenBlock forEach = iterator.getBlock().forEach(eu.uk.ncl.pet5o.esper.client.EventBean.class, "candidate", REF_VIEWABLE);
            forEach.assignArrayElement("eventsPerStream", constant(0), ref("candidate"));
            if (forge.getOptionalHavingNode() != null) {
                forEach.ifCondition(not(exprDotMethod(ref("this"), "evaluateHavingClause", ref("eventsPerStream"), constant(true), REF_AGENTINSTANCECONTEXT))).blockContinue();
            }
            forEach.exprDotMethod(ref("outgoingEvents"), "add", exprDotMethod(REF_SELECTEXPRPROCESSOR, "process", ref("eventsPerStream"), constantTrue(), constantTrue(), REF_AGENTINSTANCECONTEXT))
                    .exprDotMethod(ref("orderKeys"), "add", exprDotMethod(REF_ORDERBYPROCESSOR, "getSortKey", ref("eventsPerStream"), constantTrue(), REF_AGENTINSTANCECONTEXT));
        }

        iterator.getBlock().methodReturn(staticMethod(ResultSetProcessorUtil.class, METHOD_ORDEROUTGOINGGETITERATOR, ref("outgoingEvents"), ref("orderKeys"), REF_ORDERBYPROCESSOR, REF_AGENTINSTANCECONTEXT));
        return iterator;
    }

    /**
     * Returns the select expression processor
     *
     * @return select processor.
     */
    public SelectExprProcessor getSelectExprProcessor() {
        return selectExprProcessor;
    }

    /**
     * Returns the optional having expression.
     *
     * @return having expression node
     */
    public ExprEvaluator getOptionalHavingNode() {
        return prototype.getOptionalHavingNode();
    }

    public Iterator<EventBean> getIterator(Set<MultiKey<EventBean>> joinSet) {
        eu.uk.ncl.pet5o.esper.client.EventBean[] result;
        if (prototype.getOptionalHavingNode() == null) {
            if (orderByProcessor == null) {
                result = ResultSetProcessorUtil.getSelectJoinEventsNoHaving(selectExprProcessor, joinSet, true, true, exprEvaluatorContext);
            } else {
                result = ResultSetProcessorUtil.getSelectJoinEventsNoHavingWithOrderBy(aggregationService, selectExprProcessor, orderByProcessor, joinSet, true, true, exprEvaluatorContext);
            }
        } else {
            if (orderByProcessor == null) {
                result = ResultSetProcessorUtil.getSelectJoinEventsHaving(selectExprProcessor, joinSet, prototype.getOptionalHavingNode(), true, true, exprEvaluatorContext);
            } else {
                result = ResultSetProcessorUtil.getSelectJoinEventsHavingWithOrderBy(aggregationService, selectExprProcessor, orderByProcessor, joinSet, prototype.getOptionalHavingNode(), true, true, exprEvaluatorContext);
            }
        }
        return new ArrayEventIterator(result);
    }

    public static void getIteratorJoinCodegen(ResultSetProcessorRowPerEventForge forge, CodegenClassScope classScope, CodegenMethodNode method, CodegenInstanceAux instance) {
        if (forge.getOptionalHavingNode() == null) {
            if (!forge.isSorting()) {
                method.getBlock().declareVar(eu.uk.ncl.pet5o.esper.client.EventBean[].class, "result", staticMethod(ResultSetProcessorUtil.class, METHOD_GETSELECTJOINEVENTSNOHAVING, REF_SELECTEXPRPROCESSOR, REF_JOINSET, constantTrue(), constantTrue(), REF_AGENTINSTANCECONTEXT));
            } else {
                method.getBlock().declareVar(eu.uk.ncl.pet5o.esper.client.EventBean[].class, "result", staticMethod(ResultSetProcessorUtil.class, METHOD_GETSELECTJOINEVENTSNOHAVINGWITHORDERBY, REF_AGGREGATIONSVC, REF_SELECTEXPRPROCESSOR, REF_ORDERBYPROCESSOR, REF_JOINSET, constantTrue(), constantTrue(), REF_AGENTINSTANCECONTEXT));
            }
        } else {
            if (!forge.isSorting()) {
                CodegenMethodNode select = ResultSetProcessorUtil.getSelectJoinEventsHavingCodegen(classScope, instance);
                method.getBlock().declareVar(eu.uk.ncl.pet5o.esper.client.EventBean[].class, "result", localMethod(select, REF_SELECTEXPRPROCESSOR, REF_JOINSET, constantTrue(), constantTrue(), REF_AGENTINSTANCECONTEXT));
            } else {
                CodegenMethodNode select = ResultSetProcessorUtil.getSelectJoinEventsHavingWithOrderByCodegen(classScope, instance);
                method.getBlock().declareVar(eu.uk.ncl.pet5o.esper.client.EventBean[].class, "result", localMethod(select, REF_AGGREGATIONSVC, REF_SELECTEXPRPROCESSOR, REF_ORDERBYPROCESSOR, REF_JOINSET, constantTrue(), constantTrue(), REF_AGENTINSTANCECONTEXT));
            }
        }
        method.getBlock().methodReturn(newInstance(ArrayEventIterator.class, ref("result")));
    }

    public void clear() {
        aggregationService.clearResults(exprEvaluatorContext);
    }

    public static void clearMethodCodegen(CodegenMethodNode method) {
        method.getBlock().exprDotMethod(REF_AGGREGATIONSVC, "clearResults", REF_AGENTINSTANCECONTEXT);
    }

    public UniformPair<eu.uk.ncl.pet5o.esper.client.EventBean[]> processOutputLimitedJoin(List<UniformPair<Set<MultiKey<EventBean>>>> joinEventsSet, boolean generateSynthetic) {
        if (prototype.isOutputLast()) {
            return processOutputLimitedJoinLast(joinEventsSet, generateSynthetic);
        } else {
            return processOutputLimitedJoinDefault(joinEventsSet, generateSynthetic);
        }
    }

    public static void processOutputLimitedJoinCodegen(ResultSetProcessorRowPerEventForge forge, CodegenClassScope classScope, CodegenMethodNode method, CodegenInstanceAux instance) {
        if (forge.isOutputLast()) {
            processOutputLimitedJoinLastCodegen(forge, classScope, method, instance);
        } else {
            processOutputLimitedJoinDefaultCodegen(forge, classScope, method, instance);
        }
    }

    public UniformPair<eu.uk.ncl.pet5o.esper.client.EventBean[]> processOutputLimitedView(List<UniformPair<EventBean[]>> viewEventsList, boolean generateSynthetic) {
        if (prototype.isOutputLast()) {
            return processOutputLimitedViewLast(viewEventsList, generateSynthetic);
        } else {
            return processOutputLimitedViewDefault(viewEventsList, generateSynthetic);
        }
    }

    public static void processOutputLimitedViewCodegen(ResultSetProcessorRowPerEventForge forge, CodegenClassScope classScope, CodegenMethodNode method, CodegenInstanceAux instance) {
        if (forge.isOutputLast()) {
            processOutputLimitedViewLastCodegen(forge, classScope, method, instance);
        } else {
            processOutputLimitedViewDefaultCodegen(forge, classScope, method, instance);
        }
    }

    public void processOutputLimitedLastAllNonBufferedView(eu.uk.ncl.pet5o.esper.client.EventBean[] newData, eu.uk.ncl.pet5o.esper.client.EventBean[] oldData, boolean isGenerateSynthetic) {
        if (prototype.isOutputAll()) {
            outputAllUnordHelper.processView(newData, oldData, isGenerateSynthetic);
        } else {
            outputLastUnordHelper.processView(newData, oldData, isGenerateSynthetic);
        }
    }

    public static void processOutputLimitedLastAllNonBufferedViewCodegen(ResultSetProcessorRowPerEventForge forge, CodegenClassScope classScope, CodegenMethodNode method, CodegenInstanceAux instance) {
        processOutputLimitedLastAllNonBufferedCodegen(forge, "processView", classScope, method, instance);
    }

    public void processOutputLimitedLastAllNonBufferedJoin(Set<MultiKey<EventBean>> newEvents, Set<MultiKey<EventBean>> oldEvents, boolean isGenerateSynthetic) {
        if (prototype.isOutputAll()) {
            outputAllUnordHelper.processJoin(newEvents, oldEvents, isGenerateSynthetic);
        } else {
            outputLastUnordHelper.processJoin(newEvents, oldEvents, isGenerateSynthetic);
        }
    }

    public static void processOutputLimitedLastAllNonBufferedJoinCodegen(ResultSetProcessorRowPerEventForge forge, CodegenClassScope classScope, CodegenMethodNode method, CodegenInstanceAux instance) {
        processOutputLimitedLastAllNonBufferedCodegen(forge, "processJoin", classScope, method, instance);
    }

    private static void processOutputLimitedLastAllNonBufferedCodegen(ResultSetProcessorRowPerEventForge forge, String methodName, CodegenClassScope classScope, CodegenMethodNode method, CodegenInstanceAux instance) {
        CodegenMember factory = classScope.makeAddMember(ResultSetProcessorHelperFactory.class, forge.getResultSetProcessorHelperFactory());

        if (forge.isOutputAll()) {
            instance.addMember(NAME_OUTPUTALLUNORDHELPER, ResultSetProcessorRowPerEventOutputAllHelper.class);
            instance.getServiceCtor().getBlock().assignRef(NAME_OUTPUTALLUNORDHELPER, exprDotMethod(member(factory.getMemberId()), "makeRSRowPerEventOutputAll", ref("this"), REF_AGENTINSTANCECONTEXT));
            method.getBlock().exprDotMethod(ref(NAME_OUTPUTALLUNORDHELPER), methodName, REF_NEWDATA, REF_OLDDATA, REF_ISSYNTHESIZE);
        } else if (forge.isOutputLast()) {
            instance.addMember(NAME_OUTPUTLASTUNORDHELPER, ResultSetProcessorRowPerEventOutputLastHelper.class);
            instance.getServiceCtor().getBlock().assignRef(NAME_OUTPUTLASTUNORDHELPER, exprDotMethod(member(factory.getMemberId()), "makeRSRowPerEventOutputLast", ref("this"), REF_AGENTINSTANCECONTEXT));
            method.getBlock().exprDotMethod(ref(NAME_OUTPUTLASTUNORDHELPER), methodName, REF_NEWDATA, REF_OLDDATA, REF_ISSYNTHESIZE);
        }
    }

    public UniformPair<eu.uk.ncl.pet5o.esper.client.EventBean[]> continueOutputLimitedLastAllNonBufferedView(boolean isSynthesize) {
        if (prototype.isOutputAll()) {
            return outputAllUnordHelper.output();
        }
        return outputLastUnordHelper.output();
    }

    public static void continueOutputLimitedLastAllNonBufferedViewCodegen(ResultSetProcessorRowPerEventForge forge, CodegenMethodNode method) {
        if (forge.isOutputAll()) {
            method.getBlock().methodReturn(exprDotMethod(ref(NAME_OUTPUTALLUNORDHELPER), "output"));
        } else if (forge.isOutputLast()) {
            method.getBlock().methodReturn(exprDotMethod(ref(NAME_OUTPUTLASTUNORDHELPER), "output"));
        } else {
            method.getBlock().methodReturn(constantNull());
        }
    }

    public UniformPair<eu.uk.ncl.pet5o.esper.client.EventBean[]> continueOutputLimitedLastAllNonBufferedJoin(boolean isSynthesize) {
        if (prototype.isOutputAll()) {
            return outputAllUnordHelper.output();
        }
        return outputLastUnordHelper.output();
    }

    public static void continueOutputLimitedLastAllNonBufferedJoinCodegen(ResultSetProcessorRowPerEventForge forge, CodegenMethodNode method) {
        if (forge.isOutputAll()) {
            method.getBlock().methodReturn(exprDotMethod(ref(NAME_OUTPUTALLUNORDHELPER), "output"));
        } else if (forge.isOutputLast()) {
            method.getBlock().methodReturn(exprDotMethod(ref(NAME_OUTPUTLASTUNORDHELPER), "output"));
        } else {
            method.getBlock().methodReturn(constantNull());
        }
    }

    public void stop() {
        if (outputLastUnordHelper != null) {
            outputLastUnordHelper.destroy();
        }
        if (outputAllUnordHelper != null) {
            outputAllUnordHelper.destroy();
        }
    }

    static void stopCodegen(CodegenMethodNode method, CodegenInstanceAux instance) {
        if (instance.hasMember(NAME_OUTPUTLASTUNORDHELPER)) {
            method.getBlock().exprDotMethod(ref(NAME_OUTPUTLASTUNORDHELPER), "destroy");
        }
        if (instance.hasMember(NAME_OUTPUTALLUNORDHELPER)) {
            method.getBlock().exprDotMethod(ref(NAME_OUTPUTALLUNORDHELPER), "destroy");
        }
    }

    private UniformPair<eu.uk.ncl.pet5o.esper.client.EventBean[]> processOutputLimitedJoinDefault(List<UniformPair<Set<MultiKey<EventBean>>>> joinEventsSet, boolean generateSynthetic) {
        List<EventBean> newEvents = new LinkedList<>();
        List<EventBean> oldEvents = null;
        if (prototype.isSelectRStream()) {
            oldEvents = new LinkedList<>();
        }

        List<Object> newEventsSortKey = null;
        List<Object> oldEventsSortKey = null;
        if (orderByProcessor != null) {
            newEventsSortKey = new LinkedList<>();
            if (prototype.isSelectRStream()) {
                oldEventsSortKey = new LinkedList<>();
            }
        }

        for (UniformPair<Set<MultiKey<EventBean>>> pair : joinEventsSet) {
            Set<MultiKey<EventBean>> newData = pair.getFirst();
            Set<MultiKey<EventBean>> oldData = pair.getSecond();

            if (prototype.isUnidirectional()) {
                this.clear();
            }

            ResultSetProcessorUtil.applyAggJoinResult(aggregationService, exprEvaluatorContext, newData, oldData);

            // generate old events using select expressions
            if (prototype.isSelectRStream()) {
                if (prototype.getOptionalHavingNode() == null) {
                    if (orderByProcessor == null) {
                        ResultSetProcessorUtil.populateSelectJoinEventsNoHaving(selectExprProcessor, oldData, false, generateSynthetic, oldEvents, exprEvaluatorContext);
                    } else {
                        ResultSetProcessorUtil.populateSelectJoinEventsNoHavingWithOrderBy(selectExprProcessor, orderByProcessor, oldData, false, generateSynthetic, oldEvents, oldEventsSortKey, exprEvaluatorContext);
                    }
                } else {
                    // generate old events using having then select
                    if (orderByProcessor == null) {
                        ResultSetProcessorUtil.populateSelectJoinEventsHaving(selectExprProcessor, oldData, prototype.getOptionalHavingNode(), false, generateSynthetic, oldEvents, exprEvaluatorContext);
                    } else {
                        ResultSetProcessorUtil.populateSelectJoinEventsHavingWithOrderBy(selectExprProcessor, orderByProcessor, oldData, prototype.getOptionalHavingNode(), false, generateSynthetic, oldEvents, oldEventsSortKey, exprEvaluatorContext);
                    }
                }
            }

            // generate new events using select expressions
            if (prototype.getOptionalHavingNode() == null) {
                if (orderByProcessor == null) {
                    ResultSetProcessorUtil.populateSelectJoinEventsNoHaving(selectExprProcessor, newData, true, generateSynthetic, newEvents, exprEvaluatorContext);
                } else {
                    ResultSetProcessorUtil.populateSelectJoinEventsNoHavingWithOrderBy(selectExprProcessor, orderByProcessor, newData, true, generateSynthetic, newEvents, newEventsSortKey, exprEvaluatorContext);
                }
            } else {
                if (orderByProcessor == null) {
                    ResultSetProcessorUtil.populateSelectJoinEventsHaving(selectExprProcessor, newData, prototype.getOptionalHavingNode(), true, generateSynthetic, newEvents, exprEvaluatorContext);
                } else {
                    ResultSetProcessorUtil.populateSelectJoinEventsHavingWithOrderBy(selectExprProcessor, orderByProcessor, newData, prototype.getOptionalHavingNode(), true, generateSynthetic, newEvents, newEventsSortKey, exprEvaluatorContext);
                }
            }
        }

        return ResultSetProcessorUtil.finalizeOutputMaySortMayRStream(newEvents, newEventsSortKey, oldEvents, oldEventsSortKey, prototype.isSelectRStream(), orderByProcessor, exprEvaluatorContext);
    }

    private static void processOutputLimitedJoinDefaultCodegen(ResultSetProcessorRowPerEventForge forge, CodegenClassScope classScope, CodegenMethodNode method, CodegenInstanceAux instance) {
        ResultSetProcessorUtil.prefixCodegenNewOldEvents(method.getBlock(), forge.isSorting(), forge.isSelectRStream());

        {
            CodegenBlock forEach = method.getBlock().forEach(UniformPair.class, "pair", REF_JOINEVENTSSET);
            forEach.declareVar(Set.class, "newData", cast(Set.class, exprDotMethod(ref("pair"), "getFirst")))
                    .declareVar(Set.class, "oldData", cast(Set.class, exprDotMethod(ref("pair"), "getSecond")));
            if (forge.isUnidirectional()) {
                forEach.exprDotMethod(ref("this"), "clear");
            }
            forEach.staticMethod(ResultSetProcessorUtil.class, METHOD_APPLYAGGJOINRESULT, REF_AGGREGATIONSVC, REF_AGENTINSTANCECONTEXT, REF_NEWDATA, REF_OLDDATA);

            // generate old events using select expressions
            if (forge.isSelectRStream()) {
                if (forge.getOptionalHavingNode() == null) {
                    if (!forge.isSorting()) {
                        forEach.staticMethod(ResultSetProcessorUtil.class, METHOD_POPULATESELECTJOINEVENTSNOHAVING, REF_SELECTEXPRPROCESSOR, ref("oldData"), constantFalse(), REF_ISSYNTHESIZE, ref("oldEvents"), REF_AGENTINSTANCECONTEXT);
                    } else {
                        forEach.staticMethod(ResultSetProcessorUtil.class, METHOD_POPULATESELECTJOINEVENTSNOHAVINGWITHORDERBY, REF_SELECTEXPRPROCESSOR, REF_ORDERBYPROCESSOR, REF_OLDDATA, constantFalse(), REF_ISSYNTHESIZE, ref("oldEvents"), ref("oldEventsSortKey"), REF_AGENTINSTANCECONTEXT);
                    }
                } else {
                    // generate old events using having then select
                    if (!forge.isSorting()) {
                        CodegenMethodNode select = ResultSetProcessorUtil.populateSelectJoinEventsHavingCodegen(classScope, instance);
                        forEach.localMethod(select, REF_SELECTEXPRPROCESSOR, REF_OLDDATA, constantFalse(), REF_ISSYNTHESIZE, ref("oldEvents"), REF_AGENTINSTANCECONTEXT);
                    } else {
                        CodegenMethodNode select = ResultSetProcessorUtil.populateSelectJoinEventsHavingWithOrderByCodegen(classScope, instance);
                        forEach.localMethod(select, REF_SELECTEXPRPROCESSOR, REF_ORDERBYPROCESSOR, REF_OLDDATA, constantFalse(), REF_ISSYNTHESIZE, ref("oldEvents"), ref("oldEventsSortKey"), REF_AGENTINSTANCECONTEXT);
                    }
                }
            }

            // generate new events using select expressions
            if (forge.getOptionalHavingNode() == null) {
                if (!forge.isSorting()) {
                    forEach.staticMethod(ResultSetProcessorUtil.class, METHOD_POPULATESELECTJOINEVENTSNOHAVING, REF_SELECTEXPRPROCESSOR, ref("newData"), constantTrue(), REF_ISSYNTHESIZE, ref("newEvents"), REF_AGENTINSTANCECONTEXT);
                } else {
                    forEach.staticMethod(ResultSetProcessorUtil.class, METHOD_POPULATESELECTJOINEVENTSNOHAVINGWITHORDERBY, REF_SELECTEXPRPROCESSOR, REF_ORDERBYPROCESSOR, ref("newData"), constantTrue(), REF_ISSYNTHESIZE, ref("newEvents"), ref("newEventsSortKey"), REF_AGENTINSTANCECONTEXT);
                }
            } else {
                if (!forge.isSorting()) {
                    CodegenMethodNode select = ResultSetProcessorUtil.populateSelectJoinEventsHavingCodegen(classScope, instance);
                    forEach.localMethod(select, REF_SELECTEXPRPROCESSOR, REF_NEWDATA, constantTrue(), REF_ISSYNTHESIZE, ref("newEvents"), REF_AGENTINSTANCECONTEXT);
                } else {
                    CodegenMethodNode select = ResultSetProcessorUtil.populateSelectJoinEventsHavingWithOrderByCodegen(classScope, instance);
                    forEach.localMethod(select, REF_SELECTEXPRPROCESSOR, REF_ORDERBYPROCESSOR, ref("newData"), constantTrue(), REF_ISSYNTHESIZE, ref("newEvents"), ref("newEventsSortKey"), REF_AGENTINSTANCECONTEXT);
                }
            }
        }

        ResultSetProcessorUtil.finalizeOutputMaySortMayRStreamCodegen(method.getBlock(), ref("newEvents"), ref("newEventsSortKey"), ref("oldEvents"), ref("oldEventsSortKey"), forge.isSelectRStream(), forge.isSorting());
    }

    private UniformPair<eu.uk.ncl.pet5o.esper.client.EventBean[]> processOutputLimitedJoinLast(List<UniformPair<Set<MultiKey<EventBean>>>> joinEventsSet, boolean generateSynthetic) {
        eu.uk.ncl.pet5o.esper.client.EventBean lastOldEvent = null;
        eu.uk.ncl.pet5o.esper.client.EventBean lastNewEvent = null;

        for (UniformPair<Set<MultiKey<EventBean>>> pair : joinEventsSet) {
            Set<MultiKey<EventBean>> newData = pair.getFirst();
            Set<MultiKey<EventBean>> oldData = pair.getSecond();

            if (prototype.isUnidirectional()) {
                this.clear();
            }

            ResultSetProcessorUtil.applyAggJoinResult(aggregationService, exprEvaluatorContext, newData, oldData);

            eu.uk.ncl.pet5o.esper.client.EventBean[] selectOldEvents;
            if (prototype.isSelectRStream()) {
                if (prototype.getOptionalHavingNode() == null) {
                    selectOldEvents = ResultSetProcessorUtil.getSelectJoinEventsNoHaving(selectExprProcessor, oldData, false, generateSynthetic, exprEvaluatorContext);
                } else {
                    selectOldEvents = ResultSetProcessorUtil.getSelectJoinEventsHaving(selectExprProcessor, oldData, prototype.getOptionalHavingNode(), false, generateSynthetic, exprEvaluatorContext);
                }
                if ((selectOldEvents != null) && (selectOldEvents.length > 0)) {
                    lastOldEvent = selectOldEvents[selectOldEvents.length - 1];
                }
            }

            // generate new events using select expressions
            eu.uk.ncl.pet5o.esper.client.EventBean[] selectNewEvents;
            if (prototype.getOptionalHavingNode() == null) {
                selectNewEvents = ResultSetProcessorUtil.getSelectJoinEventsNoHaving(selectExprProcessor, newData, true, generateSynthetic, exprEvaluatorContext);
            } else {
                selectNewEvents = ResultSetProcessorUtil.getSelectJoinEventsHaving(selectExprProcessor, newData, prototype.getOptionalHavingNode(), true, generateSynthetic, exprEvaluatorContext);
            }
            if ((selectNewEvents != null) && (selectNewEvents.length > 0)) {
                lastNewEvent = selectNewEvents[selectNewEvents.length - 1];
            }
        }

        eu.uk.ncl.pet5o.esper.client.EventBean[] lastNew = CollectionUtil.toArrayMayNull(lastNewEvent);
        eu.uk.ncl.pet5o.esper.client.EventBean[] lastOld = CollectionUtil.toArrayMayNull(lastOldEvent);

        if ((lastNew == null) && (lastOld == null)) {
            return null;
        }
        return new UniformPair<>(lastNew, lastOld);
    }

    private static void processOutputLimitedJoinLastCodegen(ResultSetProcessorRowPerEventForge forge, CodegenClassScope classScope, CodegenMethodNode method, CodegenInstanceAux instance) {
        method.getBlock().declareVar(eu.uk.ncl.pet5o.esper.client.EventBean.class, "lastOldEvent", constantNull())
                .declareVar(eu.uk.ncl.pet5o.esper.client.EventBean.class, "lastNewEvent", constantNull());

        {
            CodegenBlock forEach = method.getBlock().forEach(UniformPair.class, "pair", REF_JOINEVENTSSET);
            forEach.declareVar(Set.class, "newData", cast(Set.class, exprDotMethod(ref("pair"), "getFirst")))
                    .declareVar(Set.class, "oldData", cast(Set.class, exprDotMethod(ref("pair"), "getSecond")));

            if (forge.isUnidirectional()) {
                forEach.exprDotMethod(ref("this"), "clear");
            }

            forEach.staticMethod(ResultSetProcessorUtil.class, METHOD_APPLYAGGJOINRESULT, REF_AGGREGATIONSVC, REF_AGENTINSTANCECONTEXT, ref("newData"), ref("oldData"));

            if (forge.isSelectRStream()) {
                if (forge.getOptionalHavingNode() == null) {
                    forEach.declareVar(eu.uk.ncl.pet5o.esper.client.EventBean[].class, "selectOldEvents", staticMethod(ResultSetProcessorUtil.class, METHOD_GETSELECTJOINEVENTSNOHAVING, REF_SELECTEXPRPROCESSOR, ref("oldData"), constantFalse(), REF_ISSYNTHESIZE, REF_AGENTINSTANCECONTEXT));
                } else {
                    CodegenMethodNode select = ResultSetProcessorUtil.getSelectJoinEventsHavingCodegen(classScope, instance);
                    forEach.declareVar(eu.uk.ncl.pet5o.esper.client.EventBean[].class, "selectOldEvents", localMethod(select, REF_SELECTEXPRPROCESSOR, ref("oldData"), constantFalse(), REF_ISSYNTHESIZE, REF_AGENTINSTANCECONTEXT));
                }
                forEach.ifCondition(and(notEqualsNull(ref("selectOldEvents")), relational(arrayLength(ref("selectOldEvents")), CodegenExpressionRelational.CodegenRelational.GT, constant(0))))
                        .assignRef("lastOldEvent", arrayAtIndex(ref("selectOldEvents"), op(arrayLength(ref("selectOldEvents")), "-", constant(1))))
                        .blockEnd();
            }

            // generate new events using select expressions
            if (forge.getOptionalHavingNode() == null) {
                forEach.declareVar(eu.uk.ncl.pet5o.esper.client.EventBean[].class, "selectNewEvents", staticMethod(ResultSetProcessorUtil.class, METHOD_GETSELECTJOINEVENTSNOHAVING, REF_SELECTEXPRPROCESSOR, ref("newData"), constantTrue(), REF_ISSYNTHESIZE, REF_AGENTINSTANCECONTEXT));
            } else {
                CodegenMethodNode select = ResultSetProcessorUtil.getSelectJoinEventsHavingCodegen(classScope, instance);
                forEach.declareVar(eu.uk.ncl.pet5o.esper.client.EventBean[].class, "selectNewEvents", localMethod(select, REF_SELECTEXPRPROCESSOR, ref("newData"), constantTrue(), REF_ISSYNTHESIZE, REF_AGENTINSTANCECONTEXT));
            }
            forEach.ifCondition(and(notEqualsNull(ref("selectNewEvents")), relational(arrayLength(ref("selectNewEvents")), CodegenExpressionRelational.CodegenRelational.GT, constant(0))))
                    .assignRef("lastNewEvent", arrayAtIndex(ref("selectNewEvents"), op(arrayLength(ref("selectNewEvents")), "-", constant(1))))
                    .blockEnd();
        }

        method.getBlock()
                .declareVar(eu.uk.ncl.pet5o.esper.client.EventBean[].class, "lastNew", staticMethod(CollectionUtil.class, METHOD_TOARRAYMAYNULL, ref("lastNewEvent")))
                .declareVar(eu.uk.ncl.pet5o.esper.client.EventBean[].class, "lastOld", staticMethod(CollectionUtil.class, METHOD_TOARRAYMAYNULL, ref("lastOldEvent")))
                .ifCondition(and(equalsNull(ref("lastNew")), equalsNull(ref("lastOld")))).blockReturn(constantNull())
                .methodReturn(newInstance(UniformPair.class, ref("lastNew"), ref("lastOld")));
    }

    private UniformPair<eu.uk.ncl.pet5o.esper.client.EventBean[]> processOutputLimitedViewDefault(List<UniformPair<EventBean[]>> viewEventsList, boolean generateSynthetic) {
        List<EventBean> newEvents = new LinkedList<>();
        List<EventBean> oldEvents = null;
        if (prototype.isSelectRStream()) {
            oldEvents = new LinkedList<>();
        }
        List<Object> newEventsSortKey = null;
        List<Object> oldEventsSortKey = null;
        if (orderByProcessor != null) {
            newEventsSortKey = new LinkedList<>();
            if (prototype.isSelectRStream()) {
                oldEventsSortKey = new LinkedList<>();
            }
        }

        eu.uk.ncl.pet5o.esper.client.EventBean[] eventsPerStream = new eu.uk.ncl.pet5o.esper.client.EventBean[1];
        for (UniformPair<eu.uk.ncl.pet5o.esper.client.EventBean[]> pair : viewEventsList) {
            eu.uk.ncl.pet5o.esper.client.EventBean[] newData = pair.getFirst();
            eu.uk.ncl.pet5o.esper.client.EventBean[] oldData = pair.getSecond();
            ResultSetProcessorUtil.applyAggViewResult(aggregationService, exprEvaluatorContext, newData, oldData, eventsPerStream);

            // generate old events using select expressions
            if (prototype.isSelectRStream()) {
                if (prototype.getOptionalHavingNode() == null) {
                    if (orderByProcessor == null) {
                        ResultSetProcessorUtil.populateSelectEventsNoHaving(selectExprProcessor, oldData, false, generateSynthetic, oldEvents, exprEvaluatorContext);
                    } else {
                        ResultSetProcessorUtil.populateSelectEventsNoHavingWithOrderBy(selectExprProcessor, orderByProcessor, oldData, false, generateSynthetic, oldEvents, oldEventsSortKey, exprEvaluatorContext);
                    }
                } else {
                    // generate old events using having then select
                    if (orderByProcessor == null) {
                        ResultSetProcessorUtil.populateSelectEventsHaving(selectExprProcessor, oldData, prototype.getOptionalHavingNode(), false, generateSynthetic, oldEvents, exprEvaluatorContext);
                    } else {
                        ResultSetProcessorUtil.populateSelectEventsHavingWithOrderBy(selectExprProcessor, orderByProcessor, oldData, prototype.getOptionalHavingNode(), false, generateSynthetic, oldEvents, oldEventsSortKey, exprEvaluatorContext);
                    }
                }
            }

            // generate new events using select expressions
            if (prototype.getOptionalHavingNode() == null) {
                if (orderByProcessor == null) {
                    ResultSetProcessorUtil.populateSelectEventsNoHaving(selectExprProcessor, newData, true, generateSynthetic, newEvents, exprEvaluatorContext);
                } else {
                    ResultSetProcessorUtil.populateSelectEventsNoHavingWithOrderBy(selectExprProcessor, orderByProcessor, newData, true, generateSynthetic, newEvents, newEventsSortKey, exprEvaluatorContext);
                }
            } else {
                if (orderByProcessor == null) {
                    ResultSetProcessorUtil.populateSelectEventsHaving(selectExprProcessor, newData, prototype.getOptionalHavingNode(), true, generateSynthetic, newEvents, exprEvaluatorContext);
                } else {
                    ResultSetProcessorUtil.populateSelectEventsHavingWithOrderBy(selectExprProcessor, orderByProcessor, newData, prototype.getOptionalHavingNode(), true, generateSynthetic, newEvents, newEventsSortKey, exprEvaluatorContext);
                }
            }
        }

        return ResultSetProcessorUtil.finalizeOutputMaySortMayRStream(newEvents, newEventsSortKey, oldEvents, oldEventsSortKey, prototype.isSelectRStream(), orderByProcessor, exprEvaluatorContext);
    }

    private static void processOutputLimitedViewDefaultCodegen(ResultSetProcessorRowPerEventForge forge, CodegenClassScope classScope, CodegenMethodNode method, CodegenInstanceAux instance) {
        ResultSetProcessorUtil.prefixCodegenNewOldEvents(method.getBlock(), forge.isSorting(), forge.isSelectRStream());

        {
            CodegenBlock forEach = method.getBlock().forEach(UniformPair.class, "pair", REF_VIEWEVENTSLIST);
            forEach.declareVar(eu.uk.ncl.pet5o.esper.client.EventBean[].class, "newData", cast(eu.uk.ncl.pet5o.esper.client.EventBean[].class, exprDotMethod(ref("pair"), "getFirst")))
                    .declareVar(eu.uk.ncl.pet5o.esper.client.EventBean[].class, "oldData", cast(eu.uk.ncl.pet5o.esper.client.EventBean[].class, exprDotMethod(ref("pair"), "getSecond")))
                    .declareVar(eu.uk.ncl.pet5o.esper.client.EventBean[].class, "eventsPerStream", newArrayByLength(eu.uk.ncl.pet5o.esper.client.EventBean.class, constant(1)))
                    .staticMethod(ResultSetProcessorUtil.class, METHOD_APPLYAGGVIEWRESULT, REF_AGGREGATIONSVC, REF_AGENTINSTANCECONTEXT, REF_NEWDATA, REF_OLDDATA, ref("eventsPerStream"));

            // generate old events using select expressions
            if (forge.isSelectRStream()) {
                if (forge.getOptionalHavingNode() == null) {
                    if (!forge.isSorting()) {
                        forEach.staticMethod(ResultSetProcessorUtil.class, METHOD_POPULATESELECTEVENTSNOHAVING, REF_SELECTEXPRPROCESSOR, ref("oldData"), constantFalse(), REF_ISSYNTHESIZE, ref("oldEvents"), REF_AGENTINSTANCECONTEXT);
                    } else {
                        forEach.staticMethod(ResultSetProcessorUtil.class, METHOD_POPULATESELECTEVENTSNOHAVINGWITHORDERBY, REF_SELECTEXPRPROCESSOR, REF_ORDERBYPROCESSOR, ref("oldData"), constantFalse(), REF_ISSYNTHESIZE, ref("oldEvents"), REF_AGENTINSTANCECONTEXT);
                    }
                } else {
                    // generate old events using having then select
                    if (!forge.isSorting()) {
                        CodegenMethodNode select = ResultSetProcessorUtil.populateSelectEventsHavingCodegen(classScope, instance);
                        forEach.localMethod(select, REF_SELECTEXPRPROCESSOR, REF_OLDDATA, constantFalse(), REF_ISSYNTHESIZE, ref("oldEvents"), REF_AGENTINSTANCECONTEXT);
                    } else {
                        CodegenMethodNode select = ResultSetProcessorUtil.populateSelectEventsHavingWithOrderByCodegen(classScope, instance);
                        forEach.localMethod(select, REF_SELECTEXPRPROCESSOR, REF_ORDERBYPROCESSOR, REF_OLDDATA, constantFalse(), REF_ISSYNTHESIZE, ref("oldEvents"), ref("oldEventsSortKey"), REF_AGENTINSTANCECONTEXT);
                        throw new UnsupportedOperationException();
                    }
                }
            }

            // generate new events using select expressions
            if (forge.getOptionalHavingNode() == null) {
                if (!forge.isSorting()) {
                    forEach.staticMethod(ResultSetProcessorUtil.class, METHOD_POPULATESELECTEVENTSNOHAVING, REF_SELECTEXPRPROCESSOR, ref("newData"), constantTrue(), REF_ISSYNTHESIZE, ref("newEvents"), REF_AGENTINSTANCECONTEXT);
                } else {
                    forEach.staticMethod(ResultSetProcessorUtil.class, METHOD_POPULATESELECTEVENTSNOHAVINGWITHORDERBY, REF_SELECTEXPRPROCESSOR, REF_ORDERBYPROCESSOR, ref("newData"), constantTrue(), REF_ISSYNTHESIZE, ref("newEvents"), ref("newEventsSortKey"), REF_AGENTINSTANCECONTEXT);
                }
            } else {
                if (!forge.isSorting()) {
                    CodegenMethodNode select = ResultSetProcessorUtil.populateSelectEventsHavingCodegen(classScope, instance);
                    forEach.localMethod(select, REF_SELECTEXPRPROCESSOR, REF_NEWDATA, constantTrue(), REF_ISSYNTHESIZE, ref("newEvents"), REF_AGENTINSTANCECONTEXT);
                } else {
                    CodegenMethodNode select = ResultSetProcessorUtil.populateSelectEventsHavingWithOrderByCodegen(classScope, instance);
                    forEach.localMethod(select, REF_SELECTEXPRPROCESSOR, REF_ORDERBYPROCESSOR, REF_NEWDATA, constantTrue(), REF_ISSYNTHESIZE, ref("newEvents"), ref("newEventsSortKey"), REF_AGENTINSTANCECONTEXT);
                }
            }
        }

        ResultSetProcessorUtil.finalizeOutputMaySortMayRStreamCodegen(method.getBlock(), ref("newEvents"), ref("newEventsSortKey"), ref("oldEvents"), ref("oldEventsSortKey"), forge.isSelectRStream(), forge.isSorting());
    }

    private UniformPair<eu.uk.ncl.pet5o.esper.client.EventBean[]> processOutputLimitedViewLast(List<UniformPair<EventBean[]>> viewEventsList, boolean generateSynthetic) {
        eu.uk.ncl.pet5o.esper.client.EventBean lastOldEvent = null;
        eu.uk.ncl.pet5o.esper.client.EventBean lastNewEvent = null;
        eu.uk.ncl.pet5o.esper.client.EventBean[] eventsPerStream = new eu.uk.ncl.pet5o.esper.client.EventBean[1];

        for (UniformPair<eu.uk.ncl.pet5o.esper.client.EventBean[]> pair : viewEventsList) {
            eu.uk.ncl.pet5o.esper.client.EventBean[] newData = pair.getFirst();
            eu.uk.ncl.pet5o.esper.client.EventBean[] oldData = pair.getSecond();
            ResultSetProcessorUtil.applyAggViewResult(aggregationService, exprEvaluatorContext, newData, oldData, eventsPerStream);

            eu.uk.ncl.pet5o.esper.client.EventBean[] selectOldEvents;
            if (prototype.isSelectRStream()) {
                if (prototype.getOptionalHavingNode() == null) {
                    selectOldEvents = ResultSetProcessorUtil.getSelectEventsNoHaving(selectExprProcessor, oldData, false, generateSynthetic, exprEvaluatorContext);
                } else {
                    selectOldEvents = ResultSetProcessorUtil.getSelectEventsHaving(selectExprProcessor, oldData, prototype.getOptionalHavingNode(), false, generateSynthetic, exprEvaluatorContext);
                }
                if ((selectOldEvents != null) && (selectOldEvents.length > 0)) {
                    lastOldEvent = selectOldEvents[selectOldEvents.length - 1];
                }
            }

            // generate new events using select expressions
            eu.uk.ncl.pet5o.esper.client.EventBean[] selectNewEvents;
            if (prototype.getOptionalHavingNode() == null) {
                selectNewEvents = ResultSetProcessorUtil.getSelectEventsNoHaving(selectExprProcessor, newData, true, generateSynthetic, exprEvaluatorContext);
            } else {
                selectNewEvents = ResultSetProcessorUtil.getSelectEventsHaving(selectExprProcessor, newData, prototype.getOptionalHavingNode(), true, generateSynthetic, exprEvaluatorContext);
            }
            if ((selectNewEvents != null) && (selectNewEvents.length > 0)) {
                lastNewEvent = selectNewEvents[selectNewEvents.length - 1];
            }
        }

        eu.uk.ncl.pet5o.esper.client.EventBean[] lastNew = CollectionUtil.toArrayMayNull(lastNewEvent);
        eu.uk.ncl.pet5o.esper.client.EventBean[] lastOld = CollectionUtil.toArrayMayNull(lastOldEvent);

        if ((lastNew == null) && (lastOld == null)) {
            return null;
        }
        return new UniformPair<>(lastNew, lastOld);
    }

    private static void processOutputLimitedViewLastCodegen(ResultSetProcessorRowPerEventForge forge, CodegenClassScope classScope, CodegenMethodNode method, CodegenInstanceAux instance) {
        method.getBlock().declareVar(eu.uk.ncl.pet5o.esper.client.EventBean.class, "lastOldEvent", constantNull())
                .declareVar(eu.uk.ncl.pet5o.esper.client.EventBean.class, "lastNewEvent", constantNull())
                .declareVar(eu.uk.ncl.pet5o.esper.client.EventBean[].class, "eventsPerStream", newArrayByLength(eu.uk.ncl.pet5o.esper.client.EventBean.class, constant(1)));

        {
            CodegenBlock forEach = method.getBlock().forEach(UniformPair.class, "pair", REF_VIEWEVENTSLIST);
            forEach.declareVar(eu.uk.ncl.pet5o.esper.client.EventBean[].class, "newData", cast(eu.uk.ncl.pet5o.esper.client.EventBean[].class, exprDotMethod(ref("pair"), "getFirst")))
                    .declareVar(eu.uk.ncl.pet5o.esper.client.EventBean[].class, "oldData", cast(eu.uk.ncl.pet5o.esper.client.EventBean[].class, exprDotMethod(ref("pair"), "getSecond")))
                    .staticMethod(ResultSetProcessorUtil.class, METHOD_APPLYAGGVIEWRESULT, REF_AGGREGATIONSVC, REF_AGENTINSTANCECONTEXT, ref("newData"), ref("oldData"), ref("eventsPerStream"));

            if (forge.isSelectRStream()) {
                if (forge.getOptionalHavingNode() == null) {
                    forEach.declareVar(eu.uk.ncl.pet5o.esper.client.EventBean[].class, "selectOldEvents", staticMethod(ResultSetProcessorUtil.class, METHOD_GETSELECTEVENTSNOHAVING, REF_SELECTEXPRPROCESSOR, ref("oldData"), constantFalse(), REF_ISSYNTHESIZE, REF_AGENTINSTANCECONTEXT));
                } else {
                    CodegenMethodNode select = ResultSetProcessorUtil.getSelectEventsHavingCodegen(classScope, instance);
                    forEach.declareVar(eu.uk.ncl.pet5o.esper.client.EventBean[].class, "selectOldEvents", localMethod(select, REF_SELECTEXPRPROCESSOR, ref("oldData"), constantFalse(), REF_ISSYNTHESIZE, REF_AGENTINSTANCECONTEXT));
                }
                forEach.ifCondition(and(notEqualsNull(ref("selectOldEvents")), relational(arrayLength(ref("selectOldEvents")), CodegenExpressionRelational.CodegenRelational.GT, constant(0))))
                        .assignRef("lastOldEvent", arrayAtIndex(ref("selectOldEvents"), op(arrayLength(ref("selectOldEvents")), "-", constant(1))))
                        .blockEnd();
            }

            // generate new events using select expressions
            if (forge.getOptionalHavingNode() == null) {
                forEach.declareVar(eu.uk.ncl.pet5o.esper.client.EventBean[].class, "selectNewEvents", staticMethod(ResultSetProcessorUtil.class, METHOD_GETSELECTEVENTSNOHAVING, REF_SELECTEXPRPROCESSOR, ref("newData"), constantTrue(), REF_ISSYNTHESIZE, REF_AGENTINSTANCECONTEXT));
            } else {
                CodegenMethodNode select = ResultSetProcessorUtil.getSelectEventsHavingCodegen(classScope, instance);
                forEach.declareVar(eu.uk.ncl.pet5o.esper.client.EventBean[].class, "selectNewEvents", localMethod(select, REF_SELECTEXPRPROCESSOR, ref("newData"), constantTrue(), REF_ISSYNTHESIZE, REF_AGENTINSTANCECONTEXT));
            }
            forEach.ifCondition(and(notEqualsNull(ref("selectNewEvents")), relational(arrayLength(ref("selectNewEvents")), CodegenExpressionRelational.CodegenRelational.GT, constant(0))))
                    .assignRef("lastNewEvent", arrayAtIndex(ref("selectNewEvents"), op(arrayLength(ref("selectNewEvents")), "-", constant(1))))
                    .blockEnd();
        }

        method.getBlock()
                .declareVar(eu.uk.ncl.pet5o.esper.client.EventBean[].class, "lastNew", staticMethod(CollectionUtil.class, METHOD_TOARRAYMAYNULL, ref("lastNewEvent")))
                .declareVar(eu.uk.ncl.pet5o.esper.client.EventBean[].class, "lastOld", staticMethod(CollectionUtil.class, METHOD_TOARRAYMAYNULL, ref("lastOldEvent")))
                .ifCondition(and(equalsNull(ref("lastNew")), equalsNull(ref("lastOld")))).blockReturn(constantNull())
                .methodReturn(newInstance(UniformPair.class, ref("lastNew"), ref("lastOld")));
    }

    public void acceptHelperVisitor(ResultSetProcessorOutputHelperVisitor visitor) {
        if (outputLastUnordHelper != null) {
            visitor.visit(outputLastUnordHelper);
        }
        if (outputAllUnordHelper != null) {
            visitor.visit(outputAllUnordHelper);
        }
    }

    public static void acceptHelperVisitorCodegen(CodegenMethodNode method, CodegenInstanceAux instance) {
        if (instance.hasMember(NAME_OUTPUTLASTUNORDHELPER)) {
            method.getBlock().exprDotMethod(REF_RESULTSETVISITOR, "visit", ref(NAME_OUTPUTLASTUNORDHELPER));
        }
        if (instance.hasMember(NAME_OUTPUTALLUNORDHELPER)) {
            method.getBlock().exprDotMethod(REF_RESULTSETVISITOR, "visit", ref(NAME_OUTPUTALLUNORDHELPER));
        }
    }

    public boolean hasHavingClause() {
        return prototype.getOptionalHavingNode() != null;
    }

    public boolean evaluateHavingClause(eu.uk.ncl.pet5o.esper.client.EventBean[] eventsPerStream, boolean isNewData, ExprEvaluatorContext exprEvaluatorContext) {
        return ResultSetProcessorUtil.evaluateHavingClause(prototype.getOptionalHavingNode(), eventsPerStream, isNewData, exprEvaluatorContext);
    }
}
