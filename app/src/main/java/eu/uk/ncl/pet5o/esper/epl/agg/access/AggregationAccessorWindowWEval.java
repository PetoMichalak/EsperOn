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
import eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpression;
import eu.uk.ncl.pet5o.esper.epl.agg.factory.AggregationStateLinearForge;
import eu.uk.ncl.pet5o.esper.epl.expression.codegen.CodegenLegoMethodExpression;
import eu.uk.ncl.pet5o.esper.epl.expression.core.ExprEvaluator;
import eu.uk.ncl.pet5o.esper.epl.expression.core.ExprEvaluatorContext;
import eu.uk.ncl.pet5o.esper.util.JavaClassHelper;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import static eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionBuilder.*;
import static eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionBuilder.cast;
import static eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionBuilder.constant;
import static eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionBuilder.constantNull;
import static eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionBuilder.constantTrue;
import static eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionBuilder.equalsIdentity;
import static eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionBuilder.exprDotMethod;
import static eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionBuilder.localMethod;
import static eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionBuilder.newArrayByLength;
import static eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionBuilder.ref;

/**
 * Represents the aggregation accessor that provides the result for the "window" aggregation function.
 */
public class AggregationAccessorWindowWEval implements AggregationAccessor {
    private final int streamNum;
    private final ExprEvaluator childNode;
    private final Class componentType;

    /**
     * Ctor.
     *
     * @param streamNum     stream id
     * @param childNode     expression
     * @param componentType type
     */
    public AggregationAccessorWindowWEval(int streamNum, ExprEvaluator childNode, Class componentType) {
        this.streamNum = streamNum;
        this.childNode = childNode;
        this.componentType = componentType;
    }

    public Object getValue(AggregationState state, eu.uk.ncl.pet5o.esper.client.EventBean[] eventsPerStream, boolean isNewData, ExprEvaluatorContext exprEvaluatorContext) {
        AggregationStateLinear linear = (AggregationStateLinear) state;
        if (linear.size() == 0) {
            return null;
        }
        Object array = Array.newInstance(componentType, linear.size());
        Iterator<EventBean> it = linear.iterator();
        int count = 0;
        eu.uk.ncl.pet5o.esper.client.EventBean[] eventsPerStreamBuf = new eu.uk.ncl.pet5o.esper.client.EventBean[streamNum + 1];
        while (it.hasNext()) {
            eu.uk.ncl.pet5o.esper.client.EventBean bean = it.next();
            eventsPerStreamBuf[streamNum] = bean;
            Object value = childNode.evaluate(eventsPerStreamBuf, true, null);
            Array.set(array, count++, value);
        }

        return array;
    }

    public static void getValueCodegen(AggregationAccessorWindowWEvalForge forge, AggregationStateLinearForge accessStateFactory, AggregationAccessorForgeGetCodegenContext context) {
        CodegenExpression size = accessStateFactory.sizeCodegen(context.getColumn());
        CodegenExpression iterator = accessStateFactory.iteratorCodegen(context.getColumn(), context.getClassScope(), context.getMethod(), context.getNamedMethods());
        CodegenMethodNode childExpr = CodegenLegoMethodExpression.codegenExpression(forge.getChildNode(), context.getMethod(), context.getClassScope());

        context.getMethod().getBlock().ifCondition(equalsIdentity(size, constant(0))).blockReturn(constantNull())
                .declareVar(JavaClassHelper.getArrayType(forge.getComponentType()), "array", newArrayByLength(forge.getComponentType(), size))
                .declareVar(int.class, "count", constant(0))
                .declareVar(Iterator.class, "it", iterator)
                .declareVar(eu.uk.ncl.pet5o.esper.client.EventBean[].class, "eventsPerStreamBuf", newArrayByLength(eu.uk.ncl.pet5o.esper.client.EventBean.class, constant(forge.getStreamNum() + 1)))
                .whileLoop(exprDotMethod(ref("it"), "hasNext"))
                .declareVar(eu.uk.ncl.pet5o.esper.client.EventBean.class, "bean", cast(eu.uk.ncl.pet5o.esper.client.EventBean.class, exprDotMethod(ref("it"), "next")))
                .assignArrayElement("eventsPerStreamBuf", constant(forge.getStreamNum()), ref("bean"))
                .assignArrayElement(ref("array"), ref("count"), localMethod(childExpr, ref("eventsPerStreamBuf"), constant(true), constantNull()))
                .increment("count")
                .blockEnd()
                .methodReturn(ref("array"));
    }

    public Collection<EventBean> getEnumerableEvents(AggregationState state, eu.uk.ncl.pet5o.esper.client.EventBean[] eventsPerStream, boolean isNewData, ExprEvaluatorContext exprEvaluatorContext) {
        AggregationStateLinear linear = (AggregationStateLinear) state;
        if (linear.size() == 0) {
            return null;
        }
        return linear.collectionReadOnly();
    }

    public static void getEnumerableEventsCodegen(AggregationAccessorWindowWEvalForge forge, AggregationStateLinearForge stateForge, AggregationAccessorForgeGetCodegenContext context) {
        context.getMethod().getBlock().ifCondition(equalsIdentity(stateForge.sizeCodegen(context.getColumn()), constant(0)))
                .blockReturn(constantNull())
                .methodReturn(stateForge.collectionReadOnlyCodegen(context.getColumn(), context.getMethod(), context.getClassScope(), context.getNamedMethods()));
    }

    public Collection<Object> getEnumerableScalar(AggregationState state, eu.uk.ncl.pet5o.esper.client.EventBean[] eventsPerStream, boolean isNewData, ExprEvaluatorContext exprEvaluatorContext) {
        AggregationStateLinear linear = (AggregationStateLinear) state;
        int size = linear.size();
        if (size == 0) {
            return null;
        }
        List<Object> values = new ArrayList<Object>(size);
        Iterator<EventBean> it = linear.iterator();
        eu.uk.ncl.pet5o.esper.client.EventBean[] eventsPerStreamBuf = new eu.uk.ncl.pet5o.esper.client.EventBean[streamNum + 1];
        for (; it.hasNext(); ) {
            eu.uk.ncl.pet5o.esper.client.EventBean bean = it.next();
            eventsPerStreamBuf[streamNum] = bean;
            Object value = childNode.evaluate(eventsPerStreamBuf, true, null);
            values.add(value);
        }

        return values;
    }

    public static void getEnumerableScalarCodegen(AggregationAccessorWindowWEvalForge forge, AggregationStateLinearForge stateForge, AggregationAccessorForgeGetCodegenContext context) {
        context.getMethod().getBlock().declareVar(int.class, "size", stateForge.sizeCodegen(context.getColumn()))
                .ifCondition(equalsIdentity(ref("size"), constant(0))).blockReturn(constantNull())
                .declareVar(List.class, "values", newInstance(ArrayList.class, ref("size")))
                .declareVar(Iterator.class, "it", stateForge.iteratorCodegen(context.getColumn(), context.getClassScope(), context.getMethod(), context.getNamedMethods()))
                .declareVar(eu.uk.ncl.pet5o.esper.client.EventBean[].class, "eventsPerStreamBuf", newArrayByLength(eu.uk.ncl.pet5o.esper.client.EventBean.class, constant(forge.getStreamNum() + 1)))
                .whileLoop(exprDotMethod(ref("it"), "hasNext"))
                .declareVar(eu.uk.ncl.pet5o.esper.client.EventBean.class, "bean", cast(eu.uk.ncl.pet5o.esper.client.EventBean.class, exprDotMethod(ref("it"), "next")))
                .assignArrayElement("eventsPerStreamBuf", constant(forge.getStreamNum()), ref("bean"))
                .declareVar(JavaClassHelper.getBoxedType(forge.getChildNode().getEvaluationType()), "value", localMethod(CodegenLegoMethodExpression.codegenExpression(forge.getChildNode(), context.getMethod(), context.getClassScope()), ref("eventsPerStreamBuf"), constantTrue(), constantNull()))
                .exprDotMethod(ref("values"), "add", ref("value"))
                .blockEnd()
                .methodReturn(ref("values"));
    }

    public eu.uk.ncl.pet5o.esper.client.EventBean getEnumerableEvent(AggregationState state, eu.uk.ncl.pet5o.esper.client.EventBean[] eventsPerStream, boolean isNewData, ExprEvaluatorContext exprEvaluatorContext) {
        return null;
    }
}
