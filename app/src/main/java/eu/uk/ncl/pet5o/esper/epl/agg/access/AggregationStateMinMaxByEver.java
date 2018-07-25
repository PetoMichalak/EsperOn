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

import com.espertech.esper.client.EventBean;
import com.espertech.esper.codegen.base.CodegenClassScope;
import com.espertech.esper.codegen.base.CodegenMember;
import com.espertech.esper.codegen.base.CodegenMembersColumnized;
import com.espertech.esper.codegen.base.CodegenMethodNode;
import com.espertech.esper.codegen.core.CodegenNamedMethods;
import com.espertech.esper.codegen.model.expression.CodegenExpression;
import com.espertech.esper.codegen.model.expression.CodegenExpressionRef;
import com.espertech.esper.epl.agg.aggregator.AggregatorCodegenUtil;
import com.espertech.esper.epl.agg.factory.AggregationStateMinMaxByEverForge;
import com.espertech.esper.epl.expression.codegen.ExprForgeCodegenSymbol;
import com.espertech.esper.epl.expression.core.ExprEvaluatorContext;

import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;

import static com.espertech.esper.codegen.model.expression.CodegenExpressionBuilder.*;
import static com.espertech.esper.codegen.model.expression.CodegenExpressionBuilder.arrayAtIndex;
import static com.espertech.esper.codegen.model.expression.CodegenExpressionBuilder.constant;
import static com.espertech.esper.codegen.model.expression.CodegenExpressionBuilder.constantNull;
import static com.espertech.esper.codegen.model.expression.CodegenExpressionBuilder.constantTrue;
import static com.espertech.esper.codegen.model.expression.CodegenExpressionBuilder.equalsNull;
import static com.espertech.esper.codegen.model.expression.CodegenExpressionBuilder.exprDotMethod;
import static com.espertech.esper.codegen.model.expression.CodegenExpressionBuilder.localMethod;
import static com.espertech.esper.codegen.model.expression.CodegenExpressionBuilder.member;
import static com.espertech.esper.codegen.model.expression.CodegenExpressionBuilder.ref;
import static com.espertech.esper.codegen.model.expression.CodegenExpressionBuilder.refCol;
import static com.espertech.esper.codegen.model.expression.CodegenExpressionBuilder.relational;
import static com.espertech.esper.codegen.model.expression.CodegenExpressionRelational.CodegenRelational.GT;
import static com.espertech.esper.codegen.model.expression.CodegenExpressionRelational.CodegenRelational.LT;
import static com.espertech.esper.epl.expression.codegen.ExprForgeCodegenNames.*;
import static com.espertech.esper.epl.expression.codegen.ExprForgeCodegenNames.NAME_EPS;
import static com.espertech.esper.epl.expression.codegen.ExprForgeCodegenNames.NAME_EXPREVALCONTEXT;
import static com.espertech.esper.epl.expression.codegen.ExprForgeCodegenNames.REF_EPS;
import static com.espertech.esper.epl.expression.codegen.ExprForgeCodegenNames.REF_EXPREVALCONTEXT;

/**
 * Implementation of access function for single-stream (not joins).
 */
public class AggregationStateMinMaxByEver implements AggregationState, AggregationStateSorted {
    protected final AggregationStateMinMaxByEverSpec spec;
    protected com.espertech.esper.client.EventBean currentMinMaxBean;
    protected Object currentMinMax;

    public AggregationStateMinMaxByEver(AggregationStateMinMaxByEverSpec spec) {
        this.spec = spec;
    }

    public static void rowMemberCodegen(int stateNumber, CodegenMembersColumnized membersColumnized) {
        membersColumnized.addMember(stateNumber, com.espertech.esper.client.EventBean.class, "currentMinMaxBean");
        membersColumnized.addMember(stateNumber, Object.class, "currentMinMax");
    }

    public void applyEnter(com.espertech.esper.client.EventBean[] eventsPerStream, ExprEvaluatorContext exprEvaluatorContext) {
        com.espertech.esper.client.EventBean theEvent = eventsPerStream[spec.getStreamId()];
        if (theEvent == null) {
            return;
        }
        addEvent(theEvent, eventsPerStream, exprEvaluatorContext);
    }

    public static void applyEnterCodegen(AggregationStateMinMaxByEverForge forge, int stateNumber, CodegenMethodNode method, ExprForgeCodegenSymbol symbols, CodegenClassScope classScope, CodegenNamedMethods namedMethods) {
        if (forge.getSpec().getOptionalFilter() != null) {
            AggregatorCodegenUtil.prefixWithFilterCheck(forge.getSpec().getOptionalFilter(), method, symbols, classScope);
        }
        CodegenExpression eps = symbols.getAddEPS(method);
        CodegenExpression ctx = symbols.getAddExprEvalCtx(method);
        method.getBlock().declareVar(com.espertech.esper.client.EventBean.class, "theEvent", arrayAtIndex(eps, constant(forge.getSpec().getStreamId())))
                .ifCondition(equalsNull(ref("theEvent"))).blockReturnNoValue()
                .localMethod(addEventCodegen(forge, stateNumber, method, namedMethods, classScope), ref("theEvent"), eps, ctx);
    }

    public void applyLeave(com.espertech.esper.client.EventBean[] eventsPerStream, ExprEvaluatorContext exprEvaluatorContext) {
        // this is an ever-type aggregation
    }

    public void clear() {
        currentMinMax = null;
        currentMinMaxBean = null;
    }

    public static void clearCodegen(int stateNumber, CodegenMethodNode method) {
        method.getBlock().assignRef(refCol("currentMinMaxBean", stateNumber), constantNull())
                .assignRef(refCol("currentMinMax", stateNumber), constantNull());
    }

    public com.espertech.esper.client.EventBean getFirstValue() {
        if (spec.isMax()) {
            throw new UnsupportedOperationException("Only accepts max-value queries");
        }
        return currentMinMaxBean;
    }

    public static CodegenExpression getFirstValueCodegen(AggregationStateMinMaxByEverForge forge, int slot, CodegenClassScope classScope, CodegenMethodNode method) {
        if (forge.getSpec().isMax()) {
            method.getBlock().methodThrowUnsupported();
        }
        return refCol("currentMinMaxBean", slot);
    }

    public com.espertech.esper.client.EventBean getLastValue() {
        if (!spec.isMax()) {
            throw new UnsupportedOperationException("Only accepts min-value queries");
        }
        return currentMinMaxBean;
    }

    public static CodegenExpression getLastValueCodegen(AggregationStateMinMaxByEverForge forge, int slot, CodegenClassScope classScope, CodegenMethodNode method) {
        if (!forge.getSpec().isMax()) {
            method.getBlock().methodThrowUnsupported();
        }
        return refCol("currentMinMaxBean", slot);
    }

    public Iterator<EventBean> iterator() {
        throw new UnsupportedOperationException();
    }

    public Iterator<EventBean> getReverseIterator() {
        throw new UnsupportedOperationException();
    }

    public Collection<EventBean> collectionReadOnly() {
        if (currentMinMaxBean != null) {
            return Collections.singletonList(currentMinMaxBean);
        }
        return null;
    }

    public int size() {
        return currentMinMax == null ? 0 : 1;
    }

    public AggregationStateMinMaxByEverSpec getSpec() {
        return spec;
    }

    public com.espertech.esper.client.EventBean getCurrentMinMaxBean() {
        return currentMinMaxBean;
    }

    public Object getCurrentMinMax() {
        return currentMinMax;
    }

    public void setCurrentMinMaxBean(com.espertech.esper.client.EventBean currentMinMaxBean) {
        this.currentMinMaxBean = currentMinMaxBean;
    }

    public void setCurrentMinMax(Object currentMinMax) {
        this.currentMinMax = currentMinMax;
    }

    protected void addEvent(com.espertech.esper.client.EventBean theEvent, com.espertech.esper.client.EventBean[] eventsPerStream, ExprEvaluatorContext exprEvaluatorContext) {
        Object comparable = AggregationStateSortedImpl.getComparable(spec.getCriteria(), eventsPerStream, true, exprEvaluatorContext);
        if (currentMinMax == null) {
            currentMinMax = comparable;
            currentMinMaxBean = theEvent;
        } else {
            int compareResult = spec.getComparator().compare(currentMinMax, comparable);
            if (spec.isMax()) {
                if (compareResult < 0) {
                    currentMinMax = comparable;
                    currentMinMaxBean = theEvent;
                }
            } else {
                if (compareResult > 0) {
                    currentMinMax = comparable;
                    currentMinMaxBean = theEvent;
                }
            }
        }
    }

    private static CodegenMethodNode addEventCodegen(AggregationStateMinMaxByEverForge forge, int stateNumber, CodegenMethodNode parent, CodegenNamedMethods namedMethods, CodegenClassScope classScope) {
        CodegenMethodNode comparable = AggregationStateSortedImpl.getComparableCodegen("comparable_" + stateNumber, forge.getSpec().getCriteria(), namedMethods, classScope);
        CodegenExpressionRef currentMinMax = refCol("currentMinMax", stateNumber);
        CodegenExpressionRef currentMinMaxBean = refCol("currentMinMaxBean", stateNumber);
        CodegenMember memberComparator = classScope.makeAddMember(Comparator.class, forge.getSpec().getComparator());

        CodegenMethodNode methodNode = parent.makeChild(void.class, AggregationStateMinMaxByEver.class, classScope).addParam(com.espertech.esper.client.EventBean.class, "theEvent").addParam(com.espertech.esper.client.EventBean[].class, NAME_EPS).addParam(ExprEvaluatorContext.class, NAME_EXPREVALCONTEXT);
        methodNode.getBlock().declareVar(Object.class, "comparable", localMethod(comparable, REF_EPS, constantTrue(), REF_EXPREVALCONTEXT))
                .ifCondition(equalsNull(currentMinMax))
                    .assignRef(currentMinMax, ref("comparable"))
                    .assignRef(currentMinMaxBean, ref("theEvent"))
                .ifElse()
                    .declareVar(int.class, "compareResult", exprDotMethod(member(memberComparator.getMemberId()), "compare", currentMinMax, ref("comparable")))
                    .ifCondition(relational(ref("compareResult"), forge.getSpec().isMax() ? LT : GT, constant(0)))
                        .assignRef(currentMinMax, ref("comparable"))
                        .assignRef(currentMinMaxBean, ref("theEvent"));
        return methodNode;
    }
}
