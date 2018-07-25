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

import eu.uk.ncl.pet5o.esper.codegen.base.CodegenClassScope;
import eu.uk.ncl.pet5o.esper.codegen.base.CodegenMember;
import eu.uk.ncl.pet5o.esper.codegen.base.CodegenMethodNode;
import eu.uk.ncl.pet5o.esper.codegen.core.CodegenNamedMethods;
import eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpression;
import eu.uk.ncl.pet5o.esper.core.context.util.AgentInstanceContext;
import eu.uk.ncl.pet5o.esper.epl.agg.rollup.GroupByRollupKey;
import eu.uk.ncl.pet5o.esper.epl.agg.service.common.AggregationGroupByRollupLevel;
import eu.uk.ncl.pet5o.esper.epl.agg.service.common.AggregationService;
import eu.uk.ncl.pet5o.esper.epl.expression.core.ExprEvaluatorContext;

import java.util.Comparator;
import java.util.List;

import static eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionBuilder.*;
import static eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionBuilder.and;
import static eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionBuilder.arrayLength;
import static eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionBuilder.constant;
import static eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionBuilder.equalsIdentity;
import static eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionBuilder.exprDotMethod;
import static eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionBuilder.localMethod;
import static eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionBuilder.member;
import static eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionBuilder.notEqualsNull;
import static eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionBuilder.ref;
import static eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionBuilder.relational;
import static eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionBuilder.staticMethod;
import static eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionRelational.CodegenRelational.GT;
import static eu.uk.ncl.pet5o.esper.epl.core.orderby.OrderByProcessorCodegenNames.*;
import static eu.uk.ncl.pet5o.esper.epl.core.orderby.OrderByProcessorCodegenNames.REF_GENERATINGEVENTS;
import static eu.uk.ncl.pet5o.esper.epl.core.orderby.OrderByProcessorCodegenNames.REF_ORDERCURRENTGENERATORS;
import static eu.uk.ncl.pet5o.esper.epl.core.orderby.OrderByProcessorCodegenNames.REF_ORDERFIRSTEVENT;
import static eu.uk.ncl.pet5o.esper.epl.core.orderby.OrderByProcessorCodegenNames.REF_ORDERFIRSTSORTKEY;
import static eu.uk.ncl.pet5o.esper.epl.core.orderby.OrderByProcessorCodegenNames.REF_ORDERGROUPBYKEYS;
import static eu.uk.ncl.pet5o.esper.epl.core.orderby.OrderByProcessorCodegenNames.REF_ORDERKEYS;
import static eu.uk.ncl.pet5o.esper.epl.core.orderby.OrderByProcessorCodegenNames.REF_ORDERSECONDEVENT;
import static eu.uk.ncl.pet5o.esper.epl.core.orderby.OrderByProcessorCodegenNames.REF_ORDERSECONDSORTKEY;
import static eu.uk.ncl.pet5o.esper.epl.core.orderby.OrderByProcessorCodegenNames.REF_OUTGOINGEVENTS;
import static eu.uk.ncl.pet5o.esper.epl.core.orderby.OrderByProcessorCodegenNames.SORTPLAIN_PARAMS;
import static eu.uk.ncl.pet5o.esper.epl.core.orderby.OrderByProcessorCodegenNames.SORTROLLUP_PARAMS;
import static eu.uk.ncl.pet5o.esper.epl.core.orderby.OrderByProcessorCodegenNames.SORTTWOKEYS_PARAMS;
import static eu.uk.ncl.pet5o.esper.epl.core.orderby.OrderByProcessorCodegenNames.SORTWGROUPKEYS_PARAMS;
import static eu.uk.ncl.pet5o.esper.epl.core.orderby.OrderByProcessorOrderedLimitForge.REF_ROWLIMITPROCESSOR;
import static eu.uk.ncl.pet5o.esper.epl.core.resultset.codegen.ResultSetProcessorCodegenNames.*;
import static eu.uk.ncl.pet5o.esper.epl.core.resultset.codegen.ResultSetProcessorCodegenNames.REF_AGENTINSTANCECONTEXT;
import static eu.uk.ncl.pet5o.esper.epl.core.resultset.codegen.ResultSetProcessorCodegenNames.REF_AGGREGATIONSVC;
import static eu.uk.ncl.pet5o.esper.epl.core.resultset.codegen.ResultSetProcessorCodegenNames.REF_ISNEWDATA;
import static eu.uk.ncl.pet5o.esper.epl.expression.codegen.ExprForgeCodegenNames.REF_EXPREVALCONTEXT;

/**
 * Sorter and row limiter in one: sorts using a sorter and row limits
 */
public class OrderByProcessorOrderedLimit implements OrderByProcessor {
    private final OrderByProcessorImpl orderByProcessor;
    private final RowLimitProcessor rowLimitProcessor;

    /**
     * Ctor.
     *
     * @param orderByProcessor  the sorter
     * @param rowLimitProcessor the row limiter
     */
    public OrderByProcessorOrderedLimit(OrderByProcessorImpl orderByProcessor, RowLimitProcessor rowLimitProcessor) {
        this.orderByProcessor = orderByProcessor;
        this.rowLimitProcessor = rowLimitProcessor;
    }

    public eu.uk.ncl.pet5o.esper.client.EventBean[] sortPlain(eu.uk.ncl.pet5o.esper.client.EventBean[] outgoingEvents, eu.uk.ncl.pet5o.esper.client.EventBean[][] generatingEvents, boolean isNewData, ExprEvaluatorContext exprEvaluatorContext, AggregationService aggregationService) {
        rowLimitProcessor.determineCurrentLimit();

        if (rowLimitProcessor.getCurrentRowLimit() == 1 &&
                rowLimitProcessor.getCurrentOffset() == 0 &&
                outgoingEvents != null && outgoingEvents.length > 1) {
            eu.uk.ncl.pet5o.esper.client.EventBean minmax = orderByProcessor.determineLocalMinMax(outgoingEvents, generatingEvents, isNewData, exprEvaluatorContext, aggregationService);
            return new eu.uk.ncl.pet5o.esper.client.EventBean[]{minmax};
        }

        eu.uk.ncl.pet5o.esper.client.EventBean[] sorted = orderByProcessor.sortPlain(outgoingEvents, generatingEvents, isNewData, exprEvaluatorContext, aggregationService);
        return rowLimitProcessor.applyLimit(sorted);
    }

    static void sortPlainCodegenCodegen(OrderByProcessorOrderedLimitForge forge, CodegenMethodNode method, CodegenClassScope classScope, CodegenNamedMethods namedMethods) {
        CodegenExpression limit1 = equalsIdentity(exprDotMethod(REF_ROWLIMITPROCESSOR, "getCurrentRowLimit"), constant(1));
        CodegenExpression offset0 = equalsIdentity(exprDotMethod(REF_ROWLIMITPROCESSOR, "getCurrentOffset"), constant(0));
        CodegenExpression haveOutgoing = and(notEqualsNull(REF_OUTGOINGEVENTS), relational(arrayLength(REF_OUTGOINGEVENTS), GT, constant(1)));
        CodegenMethodNode determineLocalMinMax = OrderByProcessorImpl.determineLocalMinMaxCodegen(forge.getOrderByProcessorForge(), classScope, namedMethods);

        CodegenMethodNode sortPlain = method.makeChild(eu.uk.ncl.pet5o.esper.client.EventBean[].class, OrderByProcessorOrderedLimit.class, classScope).addParam(SORTPLAIN_PARAMS);
        OrderByProcessorImpl.sortPlainCodegen(forge.getOrderByProcessorForge(), sortPlain, classScope, namedMethods);

        method.getBlock().exprDotMethod(REF_ROWLIMITPROCESSOR, "determineCurrentLimit")
                .ifCondition(and(limit1, offset0, haveOutgoing))
                    .declareVar(eu.uk.ncl.pet5o.esper.client.EventBean.class, "minmax", localMethod(determineLocalMinMax, REF_OUTGOINGEVENTS, REF_GENERATINGEVENTS, REF_ISNEWDATA, REF_EXPREVALCONTEXT, REF_AGGREGATIONSVC))
                    .blockReturn(newArrayWithInit(eu.uk.ncl.pet5o.esper.client.EventBean.class, ref("minmax")))
                .declareVar(eu.uk.ncl.pet5o.esper.client.EventBean[].class, "sorted", localMethod(sortPlain, REF_OUTGOINGEVENTS, REF_GENERATINGEVENTS, REF_ISNEWDATA, REF_EXPREVALCONTEXT, REF_AGGREGATIONSVC))
                .methodReturn(exprDotMethod(REF_ROWLIMITPROCESSOR, "applyLimit", ref("sorted")));
    }

    public eu.uk.ncl.pet5o.esper.client.EventBean[] sortRollup(eu.uk.ncl.pet5o.esper.client.EventBean[] outgoingEvents, List<GroupByRollupKey> currentGenerators, boolean newData, AgentInstanceContext agentInstanceContext, AggregationService aggregationService) {
        eu.uk.ncl.pet5o.esper.client.EventBean[] sorted = orderByProcessor.sortRollup(outgoingEvents, currentGenerators, newData, agentInstanceContext, aggregationService);
        return rowLimitProcessor.determineLimitAndApply(sorted);
    }

    public static void sortRollupCodegen(OrderByProcessorOrderedLimitForge forge, CodegenMethodNode method, CodegenClassScope classScope, CodegenNamedMethods namedMethods) {
        CodegenMethodNode sortRollup = method.makeChild(eu.uk.ncl.pet5o.esper.client.EventBean[].class, OrderByProcessorOrderedLimit.class, classScope).addParam(SORTROLLUP_PARAMS);
        OrderByProcessorImpl.sortRollupCodegen(forge.getOrderByProcessorForge(), sortRollup, classScope, namedMethods);
        method.getBlock().declareVar(eu.uk.ncl.pet5o.esper.client.EventBean[].class, "sorted", localMethod(sortRollup, REF_OUTGOINGEVENTS, REF_ORDERCURRENTGENERATORS, REF_ISNEWDATA, REF_AGENTINSTANCECONTEXT, REF_AGGREGATIONSVC))
                .methodReturn(exprDotMethod(REF_ROWLIMITPROCESSOR, "determineLimitAndApply", ref("sorted")));
    }

    public eu.uk.ncl.pet5o.esper.client.EventBean[] sortWGroupKeys(eu.uk.ncl.pet5o.esper.client.EventBean[] outgoingEvents, eu.uk.ncl.pet5o.esper.client.EventBean[][] generatingEvents, Object[] groupByKeys, boolean isNewData, ExprEvaluatorContext exprEvaluatorContext, AggregationService aggregationService) {
        eu.uk.ncl.pet5o.esper.client.EventBean[] sorted = orderByProcessor.sortWGroupKeys(outgoingEvents, generatingEvents, groupByKeys, isNewData, exprEvaluatorContext, aggregationService);
        return rowLimitProcessor.determineLimitAndApply(sorted);
    }

    static void sortWGroupKeysCodegen(OrderByProcessorOrderedLimitForge forge, CodegenMethodNode method, CodegenClassScope classScope, CodegenNamedMethods namedMethods) {
        CodegenMethodNode sortWGroupKeys = method.makeChild(eu.uk.ncl.pet5o.esper.client.EventBean[].class, OrderByProcessorOrderedLimit.class, classScope).addParam(SORTWGROUPKEYS_PARAMS);
        OrderByProcessorImpl.sortWGroupKeysCodegen(forge.getOrderByProcessorForge(), sortWGroupKeys, classScope, namedMethods);

        method.getBlock().declareVar(eu.uk.ncl.pet5o.esper.client.EventBean[].class, "sorted", localMethod(sortWGroupKeys, REF_OUTGOINGEVENTS, REF_GENERATINGEVENTS, REF_ORDERGROUPBYKEYS, REF_ISNEWDATA, REF_EXPREVALCONTEXT, REF_AGGREGATIONSVC))
                .methodReturn(exprDotMethod(REF_ROWLIMITPROCESSOR, "determineLimitAndApply", ref("sorted")));
    }

    public Object getSortKey(eu.uk.ncl.pet5o.esper.client.EventBean[] eventsPerStream, boolean isNewData, ExprEvaluatorContext exprEvaluatorContext) {
        return orderByProcessor.getSortKey(eventsPerStream, isNewData, exprEvaluatorContext);
    }

    public Object getSortKeyRollup(eu.uk.ncl.pet5o.esper.client.EventBean[] eventsPerStream, boolean isNewData, ExprEvaluatorContext exprEvaluatorContext, AggregationGroupByRollupLevel level) {
        return orderByProcessor.getSortKeyRollup(eventsPerStream, isNewData, exprEvaluatorContext, level);
    }

    public eu.uk.ncl.pet5o.esper.client.EventBean[] sortWOrderKeys(eu.uk.ncl.pet5o.esper.client.EventBean[] outgoingEvents, Object[] orderKeys, ExprEvaluatorContext exprEvaluatorContext) {
        return OrderByProcessorUtil.sortWOrderKeysWLimit(outgoingEvents, orderKeys, orderByProcessor.getComparator(), rowLimitProcessor);
    }

    public eu.uk.ncl.pet5o.esper.client.EventBean[] sortTwoKeys(eu.uk.ncl.pet5o.esper.client.EventBean first, Object sortKeyFirst, eu.uk.ncl.pet5o.esper.client.EventBean second, Object sortKeySecond) {
        eu.uk.ncl.pet5o.esper.client.EventBean[] sorted = orderByProcessor.sortTwoKeys(first, sortKeyFirst, second, sortKeySecond);
        return rowLimitProcessor.determineLimitAndApply(sorted);
    }

    static void sortTwoKeysCodegen(OrderByProcessorOrderedLimitForge forge, CodegenMethodNode method, CodegenClassScope classScope, CodegenNamedMethods namedMethods) {
        CodegenMethodNode sortTwoKeys = method.makeChild(eu.uk.ncl.pet5o.esper.client.EventBean[].class, OrderByProcessorOrderedLimit.class, classScope).addParam(SORTTWOKEYS_PARAMS);
        OrderByProcessorImpl.sortTwoKeysCodegen(forge.getOrderByProcessorForge(), sortTwoKeys, classScope, namedMethods);

        method.getBlock().declareVar(eu.uk.ncl.pet5o.esper.client.EventBean[].class, "sorted", localMethod(sortTwoKeys, REF_ORDERFIRSTEVENT, REF_ORDERFIRSTSORTKEY, REF_ORDERSECONDEVENT, REF_ORDERSECONDSORTKEY))
                .methodReturn(exprDotMethod(REF_ROWLIMITPROCESSOR, "determineLimitAndApply", ref("sorted")));
    }

    static void sortWOrderKeysCodegen(OrderByProcessorOrderedLimitForge forge, CodegenMethodNode method, CodegenClassScope classScope, CodegenNamedMethods namedMethods) {
        CodegenMember comparator = classScope.makeAddMember(Comparator.class, forge.getOrderByProcessorForge().getComparator());
        method.getBlock().methodReturn(staticMethod(OrderByProcessorUtil.class, "sortWOrderKeysWLimit", REF_OUTGOINGEVENTS, REF_ORDERKEYS, member(comparator.getMemberId()), REF_ROWLIMITPROCESSOR));
    }
}
