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
import eu.uk.ncl.pet5o.esper.codegen.base.CodegenClassScope;
import eu.uk.ncl.pet5o.esper.codegen.base.CodegenMethodNode;
import eu.uk.ncl.pet5o.esper.codegen.core.CodegenNamedMethods;
import eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpression;
import eu.uk.ncl.pet5o.esper.epl.agg.factory.AggregationStateLinearForge;
import eu.uk.ncl.pet5o.esper.epl.expression.codegen.CodegenLegoMethodExpression;
import eu.uk.ncl.pet5o.esper.epl.expression.core.ExprEvaluator;
import eu.uk.ncl.pet5o.esper.epl.expression.core.ExprEvaluatorContext;
import eu.uk.ncl.pet5o.esper.util.SimpleNumberCoercerFactory;

import java.util.Collection;
import java.util.Collections;

import static eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionBuilder.*;
import static eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionBuilder.constant;
import static eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionBuilder.constantNull;
import static eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionBuilder.constantTrue;
import static eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionBuilder.localMethod;
import static eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionBuilder.newArrayByLength;
import static eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionBuilder.ref;

/**
 * Represents the aggregation accessor that provides the result for the "first" and "last" aggregation function with index.
 */
public class AggregationAccessorFirstLastIndexWEval implements AggregationAccessor {
    private final int streamNum;
    private final ExprEvaluator childNode;
    private final ExprEvaluator indexNode;
    private final int constant;
    private final boolean isFirst;

    /**
     * Ctor.
     *
     * @param streamNum stream id
     * @param childNode expression
     * @param indexNode index expression
     * @param constant  constant index
     * @param isFirst   true if returning first, false for returning last
     */
    public AggregationAccessorFirstLastIndexWEval(int streamNum, ExprEvaluator childNode, ExprEvaluator indexNode, int constant, boolean isFirst) {
        this.streamNum = streamNum;
        this.childNode = childNode;
        this.indexNode = indexNode;
        this.constant = constant;
        this.isFirst = isFirst;
    }

    public Object getValue(AggregationState state, eu.uk.ncl.pet5o.esper.client.EventBean[] eventsPerStream, boolean isNewData, ExprEvaluatorContext exprEvaluatorContext) {
        eu.uk.ncl.pet5o.esper.client.EventBean bean = getBeanFirstLastIndex(state);
        if (bean == null) {
            return null;
        }
        eu.uk.ncl.pet5o.esper.client.EventBean[] eventsPerStreamBuf = new eu.uk.ncl.pet5o.esper.client.EventBean[streamNum + 1];
        eventsPerStreamBuf[streamNum] = bean;
        return childNode.evaluate(eventsPerStreamBuf, true, null);
    }

    public static void getValueCodegen(AggregationAccessorFirstLastIndexWEvalForge forge, AggregationAccessorForgeGetCodegenContext context) {
        AggregationStateLinearForge stateForge = (AggregationStateLinearForge) context.getAccessStateForge();
        CodegenMethodNode getBeanFirstLastIndex = getBeanFirstLastIndexCodegen(forge, context.getColumn(), context.getClassScope(), stateForge, context.getMethod(), context.getNamedMethods());
        context.getMethod().getBlock().declareVar(eu.uk.ncl.pet5o.esper.client.EventBean.class, "bean", localMethod(getBeanFirstLastIndex))
                .ifRefNullReturnNull("bean")
                .declareVar(eu.uk.ncl.pet5o.esper.client.EventBean[].class, "eventsPerStreamBuf", newArrayByLength(eu.uk.ncl.pet5o.esper.client.EventBean.class, constant(forge.getStreamNum() + 1)))
                .assignArrayElement("eventsPerStreamBuf", constant(forge.getStreamNum()), ref("bean"))
                .methodReturn(localMethod(CodegenLegoMethodExpression.codegenExpression(forge.getChildNode(), context.getMethod(), context.getClassScope()), ref("eventsPerStreamBuf"), constantTrue(), constantNull()));
    }

    public Collection<EventBean> getEnumerableEvents(AggregationState state, eu.uk.ncl.pet5o.esper.client.EventBean[] eventsPerStream, boolean isNewData, ExprEvaluatorContext exprEvaluatorContext) {
        eu.uk.ncl.pet5o.esper.client.EventBean bean = getBeanFirstLastIndex(state);
        if (bean == null) {
            return null;
        }
        return Collections.singletonList(bean);
    }

    public static void getEnumerableEventsCodegen(AggregationAccessorFirstLastIndexWEvalForge forge, AggregationAccessorForgeGetCodegenContext context) {
        AggregationStateLinearForge stateForge = (AggregationStateLinearForge) context.getAccessStateForge();
        CodegenMethodNode getBeanFirstLastIndex = getBeanFirstLastIndexCodegen(forge, context.getColumn(), context.getClassScope(), stateForge, context.getMethod(), context.getNamedMethods());
        context.getMethod().getBlock().declareVar(eu.uk.ncl.pet5o.esper.client.EventBean.class, "bean", localMethod(getBeanFirstLastIndex))
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

    public static void getEnumerableScalarCodegen(AggregationAccessorFirstLastIndexWEvalForge forge, AggregationAccessorForgeGetCodegenContext context) {
        AggregationStateLinearForge stateForge = (AggregationStateLinearForge) context.getAccessStateForge();
        CodegenMethodNode getBeanFirstLastIndex = getBeanFirstLastIndexCodegen(forge, context.getColumn(), context.getClassScope(), stateForge, context.getMethod(), context.getNamedMethods());
        context.getMethod().getBlock().declareVar(eu.uk.ncl.pet5o.esper.client.EventBean.class, "bean", localMethod(getBeanFirstLastIndex))
                .ifRefNullReturnNull("bean")
                .declareVar(eu.uk.ncl.pet5o.esper.client.EventBean[].class, "eventsPerStreamBuf", newArrayByLength(eu.uk.ncl.pet5o.esper.client.EventBean.class, constant(forge.getStreamNum() + 1)))
                .assignArrayElement("eventsPerStreamBuf", constant(forge.getStreamNum()), ref("bean"))
                .declareVar(Object.class, "value", localMethod(CodegenLegoMethodExpression.codegenExpression(forge.getChildNode(), context.getMethod(), context.getClassScope()), ref("eventsPerStreamBuf"), constantTrue(), constantNull()))
                .ifRefNullReturnNull("value")
                .methodReturn(staticMethod(Collections.class, "singletonList", ref("value")));
    }

    public eu.uk.ncl.pet5o.esper.client.EventBean getEnumerableEvent(AggregationState state, eu.uk.ncl.pet5o.esper.client.EventBean[] eventsPerStream, boolean isNewData, ExprEvaluatorContext exprEvaluatorContext) {
        return getBeanFirstLastIndex(state);
    }

    public static void getEnumerableEventCodegen(AggregationAccessorFirstLastIndexWEvalForge forge, AggregationAccessorForgeGetCodegenContext context) {
        AggregationStateLinearForge stateForge = (AggregationStateLinearForge) context.getAccessStateForge();
        CodegenMethodNode getBeanFirstLastIndex = getBeanFirstLastIndexCodegen(forge, context.getColumn(), context.getClassScope(), stateForge, context.getMethod(), context.getNamedMethods());
        context.getMethod().getBlock().methodReturn(localMethod(getBeanFirstLastIndex));
    }

    private eu.uk.ncl.pet5o.esper.client.EventBean getBeanFirstLastIndex(AggregationState state) {
        int index = constant;
        if (index == -1) {
            Object result = indexNode.evaluate(null, true, null);
            if ((result == null) || (!(result instanceof Integer))) {
                return null;
            }
            index = (Integer) result;
        }
        if (isFirst) {
            return ((AggregationStateLinear) state).getFirstNthValue(index);
        } else {
            return ((AggregationStateLinear) state).getLastNthValue(index);
        }
    }

    private static CodegenMethodNode getBeanFirstLastIndexCodegen(AggregationAccessorFirstLastIndexWEvalForge forge, int column, CodegenClassScope classScope, AggregationStateLinearForge stateForge, CodegenMethodNode parent, CodegenNamedMethods namedMethods) {
        CodegenMethodNode method = parent.makeChild(eu.uk.ncl.pet5o.esper.client.EventBean.class, AggregationAccessorFirstLastIndexWEval.class, classScope);
        if (forge.getConstant() == -1) {
            Class evalType = forge.getIndexNode().getEvaluationType();
            method.getBlock().declareVar(evalType, "indexResult", localMethod(CodegenLegoMethodExpression.codegenExpression(forge.getIndexNode(), method, classScope), constantNull(), constantTrue(), constantNull()));
            if (!evalType.isPrimitive()) {
                method.getBlock().ifRefNullReturnNull("indexResult");
            }
            method.getBlock().declareVar(int.class, "index", SimpleNumberCoercerFactory.SimpleNumberCoercerInt.codegenInt(ref("indexResult"), evalType));
        } else {
            method.getBlock().declareVar(int.class, "index", constant(forge.getConstant()));
        }
        CodegenExpression value = forge.isFirst() ? stateForge.getFirstNthValueCodegen(ref("index"), column, method, classScope, namedMethods) : stateForge.getLastNthValueCodegen(ref("index"), column, method, classScope, namedMethods);
        method.getBlock().methodReturn(value);
        return method;
    }
}
