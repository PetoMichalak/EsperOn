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
package eu.uk.ncl.pet5o.esper.epl.agg.access;

import eu.uk.ncl.pet5o.esper.client.EventBean;
import eu.uk.ncl.pet5o.esper.codegen.base.CodegenMethodNode;
import eu.uk.ncl.pet5o.esper.epl.agg.factory.AggregationStateLinearForge;
import eu.uk.ncl.pet5o.esper.epl.expression.codegen.CodegenLegoMethodExpression;
import eu.uk.ncl.pet5o.esper.epl.expression.core.ExprEvaluator;
import eu.uk.ncl.pet5o.esper.epl.expression.core.ExprEvaluatorContext;

import java.util.Collection;
import java.util.Collections;

import static eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionBuilder.*;
import static eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionBuilder.constant;
import static eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionBuilder.constantNull;
import static eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionBuilder.localMethod;
import static eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionBuilder.newArrayByLength;
import static eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionBuilder.ref;

/**
 * Represents the aggregation accessor that provides the result for the "first" aggregation function without index.
 */
public class AggregationAccessorFirstWEval implements AggregationAccessor {
    private final int streamNum;
    private final ExprEvaluator childNode;

    /**
     * Ctor.
     *
     * @param streamNum stream id
     * @param childNode expression
     */
    public AggregationAccessorFirstWEval(int streamNum, ExprEvaluator childNode) {
        this.streamNum = streamNum;
        this.childNode = childNode;
    }

    public Object getValue(AggregationState state, eu.uk.ncl.pet5o.esper.client.EventBean[] eventsPerStream, boolean isNewData, ExprEvaluatorContext exprEvaluatorContext) {
        eu.uk.ncl.pet5o.esper.client.EventBean bean = ((AggregationStateLinear) state).getFirstValue();
        if (bean == null) {
            return null;
        }
        eu.uk.ncl.pet5o.esper.client.EventBean[] eventsPerStreamBuf = new eu.uk.ncl.pet5o.esper.client.EventBean[streamNum + 1];
        eventsPerStreamBuf[streamNum] = bean;
        return childNode.evaluate(eventsPerStreamBuf, true, null);
    }


    public static void getValueCodegen(AggregationAccessorFirstWEvalForge forge, AggregationStateLinearForge accessStateFactory, AggregationAccessorForgeGetCodegenContext context) {
        CodegenMethodNode childExpr = CodegenLegoMethodExpression.codegenExpression(forge.getChildNode(), context.getMethod(), context.getClassScope());
        context.getMethod().getBlock().declareVar(eu.uk.ncl.pet5o.esper.client.EventBean.class, "bean", accessStateFactory.getFirstValueCodegen(context.getColumn(), context.getClassScope(), context.getMethod()))
                .ifRefNullReturnNull("bean")
                .declareVar(eu.uk.ncl.pet5o.esper.client.EventBean[].class, "eventsPerStreamBuf", newArrayByLength(eu.uk.ncl.pet5o.esper.client.EventBean.class, constant(forge.getStreamNum() + 1)))
                .assignArrayElement("eventsPerStreamBuf", constant(forge.getStreamNum()), ref("bean"))
                .methodReturn(localMethod(childExpr, ref("eventsPerStreamBuf"), constant(true), constantNull()));
    }

    public Collection<EventBean> getEnumerableEvents(AggregationState state, eu.uk.ncl.pet5o.esper.client.EventBean[] eventsPerStream, boolean isNewData, ExprEvaluatorContext exprEvaluatorContext) {
        eu.uk.ncl.pet5o.esper.client.EventBean bean = ((AggregationStateLinear) state).getFirstValue();
        if (bean == null) {
            return null;
        }
        return Collections.singletonList(bean);
    }

    public static void getEnumerableEventsCodegen(AggregationAccessorFirstWEvalForge forge, AggregationStateLinearForge stateForge, AggregationAccessorForgeGetCodegenContext context) {
        context.getMethod().getBlock().declareVar(eu.uk.ncl.pet5o.esper.client.EventBean.class, "bean", stateForge.getFirstValueCodegen(context.getColumn(), context.getClassScope(), context.getMethod()))
                .ifRefNullReturnNull("bean")
                .methodReturn(staticMethod(Collections.class, "singletonList", ref("bean")));
    }

    public Collection<Object> getEnumerableScalar(AggregationState state, eu.uk.ncl.pet5o.esper.client.EventBean[] eventsPerStream, boolean isNewData, ExprEvaluatorContext exprEvaluatorContext) {
        Object value = getValue(state, eventsPerStream, isNewData, exprEvaluatorContext);
        if (value == null) {
            return null;
        }
        return Collections.singletonList(value);
    }

    public static void getEnumerableScalarCodegen(AggregationAccessorFirstWEvalForge forge, AggregationStateLinearForge accessStateFactory, AggregationAccessorForgeGetCodegenContext context) {
        CodegenMethodNode childExpr = CodegenLegoMethodExpression.codegenExpression(forge.getChildNode(), context.getMethod(), context.getClassScope());
        context.getMethod().getBlock().declareVar(eu.uk.ncl.pet5o.esper.client.EventBean.class, "bean", accessStateFactory.getFirstValueCodegen(context.getColumn(), context.getClassScope(), context.getMethod()))
                .ifRefNullReturnNull("bean")
                .declareVar(eu.uk.ncl.pet5o.esper.client.EventBean[].class, "eventsPerStreamBuf", newArrayByLength(eu.uk.ncl.pet5o.esper.client.EventBean.class, constant(forge.getStreamNum() + 1)))
                .assignArrayElement("eventsPerStreamBuf", constant(forge.getStreamNum()), ref("bean"))
                .declareVar(Object.class, "value", localMethod(childExpr, ref("eventsPerStreamBuf"), constant(true), constantNull()))
                .ifRefNullReturnNull("value")
                .methodReturn(staticMethod(Collections.class, "singletonList", ref("value")));
    }

    public eu.uk.ncl.pet5o.esper.client.EventBean getEnumerableEvent(AggregationState state, eu.uk.ncl.pet5o.esper.client.EventBean[] eventsPerStream, boolean isNewData, ExprEvaluatorContext exprEvaluatorContext) {
        return ((AggregationStateLinear) state).getFirstValue();
    }

    public static void getEnumerableEventCodegen(AggregationAccessorFirstWEvalForge forge, AggregationStateLinearForge stateForge, AggregationAccessorForgeGetCodegenContext context) {
        context.getMethod().getBlock().methodReturn(stateForge.getFirstValueCodegen(context.getColumn(), context.getClassScope(), context.getMethod()));
    }
}
