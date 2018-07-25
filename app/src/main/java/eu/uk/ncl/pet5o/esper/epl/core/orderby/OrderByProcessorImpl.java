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
package eu.uk.ncl.pet5o.esper.epl.core.orderby;

import eu.uk.ncl.pet5o.esper.codegen.base.CodegenBlock;
import eu.uk.ncl.pet5o.esper.codegen.base.CodegenClassScope;
import eu.uk.ncl.pet5o.esper.codegen.base.CodegenMember;
import eu.uk.ncl.pet5o.esper.codegen.base.CodegenMethodNode;
import eu.uk.ncl.pet5o.esper.codegen.core.CodegenNamedMethods;
import eu.uk.ncl.pet5o.esper.codegen.core.CodegenNamedParam;
import eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpression;
import eu.uk.ncl.pet5o.esper.collection.HashableMultiKey;
import eu.uk.ncl.pet5o.esper.core.context.util.AgentInstanceContext;
import eu.uk.ncl.pet5o.esper.epl.agg.rollup.GroupByRollupKey;
import eu.uk.ncl.pet5o.esper.epl.agg.service.common.AggregationGroupByRollupLevel;
import eu.uk.ncl.pet5o.esper.epl.agg.service.common.AggregationService;
import eu.uk.ncl.pet5o.esper.epl.core.resultset.codegen.ResultSetProcessorCodegenNames;
import eu.uk.ncl.pet5o.esper.epl.core.resultset.core.ResultSetProcessorUtil;
import eu.uk.ncl.pet5o.esper.epl.enummethod.codegen.EnumForgeCodegenNames;
import eu.uk.ncl.pet5o.esper.epl.expression.codegen.CodegenLegoMethodExpression;
import eu.uk.ncl.pet5o.esper.epl.expression.core.ExprEvaluatorContext;
import eu.uk.ncl.pet5o.esper.metrics.instrumentation.InstrumentationHelper;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.function.Consumer;

import static eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionBuilder.*;
import static eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionBuilder.arrayAtIndex;
import static eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionBuilder.arrayLength;
import static eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionBuilder.constant;
import static eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionBuilder.constantNull;
import static eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionBuilder.equalsNull;
import static eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionBuilder.exprDotMethod;
import static eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionBuilder.exprDotMethodChain;
import static eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionBuilder.localMethod;
import static eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionBuilder.member;
import static eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionBuilder.newArrayByLength;
import static eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionBuilder.newArrayWithInit;
import static eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionBuilder.or;
import static eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionBuilder.ref;
import static eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionBuilder.relational;
import static eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionBuilder.staticMethod;
import static eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionRelational.CodegenRelational.*;
import static eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionRelational.CodegenRelational.GT;
import static eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionRelational.CodegenRelational.LE;
import static eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionRelational.CodegenRelational.LT;
import static eu.uk.ncl.pet5o.esper.epl.core.orderby.OrderByProcessorCodegenNames.*;
import static eu.uk.ncl.pet5o.esper.epl.core.orderby.OrderByProcessorCodegenNames.REF_GENERATINGEVENTS;
import static eu.uk.ncl.pet5o.esper.epl.core.orderby.OrderByProcessorCodegenNames.REF_ORDERCURRENTGENERATORS;
import static eu.uk.ncl.pet5o.esper.epl.core.orderby.OrderByProcessorCodegenNames.REF_ORDERFIRSTEVENT;
import static eu.uk.ncl.pet5o.esper.epl.core.orderby.OrderByProcessorCodegenNames.REF_ORDERFIRSTSORTKEY;
import static eu.uk.ncl.pet5o.esper.epl.core.orderby.OrderByProcessorCodegenNames.REF_ORDERGROUPBYKEYS;
import static eu.uk.ncl.pet5o.esper.epl.core.orderby.OrderByProcessorCodegenNames.REF_ORDERKEYS;
import static eu.uk.ncl.pet5o.esper.epl.core.orderby.OrderByProcessorCodegenNames.REF_ORDERROLLUPLEVEL;
import static eu.uk.ncl.pet5o.esper.epl.core.orderby.OrderByProcessorCodegenNames.REF_ORDERSECONDEVENT;
import static eu.uk.ncl.pet5o.esper.epl.core.orderby.OrderByProcessorCodegenNames.REF_ORDERSECONDSORTKEY;
import static eu.uk.ncl.pet5o.esper.epl.core.orderby.OrderByProcessorCodegenNames.REF_OUTGOINGEVENTS;
import static eu.uk.ncl.pet5o.esper.epl.core.orderby.OrderByProcessorUtil.sortGivenOutgoingAndSortKeys;
import static eu.uk.ncl.pet5o.esper.epl.core.resultset.codegen.ResultSetProcessorCodegenNames.*;
import static eu.uk.ncl.pet5o.esper.epl.core.resultset.codegen.ResultSetProcessorCodegenNames.REF_AGENTINSTANCECONTEXT;
import static eu.uk.ncl.pet5o.esper.epl.core.resultset.codegen.ResultSetProcessorCodegenNames.REF_AGGREGATIONSVC;
import static eu.uk.ncl.pet5o.esper.epl.core.resultset.codegen.ResultSetProcessorCodegenNames.REF_ISNEWDATA;
import static eu.uk.ncl.pet5o.esper.epl.enummethod.codegen.EnumForgeCodegenNames.REF_EPS;
import static eu.uk.ncl.pet5o.esper.epl.expression.codegen.ExprForgeCodegenNames.*;
import static eu.uk.ncl.pet5o.esper.epl.expression.codegen.ExprForgeCodegenNames.NAME_EPS;
import static eu.uk.ncl.pet5o.esper.epl.expression.codegen.ExprForgeCodegenNames.NAME_EXPREVALCONTEXT;
import static eu.uk.ncl.pet5o.esper.epl.expression.codegen.ExprForgeCodegenNames.NAME_ISNEWDATA;
import static eu.uk.ncl.pet5o.esper.epl.expression.codegen.ExprForgeCodegenNames.REF_EXPREVALCONTEXT;

/**
 * An order-by processor that sorts events according to the expressions
 * in the order_by clause.
 */
public class OrderByProcessorImpl implements OrderByProcessor {

    private final OrderByProcessorFactoryImpl factory;

    public OrderByProcessorImpl(OrderByProcessorFactoryImpl factory) {
        this.factory = factory;
    }

    public Object getSortKey(eu.uk.ncl.pet5o.esper.client.EventBean[] eventsPerStream, boolean isNewData, ExprEvaluatorContext exprEvaluatorContext) {
        return getSortKeyInternal(eventsPerStream, isNewData, exprEvaluatorContext, factory.getOrderBy());
    }

    public static void getSortKeyCodegen(OrderByProcessorForgeImpl forge, CodegenMethodNode method, CodegenClassScope classScope, CodegenNamedMethods namedMethods) {
        CodegenMethodNode getSortKey = generateOrderKeyCodegen("getSortKeyInternal", forge.getOrderBy(), classScope, namedMethods);
        method.getBlock().methodReturn(localMethod(getSortKey, REF_EPS, REF_ISNEWDATA, REF_EXPREVALCONTEXT));
    }

    public Object getSortKeyRollup(eu.uk.ncl.pet5o.esper.client.EventBean[] eventsPerStream, boolean isNewData, ExprEvaluatorContext exprEvaluatorContext, AggregationGroupByRollupLevel level) {
        return getSortKeyInternal(eventsPerStream, isNewData, exprEvaluatorContext, factory.getOrderByRollup()[level.getLevelNumber()]);
    }

    public static void getSortKeyRollupCodegen(OrderByProcessorForgeImpl forge, CodegenMethodNode method, CodegenClassScope classScope, CodegenNamedMethods namedMethods) {
        method.getBlock().declareVar(int.class, "num", exprDotMethod(REF_ORDERROLLUPLEVEL, "getLevelNumber"));
        CodegenBlock[] blocks = method.getBlock().switchBlockOfLength("num", forge.getOrderByRollup().length, true);
        for (int i = 0; i < blocks.length; i++) {
            CodegenMethodNode getSortKey = generateOrderKeyCodegen("getSortKeyInternal_" + i, forge.getOrderByRollup()[i], classScope, namedMethods);
            blocks[i].blockReturn(localMethod(getSortKey, REF_EPS, REF_ISNEWDATA, REF_EXPREVALCONTEXT));
        }
    }

    private static Object getSortKeyInternal(eu.uk.ncl.pet5o.esper.client.EventBean[] eventsPerStream, boolean isNewData, ExprEvaluatorContext exprEvaluatorContext, OrderByElementEval[] elements) {
        if (InstrumentationHelper.ENABLED) {
            InstrumentationHelper.get().qOrderBy(eventsPerStream, elements);
        }

        if (elements.length == 1) {
            if (InstrumentationHelper.ENABLED) {
                Object value = elements[0].getExpr().evaluate(eventsPerStream, isNewData, exprEvaluatorContext);
                InstrumentationHelper.get().aOrderBy(value);
                return value;
            }
            return elements[0].getExpr().evaluate(eventsPerStream, isNewData, exprEvaluatorContext);
        }

        Object[] values = new Object[elements.length];
        int count = 0;
        for (OrderByElementEval sortPair : elements) {
            values[count++] = sortPair.getExpr().evaluate(eventsPerStream, isNewData, exprEvaluatorContext);
        }

        if (InstrumentationHelper.ENABLED) {
            InstrumentationHelper.get().aOrderBy(values);
        }
        return new HashableMultiKey(values);
    }

    public eu.uk.ncl.pet5o.esper.client.EventBean[] sortPlain(eu.uk.ncl.pet5o.esper.client.EventBean[] outgoingEvents, eu.uk.ncl.pet5o.esper.client.EventBean[][] generatingEvents, boolean isNewData, ExprEvaluatorContext exprEvaluatorContext, AggregationService aggregationService) {
        if (outgoingEvents == null || outgoingEvents.length < 2) {
            return outgoingEvents;
        }

        return sortWGroupKeysInternal(outgoingEvents, generatingEvents, null, isNewData, exprEvaluatorContext, aggregationService);
    }

    public eu.uk.ncl.pet5o.esper.client.EventBean[] sortRollup(eu.uk.ncl.pet5o.esper.client.EventBean[] outgoingEvents, List<GroupByRollupKey> currentGenerators, boolean newData, AgentInstanceContext agentInstanceContext, AggregationService aggregationService) {
        List<Object> sortValuesMultiKeys = createSortPropertiesWRollup(currentGenerators, factory.getOrderByRollup(), newData, agentInstanceContext, aggregationService);
        return sortGivenOutgoingAndSortKeys(outgoingEvents, sortValuesMultiKeys, factory.getComparator());
    }

    static void sortPlainCodegen(OrderByProcessorForgeImpl forge, CodegenMethodNode method, CodegenClassScope classScope, CodegenNamedMethods namedMethods) {
        CodegenMethodNode node = sortWGroupKeysInternalCodegen(forge, classScope, namedMethods);
        method.getBlock().ifCondition(or(equalsNull(REF_OUTGOINGEVENTS), relational(arrayLength(REF_OUTGOINGEVENTS), LT, constant(2))))
                .blockReturn(REF_OUTGOINGEVENTS);

        method.getBlock().methodReturn(localMethod(node, REF_OUTGOINGEVENTS, REF_GENERATINGEVENTS, constantNull(), REF_ISNEWDATA, REF_EXPREVALCONTEXT, REF_AGGREGATIONSVC));
    }

    static void sortRollupCodegen(OrderByProcessorForgeImpl forge, CodegenMethodNode method, CodegenClassScope classScope, CodegenNamedMethods namedMethods) {
        CodegenMethodNode createSortPropertiesWRollup = createSortPropertiesWRollupCodegen(forge, classScope, namedMethods);
        CodegenMember comparator = classScope.makeAddMember(Comparator.class, forge.getComparator());
        method.getBlock().declareVar(List.class, "sortValuesMultiKeys", localMethod(createSortPropertiesWRollup, REF_ORDERCURRENTGENERATORS, REF_ISNEWDATA, REF_AGENTINSTANCECONTEXT, REF_AGGREGATIONSVC))
                .methodReturn(staticMethod(OrderByProcessorUtil.class, "sortGivenOutgoingAndSortKeys", REF_OUTGOINGEVENTS, ref("sortValuesMultiKeys"), member(comparator.getMemberId())));
    }

    public eu.uk.ncl.pet5o.esper.client.EventBean[] sortWGroupKeys(eu.uk.ncl.pet5o.esper.client.EventBean[] outgoingEvents, eu.uk.ncl.pet5o.esper.client.EventBean[][] generatingEvents, Object[] groupByKeys, boolean isNewData, ExprEvaluatorContext exprEvaluatorContext, AggregationService aggregationService) {
        if (outgoingEvents == null || outgoingEvents.length < 2) {
            return outgoingEvents;
        }
        return sortWGroupKeysInternal(outgoingEvents, generatingEvents, groupByKeys, isNewData, exprEvaluatorContext, aggregationService);
    }

    static void sortWGroupKeysCodegen(OrderByProcessorForgeImpl forge, CodegenMethodNode method, CodegenClassScope classScope, CodegenNamedMethods namedMethods) {
        CodegenMethodNode sortWGroupKeysInternal = sortWGroupKeysInternalCodegen(forge, classScope, namedMethods);
        method.getBlock().ifCondition(or(equalsNull(REF_OUTGOINGEVENTS), relational(arrayLength(REF_OUTGOINGEVENTS), LT, constant(2))))
                .blockReturn(REF_OUTGOINGEVENTS)
                .methodReturn(localMethod(sortWGroupKeysInternal, REF_OUTGOINGEVENTS, REF_GENERATINGEVENTS, REF_ORDERGROUPBYKEYS, REF_ISNEWDATA, REF_EXPREVALCONTEXT, REF_AGGREGATIONSVC));
    }

    private eu.uk.ncl.pet5o.esper.client.EventBean[] sortWGroupKeysInternal(eu.uk.ncl.pet5o.esper.client.EventBean[] outgoingEvents, eu.uk.ncl.pet5o.esper.client.EventBean[][] generatingEvents, Object[] groupByKeys, boolean isNewData, ExprEvaluatorContext exprEvaluatorContext, AggregationService aggregationService) {
        List<Object> sortValuesMultiKeys = createSortProperties(generatingEvents, groupByKeys, isNewData, exprEvaluatorContext, aggregationService);
        return sortGivenOutgoingAndSortKeys(outgoingEvents, sortValuesMultiKeys, factory.getComparator());
    }

    private static CodegenMethodNode sortWGroupKeysInternalCodegen(OrderByProcessorForgeImpl forge, CodegenClassScope classScope, CodegenNamedMethods namedMethods) {
        CodegenMethodNode createSortProperties = createSortPropertiesCodegen(forge, classScope, namedMethods);
        CodegenMember comparator = classScope.makeAddMember(Comparator.class, forge.getComparator());
        Consumer<CodegenMethodNode> code = method -> {
            method.getBlock().declareVar(List.class, "sortValuesMultiKeys", localMethod(createSortProperties, REF_GENERATINGEVENTS, ref("groupByKeys"), REF_ISNEWDATA, REF_EXPREVALCONTEXT, REF_AGGREGATIONSVC))
                    .methodReturn(staticMethod(OrderByProcessorUtil.class, "sortGivenOutgoingAndSortKeys", REF_OUTGOINGEVENTS, ref("sortValuesMultiKeys"), member(comparator.getMemberId())));
        };
        return namedMethods.addMethod(eu.uk.ncl.pet5o.esper.client.EventBean[].class, "sortWGroupKeysInternal", CodegenNamedParam.from(eu.uk.ncl.pet5o.esper.client.EventBean[].class, REF_OUTGOINGEVENTS.getRef(), eu.uk.ncl.pet5o.esper.client.EventBean[][].class, REF_GENERATINGEVENTS.getRef(),
                Object[].class, "groupByKeys", boolean.class, REF_ISNEWDATA.getRef(), ExprEvaluatorContext.class, REF_EXPREVALCONTEXT.getRef(), AggregationService.class, REF_AGGREGATIONSVC.getRef()), OrderByProcessorImpl.class, classScope, code);
    }

    private List<Object> createSortProperties(eu.uk.ncl.pet5o.esper.client.EventBean[][] generatingEvents, Object[] groupByKeys, boolean isNewData, ExprEvaluatorContext exprEvaluatorContext, AggregationService aggregationService) {
        Object[] sortProperties = new Object[generatingEvents.length];

        OrderByElementEval[] elements = factory.getOrderBy();
        if (elements.length == 1) {
            int count = 0;
            for (eu.uk.ncl.pet5o.esper.client.EventBean[] eventsPerStream : generatingEvents) {
                // Make a new multikey that contains the sort-by values.
                if (factory.isNeedsGroupByKeys()) {
                    aggregationService.setCurrentAccess(groupByKeys[count], exprEvaluatorContext.getAgentInstanceId(), null);
                }

                if (InstrumentationHelper.ENABLED) {
                    InstrumentationHelper.get().qOrderBy(eventsPerStream, factory.getOrderBy());
                }
                sortProperties[count] = elements[0].getExpr().evaluate(eventsPerStream, isNewData, exprEvaluatorContext);
                if (InstrumentationHelper.ENABLED) {
                    InstrumentationHelper.get().aOrderBy(sortProperties[count]);
                }
                count++;
            }
        } else {
            int count = 0;
            for (eu.uk.ncl.pet5o.esper.client.EventBean[] eventsPerStream : generatingEvents) {
                // Make a new multikey that contains the sort-by values.
                if (factory.isNeedsGroupByKeys()) {
                    aggregationService.setCurrentAccess(groupByKeys[count], exprEvaluatorContext.getAgentInstanceId(), null);
                }

                Object[] values = new Object[factory.getOrderBy().length];
                int countTwo = 0;
                if (InstrumentationHelper.ENABLED) {
                    InstrumentationHelper.get().qOrderBy(eventsPerStream, factory.getOrderBy());
                }
                for (OrderByElementEval sortPair : factory.getOrderBy()) {
                    values[countTwo++] = sortPair.getExpr().evaluate(eventsPerStream, isNewData, exprEvaluatorContext);
                }
                if (InstrumentationHelper.ENABLED) {
                    InstrumentationHelper.get().aOrderBy(values);
                }

                sortProperties[count] = new HashableMultiKey(values);
                count++;
            }
        }
        return Arrays.asList(sortProperties);
    }

    private static CodegenMethodNode createSortPropertiesCodegen(OrderByProcessorForgeImpl forge, CodegenClassScope classScope, CodegenNamedMethods namedMethods) {
        Consumer<CodegenMethodNode> code = method -> {

            method.getBlock().declareVar(Object[].class, "sortProperties", newArrayByLength(Object.class, arrayLength(REF_GENERATINGEVENTS)));

            OrderByElementForge[] elements = forge.getOrderBy();
            CodegenBlock forEach = method.getBlock().declareVar(int.class, "count", constant(0))
                    .forEach(eu.uk.ncl.pet5o.esper.client.EventBean[].class, "eventsPerStream", REF_GENERATINGEVENTS);

            if (forge.isNeedsGroupByKeys()) {
                forEach.exprDotMethod(REF_AGGREGATIONSVC, "setCurrentAccess", arrayAtIndex(ref("groupByKeys"), ref("count")), exprDotMethod(REF_EXPREVALCONTEXT, "getAgentInstanceId"), constantNull());
            }

            if (elements.length == 1) {
                forEach.assignArrayElement("sortProperties", ref("count"), localMethod(CodegenLegoMethodExpression.codegenExpression(elements[0].getExprNode().getForge(), method, classScope), ref("eventsPerStream"), REF_ISNEWDATA, REF_EXPREVALCONTEXT));
            } else {
                forEach.declareVar(Object[].class, "values", newArrayByLength(Object.class, constant(forge.getOrderBy().length)));
                for (int i = 0; i < forge.getOrderBy().length; i++) {
                    forEach.assignArrayElement("values", constant(i), localMethod(CodegenLegoMethodExpression.codegenExpression(elements[i].getExprNode().getForge(), method, classScope), ref("eventsPerStream"), REF_ISNEWDATA, REF_EXPREVALCONTEXT));
                }
                forEach.assignArrayElement("sortProperties", ref("count"), newInstance(HashableMultiKey.class, ref("values")));
            }
            forEach.increment("count");
            method.getBlock().methodReturn(staticMethod(Arrays.class, "asList", ref("sortProperties")));
        };
        return namedMethods.addMethod(List.class, "createSortProperties", CodegenNamedParam.from(eu.uk.ncl.pet5o.esper.client.EventBean[][].class, REF_GENERATINGEVENTS.getRef(),
                Object[].class, "groupByKeys", boolean.class, REF_ISNEWDATA.getRef(), ExprEvaluatorContext.class, REF_EXPREVALCONTEXT.getRef(), AggregationService.class, REF_AGGREGATIONSVC.getRef()), OrderByProcessorImpl.class, classScope, code);
    }

    public eu.uk.ncl.pet5o.esper.client.EventBean[] sortWOrderKeys(eu.uk.ncl.pet5o.esper.client.EventBean[] outgoingEvents, Object[] orderKeys, ExprEvaluatorContext exprEvaluatorContext) {
        return OrderByProcessorUtil.sortWOrderKeys(outgoingEvents, orderKeys, factory.getComparator());
    }

    static void sortWOrderKeysCodegen(OrderByProcessorForgeImpl forge, CodegenMethodNode method, CodegenClassScope classScope) {
        CodegenMember comparator = classScope.makeAddMember(Comparator.class, forge.getComparator());
        method.getBlock().methodReturn(staticMethod(OrderByProcessorUtil.class, "sortWOrderKeys", REF_OUTGOINGEVENTS, REF_ORDERKEYS, member(comparator.getMemberId())));
    }

    public eu.uk.ncl.pet5o.esper.client.EventBean[] sortTwoKeys(eu.uk.ncl.pet5o.esper.client.EventBean first, Object sortKeyFirst, eu.uk.ncl.pet5o.esper.client.EventBean second, Object sortKeySecond) {
        if (factory.getComparator().compare(sortKeyFirst, sortKeySecond) <= 0) {
            return new eu.uk.ncl.pet5o.esper.client.EventBean[] {first, second};
        }
        return new eu.uk.ncl.pet5o.esper.client.EventBean[] {second, first};
    }

    static void sortTwoKeysCodegen(OrderByProcessorForgeImpl forge, CodegenMethodNode method, CodegenClassScope classScope, CodegenNamedMethods namedMethods) {
        CodegenMember comparator = classScope.makeAddMember(Comparator.class, forge.getComparator());
        CodegenExpression compare = exprDotMethod(member(comparator.getMemberId()), "compare", REF_ORDERFIRSTSORTKEY, REF_ORDERSECONDSORTKEY);
        method.getBlock().ifCondition(relational(compare, LE, constant(0)))
                .blockReturn(newArrayWithInit(eu.uk.ncl.pet5o.esper.client.EventBean.class, REF_ORDERFIRSTEVENT, REF_ORDERSECONDEVENT))
                .methodReturn(newArrayWithInit(eu.uk.ncl.pet5o.esper.client.EventBean.class, REF_ORDERSECONDEVENT, REF_ORDERFIRSTEVENT));
    }

    private List<Object> createSortPropertiesWRollup(List<GroupByRollupKey> currentGenerators, OrderByElementEval[][] elementsPerLevel, boolean isNewData, AgentInstanceContext exprEvaluatorContext, AggregationService aggregationService) {
        Object[] sortProperties = new Object[currentGenerators.size()];

        OrderByElementEval[] elements = factory.getOrderBy();
        if (elements.length == 1) {
            int count = 0;
            for (GroupByRollupKey rollup : currentGenerators) {

                // Make a new multikey that contains the sort-by values.
                if (factory.isNeedsGroupByKeys()) {
                    aggregationService.setCurrentAccess(rollup.getGroupKey(), exprEvaluatorContext.getAgentInstanceId(), rollup.getLevel());
                }

                if (InstrumentationHelper.ENABLED) {
                    InstrumentationHelper.get().qOrderBy(rollup.getGenerator(), factory.getOrderBy());
                }
                sortProperties[count] = elementsPerLevel[rollup.getLevel().getLevelNumber()][0].getExpr().evaluate(rollup.getGenerator(), isNewData, exprEvaluatorContext);
                if (InstrumentationHelper.ENABLED) {
                    InstrumentationHelper.get().aOrderBy(sortProperties[count]);
                }

                count++;
            }
        } else {
            int count = 0;
            for (GroupByRollupKey rollup : currentGenerators) {

                // Make a new multikey that contains the sort-by values.
                if (factory.isNeedsGroupByKeys()) {
                    aggregationService.setCurrentAccess(rollup.getGroupKey(), exprEvaluatorContext.getAgentInstanceId(), rollup.getLevel());
                }

                Object[] values = new Object[factory.getOrderBy().length];
                int countTwo = 0;
                if (InstrumentationHelper.ENABLED) {
                    InstrumentationHelper.get().qOrderBy(rollup.getGenerator(), factory.getOrderBy());
                }
                for (OrderByElementEval sortPair : elementsPerLevel[rollup.getLevel().getLevelNumber()]) {
                    values[countTwo++] = sortPair.getExpr().evaluate(rollup.getGenerator(), isNewData, exprEvaluatorContext);
                }
                if (InstrumentationHelper.ENABLED) {
                    InstrumentationHelper.get().aOrderBy(values);
                }

                sortProperties[count] = new HashableMultiKey(values);
                count++;
            }
        }
        return Arrays.asList(sortProperties);
    }

    private static CodegenMethodNode createSortPropertiesWRollupCodegen(OrderByProcessorForgeImpl forge, CodegenClassScope classScope, CodegenNamedMethods namedMethods) {
        Consumer<CodegenMethodNode> code = method -> {
            method.getBlock().declareVar(Object[].class, "sortProperties", newArrayByLength(Object.class, exprDotMethod(REF_ORDERCURRENTGENERATORS, "size")))
                    .declareVar(int.class, "count", constant(0));

            CodegenBlock forEach = method.getBlock().forEach(GroupByRollupKey.class, "rollup", REF_ORDERCURRENTGENERATORS);

            if (forge.isNeedsGroupByKeys()) {
                forEach.exprDotMethod(REF_AGGREGATIONSVC, "setCurrentAccess", exprDotMethod(ref("rollup"), "getGroupKey"), exprDotMethod(REF_EXPREVALCONTEXT, "getAgentInstanceId"), exprDotMethod(ref("rollup"), "getLevel"));
            }

            forEach.declareVar(int.class, "num", exprDotMethodChain(ref("rollup")).add("getLevel").add("getLevelNumber"));
            CodegenBlock[] blocks = forEach.switchBlockOfLength("num", forge.getOrderByRollup().length, false);
            for (int i = 0; i < blocks.length; i++) {
                CodegenMethodNode getSortKey = generateOrderKeyCodegen("getSortKeyInternal_" + i, forge.getOrderByRollup()[i], classScope, namedMethods);
                blocks[i].assignArrayElement("sortProperties", ref("count"), localMethod(getSortKey, exprDotMethod(ref("rollup"), "getGenerator"), REF_ISNEWDATA, REF_EXPREVALCONTEXT));
            }

            forEach.increment("count");
            method.getBlock().methodReturn(staticMethod(Arrays.class, "asList", ref("sortProperties")));
        };
        return namedMethods.addMethod(List.class, "createSortPropertiesWRollup", CodegenNamedParam.from(List.class, REF_ORDERCURRENTGENERATORS.getRef(), boolean.class, REF_ISNEWDATA.getRef(), ExprEvaluatorContext.class, REF_EXPREVALCONTEXT.getRef(), AggregationService.class, REF_AGGREGATIONSVC.getRef()), OrderByProcessorImpl.class, classScope, code);
    }

    public eu.uk.ncl.pet5o.esper.client.EventBean determineLocalMinMax(eu.uk.ncl.pet5o.esper.client.EventBean[] outgoingEvents, eu.uk.ncl.pet5o.esper.client.EventBean[][] generatingEvents, boolean isNewData, ExprEvaluatorContext exprEvaluatorContext, AggregationService aggregationService) {
        OrderByElementEval[] elements = factory.getOrderBy();
        Object localMinMax = null;
        eu.uk.ncl.pet5o.esper.client.EventBean outgoingMinMaxBean = null;

        if (elements.length == 1) {
            int count = 0;
            for (eu.uk.ncl.pet5o.esper.client.EventBean[] eventsPerStream : generatingEvents) {

                if (InstrumentationHelper.ENABLED) {
                    InstrumentationHelper.get().qOrderBy(eventsPerStream, factory.getOrderBy());
                }
                Object sortKey = elements[0].getExpr().evaluate(eventsPerStream, isNewData, exprEvaluatorContext);
                if (InstrumentationHelper.ENABLED) {
                    InstrumentationHelper.get().aOrderBy(localMinMax);
                }

                boolean newMinMax = localMinMax == null || factory.getComparator().compare(localMinMax, sortKey) > 0;
                if (newMinMax) {
                    localMinMax = sortKey;
                    outgoingMinMaxBean = outgoingEvents[count];
                }

                count++;
            }
        } else {
            int count = 0;
            Object[] values = new Object[factory.getOrderBy().length];
            HashableMultiKey valuesMk = new HashableMultiKey(values);

            for (eu.uk.ncl.pet5o.esper.client.EventBean[] eventsPerStream : generatingEvents) {

                int countTwo = 0;
                if (InstrumentationHelper.ENABLED) {
                    InstrumentationHelper.get().qOrderBy(eventsPerStream, factory.getOrderBy());
                }
                for (OrderByElementEval sortPair : factory.getOrderBy()) {
                    values[countTwo++] = sortPair.getExpr().evaluate(eventsPerStream, isNewData, exprEvaluatorContext);
                }
                if (InstrumentationHelper.ENABLED) {
                    InstrumentationHelper.get().aOrderBy(values);
                }

                boolean newMinMax = localMinMax == null || factory.getComparator().compare(localMinMax, valuesMk) > 0;
                if (newMinMax) {
                    localMinMax = valuesMk;
                    values = new Object[factory.getOrderBy().length];
                    valuesMk = new HashableMultiKey(values);
                    outgoingMinMaxBean = outgoingEvents[count];
                }

                count++;
            }
        }

        return outgoingMinMaxBean;
    }

    public static CodegenMethodNode determineLocalMinMaxCodegen(OrderByProcessorForgeImpl forge, CodegenClassScope classScope, CodegenNamedMethods namedMethods) {
        OrderByElementForge[] elements = forge.getOrderBy();
        CodegenMember comparator = classScope.makeAddMember(Comparator.class, forge.getComparator());

        Consumer<CodegenMethodNode> code = method -> {
            method.getBlock().declareVar(Object.class, "localMinMax", constantNull())
                    .declareVar(eu.uk.ncl.pet5o.esper.client.EventBean.class, "outgoingMinMaxBean", constantNull())
                    .declareVar(int.class, "count", constant(0));

            if (elements.length == 1) {
                CodegenBlock forEach = method.getBlock().forEach(eu.uk.ncl.pet5o.esper.client.EventBean[].class, "eventsPerStream", REF_GENERATINGEVENTS);

                forEach.declareVar(Object.class, "sortKey", localMethod(CodegenLegoMethodExpression.codegenExpression(elements[0].getExprNode().getForge(), method, classScope), ref("eventsPerStream"), REF_ISNEWDATA, REF_EXPREVALCONTEXT))
                        .ifCondition(or(equalsNull(ref("localMinMax")), relational(exprDotMethod(member(comparator.getMemberId()), "compare", ref("localMinMax"), ref("sortKey")), GT, constant(0))))
                        .assignRef("localMinMax", ref("sortKey"))
                        .assignRef("outgoingMinMaxBean", arrayAtIndex(REF_OUTGOINGEVENTS, ref("count")))
                        .blockEnd()
                        .increment("count");
            } else {
                method.getBlock().declareVar(Object[].class, "values", newArrayByLength(Object.class, constant(elements.length)))
                        .declareVar(HashableMultiKey.class, "valuesMk", newInstance(HashableMultiKey.class, ref("values")));

                CodegenBlock forEach = method.getBlock().forEach(eu.uk.ncl.pet5o.esper.client.EventBean[].class, "eventsPerStream", REF_GENERATINGEVENTS);

                if (forge.isNeedsGroupByKeys()) {
                    forEach.exprDotMethod(REF_AGGREGATIONSVC, "setCurrentAccess", arrayAtIndex(ref("groupByKeys"), ref("count")), exprDotMethod(REF_EXPREVALCONTEXT, "getAgentInstanceId", constantNull()));
                }

                for (int i = 0; i < elements.length; i++) {
                    forEach.assignArrayElement("values", constant(i), localMethod(CodegenLegoMethodExpression.codegenExpression(elements[i].getExprNode().getForge(), method, classScope), ref("eventsPerStream"), REF_ISNEWDATA, REF_EXPREVALCONTEXT));
                }

                forEach.ifCondition(or(equalsNull(ref("localMinMax")), relational(exprDotMethod(member(comparator.getMemberId()), "compare", ref("localMinMax"), ref("valuesMk")), GT, constant(0))))
                        .assignRef("localMinMax", ref("valuesMk"))
                        .assignRef("values", newArrayByLength(Object.class, constant(elements.length)))
                        .assignRef("valuesMk", newInstance(HashableMultiKey.class, ref("values")))
                        .assignRef("outgoingMinMaxBean", arrayAtIndex(REF_OUTGOINGEVENTS, ref("count")))
                        .blockEnd()
                        .increment("count");
            }

            method.getBlock().methodReturn(ref("outgoingMinMaxBean"));
        };

        return namedMethods.addMethod(eu.uk.ncl.pet5o.esper.client.EventBean.class, "determineLocalMinMax", CodegenNamedParam.from(eu.uk.ncl.pet5o.esper.client.EventBean[].class, REF_OUTGOINGEVENTS.getRef(), eu.uk.ncl.pet5o.esper.client.EventBean[][].class, REF_GENERATINGEVENTS.getRef(), boolean.class, NAME_ISNEWDATA, ExprEvaluatorContext.class, NAME_EXPREVALCONTEXT, AggregationService.class, REF_AGGREGATIONSVC.getRef()), OrderByProcessorImpl.class, classScope, code);
    }

    static CodegenMethodNode generateOrderKeyCodegen(String methodName, OrderByElementForge[] orderBy, CodegenClassScope classScope, CodegenNamedMethods namedMethods) {
        Consumer<CodegenMethodNode> code = methodNode -> {
            if (orderBy.length == 1) {
                CodegenMethodNode expression = CodegenLegoMethodExpression.codegenExpression(orderBy[0].getExprNode().getForge(), methodNode, classScope);
                methodNode.getBlock().methodReturn(localMethod(expression, EnumForgeCodegenNames.REF_EPS, ResultSetProcessorCodegenNames.REF_ISNEWDATA, REF_EXPREVALCONTEXT));
                return;
            }

            methodNode.getBlock().declareVar(Object[].class, "keys", newArrayByLength(Object.class, constant(orderBy.length)));
            for (int i = 0; i < orderBy.length; i++) {
                CodegenMethodNode expression = CodegenLegoMethodExpression.codegenExpression(orderBy[i].getExprNode().getForge(), methodNode, classScope);
                methodNode.getBlock().assignArrayElement("keys", constant(i), localMethod(expression, EnumForgeCodegenNames.REF_EPS, ResultSetProcessorCodegenNames.REF_ISNEWDATA, REF_EXPREVALCONTEXT));
            }
            methodNode.getBlock().methodReturn(newInstance(HashableMultiKey.class, ref("keys")));
        };

        return namedMethods.addMethod(Object.class, methodName, CodegenNamedParam.from(eu.uk.ncl.pet5o.esper.client.EventBean[].class, NAME_EPS, boolean.class, NAME_ISNEWDATA, ExprEvaluatorContext.class, NAME_EXPREVALCONTEXT), ResultSetProcessorUtil.class, classScope, code);
    }

    public Comparator<Object> getComparator() {
        return factory.getComparator();
    }
}
