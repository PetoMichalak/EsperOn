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

import eu.uk.ncl.pet5o.esper.client.EventBean;
import eu.uk.ncl.pet5o.esper.codegen.base.CodegenBlock;
import eu.uk.ncl.pet5o.esper.codegen.base.CodegenClassScope;
import eu.uk.ncl.pet5o.esper.codegen.base.CodegenMethodNode;
import eu.uk.ncl.pet5o.esper.codegen.core.CodegenInstanceAux;
import eu.uk.ncl.pet5o.esper.codegen.core.CodegenNamedParam;
import eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionRef;
import eu.uk.ncl.pet5o.esper.collection.ArrayEventIterator;
import eu.uk.ncl.pet5o.esper.collection.MultiKey;
import eu.uk.ncl.pet5o.esper.collection.UniformPair;
import eu.uk.ncl.pet5o.esper.core.context.util.AgentInstanceContext;
import eu.uk.ncl.pet5o.esper.epl.agg.service.common.AggregationService;
import eu.uk.ncl.pet5o.esper.epl.core.orderby.OrderByProcessor;
import eu.uk.ncl.pet5o.esper.epl.core.select.SelectExprProcessor;
import eu.uk.ncl.pet5o.esper.epl.expression.codegen.CodegenLegoMethodExpression;
import eu.uk.ncl.pet5o.esper.epl.expression.core.ExprEvaluator;
import eu.uk.ncl.pet5o.esper.epl.expression.core.ExprEvaluatorContext;
import eu.uk.ncl.pet5o.esper.epl.expression.core.ExprForge;
import eu.uk.ncl.pet5o.esper.metrics.instrumentation.InstrumentationHelper;
import eu.uk.ncl.pet5o.esper.util.CollectionUtil;
import eu.uk.ncl.pet5o.esper.view.Viewable;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;

import static eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionBuilder.*;
import static eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionBuilder.and;
import static eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionBuilder.arrayLength;
import static eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionBuilder.cast;
import static eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionBuilder.constant;
import static eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionBuilder.constantFalse;
import static eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionBuilder.constantNull;
import static eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionBuilder.constantTrue;
import static eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionBuilder.equalsIdentity;
import static eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionBuilder.equalsNull;
import static eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionBuilder.exprDotMethod;
import static eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionBuilder.localMethod;
import static eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionBuilder.newArrayByLength;
import static eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionBuilder.newInstance;
import static eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionBuilder.not;
import static eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionBuilder.notEqualsNull;
import static eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionBuilder.or;
import static eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionBuilder.ref;
import static eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionBuilder.staticMethod;
import static eu.uk.ncl.pet5o.esper.epl.core.resultset.codegen.ResultSetProcessorCodegenNames.*;
import static eu.uk.ncl.pet5o.esper.epl.core.resultset.codegen.ResultSetProcessorCodegenNames.NAME_ISNEWDATA;
import static eu.uk.ncl.pet5o.esper.epl.core.resultset.codegen.ResultSetProcessorCodegenNames.NAME_ISSYNTHESIZE;
import static eu.uk.ncl.pet5o.esper.epl.core.resultset.codegen.ResultSetProcessorCodegenNames.NAME_ORDERBYPROCESSOR;
import static eu.uk.ncl.pet5o.esper.epl.core.resultset.codegen.ResultSetProcessorCodegenNames.NAME_SELECTEXPRPROCESSOR;
import static eu.uk.ncl.pet5o.esper.epl.core.resultset.codegen.ResultSetProcessorCodegenNames.REF_AGENTINSTANCECONTEXT;
import static eu.uk.ncl.pet5o.esper.epl.core.resultset.codegen.ResultSetProcessorCodegenNames.REF_AGGREGATIONSVC;
import static eu.uk.ncl.pet5o.esper.epl.core.resultset.codegen.ResultSetProcessorCodegenNames.REF_ISNEWDATA;
import static eu.uk.ncl.pet5o.esper.epl.core.resultset.codegen.ResultSetProcessorCodegenNames.REF_ISSYNTHESIZE;
import static eu.uk.ncl.pet5o.esper.epl.core.resultset.codegen.ResultSetProcessorCodegenNames.REF_NEWDATA;
import static eu.uk.ncl.pet5o.esper.epl.core.resultset.codegen.ResultSetProcessorCodegenNames.REF_OLDDATA;
import static eu.uk.ncl.pet5o.esper.epl.core.resultset.codegen.ResultSetProcessorCodegenNames.REF_ORDERBYPROCESSOR;
import static eu.uk.ncl.pet5o.esper.epl.core.resultset.codegen.ResultSetProcessorCodegenNames.REF_SELECTEXPRNONMEMBER;
import static eu.uk.ncl.pet5o.esper.epl.core.resultset.codegen.ResultSetProcessorCodegenNames.REF_SELECTEXPRPROCESSOR;
import static eu.uk.ncl.pet5o.esper.epl.enummethod.codegen.EnumForgeCodegenNames.REF_EPS;
import static eu.uk.ncl.pet5o.esper.epl.expression.codegen.ExprForgeCodegenNames.*;
import static eu.uk.ncl.pet5o.esper.epl.expression.codegen.ExprForgeCodegenNames.NAME_EPS;
import static eu.uk.ncl.pet5o.esper.epl.expression.codegen.ExprForgeCodegenNames.NAME_EXPREVALCONTEXT;
import static eu.uk.ncl.pet5o.esper.epl.expression.codegen.ExprForgeCodegenNames.REF_EXPREVALCONTEXT;
import static eu.uk.ncl.pet5o.esper.util.CollectionUtil.*;
import static eu.uk.ncl.pet5o.esper.util.CollectionUtil.METHOD_SHRINKARRAYEVENTARRAY;
import static eu.uk.ncl.pet5o.esper.util.CollectionUtil.METHOD_SHRINKARRAYEVENTS;
import static eu.uk.ncl.pet5o.esper.util.CollectionUtil.METHOD_SHRINKARRAYOBJECTS;
import static eu.uk.ncl.pet5o.esper.util.CollectionUtil.METHOD_TOARRAYEVENTS;
import static eu.uk.ncl.pet5o.esper.util.CollectionUtil.METHOD_TOARRAYEVENTSARRAY;
import static eu.uk.ncl.pet5o.esper.util.CollectionUtil.METHOD_TOARRAYMAYNULL;
import static eu.uk.ncl.pet5o.esper.util.CollectionUtil.METHOD_TOARRAYNULLFOREMPTYEVENTS;
import static eu.uk.ncl.pet5o.esper.util.CollectionUtil.METHOD_TOARRAYNULLFOREMPTYOBJECTS;

public class ResultSetProcessorUtil {
    public final static String METHOD_ITERATORTODEQUE = "iteratorToDeque";
    public final static String METHOD_TOPAIRNULLIFALLNULL = "toPairNullIfAllNull";
    public final static String METHOD_APPLYAGGVIEWRESULT = "applyAggViewResult";
    public final static String METHOD_APPLYAGGJOINRESULT = "applyAggJoinResult";
    public final static String METHOD_CLEARANDAGGREGATEUNGROUPED = "clearAndAggregateUngrouped";
    public final static String METHOD_POPULATESELECTJOINEVENTSNOHAVING = "populateSelectJoinEventsNoHaving";
    public final static String METHOD_POPULATESELECTJOINEVENTSNOHAVINGWITHORDERBY = "populateSelectJoinEventsNoHavingWithOrderBy";
    public final static String METHOD_POPULATESELECTEVENTSNOHAVING = "populateSelectEventsNoHaving";
    public final static String METHOD_POPULATESELECTEVENTSNOHAVINGWITHORDERBY = "populateSelectEventsNoHavingWithOrderBy";
    public final static String METHOD_GETSELECTJOINEVENTSNOHAVING = "getSelectJoinEventsNoHaving";
    public final static String METHOD_GETSELECTJOINEVENTSNOHAVINGWITHORDERBY = "getSelectJoinEventsNoHavingWithOrderBy";
    public final static String METHOD_GETSELECTEVENTSNOHAVING = "getSelectEventsNoHaving";
    public final static String METHOD_GETSELECTEVENTSNOHAVINGWITHORDERBY = "getSelectEventsNoHavingWithOrderBy";
    public final static String METHOD_ORDEROUTGOINGGETITERATOR = "orderOutgoingGetIterator";

    public static void evaluateHavingClauseCodegen(ExprForge optionalHavingClause, CodegenClassScope classScope, CodegenInstanceAux instance) {
        Consumer<CodegenMethodNode> code = method -> {
            if (optionalHavingClause == null) {
                method.getBlock().methodReturn(constantTrue());
            } else {
                method.getBlock().methodReturn(CodegenLegoMethodExpression.codegenBooleanExpressionReturnTrueFalse(optionalHavingClause, classScope, method, REF_EPS, REF_ISNEWDATA, REF_EXPREVALCONTEXT));
            }
        };
        instance.getMethods().addMethod(boolean.class, "evaluateHavingClause",
                CodegenNamedParam.from(eu.uk.ncl.pet5o.esper.client.EventBean[].class, NAME_EPS, boolean.class, NAME_ISNEWDATA, ExprEvaluatorContext.class, NAME_EXPREVALCONTEXT), ResultSetProcessorUtil.class, classScope, code);
    }

    /**
     * NOTE: Code-generation-invoked method, method name and parameter order matters
     *
     * @param aggregationService   aggregations
     * @param exprEvaluatorContext ctx
     * @param newData              istream
     * @param oldData              rstream
     * @param eventsPerStream      buf
     */
    public static void applyAggViewResult(AggregationService aggregationService, ExprEvaluatorContext exprEvaluatorContext, eu.uk.ncl.pet5o.esper.client.EventBean[] newData, eu.uk.ncl.pet5o.esper.client.EventBean[] oldData, eu.uk.ncl.pet5o.esper.client.EventBean[] eventsPerStream) {
        if (newData != null) {
            // apply new data to aggregates
            for (int i = 0; i < newData.length; i++) {
                eventsPerStream[0] = newData[i];
                aggregationService.applyEnter(eventsPerStream, null, exprEvaluatorContext);
            }
        }
        if (oldData != null) {
            // apply old data to aggregates
            for (int i = 0; i < oldData.length; i++) {
                eventsPerStream[0] = oldData[i];
                aggregationService.applyLeave(eventsPerStream, null, exprEvaluatorContext);
            }
        }
    }

    /**
     * NOTE: Code-generation-invoked method, method name and parameter order matters
     *
     * @param aggregationService   aggregations
     * @param exprEvaluatorContext ctx
     * @param newEvents            istream
     * @param oldEvents            rstream
     */
    public static void applyAggJoinResult(AggregationService aggregationService, ExprEvaluatorContext exprEvaluatorContext, Set<MultiKey<EventBean>> newEvents, Set<MultiKey<EventBean>> oldEvents) {
        if (newEvents != null) {
            // apply new data to aggregates
            for (MultiKey<eu.uk.ncl.pet5o.esper.client.EventBean> events : newEvents) {
                aggregationService.applyEnter(events.getArray(), null, exprEvaluatorContext);
            }
        }
        if (oldEvents != null) {
            // apply old data to aggregates
            for (MultiKey<eu.uk.ncl.pet5o.esper.client.EventBean> events : oldEvents) {
                aggregationService.applyLeave(events.getArray(), null, exprEvaluatorContext);
            }
        }
    }

    /**
     * NOTE: Code-generation-invoked method, method name and parameter order matters
     * Applies the select-clause to the given events returning the selected events. The number of events stays the
     * same, i.e. this method does not filter it just transforms the result set.
     *
     * @param exprProcessor        - processes each input event and returns output event
     * @param events               - input events
     * @param isNewData            - indicates whether we are dealing with new data (istream) or old data (rstream)
     * @param isSynthesize         - set to true to indicate that synthetic events are required for an iterator result set
     * @param exprEvaluatorContext context for expression evalauation
     * @return output events, one for each input event
     */
    public static eu.uk.ncl.pet5o.esper.client.EventBean[] getSelectEventsNoHaving(SelectExprProcessor exprProcessor, eu.uk.ncl.pet5o.esper.client.EventBean[] events, boolean isNewData, boolean isSynthesize, ExprEvaluatorContext exprEvaluatorContext) {
        if (events == null) {
            return null;
        }

        eu.uk.ncl.pet5o.esper.client.EventBean[] result = new eu.uk.ncl.pet5o.esper.client.EventBean[events.length];
        eu.uk.ncl.pet5o.esper.client.EventBean[] eventsPerStream = new eu.uk.ncl.pet5o.esper.client.EventBean[1];
        for (int i = 0; i < events.length; i++) {
            eventsPerStream[0] = events[i];
            result[i] = exprProcessor.process(eventsPerStream, isNewData, isSynthesize, exprEvaluatorContext);
        }
        return result;
    }

    /**
     * Applies the select-clause to the given events returning the selected events. The number of events stays the
     * same, i.e. this method does not filter it just transforms the result set.
     *
     * @param aggregationService   - aggregation svc
     * @param exprProcessor        - processes each input event and returns output event
     * @param orderByProcessor     - orders the outgoing events according to the order-by clause
     * @param events               - input events
     * @param isNewData            - indicates whether we are dealing with new data (istream) or old data (rstream)
     * @param isSynthesize         - set to true to indicate that synthetic events are required for an iterator result set
     * @param exprEvaluatorContext context for expression evalauation
     * @return output events, one for each input event
     */
    public static eu.uk.ncl.pet5o.esper.client.EventBean[] getSelectEventsNoHavingWithOrderBy(AggregationService aggregationService, SelectExprProcessor exprProcessor, OrderByProcessor orderByProcessor, eu.uk.ncl.pet5o.esper.client.EventBean[] events, boolean isNewData, boolean isSynthesize, ExprEvaluatorContext exprEvaluatorContext) {
        if (events == null) {
            return null;
        }

        eu.uk.ncl.pet5o.esper.client.EventBean[] result = new eu.uk.ncl.pet5o.esper.client.EventBean[events.length];
        eu.uk.ncl.pet5o.esper.client.EventBean[][] eventGenerators = new eu.uk.ncl.pet5o.esper.client.EventBean[events.length][];

        eu.uk.ncl.pet5o.esper.client.EventBean[] eventsPerStream = new eu.uk.ncl.pet5o.esper.client.EventBean[1];
        for (int i = 0; i < events.length; i++) {
            eventsPerStream[0] = events[i];
            result[i] = exprProcessor.process(eventsPerStream, isNewData, isSynthesize, exprEvaluatorContext);
            eventGenerators[i] = new eu.uk.ncl.pet5o.esper.client.EventBean[]{events[i]};
        }

        return orderByProcessor.sortPlain(result, eventGenerators, isNewData, exprEvaluatorContext, aggregationService);
    }

    /**
     * Applies the select-clause to the given events returning the selected events. The number of events stays the
     * same, i.e. this method does not filter it just transforms the result set.
     * <p>
     * Also applies a having clause.
     *
     * @param aggregationService   - aggregation svc
     * @param exprProcessor        - processes each input event and returns output event
     * @param orderByProcessor     - for sorting output events according to the order-by clause
     * @param events               - input events
     * @param havingNode           - supplies the having-clause expression
     * @param isNewData            - indicates whether we are dealing with new data (istream) or old data (rstream)
     * @param isSynthesize         - set to true to indicate that synthetic events are required for an iterator result set
     * @param exprEvaluatorContext context for expression evalauation
     * @return output events, one for each input event
     */
    public static eu.uk.ncl.pet5o.esper.client.EventBean[] getSelectEventsHavingWithOrderBy(AggregationService aggregationService, SelectExprProcessor exprProcessor, OrderByProcessor orderByProcessor, eu.uk.ncl.pet5o.esper.client.EventBean[] events, ExprEvaluator havingNode, boolean isNewData, boolean isSynthesize, ExprEvaluatorContext exprEvaluatorContext) {
        if (events == null) {
            return null;
        }

        ArrayDeque<EventBean> result = null;
        ArrayDeque<EventBean[]> eventGenerators = null;

        eu.uk.ncl.pet5o.esper.client.EventBean[] eventsPerStream = new eu.uk.ncl.pet5o.esper.client.EventBean[1];
        for (eu.uk.ncl.pet5o.esper.client.EventBean theEvent : events) {
            eventsPerStream[0] = theEvent;

            boolean passesHaving = ResultSetProcessorUtil.evaluateHavingClause(havingNode, eventsPerStream, isNewData, exprEvaluatorContext);
            if (!passesHaving) {
                continue;
            }

            eu.uk.ncl.pet5o.esper.client.EventBean generated = exprProcessor.process(eventsPerStream, isNewData, isSynthesize, exprEvaluatorContext);
            if (generated != null) {
                if (result == null) {
                    result = new ArrayDeque<>(events.length);
                    eventGenerators = new ArrayDeque<>(events.length);
                }
                result.add(generated);
                eventGenerators.add(new eu.uk.ncl.pet5o.esper.client.EventBean[]{theEvent});
            }
        }

        if (result != null) {
            return orderByProcessor.sortPlain(CollectionUtil.toArrayEvents(result), CollectionUtil.toArrayEventsArray(eventGenerators), isNewData, exprEvaluatorContext, aggregationService);
        }
        return null;
    }

    public static CodegenMethodNode getSelectEventsHavingWithOrderByCodegen(CodegenClassScope classScope, CodegenInstanceAux instance) {
        Consumer<CodegenMethodNode> code = methodNode -> {
            methodNode.getBlock().ifRefNullReturnNull("events")
                    .declareVar(ArrayDeque.class, "result", constantNull())
                    .declareVar(ArrayDeque.class, "eventGenerators", constantNull())
                    .declareVar(eu.uk.ncl.pet5o.esper.client.EventBean[].class, NAME_EPS, newArrayByLength(eu.uk.ncl.pet5o.esper.client.EventBean.class, constant(1)));
            {
                CodegenBlock forEach = methodNode.getBlock().forEach(eu.uk.ncl.pet5o.esper.client.EventBean.class, "theEvent", ref("events"));
                forEach.assignArrayElement(NAME_EPS, constant(0), ref("theEvent"));
                forEach.ifCondition(not(localMethod(instance.getMethods().getMethod("evaluateHavingClause"), REF_EPS, REF_ISNEWDATA, REF_EXPREVALCONTEXT))).blockContinue();
                forEach.declareVar(eu.uk.ncl.pet5o.esper.client.EventBean.class, "generated", exprDotMethod(REF_SELECTEXPRNONMEMBER, "process", REF_EPS, REF_ISNEWDATA, REF_ISSYNTHESIZE, REF_EXPREVALCONTEXT))
                        .ifCondition(notEqualsNull(ref("generated")))
                        .ifCondition(equalsNull(ref("result")))
                        .assignRef("result", newInstance(ArrayDeque.class, arrayLength(ref("events"))))
                        .assignRef("eventGenerators", newInstance(ArrayDeque.class, arrayLength(ref("events"))))
                        .blockEnd()
                        .exprDotMethod(ref("result"), "add", ref("generated"))
                        .declareVar(eu.uk.ncl.pet5o.esper.client.EventBean[].class, "tmp", newArrayByLength(eu.uk.ncl.pet5o.esper.client.EventBean.class, constant(0)))
                        .assignArrayElement("tmp", constant(0), ref("theEvent"))
                        .exprDotMethod(ref("eventGenerators"), "add", ref("tmp"))
                        .blockEnd();
            }

            methodNode.getBlock().ifRefNullReturnNull("result")
                    .declareVar(eu.uk.ncl.pet5o.esper.client.EventBean[].class, "arr", staticMethod(CollectionUtil.class, METHOD_TOARRAYEVENTS, ref("result")))
                    .declareVar(eu.uk.ncl.pet5o.esper.client.EventBean[][].class, "gen", staticMethod(CollectionUtil.class, METHOD_TOARRAYEVENTSARRAY, ref("eventGenerators")))
                    .methodReturn(exprDotMethod(REF_ORDERBYPROCESSOR, "sortPlain", ref("arr"), ref("gen"), REF_ISNEWDATA, REF_EXPREVALCONTEXT, REF_AGGREGATIONSVC));
        };

        return instance.getMethods().addMethod(eu.uk.ncl.pet5o.esper.client.EventBean[].class, "getSelectEventsHavingWithOrderBy",
                CodegenNamedParam.from(AggregationService.class, REF_AGGREGATIONSVC.getRef(), SelectExprProcessor.class, NAME_SELECTEXPRPROCESSOR, OrderByProcessor.class, NAME_ORDERBYPROCESSOR, eu.uk.ncl.pet5o.esper.client.EventBean[].class, "events", boolean.class, NAME_ISNEWDATA, boolean.class, NAME_ISSYNTHESIZE, ExprEvaluatorContext.class, NAME_EXPREVALCONTEXT),
                ResultSetProcessorUtil.class, classScope, code);
    }

    /**
     * Applies the select-clause to the given events returning the selected events. The number of events stays the
     * same, i.e. this method does not filter it just transforms the result set.
     * <p>
     * Also applies a having clause.
     *
     * @param exprProcessor        - processes each input event and returns output event
     * @param events               - input events
     * @param havingNode           - supplies the having-clause expression
     * @param isNewData            - indicates whether we are dealing with new data (istream) or old data (rstream)
     * @param isSynthesize         - set to true to indicate that synthetic events are required for an iterator result set
     * @param exprEvaluatorContext context for expression evalauation
     * @return output events, one for each input event
     */
    public static eu.uk.ncl.pet5o.esper.client.EventBean[] getSelectEventsHaving(SelectExprProcessor exprProcessor,
                                                                               eu.uk.ncl.pet5o.esper.client.EventBean[] events,
                                                                               ExprEvaluator havingNode,
                                                                               boolean isNewData,
                                                                               boolean isSynthesize,
                                                                               ExprEvaluatorContext exprEvaluatorContext) {
        if (events == null) {
            return null;
        }

        ArrayDeque<EventBean> result = null;
        eu.uk.ncl.pet5o.esper.client.EventBean[] eventsPerStream = new eu.uk.ncl.pet5o.esper.client.EventBean[1];

        for (eu.uk.ncl.pet5o.esper.client.EventBean theEvent : events) {
            eventsPerStream[0] = theEvent;

            boolean passesHaving = ResultSetProcessorUtil.evaluateHavingClause(havingNode, eventsPerStream, isNewData, exprEvaluatorContext);
            if (!passesHaving) {
                continue;
            }

            eu.uk.ncl.pet5o.esper.client.EventBean generated = exprProcessor.process(eventsPerStream, isNewData, isSynthesize, exprEvaluatorContext);
            if (generated != null) {
                if (result == null) {
                    result = new ArrayDeque<>(events.length);
                }
                result.add(generated);
            }
        }

        return CollectionUtil.toArrayMayNull(result);
    }

    public static CodegenMethodNode getSelectEventsHavingCodegen(CodegenClassScope classScope,
                                                                 CodegenInstanceAux instance) {

        Consumer<CodegenMethodNode> code = methodNode -> {
            methodNode.getBlock()
                    .ifRefNullReturnNull("events")
                    .declareVar(ArrayDeque.class, "result", constantNull())
                    .declareVar(eu.uk.ncl.pet5o.esper.client.EventBean[].class, "eventsPerStream", newArrayByLength(eu.uk.ncl.pet5o.esper.client.EventBean.class, constant(1)));

            {
                CodegenBlock forEach = methodNode.getBlock().forEach(eu.uk.ncl.pet5o.esper.client.EventBean.class, "theEvent", ref("events"));
                forEach.assignArrayElement(REF_EPS, constant(0), ref("theEvent"));
                forEach.ifCondition(not(localMethod(instance.getMethods().getMethod("evaluateHavingClause"), REF_EPS, REF_ISNEWDATA, REF_EXPREVALCONTEXT))).blockContinue();
                forEach.declareVar(eu.uk.ncl.pet5o.esper.client.EventBean.class, "generated", exprDotMethod(REF_SELECTEXPRNONMEMBER, "process", REF_EPS, REF_ISNEWDATA, REF_ISSYNTHESIZE, REF_EXPREVALCONTEXT))
                        .ifCondition(notEqualsNull(ref("generated")))
                        .ifCondition(equalsNull(ref("result")))
                        .assignRef("result", newInstance(ArrayDeque.class, arrayLength(ref("events")))).blockEnd()
                        .exprDotMethod(ref("result"), "add", ref("generated")).blockEnd();
            }
            methodNode.getBlock().methodReturn(staticMethod(CollectionUtil.class, METHOD_TOARRAYMAYNULL, ref("result")));
        };

        return instance.getMethods().addMethod(eu.uk.ncl.pet5o.esper.client.EventBean[].class, "getSelectEventsHaving",
                CodegenNamedParam.from(SelectExprProcessor.class, NAME_SELECTEXPRPROCESSOR, eu.uk.ncl.pet5o.esper.client.EventBean[].class, "events", boolean.class, NAME_ISNEWDATA, boolean.class, NAME_ISSYNTHESIZE, ExprEvaluatorContext.class, NAME_EXPREVALCONTEXT),
                ResultSetProcessorUtil.class, classScope, code);
    }

    /**
     * Applies the select-clause to the given events returning the selected events. The number of events stays the
     * same, i.e. this method does not filter it just transforms the result set.
     *
     * @param aggregationService   - aggregation svc
     * @param exprProcessor        - processes each input event and returns output event
     * @param orderByProcessor     - for sorting output events according to the order-by clause
     * @param events               - input events
     * @param isNewData            - indicates whether we are dealing with new data (istream) or old data (rstream)
     * @param isSynthesize         - set to true to indicate that synthetic events are required for an iterator result set
     * @param exprEvaluatorContext context for expression evalauation
     * @return output events, one for each input event
     */
    public static eu.uk.ncl.pet5o.esper.client.EventBean[] getSelectJoinEventsNoHavingWithOrderBy(AggregationService aggregationService, SelectExprProcessor exprProcessor, OrderByProcessor orderByProcessor, Set<MultiKey<EventBean>> events, boolean isNewData, boolean isSynthesize, ExprEvaluatorContext exprEvaluatorContext) {
        if ((events == null) || (events.isEmpty())) {
            return null;
        }

        eu.uk.ncl.pet5o.esper.client.EventBean[] result = new eu.uk.ncl.pet5o.esper.client.EventBean[events.size()];
        eu.uk.ncl.pet5o.esper.client.EventBean[][] eventGenerators = new eu.uk.ncl.pet5o.esper.client.EventBean[events.size()][];

        int count = 0;
        for (MultiKey<eu.uk.ncl.pet5o.esper.client.EventBean> key : events) {
            eu.uk.ncl.pet5o.esper.client.EventBean[] eventsPerStream = key.getArray();
            result[count] = exprProcessor.process(eventsPerStream, isNewData, isSynthesize, exprEvaluatorContext);
            eventGenerators[count] = eventsPerStream;
            count++;
        }

        return orderByProcessor.sortPlain(result, eventGenerators, isNewData, exprEvaluatorContext, aggregationService);
    }

    /**
     * Applies the select-clause to the given events returning the selected events. The number of events stays the
     * same, i.e. this method does not filter it just transforms the result set.
     *
     * @param exprProcessor        - processes each input event and returns output event
     * @param events               - input events
     * @param isNewData            - indicates whether we are dealing with new data (istream) or old data (rstream)
     * @param isSynthesize         - set to true to indicate that synthetic events are required for an iterator result set
     * @param exprEvaluatorContext context for expression evalauation
     * @return output events, one for each input event
     */
    public static eu.uk.ncl.pet5o.esper.client.EventBean[] getSelectJoinEventsNoHaving(SelectExprProcessor exprProcessor, Set<MultiKey<EventBean>> events, boolean isNewData, boolean isSynthesize, ExprEvaluatorContext exprEvaluatorContext) {
        if ((events == null) || (events.isEmpty())) {
            return null;
        }

        eu.uk.ncl.pet5o.esper.client.EventBean[] result = new eu.uk.ncl.pet5o.esper.client.EventBean[events.size()];
        int count = 0;

        for (MultiKey<eu.uk.ncl.pet5o.esper.client.EventBean> key : events) {
            eu.uk.ncl.pet5o.esper.client.EventBean[] eventsPerStream = key.getArray();
            result[count] = exprProcessor.process(eventsPerStream, isNewData, isSynthesize, exprEvaluatorContext);
            count++;
        }

        return result;
    }

    /**
     * Applies the select-clause to the given events returning the selected events. The number of events stays the
     * same, i.e. this method does not filter it just transforms the result set.
     * <p>
     * Also applies a having clause.
     *
     * @param exprProcessor        - processes each input event and returns output event
     * @param events               - input events
     * @param havingNode           - supplies the having-clause expression
     * @param isNewData            - indicates whether we are dealing with new data (istream) or old data (rstream)
     * @param isSynthesize         - set to true to indicate that synthetic events are required for an iterator result set
     * @param exprEvaluatorContext context for expression evalauation
     * @return output events, one for each input event
     */
    public static eu.uk.ncl.pet5o.esper.client.EventBean[] getSelectJoinEventsHaving(SelectExprProcessor exprProcessor, Set<MultiKey<EventBean>> events, ExprEvaluator havingNode, boolean isNewData, boolean isSynthesize, ExprEvaluatorContext exprEvaluatorContext) {
        if ((events == null) || (events.isEmpty())) {
            return null;
        }

        ArrayDeque<EventBean> result = null;

        for (MultiKey<eu.uk.ncl.pet5o.esper.client.EventBean> key : events) {
            eu.uk.ncl.pet5o.esper.client.EventBean[] eventsPerStream = key.getArray();

            boolean passesHaving = ResultSetProcessorUtil.evaluateHavingClause(havingNode, eventsPerStream, isNewData, exprEvaluatorContext);
            if (!passesHaving) {
                continue;
            }

            eu.uk.ncl.pet5o.esper.client.EventBean resultEvent = exprProcessor.process(eventsPerStream, isNewData, isSynthesize, exprEvaluatorContext);
            if (resultEvent != null) {
                if (result == null) {
                    result = new ArrayDeque<>(events.size());
                }
                result.add(resultEvent);
            }
        }

        return CollectionUtil.toArrayMayNull(result);
    }

    public static CodegenMethodNode getSelectJoinEventsHavingCodegen(CodegenClassScope classScope,
                                                                     CodegenInstanceAux instance) {

        Consumer<CodegenMethodNode> code = methodNode -> {
            methodNode.getBlock()
                    .ifCondition(or(equalsNull(ref("events")), exprDotMethod(ref("events"), "isEmpty"))).blockReturn(constantNull())
                    .ifRefNullReturnNull("events")
                    .declareVar(ArrayDeque.class, "result", constantNull());
            {
                CodegenBlock forEach = methodNode.getBlock().forEach(MultiKey.class, "key", ref("events"));
                forEach.declareVar(eu.uk.ncl.pet5o.esper.client.EventBean[].class, NAME_EPS, cast(eu.uk.ncl.pet5o.esper.client.EventBean[].class, exprDotMethod(ref("key"), "getArray")));
                forEach.ifCondition(not(localMethod(instance.getMethods().getMethod("evaluateHavingClause"), REF_EPS, REF_ISNEWDATA, REF_EXPREVALCONTEXT))).blockContinue();
                forEach.declareVar(eu.uk.ncl.pet5o.esper.client.EventBean.class, "generated", exprDotMethod(REF_SELECTEXPRNONMEMBER, "process", REF_EPS, REF_ISNEWDATA, REF_ISSYNTHESIZE, REF_EXPREVALCONTEXT))
                        .ifCondition(notEqualsNull(ref("generated")))
                        .ifCondition(equalsNull(ref("result")))
                        .assignRef("result", newInstance(ArrayDeque.class, exprDotMethod(ref("events"), "size"))).blockEnd()
                        .exprDotMethod(ref("result"), "add", ref("generated")).blockEnd();
            }
            methodNode.getBlock().methodReturn(staticMethod(CollectionUtil.class, METHOD_TOARRAYMAYNULL, ref("result")));
        };

        return instance.getMethods().addMethod(eu.uk.ncl.pet5o.esper.client.EventBean[].class, "getSelectJoinEventsHaving",
                CodegenNamedParam.from(SelectExprProcessor.class, NAME_SELECTEXPRPROCESSOR, Set.class, "events", boolean.class, NAME_ISNEWDATA, boolean.class, NAME_ISSYNTHESIZE, ExprEvaluatorContext.class, NAME_EXPREVALCONTEXT),
                ResultSetProcessorUtil.class, classScope, code);
    }

    /**
     * Applies the select-clause to the given events returning the selected events. The number of events stays the
     * same, i.e. this method does not filter it just transforms the result set.
     * <p>
     * Also applies a having clause.
     *
     * @param aggregationService   - aggregation svc
     * @param exprProcessor        - processes each input event and returns output event
     * @param orderByProcessor     - for sorting output events according to the order-by clause
     * @param events               - input events
     * @param havingNode           - supplies the having-clause expression
     * @param isNewData            - indicates whether we are dealing with new data (istream) or old data (rstream)
     * @param isSynthesize         - set to true to indicate that synthetic events are required for an iterator result set
     * @param exprEvaluatorContext context for expression evalauation
     * @return output events, one for each input event
     */
    public static eu.uk.ncl.pet5o.esper.client.EventBean[] getSelectJoinEventsHavingWithOrderBy(AggregationService aggregationService, SelectExprProcessor exprProcessor, OrderByProcessor orderByProcessor, Set<MultiKey<EventBean>> events, ExprEvaluator havingNode, boolean isNewData, boolean isSynthesize, ExprEvaluatorContext exprEvaluatorContext) {
        if ((events == null) || (events.isEmpty())) {
            return null;
        }

        ArrayDeque<EventBean> result = null;
        ArrayDeque<EventBean[]> eventGenerators = null;

        for (MultiKey<eu.uk.ncl.pet5o.esper.client.EventBean> key : events) {
            eu.uk.ncl.pet5o.esper.client.EventBean[] eventsPerStream = key.getArray();

            boolean passesHaving = ResultSetProcessorUtil.evaluateHavingClause(havingNode, eventsPerStream, isNewData, exprEvaluatorContext);
            if (!passesHaving) {
                continue;
            }

            eu.uk.ncl.pet5o.esper.client.EventBean resultEvent = exprProcessor.process(eventsPerStream, isNewData, isSynthesize, exprEvaluatorContext);
            if (resultEvent != null) {
                if (result == null) {
                    result = new ArrayDeque<EventBean>(events.size());
                    eventGenerators = new ArrayDeque<EventBean[]>(events.size());
                }
                result.add(resultEvent);
                eventGenerators.add(eventsPerStream);
            }
        }

        if (result != null) {
            return orderByProcessor.sortPlain(CollectionUtil.toArrayEvents(result), CollectionUtil.toArrayEventsArray(eventGenerators), isNewData, exprEvaluatorContext, aggregationService);
        }
        return null;
    }

    public static CodegenMethodNode getSelectJoinEventsHavingWithOrderByCodegen(CodegenClassScope classScope, CodegenInstanceAux instance) {
        Consumer<CodegenMethodNode> code = methodNode -> {
            methodNode.getBlock()
                    .ifCondition(or(equalsNull(ref("events")), exprDotMethod(ref("events"), "isEmpty"))).blockReturn(constantNull())
                    .ifRefNullReturnNull("events")
                    .declareVar(ArrayDeque.class, "result", constantNull())
                    .declareVar(ArrayDeque.class, "eventGenerators", constantNull());
            {
                CodegenBlock forEach = methodNode.getBlock().forEach(MultiKey.class, "key", ref("events"));
                forEach.declareVar(eu.uk.ncl.pet5o.esper.client.EventBean[].class, NAME_EPS, cast(eu.uk.ncl.pet5o.esper.client.EventBean[].class, exprDotMethod(ref("key"), "getArray")));
                forEach.ifCondition(not(localMethod(instance.getMethods().getMethod("evaluateHavingClause"), REF_EPS, REF_ISNEWDATA, REF_EXPREVALCONTEXT))).blockContinue();
                forEach.declareVar(eu.uk.ncl.pet5o.esper.client.EventBean.class, "resultEvent", exprDotMethod(REF_SELECTEXPRNONMEMBER, "process", REF_EPS, REF_ISNEWDATA, REF_ISSYNTHESIZE, REF_EXPREVALCONTEXT))
                        .ifCondition(notEqualsNull(ref("resultEvent")))
                        .ifCondition(equalsNull(ref("result")))
                        .assignRef("result", newInstance(ArrayDeque.class, exprDotMethod(ref("events"), "size")))
                        .assignRef("eventGenerators", newInstance(ArrayDeque.class, exprDotMethod(ref("events"), "size")))
                        .blockEnd()
                        .exprDotMethod(ref("result"), "add", ref("resultEvent"))
                        .exprDotMethod(ref("eventGenerators"), "add", ref("eventsPerStream"))
                        .blockEnd();
            }
            methodNode.getBlock().ifRefNullReturnNull("result")
                    .declareVar(eu.uk.ncl.pet5o.esper.client.EventBean[].class, "arr", staticMethod(CollectionUtil.class, METHOD_TOARRAYEVENTS, ref("result")))
                    .declareVar(eu.uk.ncl.pet5o.esper.client.EventBean[][].class, "gen", staticMethod(CollectionUtil.class, METHOD_TOARRAYEVENTSARRAY, ref("eventGenerators")))
                    .methodReturn(exprDotMethod(REF_ORDERBYPROCESSOR, "sortPlain", ref("arr"), ref("gen"), REF_ISNEWDATA, REF_EXPREVALCONTEXT, REF_AGGREGATIONSVC));
        };

        return instance.getMethods().addMethod(eu.uk.ncl.pet5o.esper.client.EventBean[].class, "getSelectJoinEventsHavingWithOrderBy",
                CodegenNamedParam.from(AggregationService.class, REF_AGGREGATIONSVC.getRef(), SelectExprProcessor.class, NAME_SELECTEXPRPROCESSOR, OrderByProcessor.class, NAME_ORDERBYPROCESSOR, Set.class, "events", boolean.class, NAME_ISNEWDATA, boolean.class, NAME_ISSYNTHESIZE, ExprEvaluatorContext.class, NAME_EXPREVALCONTEXT),
                ResultSetProcessorUtil.class, classScope, code);
    }

    public static void populateSelectEventsNoHaving(SelectExprProcessor exprProcessor, eu.uk.ncl.pet5o.esper.client.EventBean[] events, boolean isNewData, boolean isSynthesize, Collection<EventBean> result, ExprEvaluatorContext exprEvaluatorContext) {
        if (events == null) {
            return;
        }

        eu.uk.ncl.pet5o.esper.client.EventBean[] eventsPerStream = new eu.uk.ncl.pet5o.esper.client.EventBean[1];
        for (eu.uk.ncl.pet5o.esper.client.EventBean theEvent : events) {
            eventsPerStream[0] = theEvent;

            eu.uk.ncl.pet5o.esper.client.EventBean resultEvent = exprProcessor.process(eventsPerStream, isNewData, isSynthesize, exprEvaluatorContext);
            if (resultEvent != null) {
                result.add(resultEvent);
            }
        }
    }

    public static void populateSelectEventsNoHavingWithOrderBy(SelectExprProcessor exprProcessor, OrderByProcessor orderByProcessor, eu.uk.ncl.pet5o.esper.client.EventBean[] events, boolean isNewData, boolean isSynthesize, Collection<EventBean> result, List<Object> sortKeys, ExprEvaluatorContext exprEvaluatorContext) {
        if (events == null) {
            return;
        }

        eu.uk.ncl.pet5o.esper.client.EventBean[] eventsPerStream = new eu.uk.ncl.pet5o.esper.client.EventBean[1];
        for (eu.uk.ncl.pet5o.esper.client.EventBean theEvent : events) {
            eventsPerStream[0] = theEvent;

            eu.uk.ncl.pet5o.esper.client.EventBean resultEvent = exprProcessor.process(eventsPerStream, isNewData, isSynthesize, exprEvaluatorContext);
            if (resultEvent != null) {
                result.add(resultEvent);
                sortKeys.add(orderByProcessor.getSortKey(eventsPerStream, isNewData, exprEvaluatorContext));
            }
        }
    }

    public static void populateSelectEventsHaving(SelectExprProcessor exprProcessor, eu.uk.ncl.pet5o.esper.client.EventBean[] events, ExprEvaluator havingNode, boolean isNewData, boolean isSynthesize, List<EventBean> result, ExprEvaluatorContext exprEvaluatorContext) {
        if (events == null) {
            return;
        }

        eu.uk.ncl.pet5o.esper.client.EventBean[] eventsPerStream = new eu.uk.ncl.pet5o.esper.client.EventBean[1];
        for (eu.uk.ncl.pet5o.esper.client.EventBean theEvent : events) {
            eventsPerStream[0] = theEvent;

            boolean passesHaving = ResultSetProcessorUtil.evaluateHavingClause(havingNode, eventsPerStream, isNewData, exprEvaluatorContext);
            if (!passesHaving) {
                continue;
            }

            eu.uk.ncl.pet5o.esper.client.EventBean resultEvent = exprProcessor.process(eventsPerStream, isNewData, isSynthesize, exprEvaluatorContext);
            if (resultEvent != null) {
                result.add(resultEvent);
            }
        }
    }

    public static CodegenMethodNode populateSelectEventsHavingCodegen(CodegenClassScope classScope, CodegenInstanceAux instance) {
        Consumer<CodegenMethodNode> code = methodNode -> {
            methodNode.getBlock()
                    .ifRefNull("events").blockReturnNoValue()
                    .declareVar(eu.uk.ncl.pet5o.esper.client.EventBean[].class, "eventsPerStream", newArrayByLength(eu.uk.ncl.pet5o.esper.client.EventBean.class, constant(1)));

            {
                CodegenBlock forEach = methodNode.getBlock().forEach(eu.uk.ncl.pet5o.esper.client.EventBean.class, "theEvent", ref("events"));
                forEach.assignArrayElement(REF_EPS, constant(0), ref("theEvent"));
                forEach.ifCondition(not(localMethod(instance.getMethods().getMethod("evaluateHavingClause"), REF_EPS, REF_ISNEWDATA, REF_EXPREVALCONTEXT))).blockContinue();
                forEach.declareVar(eu.uk.ncl.pet5o.esper.client.EventBean.class, "resultEvent", exprDotMethod(REF_SELECTEXPRNONMEMBER, "process", REF_EPS, REF_ISNEWDATA, REF_ISSYNTHESIZE, REF_EXPREVALCONTEXT))
                        .ifCondition(notEqualsNull(ref("resultEvent")))
                        .exprDotMethod(ref("result"), "add", ref("resultEvent"));
            }
        };

        return instance.getMethods().addMethod(void.class, "populateSelectEventsHaving",
                CodegenNamedParam.from(SelectExprProcessor.class, NAME_SELECTEXPRPROCESSOR, eu.uk.ncl.pet5o.esper.client.EventBean[].class, "events", boolean.class, NAME_ISNEWDATA, boolean.class, NAME_ISSYNTHESIZE, List.class, "result", ExprEvaluatorContext.class, NAME_EXPREVALCONTEXT),
                ResultSetProcessorUtil.class, classScope, code);
    }

    public static void populateSelectEventsHavingWithOrderBy(SelectExprProcessor exprProcessor, OrderByProcessor orderByProcessor, eu.uk.ncl.pet5o.esper.client.EventBean[] events, ExprEvaluator havingNode, boolean isNewData, boolean isSynthesize, List<EventBean> result, List<Object> optSortKeys, ExprEvaluatorContext exprEvaluatorContext) {
        if (events == null) {
            return;
        }

        eu.uk.ncl.pet5o.esper.client.EventBean[] eventsPerStream = new eu.uk.ncl.pet5o.esper.client.EventBean[1];
        for (eu.uk.ncl.pet5o.esper.client.EventBean theEvent : events) {
            eventsPerStream[0] = theEvent;

            boolean passesHaving = ResultSetProcessorUtil.evaluateHavingClause(havingNode, eventsPerStream, isNewData, exprEvaluatorContext);
            if (!passesHaving) {
                continue;
            }

            eu.uk.ncl.pet5o.esper.client.EventBean resultEvent = exprProcessor.process(eventsPerStream, isNewData, isSynthesize, exprEvaluatorContext);
            if (resultEvent != null) {
                result.add(resultEvent);
                optSortKeys.add(orderByProcessor.getSortKey(eventsPerStream, isNewData, exprEvaluatorContext));
            }
        }
    }

    public static CodegenMethodNode populateSelectEventsHavingWithOrderByCodegen(CodegenClassScope classScope, CodegenInstanceAux instance) {
        Consumer<CodegenMethodNode> code = methodNode -> {
            methodNode.getBlock()
                    .ifRefNull("events").blockReturnNoValue()
                    .declareVar(eu.uk.ncl.pet5o.esper.client.EventBean[].class, "eventsPerStream", newArrayByLength(eu.uk.ncl.pet5o.esper.client.EventBean.class, constant(1)));

            {
                CodegenBlock forEach = methodNode.getBlock().forEach(eu.uk.ncl.pet5o.esper.client.EventBean.class, "theEvent", ref("events"));
                forEach.assignArrayElement(REF_EPS, constant(0), ref("theEvent"));
                forEach.ifCondition(not(localMethod(instance.getMethods().getMethod("evaluateHavingClause"), REF_EPS, REF_ISNEWDATA, REF_EXPREVALCONTEXT))).blockContinue();
                forEach.declareVar(eu.uk.ncl.pet5o.esper.client.EventBean.class, "resultEvent", exprDotMethod(REF_SELECTEXPRNONMEMBER, "process", REF_EPS, REF_ISNEWDATA, REF_ISSYNTHESIZE, REF_EXPREVALCONTEXT))
                        .ifCondition(notEqualsNull(ref("resultEvent")))
                        .exprDotMethod(ref("result"), "add", ref("resultEvent"))
                        .exprDotMethod(ref("optSortKeys"), "add", exprDotMethod(REF_ORDERBYPROCESSOR, "getSortKey", REF_EPS, REF_ISNEWDATA, REF_EXPREVALCONTEXT));
            }
        };

        return instance.getMethods().addMethod(void.class, "populateSelectEventsHavingWithOrderBy",
                CodegenNamedParam.from(SelectExprProcessor.class, NAME_SELECTEXPRPROCESSOR, OrderByProcessor.class, NAME_ORDERBYPROCESSOR, eu.uk.ncl.pet5o.esper.client.EventBean[].class, "events", boolean.class, NAME_ISNEWDATA, boolean.class, NAME_ISSYNTHESIZE, List.class, "result", List.class, "optSortKeys", ExprEvaluatorContext.class, NAME_EXPREVALCONTEXT),
                ResultSetProcessorUtil.class, classScope, code);
    }

    public static void populateSelectJoinEventsHaving(SelectExprProcessor exprProcessor, Set<MultiKey<EventBean>> events, ExprEvaluator havingNode, boolean isNewData, boolean isSynthesize, List<EventBean> result, ExprEvaluatorContext exprEvaluatorContext) {
        if (events == null) {
            return;
        }

        for (MultiKey<eu.uk.ncl.pet5o.esper.client.EventBean> key : events) {
            eu.uk.ncl.pet5o.esper.client.EventBean[] eventsPerStream = key.getArray();

            boolean passesHaving = ResultSetProcessorUtil.evaluateHavingClause(havingNode, eventsPerStream, isNewData, exprEvaluatorContext);
            if (!passesHaving) {
                continue;
            }

            eu.uk.ncl.pet5o.esper.client.EventBean resultEvent = exprProcessor.process(eventsPerStream, isNewData, isSynthesize, exprEvaluatorContext);
            if (resultEvent != null) {
                result.add(resultEvent);
            }
        }
    }

    public static CodegenMethodNode populateSelectJoinEventsHavingCodegen(CodegenClassScope classScope, CodegenInstanceAux instance) {
        Consumer<CodegenMethodNode> code = methodNode -> {
            methodNode.getBlock().ifRefNull("events").blockReturnNoValue();

            {
                CodegenBlock forEach = methodNode.getBlock().forEach(MultiKey.class, "key", ref("events"));
                forEach.declareVar(eu.uk.ncl.pet5o.esper.client.EventBean[].class, NAME_EPS, cast(eu.uk.ncl.pet5o.esper.client.EventBean[].class, exprDotMethod(ref("key"), "getArray")));
                forEach.ifCondition(not(localMethod(instance.getMethods().getMethod("evaluateHavingClause"), REF_EPS, REF_ISNEWDATA, REF_EXPREVALCONTEXT))).blockContinue();
                forEach.declareVar(eu.uk.ncl.pet5o.esper.client.EventBean.class, "resultEvent", exprDotMethod(REF_SELECTEXPRNONMEMBER, "process", REF_EPS, REF_ISNEWDATA, REF_ISSYNTHESIZE, REF_EXPREVALCONTEXT))
                        .ifCondition(notEqualsNull(ref("resultEvent")))
                        .exprDotMethod(ref("result"), "add", ref("resultEvent"));
            }
        };

        return instance.getMethods().addMethod(void.class, "populateSelectJoinEventsHavingCodegen",
                CodegenNamedParam.from(SelectExprProcessor.class, NAME_SELECTEXPRPROCESSOR, Set.class, "events", boolean.class, NAME_ISNEWDATA, boolean.class, NAME_ISSYNTHESIZE, List.class, "result", ExprEvaluatorContext.class, NAME_EXPREVALCONTEXT),
                ResultSetProcessorUtil.class, classScope, code);
    }

    public static void populateSelectJoinEventsHavingWithOrderBy(SelectExprProcessor exprProcessor, OrderByProcessor orderByProcessor, Set<MultiKey<EventBean>> events, ExprEvaluator havingNode, boolean isNewData, boolean isSynthesize, List<EventBean> result, List<Object> sortKeys, ExprEvaluatorContext exprEvaluatorContext) {
        if (events == null) {
            return;
        }

        for (MultiKey<eu.uk.ncl.pet5o.esper.client.EventBean> key : events) {
            eu.uk.ncl.pet5o.esper.client.EventBean[] eventsPerStream = key.getArray();

            boolean passesHaving = ResultSetProcessorUtil.evaluateHavingClause(havingNode, eventsPerStream, isNewData, exprEvaluatorContext);
            if (!passesHaving) {
                continue;
            }

            eu.uk.ncl.pet5o.esper.client.EventBean resultEvent = exprProcessor.process(eventsPerStream, isNewData, isSynthesize, exprEvaluatorContext);
            if (resultEvent != null) {
                result.add(resultEvent);
                sortKeys.add(orderByProcessor.getSortKey(eventsPerStream, isNewData, exprEvaluatorContext));
            }
        }
    }

    public static CodegenMethodNode populateSelectJoinEventsHavingWithOrderByCodegen(CodegenClassScope classScope, CodegenInstanceAux instance) {
        Consumer<CodegenMethodNode> code = methodNode -> {
            methodNode.getBlock().ifRefNull("events").blockReturnNoValue();

            {
                CodegenBlock forEach = methodNode.getBlock().forEach(MultiKey.class, "key", ref("events"));
                forEach.declareVar(eu.uk.ncl.pet5o.esper.client.EventBean[].class, NAME_EPS, cast(eu.uk.ncl.pet5o.esper.client.EventBean[].class, exprDotMethod(ref("key"), "getArray")));
                forEach.ifCondition(not(localMethod(instance.getMethods().getMethod("evaluateHavingClause"), REF_EPS, REF_ISNEWDATA, REF_EXPREVALCONTEXT))).blockContinue();
                forEach.declareVar(eu.uk.ncl.pet5o.esper.client.EventBean.class, "resultEvent", exprDotMethod(REF_SELECTEXPRNONMEMBER, "process", REF_EPS, REF_ISNEWDATA, REF_ISSYNTHESIZE, REF_EXPREVALCONTEXT))
                        .ifCondition(notEqualsNull(ref("resultEvent")))
                        .exprDotMethod(ref("result"), "add", ref("resultEvent"))
                        .exprDotMethod(ref("sortKeys"), "add", exprDotMethod(REF_ORDERBYPROCESSOR, "getSortKey", REF_EPS, REF_ISNEWDATA, REF_EXPREVALCONTEXT));
            }
        };

        return instance.getMethods().addMethod(void.class, "populateSelectJoinEventsHavingWithOrderBy",
                CodegenNamedParam.from(SelectExprProcessor.class, NAME_SELECTEXPRPROCESSOR, OrderByProcessor.class, NAME_ORDERBYPROCESSOR, Set.class, "events", boolean.class, NAME_ISNEWDATA, boolean.class, NAME_ISSYNTHESIZE, List.class, "result", List.class, "sortKeys", ExprEvaluatorContext.class, NAME_EXPREVALCONTEXT),
                ResultSetProcessorUtil.class, classScope, code);
    }

    public static void populateSelectJoinEventsNoHaving(SelectExprProcessor exprProcessor, Set<MultiKey<EventBean>> events, boolean isNewData, boolean isSynthesize, List<EventBean> result, ExprEvaluatorContext exprEvaluatorContext) {
        int length = (events != null) ? events.size() : 0;
        if (length == 0) {
            return;
        }

        for (MultiKey<eu.uk.ncl.pet5o.esper.client.EventBean> key : events) {
            eu.uk.ncl.pet5o.esper.client.EventBean[] eventsPerStream = key.getArray();
            eu.uk.ncl.pet5o.esper.client.EventBean resultEvent = exprProcessor.process(eventsPerStream, isNewData, isSynthesize, exprEvaluatorContext);
            if (resultEvent != null) {
                result.add(resultEvent);
            }
        }
    }

    public static void populateSelectJoinEventsNoHavingWithOrderBy(SelectExprProcessor exprProcessor, OrderByProcessor orderByProcessor, Set<MultiKey<EventBean>> events, boolean isNewData, boolean isSynthesize, List<EventBean> result, List<Object> optSortKeys, ExprEvaluatorContext exprEvaluatorContext) {
        int length = (events != null) ? events.size() : 0;
        if (length == 0) {
            return;
        }

        for (MultiKey<eu.uk.ncl.pet5o.esper.client.EventBean> key : events) {
            eu.uk.ncl.pet5o.esper.client.EventBean[] eventsPerStream = key.getArray();
            eu.uk.ncl.pet5o.esper.client.EventBean resultEvent = exprProcessor.process(eventsPerStream, isNewData, isSynthesize, exprEvaluatorContext);
            if (resultEvent != null) {
                result.add(resultEvent);
                optSortKeys.add(orderByProcessor.getSortKey(eventsPerStream, isNewData, exprEvaluatorContext));
            }
        }
    }

    public static void clearAndAggregateUngrouped(ExprEvaluatorContext exprEvaluatorContext, AggregationService aggregationService, Viewable parent) {
        aggregationService.clearResults(exprEvaluatorContext);
        Iterator<EventBean> it = parent.iterator();
        eu.uk.ncl.pet5o.esper.client.EventBean[] eventsPerStream = new eu.uk.ncl.pet5o.esper.client.EventBean[1];
        for (; it.hasNext(); ) {
            eventsPerStream[0] = it.next();
            aggregationService.applyEnter(eventsPerStream, null, exprEvaluatorContext);
        }
    }

    /**
     * NOTE: Code-generation-invoked method, method name and parameter order matters
     *
     * @param it iterator
     * @return deque
     */
    public static ArrayDeque<EventBean> iteratorToDeque(Iterator<EventBean> it) {
        ArrayDeque<EventBean> deque = new ArrayDeque<EventBean>();
        for (; it.hasNext(); ) {
            deque.add(it.next());
        }
        return deque;
    }

    /**
     * NOTE: Code-generation-invoked method, method name and parameter order matters
     *
     * @param selectNewEvents new events
     * @param selectOldEvents old events
     * @return pair or null
     */
    public static UniformPair<eu.uk.ncl.pet5o.esper.client.EventBean[]> toPairNullIfAllNull(eu.uk.ncl.pet5o.esper.client.EventBean[] selectNewEvents, eu.uk.ncl.pet5o.esper.client.EventBean[] selectOldEvents) {
        if ((selectNewEvents != null) || (selectOldEvents != null)) {
            return new UniformPair<>(selectNewEvents, selectOldEvents);
        }
        return null;
    }

    public static void processViewResultCodegen(CodegenMethodNode method, CodegenClassScope classScope, CodegenInstanceAux instance, boolean hasHaving, boolean selectRStream, boolean hasOrderBy, boolean outputNullIfBothNull) {
        // generate new events using select expressions
        if (!hasHaving) {
            if (selectRStream) {
                if (!hasOrderBy) {
                    method.getBlock().assignRef("selectOldEvents", staticMethod(ResultSetProcessorUtil.class, METHOD_GETSELECTEVENTSNOHAVING, REF_SELECTEXPRPROCESSOR, REF_OLDDATA, constant(false), REF_ISSYNTHESIZE, REF_AGENTINSTANCECONTEXT));
                } else {
                    method.getBlock().assignRef("selectOldEvents", staticMethod(ResultSetProcessorUtil.class, METHOD_GETSELECTEVENTSNOHAVINGWITHORDERBY, REF_AGGREGATIONSVC, REF_SELECTEXPRPROCESSOR, REF_ORDERBYPROCESSOR, REF_OLDDATA, constantFalse(), REF_ISSYNTHESIZE, REF_AGENTINSTANCECONTEXT));
                }
            }

            if (!hasOrderBy) {
                method.getBlock().assignRef("selectNewEvents", staticMethod(ResultSetProcessorUtil.class, METHOD_GETSELECTEVENTSNOHAVING, REF_SELECTEXPRPROCESSOR, REF_NEWDATA, constant(true), REF_ISSYNTHESIZE, REF_AGENTINSTANCECONTEXT));
            } else {
                method.getBlock().assignRef("selectNewEvents", staticMethod(ResultSetProcessorUtil.class, METHOD_GETSELECTEVENTSNOHAVINGWITHORDERBY, REF_AGGREGATIONSVC, REF_SELECTEXPRPROCESSOR, REF_ORDERBYPROCESSOR, REF_NEWDATA, constantTrue(), REF_ISSYNTHESIZE, REF_AGENTINSTANCECONTEXT));
            }
        } else {
            if (selectRStream) {
                if (!hasOrderBy) {
                    CodegenMethodNode select = ResultSetProcessorUtil.getSelectEventsHavingCodegen(classScope, instance);
                    method.getBlock().assignRef("selectOldEvents", localMethod(select, REF_SELECTEXPRPROCESSOR, REF_OLDDATA, constantFalse(), REF_ISSYNTHESIZE, REF_AGENTINSTANCECONTEXT));
                } else {
                    CodegenMethodNode select = ResultSetProcessorUtil.getSelectEventsHavingWithOrderByCodegen(classScope, instance);
                    method.getBlock().assignRef("selectOldEvents", localMethod(select, REF_AGGREGATIONSVC, REF_SELECTEXPRPROCESSOR, REF_ORDERBYPROCESSOR, REF_OLDDATA, constantFalse(), REF_ISSYNTHESIZE, REF_AGENTINSTANCECONTEXT));
                }
            }

            if (!hasOrderBy) {
                CodegenMethodNode select = ResultSetProcessorUtil.getSelectEventsHavingCodegen(classScope, instance);
                method.getBlock().assignRef("selectNewEvents", localMethod(select, REF_SELECTEXPRPROCESSOR, REF_NEWDATA, constantTrue(), REF_ISSYNTHESIZE, REF_AGENTINSTANCECONTEXT));
            } else {
                CodegenMethodNode select = ResultSetProcessorUtil.getSelectEventsHavingWithOrderByCodegen(classScope, instance);
                method.getBlock().assignRef("selectNewEvents", localMethod(select, REF_AGGREGATIONSVC, REF_SELECTEXPRPROCESSOR, REF_ORDERBYPROCESSOR, REF_NEWDATA, constantTrue(), REF_ISSYNTHESIZE, REF_AGENTINSTANCECONTEXT));
            }
        }

        if (outputNullIfBothNull) {
            method.getBlock().ifCondition(and(equalsNull(ref("selectNewEvents")), equalsNull(ref("selectOldEvents")))).blockReturn(constantNull());
        }
        method.getBlock().methodReturn(newInstance(UniformPair.class, ref("selectNewEvents"), ref("selectOldEvents")));
    }

    public static void processJoinResultCodegen(CodegenMethodNode method, CodegenClassScope classScope, CodegenInstanceAux instance, boolean hasHaving, boolean selectRStream, boolean hasOrderBy, boolean outputNullIfBothNull) {
        if (!hasHaving) {
            if (selectRStream) {
                if (!hasOrderBy) {
                    method.getBlock().assignRef("selectOldEvents", staticMethod(ResultSetProcessorUtil.class, METHOD_GETSELECTJOINEVENTSNOHAVING, REF_SELECTEXPRPROCESSOR, REF_OLDDATA, constantFalse(), REF_ISSYNTHESIZE, REF_AGENTINSTANCECONTEXT));
                } else {
                    method.getBlock().assignRef("selectOldEvents", staticMethod(ResultSetProcessorUtil.class, METHOD_GETSELECTJOINEVENTSNOHAVINGWITHORDERBY, constantNull(), REF_SELECTEXPRPROCESSOR, REF_ORDERBYPROCESSOR, REF_OLDDATA, constantFalse(), REF_ISSYNTHESIZE, REF_AGENTINSTANCECONTEXT));
                }
            }

            if (!hasOrderBy) {
                method.getBlock().assignRef("selectNewEvents ", staticMethod(ResultSetProcessorUtil.class, METHOD_GETSELECTJOINEVENTSNOHAVING, REF_SELECTEXPRPROCESSOR, REF_NEWDATA, constantTrue(), REF_ISSYNTHESIZE, REF_AGENTINSTANCECONTEXT));
            } else {
                method.getBlock().assignRef("selectNewEvents ", staticMethod(ResultSetProcessorUtil.class, METHOD_GETSELECTJOINEVENTSNOHAVINGWITHORDERBY, constantNull(), REF_SELECTEXPRPROCESSOR, REF_ORDERBYPROCESSOR, REF_NEWDATA, constantTrue(), REF_ISSYNTHESIZE, REF_AGENTINSTANCECONTEXT));
            }
        } else {
            if (selectRStream) {
                if (!hasOrderBy) {
                    CodegenMethodNode select = ResultSetProcessorUtil.getSelectJoinEventsHavingCodegen(classScope, instance);
                    method.getBlock().assignRef("selectOldEvents", localMethod(select, REF_SELECTEXPRPROCESSOR, REF_OLDDATA, constantFalse(), REF_ISSYNTHESIZE, REF_AGENTINSTANCECONTEXT));
                } else {
                    CodegenMethodNode select = ResultSetProcessorUtil.getSelectJoinEventsHavingWithOrderByCodegen(classScope, instance);
                    method.getBlock().assignRef("selectOldEvents", localMethod(select, REF_AGGREGATIONSVC, REF_SELECTEXPRPROCESSOR, REF_ORDERBYPROCESSOR, REF_OLDDATA, constantFalse(), REF_ISSYNTHESIZE, REF_EXPREVALCONTEXT));
                }
            }

            if (!hasOrderBy) {
                CodegenMethodNode select = ResultSetProcessorUtil.getSelectJoinEventsHavingCodegen(classScope, instance);
                method.getBlock().assignRef("selectNewEvents", localMethod(select, REF_SELECTEXPRPROCESSOR, REF_NEWDATA, constantTrue(), REF_ISSYNTHESIZE, REF_AGENTINSTANCECONTEXT));
            } else {
                CodegenMethodNode select = ResultSetProcessorUtil.getSelectJoinEventsHavingWithOrderByCodegen(classScope, instance);
                method.getBlock().assignRef("selectNewEvents", localMethod(select, REF_AGGREGATIONSVC, REF_SELECTEXPRPROCESSOR, REF_ORDERBYPROCESSOR, REF_NEWDATA, constantTrue(), REF_ISSYNTHESIZE, REF_AGENTINSTANCECONTEXT));
            }
        }

        if (outputNullIfBothNull) {
            method.getBlock().ifCondition(and(equalsNull(ref("selectNewEvents")), equalsNull(ref("selectOldEvents")))).blockReturn(constantNull());
        }
        method.getBlock().methodReturn(newInstance(UniformPair.class, ref("selectNewEvents"), ref("selectOldEvents")));
    }

    /**
     * NOTE: Code-generation-invoked method, method name and parameter order matters
     *
     * @param outgoingEvents       events
     * @param orderKeys            keys
     * @param orderByProcessor     ordering
     * @param exprEvaluatorContext ctx
     * @return ordered events
     */
    public static ArrayEventIterator orderOutgoingGetIterator(List<EventBean> outgoingEvents, List<Object> orderKeys, OrderByProcessor orderByProcessor, ExprEvaluatorContext exprEvaluatorContext) {
        eu.uk.ncl.pet5o.esper.client.EventBean[] outgoingEventsArr = CollectionUtil.toArrayEvents(outgoingEvents);
        Object[] orderKeysArr = CollectionUtil.toArrayObjects(orderKeys);
        eu.uk.ncl.pet5o.esper.client.EventBean[] orderedEvents = orderByProcessor.sortWOrderKeys(outgoingEventsArr, orderKeysArr, exprEvaluatorContext);
        return new ArrayEventIterator(orderedEvents);
    }

    /**
     * NOTE: Code-generation-invoked method, method name and parameter order matters
     *
     * @param count                count
     * @param events               events
     * @param keys                 keys
     * @param currentGenerators    key-generators
     * @param isNewData            irstream
     * @param orderByProcessor     order-by
     * @param agentInstanceContext ctx
     * @param aggregationService   aggregation svc
     * @return events for output
     */
    public static eu.uk.ncl.pet5o.esper.client.EventBean[] outputFromCountMaySort(int count, eu.uk.ncl.pet5o.esper.client.EventBean[] events, Object[] keys, eu.uk.ncl.pet5o.esper.client.EventBean[][] currentGenerators, boolean isNewData, OrderByProcessor orderByProcessor, AgentInstanceContext agentInstanceContext, AggregationService aggregationService) {
        // Resize if some rows were filtered out
        if (count != events.length) {
            if (count == 0) {
                return null;
            }
            events = CollectionUtil.shrinkArrayEvents(count, events);

            if (orderByProcessor != null) {
                keys = CollectionUtil.shrinkArrayObjects(count, keys);
                currentGenerators = CollectionUtil.shrinkArrayEventArray(count, currentGenerators);
            }
        }

        if (orderByProcessor != null) {
            events = orderByProcessor.sortWGroupKeys(events, currentGenerators, keys, isNewData, agentInstanceContext, aggregationService);
        }

        return events;
    }

    public static void outputFromCountMaySortCodegen(CodegenBlock block, CodegenExpressionRef count, CodegenExpressionRef events, CodegenExpressionRef keys, CodegenExpressionRef currentGenerators, boolean hasOrderBy) {
        CodegenBlock resize = block.ifCondition(not(equalsIdentity(count, arrayLength(events))));
        resize.ifCondition(equalsIdentity(count, constant(0))).blockReturn(constantNull())
                .assignRef(events.getRef(), staticMethod(CollectionUtil.class, METHOD_SHRINKARRAYEVENTS, count, events));

        if (hasOrderBy) {
            resize.assignRef(keys.getRef(), staticMethod(CollectionUtil.class, METHOD_SHRINKARRAYOBJECTS, count, keys))
                    .assignRef(currentGenerators.getRef(), staticMethod(CollectionUtil.class, METHOD_SHRINKARRAYEVENTARRAY, count, currentGenerators));
        }

        if (hasOrderBy) {
            block.assignRef(events.getRef(), exprDotMethod(REF_ORDERBYPROCESSOR, "sortWGroupKeys", events, currentGenerators, keys, REF_ISNEWDATA, REF_AGENTINSTANCECONTEXT, REF_AGGREGATIONSVC));
        }

        block.methodReturn(events);
    }


    /**
     * NOTE: Code-generation-invoked method, method name and parameter order matters
     *
     * @param newEvents            newdata
     * @param newEventsSortKey     newdata sortkey
     * @param oldEvents            olddata
     * @param oldEventsSortKey     olddata sortkey
     * @param selectRStream        rstream flag
     * @param orderByProcessor     ordering
     * @param exprEvaluatorContext ctx
     * @return pair
     */
    public static UniformPair<eu.uk.ncl.pet5o.esper.client.EventBean[]> finalizeOutputMaySortMayRStream(List<EventBean> newEvents, List<Object> newEventsSortKey, List<EventBean> oldEvents, List<Object> oldEventsSortKey, boolean selectRStream, OrderByProcessor orderByProcessor, ExprEvaluatorContext exprEvaluatorContext) {
        eu.uk.ncl.pet5o.esper.client.EventBean[] newEventsArr = CollectionUtil.toArrayNullForEmptyEvents(newEvents);
        eu.uk.ncl.pet5o.esper.client.EventBean[] oldEventsArr = null;
        if (selectRStream) {
            oldEventsArr = CollectionUtil.toArrayNullForEmptyEvents(oldEvents);
        }

        if (orderByProcessor != null) {
            Object[] sortKeysNew = CollectionUtil.toArrayNullForEmptyObjects(newEventsSortKey);
            newEventsArr = orderByProcessor.sortWOrderKeys(newEventsArr, sortKeysNew, exprEvaluatorContext);
            if (selectRStream) {
                Object[] sortKeysOld = CollectionUtil.toArrayNullForEmptyObjects(oldEventsSortKey);
                oldEventsArr = orderByProcessor.sortWOrderKeys(oldEventsArr, sortKeysOld, exprEvaluatorContext);
            }
        }

        return ResultSetProcessorUtil.toPairNullIfAllNull(newEventsArr, oldEventsArr);
    }

    public static void finalizeOutputMaySortMayRStreamCodegen(CodegenBlock block, CodegenExpressionRef newEvents, CodegenExpressionRef newEventsSortKey, CodegenExpressionRef oldEvents, CodegenExpressionRef oldEventsSortKey, boolean selectRStream, boolean hasOrderBy) {
        block.declareVar(eu.uk.ncl.pet5o.esper.client.EventBean[].class, "newEventsArr", staticMethod(CollectionUtil.class, METHOD_TOARRAYNULLFOREMPTYEVENTS, newEvents))
                .declareVar(eu.uk.ncl.pet5o.esper.client.EventBean[].class, "oldEventsArr", selectRStream ? staticMethod(CollectionUtil.class, METHOD_TOARRAYNULLFOREMPTYEVENTS, oldEvents) : constantNull());

        if (hasOrderBy) {
            block.declareVar(Object[].class, "sortKeysNew", staticMethod(CollectionUtil.class, METHOD_TOARRAYNULLFOREMPTYOBJECTS, newEventsSortKey))
                    .assignRef("newEventsArr", exprDotMethod(REF_ORDERBYPROCESSOR, "sortWOrderKeys", ref("newEventsArr"), ref("sortKeysNew"), REF_AGENTINSTANCECONTEXT));
            if (selectRStream) {
                block.declareVar(Object[].class, "sortKeysOld", staticMethod(CollectionUtil.class, METHOD_TOARRAYNULLFOREMPTYOBJECTS, oldEventsSortKey))
                        .assignRef("oldEventsArr", exprDotMethod(REF_ORDERBYPROCESSOR, "sortWOrderKeys", ref("oldEventsArr"), ref("sortKeysOld"), REF_AGENTINSTANCECONTEXT));
            }
        }

        block.returnMethodOrBlock(staticMethod(ResultSetProcessorUtil.class, METHOD_TOPAIRNULLIFALLNULL, ref("newEventsArr"), ref("oldEventsArr")));
    }

    public static void prefixCodegenNewOldEvents(CodegenBlock block, boolean sorting, boolean selectRStream) {
        block.declareVar(List.class, "newEvents", newInstance(ArrayList.class))
                .declareVar(List.class, "oldEvents", selectRStream ? newInstance(ArrayList.class) : constantNull());

        block.declareVar(List.class, "newEventsSortKey", constantNull())
                .declareVar(List.class, "oldEventsSortKey", constantNull());
        if (sorting) {
            block.assignRef("newEventsSortKey", newInstance(ArrayList.class))
                    .assignRef("oldEventsSortKey", selectRStream ? newInstance(ArrayList.class) : constantNull());
        }
    }

    /**
     * NOTE: Code-generation-invoked method, method name and parameter order matters
     * @param istream istream event
     * @param rstream rstream event
     * @return pair
     */
    public static UniformPair<eu.uk.ncl.pet5o.esper.client.EventBean[]> toPairNullIfAllNullSingle(eu.uk.ncl.pet5o.esper.client.EventBean istream, eu.uk.ncl.pet5o.esper.client.EventBean rstream) {
        if (istream != null) {
            return new UniformPair<>(new eu.uk.ncl.pet5o.esper.client.EventBean[] {istream}, rstream == null ? null : new eu.uk.ncl.pet5o.esper.client.EventBean[] {rstream});
        }
        return rstream == null ? null : new UniformPair<>(null, new eu.uk.ncl.pet5o.esper.client.EventBean[] {rstream});
    }

    /**
     * NOTE: Code-generation-invoked method, method name and parameter order matters
     * @param istream istream event
     * @return pair
     */
    public static UniformPair<eu.uk.ncl.pet5o.esper.client.EventBean[]> toPairNullIfNullIStream(eu.uk.ncl.pet5o.esper.client.EventBean istream) {
        return istream == null ? null : new UniformPair<>(new eu.uk.ncl.pet5o.esper.client.EventBean[] {istream}, null);
    }

    public static boolean evaluateHavingClause(ExprEvaluator havingEval, eu.uk.ncl.pet5o.esper.client.EventBean[] eventsPerStream, boolean isNewData, ExprEvaluatorContext exprEvaluatorContext) {
        if (InstrumentationHelper.ENABLED) {
            InstrumentationHelper.get().qHavingClause(eventsPerStream);
        }
        Boolean pass = (Boolean) havingEval.evaluate(eventsPerStream, isNewData, exprEvaluatorContext);
        if (InstrumentationHelper.ENABLED) {
            InstrumentationHelper.get().aHavingClause(pass);
        }
        return pass == null ? false : pass;
    }
}
