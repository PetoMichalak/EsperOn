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

import com.espertech.esper.codegen.base.CodegenMethodNode;
import com.espertech.esper.core.context.util.AgentInstanceContext;
import com.espertech.esper.epl.agg.rollup.GroupByRollupKey;
import com.espertech.esper.epl.agg.service.common.AggregationGroupByRollupLevel;
import com.espertech.esper.epl.agg.service.common.AggregationService;
import com.espertech.esper.epl.expression.core.ExprEvaluatorContext;

import java.util.List;

import static com.espertech.esper.codegen.model.expression.CodegenExpressionBuilder.exprDotMethod;
import static com.espertech.esper.epl.core.orderby.OrderByProcessorCodegenNames.*;
import static com.espertech.esper.epl.core.orderby.OrderByProcessorCodegenNames.REF_ORDERFIRSTEVENT;
import static com.espertech.esper.epl.core.orderby.OrderByProcessorCodegenNames.REF_ORDERSECONDEVENT;
import static com.espertech.esper.epl.core.orderby.OrderByProcessorCodegenNames.REF_OUTGOINGEVENTS;
import static com.espertech.esper.epl.core.orderby.OrderByProcessorOrderedLimitForge.REF_ROWLIMITPROCESSOR;

/**
 * An order-by processor that sorts events according to the expressions
 * in the order_by clause.
 */
public class OrderByProcessorRowLimitOnly implements OrderByProcessor {

    private final RowLimitProcessor rowLimitProcessor;

    public OrderByProcessorRowLimitOnly(RowLimitProcessor rowLimitProcessor) {
        this.rowLimitProcessor = rowLimitProcessor;
    }

    public com.espertech.esper.client.EventBean[] sortPlain(com.espertech.esper.client.EventBean[] outgoingEvents, com.espertech.esper.client.EventBean[][] generatingEvents, boolean isNewData, ExprEvaluatorContext exprEvaluatorContext, AggregationService aggregationService) {
        return rowLimitProcessor.determineLimitAndApply(outgoingEvents);
    }

    public static void sortPlainCodegen(CodegenMethodNode method) {
        determineLimitAndApplyCodegen(method);
    }

    public com.espertech.esper.client.EventBean[] sortWGroupKeys(com.espertech.esper.client.EventBean[] outgoingEvents, com.espertech.esper.client.EventBean[][] generatingEvents, Object[] groupByKeys, boolean isNewData, ExprEvaluatorContext exprEvaluatorContext, AggregationService aggregationService) {
        return rowLimitProcessor.determineLimitAndApply(outgoingEvents);
    }

    static void sortWGroupKeysCodegen(CodegenMethodNode method) {
        determineLimitAndApplyCodegen(method);
    }

    public com.espertech.esper.client.EventBean[] sortRollup(com.espertech.esper.client.EventBean[] outgoingEvents, List<GroupByRollupKey> currentGenerators, boolean newData, AgentInstanceContext agentInstanceContext, AggregationService aggregationService) {
        return rowLimitProcessor.determineLimitAndApply(outgoingEvents);
    }

    static void sortRollupCodegen(CodegenMethodNode method) {
        determineLimitAndApplyCodegen(method);
    }

    public Object getSortKey(com.espertech.esper.client.EventBean[] eventsPerStream, boolean isNewData, ExprEvaluatorContext exprEvaluatorContext) {
        return null;
    }

    public Object getSortKeyRollup(com.espertech.esper.client.EventBean[] eventsPerStream, boolean isNewData, ExprEvaluatorContext exprEvaluatorContext, AggregationGroupByRollupLevel level) {
        return null;
    }

    public com.espertech.esper.client.EventBean[] sortWOrderKeys(com.espertech.esper.client.EventBean[] outgoingEvents, Object[] orderKeys, ExprEvaluatorContext exprEvaluatorContext) {
        return rowLimitProcessor.determineLimitAndApply(outgoingEvents);
    }

    public com.espertech.esper.client.EventBean[] sortTwoKeys(com.espertech.esper.client.EventBean first, Object sortKeyFirst, com.espertech.esper.client.EventBean second, Object sortKeySecond) {
        return rowLimitProcessor.determineApplyLimit2Events(first, second);
    }

    static void sortTwoKeysCodegen(CodegenMethodNode method) {
        method.getBlock().methodReturn(exprDotMethod(REF_ROWLIMITPROCESSOR, "determineApplyLimit2Events", REF_ORDERFIRSTEVENT, REF_ORDERSECONDEVENT));
    }

    static void sortWOrderKeysCodegen(CodegenMethodNode method) {
        determineLimitAndApplyCodegen(method);
    }

    private static void determineLimitAndApplyCodegen(CodegenMethodNode method) {
        method.getBlock().methodReturn(exprDotMethod(REF_ROWLIMITPROCESSOR, "determineLimitAndApply", REF_OUTGOINGEVENTS));
    }
}
