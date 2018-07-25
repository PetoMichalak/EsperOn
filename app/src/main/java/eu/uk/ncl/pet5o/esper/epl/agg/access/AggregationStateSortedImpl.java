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
import eu.uk.ncl.pet5o.esper.codegen.base.*;
import eu.uk.ncl.pet5o.esper.codegen.core.CodegenCtor;
import eu.uk.ncl.pet5o.esper.codegen.core.CodegenNamedMethods;
import eu.uk.ncl.pet5o.esper.codegen.core.CodegenNamedParam;
import eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpression;
import eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionRef;
import eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionRefWCol;
import eu.uk.ncl.pet5o.esper.collection.HashableMultiKey;
import eu.uk.ncl.pet5o.esper.epl.agg.aggregator.AggregatorCodegenUtil;
import eu.uk.ncl.pet5o.esper.epl.agg.factory.AggregationStateSortedForge;
import eu.uk.ncl.pet5o.esper.epl.expression.codegen.CodegenLegoMethodExpression;
import eu.uk.ncl.pet5o.esper.epl.expression.codegen.ExprForgeCodegenSymbol;
import eu.uk.ncl.pet5o.esper.epl.expression.core.ExprEvaluator;
import eu.uk.ncl.pet5o.esper.epl.expression.core.ExprEvaluatorContext;
import eu.uk.ncl.pet5o.esper.epl.expression.core.ExprNode;

import java.util.ArrayDeque;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;
import java.util.function.Consumer;

import static eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionBuilder.*;
import static eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionBuilder.arrayAtIndex;
import static eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionBuilder.constant;
import static eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionBuilder.constantFalse;
import static eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionBuilder.constantNull;
import static eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionBuilder.constantTrue;
import static eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionBuilder.exprDotMethod;
import static eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionBuilder.instanceOf;
import static eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionBuilder.localMethod;
import static eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionBuilder.member;
import static eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionBuilder.newArrayByLength;
import static eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionBuilder.newInstance;
import static eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionBuilder.ref;
import static eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionBuilder.refCol;
import static eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionBuilder.staticMethod;
import static eu.uk.ncl.pet5o.esper.epl.core.resultset.codegen.ResultSetProcessorCodegenNames.NAME_ISNEWDATA;
import static eu.uk.ncl.pet5o.esper.epl.expression.codegen.ExprForgeCodegenNames.*;
import static eu.uk.ncl.pet5o.esper.epl.expression.codegen.ExprForgeCodegenNames.NAME_EPS;
import static eu.uk.ncl.pet5o.esper.epl.expression.codegen.ExprForgeCodegenNames.NAME_EXPREVALCONTEXT;
import static eu.uk.ncl.pet5o.esper.epl.expression.codegen.ExprForgeCodegenNames.REF_EPS;
import static eu.uk.ncl.pet5o.esper.epl.expression.codegen.ExprForgeCodegenNames.REF_EXPREVALCONTEXT;
import static eu.uk.ncl.pet5o.esper.epl.expression.codegen.ExprForgeCodegenNames.REF_ISNEWDATA;

/**
 * Implementation of access function for single-stream (not joins).
 */
public class AggregationStateSortedImpl implements AggregationStateWithSize, AggregationStateSorted {
    protected final AggregationStateSortedSpec spec;
    protected final TreeMap<Object, Object> sorted;
    protected int size;

    /**
     * Ctor.
     *
     * @param spec aggregation spec
     */
    public AggregationStateSortedImpl(AggregationStateSortedSpec spec) {
        this.spec = spec;
        sorted = new TreeMap<>(spec.getComparator());
    }

    public static void rowMemberCodegen(AggregationStateSortedForge forge, int stateNumber, CodegenCtor ctor, CodegenMembersColumnized membersColumnized, CodegenClassScope classScope) {
        membersColumnized.addMember(stateNumber, TreeMap.class, "sorted");
        membersColumnized.addMember(stateNumber, int.class, "size");
        CodegenMember memberComparator = classScope.makeAddMember(Comparator.class, forge.getSpec().getComparator());
        ctor.getBlock().assignRef(new CodegenExpressionRefWCol("sorted", stateNumber), newInstance(TreeMap.class, member(memberComparator.getMemberId())));
    }

    public void applyEnter(eu.uk.ncl.pet5o.esper.client.EventBean[] eventsPerStream, ExprEvaluatorContext exprEvaluatorContext) {
        eu.uk.ncl.pet5o.esper.client.EventBean theEvent = eventsPerStream[spec.getStreamId()];
        if (theEvent == null) {
            return;
        }
        referenceAdd(theEvent, eventsPerStream, exprEvaluatorContext);
    }

    public static void applyEnterCodegen(AggregationStateSortedForge forge, int stateNumber, CodegenMethodNode method, ExprForgeCodegenSymbol symbols, CodegenNamedMethods namedMethods, CodegenClassScope classScope) {
        if (forge.getExpr().getOptionalFilter() != null) {
            AggregatorCodegenUtil.prefixWithFilterCheck(forge.getExpr().getOptionalFilter().getForge(), method, symbols, classScope);
        }
        CodegenExpressionRef eps = symbols.getAddEPS(method);
        CodegenExpressionRef ctx = symbols.getAddExprEvalCtx(method);
        CodegenMethodNode referenceAddToColl = referenceAddToCollCodegen(forge, stateNumber, method, namedMethods, classScope);
        method.getBlock().declareVar(eu.uk.ncl.pet5o.esper.client.EventBean.class, "theEvent", arrayAtIndex(eps, constant(forge.getSpec().getStreamId())))
                .ifRefNull("theEvent").blockReturnNoValue()
                .localMethod(referenceAddToColl, ref("theEvent"), eps, ctx);
    }

    public void applyLeave(eu.uk.ncl.pet5o.esper.client.EventBean[] eventsPerStream, ExprEvaluatorContext exprEvaluatorContext) {
        eu.uk.ncl.pet5o.esper.client.EventBean theEvent = eventsPerStream[spec.getStreamId()];
        if (theEvent == null) {
            return;
        }
        dereferenceRemove(theEvent, eventsPerStream, exprEvaluatorContext);
    }

    public static void applyLeaveCodegen(AggregationStateSortedForge forge, int stateNumber, CodegenMethodNode method, ExprForgeCodegenSymbol symbols, CodegenNamedMethods namedMethods, CodegenClassScope classScope) {
        if (forge.getExpr().getOptionalFilter() != null) {
            AggregatorCodegenUtil.prefixWithFilterCheck(forge.getExpr().getOptionalFilter().getForge(), method, symbols, classScope);
        }
        CodegenExpressionRef eps = symbols.getAddEPS(method);
        CodegenExpressionRef ctx = symbols.getAddExprEvalCtx(method);
        CodegenMethodNode dereferenceRemove = dereferenceRemoveFromCollCodegen(forge, stateNumber, method, namedMethods, classScope);
        method.getBlock().declareVar(eu.uk.ncl.pet5o.esper.client.EventBean.class, "theEvent", arrayAtIndex(eps, constant(forge.getSpec().getStreamId())))
                .ifRefNull("theEvent").blockReturnNoValue()
                .localMethod(dereferenceRemove, ref("theEvent"), eps, ctx);
    }

    public void clear() {
        sorted.clear();
        size = 0;
    }

    public static void clearCodegen(int stateNumber, CodegenMethodNode method) {
        method.getBlock().exprDotMethod(refCol("sorted", stateNumber), "clear")
                .assignRef(refCol("size", stateNumber), constant(0));
    }

    protected boolean referenceEvent(eu.uk.ncl.pet5o.esper.client.EventBean theEvent) {
        // no action
        return true;
    }

    protected boolean dereferenceEvent(eu.uk.ncl.pet5o.esper.client.EventBean theEvent) {
        // no action
        return true;
    }

    public eu.uk.ncl.pet5o.esper.client.EventBean getFirstValue() {
        if (sorted.isEmpty()) {
            return null;
        }
        Map.Entry<Object, Object> max = sorted.firstEntry();
        return checkedPayloadMayDeque(max.getValue());
    }

    public static CodegenExpression getFirstValueCodegen(AggregationStateSortedForge forge, int slot, CodegenClassScope classScope, CodegenMethodNode parent) {
        CodegenExpression sorted = refCol("sorted", slot);
        CodegenMethodNode method = parent.makeChildWithScope(eu.uk.ncl.pet5o.esper.client.EventBean.class, AggregationStateSortedImpl.class, CodegenSymbolProviderEmpty.INSTANCE, classScope);
        method.getBlock().ifCondition(exprDotMethod(sorted, "isEmpty"))
                .blockReturn(constantNull())
                .declareVar(Map.Entry.class, "max", exprDotMethod(sorted, "firstEntry"))
                .methodReturn(staticMethod(AggregationStateSortedImpl.class, "checkedPayloadMayDeque", exprDotMethod(ref("max"), "getValue")));
        return localMethod(method);
    }

    public eu.uk.ncl.pet5o.esper.client.EventBean getLastValue() {
        if (sorted.isEmpty()) {
            return null;
        }
        Map.Entry<Object, Object> min = sorted.lastEntry();
        return checkedPayloadMayDeque(min.getValue());
    }

    public static CodegenExpression getLastValueCodegen(AggregationStateSortedForge forge, int slot, CodegenClassScope classScope, CodegenMethodNode parent) {
        CodegenExpression sorted = refCol("sorted", slot);
        CodegenMethodNode method = parent.makeChildWithScope(eu.uk.ncl.pet5o.esper.client.EventBean.class, AggregationStateSortedImpl.class, CodegenSymbolProviderEmpty.INSTANCE, classScope);
        method.getBlock().ifCondition(exprDotMethod(sorted, "isEmpty"))
                .blockReturn(constantNull())
                .declareVar(Map.Entry.class, "min", exprDotMethod(sorted, "lastEntry"))
                .methodReturn(staticMethod(AggregationStateSortedImpl.class, "checkedPayloadMayDeque", exprDotMethod(ref("min"), "getValue")));
        return localMethod(method);
    }

    public Iterator<EventBean> iterator() {
        return new AggregationStateSortedIterator(sorted, false);
    }

    public static CodegenExpression iteratorCodegen(AggregationStateSortedForge forge, int slot) {
        return newInstance(AggregationStateSortedIterator.class, refCol("sorted", slot), constantFalse());
    }

    public Iterator<EventBean> getReverseIterator() {
        return new AggregationStateSortedIterator(sorted, true);
    }

    public static CodegenExpression getReverseIteratorCodegen(AggregationStateSortedForge forge, int slot) {
        return newInstance(AggregationStateSortedIterator.class, refCol("sorted", slot), constantTrue());
    }

    public Collection<EventBean> collectionReadOnly() {
        return new AggregationStateSortedWrappingCollection(sorted, size);
    }

    public static CodegenExpression collectionReadOnlyCodegen(int column) {
        return newInstance(AggregationStateSortedWrappingCollection.class, refCol("sorted", column), refCol("size", column));
    }

    public int size() {
        return size;
    }

    public static CodegenExpression sizeCodegen(AggregationStateSortedForge forge, int slot) {
        return refCol("size", slot);
    }

    public static Object getComparable(ExprEvaluator[] criteria, eu.uk.ncl.pet5o.esper.client.EventBean[] eventsPerStream, boolean istream, ExprEvaluatorContext exprEvaluatorContext) {
        if (criteria.length == 1) {
            return criteria[0].evaluate(eventsPerStream, istream, exprEvaluatorContext);
        } else {
            Object[] result = new Object[criteria.length];
            int count = 0;
            for (ExprEvaluator expr : criteria) {
                result[count++] = expr.evaluate(eventsPerStream, true, exprEvaluatorContext);
            }
            return new HashableMultiKey(result);
        }
    }

    public static CodegenMethodNode getComparableCodegen(String methodName, ExprNode[] criteria, CodegenNamedMethods namedMethods, CodegenClassScope classScope) {
        Consumer<CodegenMethodNode> code = method -> {
            if (criteria.length == 1) {
                method.getBlock().methodReturn(localMethod(CodegenLegoMethodExpression.codegenExpression(criteria[0].getForge(), method, classScope), REF_EPS, REF_ISNEWDATA, REF_EXPREVALCONTEXT));
            } else {
                ExprForgeCodegenSymbol exprSymbol = new ExprForgeCodegenSymbol(true, null);
                CodegenExpression[] expressions = new CodegenExpression[criteria.length];
                for (int i = 0; i < criteria.length; i++) {
                    expressions[i] = criteria[i].getForge().evaluateCodegen(Object.class, method, exprSymbol, classScope);
                }
                exprSymbol.derivedSymbolsCodegen(method, method.getBlock(), classScope);

                method.getBlock().declareVar(Object[].class, "result", newArrayByLength(Object.class, constant(criteria.length)));
                for (int i = 0; i < criteria.length; i++) {
                    method.getBlock().assignArrayElement(ref("result"), constant(i), expressions[i]);
                }
                method.getBlock().methodReturn(newInstance(HashableMultiKey.class, ref("result")));
            }
        };
        return namedMethods.addMethod(Object.class, methodName, CodegenNamedParam.from(eu.uk.ncl.pet5o.esper.client.EventBean[].class, NAME_EPS, boolean.class, NAME_ISNEWDATA, ExprEvaluatorContext.class, NAME_EXPREVALCONTEXT), AggregationStateSortedImpl.class, classScope, code);
    }

    protected void referenceAdd(eu.uk.ncl.pet5o.esper.client.EventBean theEvent, eu.uk.ncl.pet5o.esper.client.EventBean[] eventsPerStream, ExprEvaluatorContext exprEvaluatorContext) {
        if (referenceEvent(theEvent)) {
            referenceAddToColl(theEvent, eventsPerStream, exprEvaluatorContext);
        }
    }

    private void referenceAddToColl(eu.uk.ncl.pet5o.esper.client.EventBean theEvent, eu.uk.ncl.pet5o.esper.client.EventBean[] eventsPerStream, ExprEvaluatorContext exprEvaluatorContext) {
        Object comparable = getComparable(spec.getCriteria(), eventsPerStream, true, exprEvaluatorContext);
        Object existing = sorted.get(comparable);
        if (existing == null) {
            sorted.put(comparable, theEvent);
        } else if (existing instanceof eu.uk.ncl.pet5o.esper.client.EventBean) {
            ArrayDeque coll = new ArrayDeque(2);
            coll.add(existing);
            coll.add(theEvent);
            sorted.put(comparable, coll);
        } else {
            ArrayDeque q = (ArrayDeque) existing;
            q.add(theEvent);
        }
        size++;
    }

    protected static CodegenMethodNode referenceAddToCollCodegen(AggregationStateSortedForge forge, int stateNumber, CodegenMethodNode parent, CodegenNamedMethods namedMethods, CodegenClassScope classScope) {
        CodegenMethodNode getComparable = getComparableCodegen("getComparable_" + stateNumber, forge.getSpec().getCriteria(), namedMethods, classScope);
        CodegenExpressionRef sorted = refCol("sorted", stateNumber);

        CodegenMethodNode method = parent.makeChildWithScope(void.class, AggregationStateSortedImpl.class, CodegenSymbolProviderEmpty.INSTANCE, classScope).addParam(eu.uk.ncl.pet5o.esper.client.EventBean.class, "theEvent").addParam(eu.uk.ncl.pet5o.esper.client.EventBean[].class, NAME_EPS).addParam(ExprEvaluatorContext.class, NAME_EXPREVALCONTEXT);
        method.getBlock().declareVar(Object.class, "comparable", localMethod(getComparable, REF_EPS, constantTrue(), REF_EXPREVALCONTEXT))
                .declareVar(Object.class, "existing", exprDotMethod(sorted, "get", ref("comparable")))
                .ifRefNull("existing")
                    .exprDotMethod(sorted, "put", ref("comparable"), ref("theEvent"))
                .ifElseIf(instanceOf(ref("existing"), eu.uk.ncl.pet5o.esper.client.EventBean.class))
                    .declareVar(ArrayDeque.class, "coll", newInstance(ArrayDeque.class, constant(2)))
                    .exprDotMethod(ref("coll"), "add", ref("existing"))
                    .exprDotMethod(ref("coll"), "add", ref("theEvent"))
                    .exprDotMethod(sorted, "put", ref("comparable"), ref("coll"))
                .ifElse()
                    .declareVar(ArrayDeque.class, "q", cast(ArrayDeque.class, ref("existing")))
                    .exprDotMethod(ref("q"), "add", ref("theEvent"))
                .blockEnd()
                .increment(refCol("size", stateNumber));

        return method;
    }

    protected void dereferenceRemove(eu.uk.ncl.pet5o.esper.client.EventBean theEvent, eu.uk.ncl.pet5o.esper.client.EventBean[] eventsPerStream, ExprEvaluatorContext exprEvaluatorContext) {
        if (dereferenceEvent(theEvent)) {
            dereferenceEventRemoveFromColl(theEvent, eventsPerStream, exprEvaluatorContext);
        }
    }

    private void dereferenceEventRemoveFromColl(eu.uk.ncl.pet5o.esper.client.EventBean theEvent, eu.uk.ncl.pet5o.esper.client.EventBean[] eventsPerStream, ExprEvaluatorContext exprEvaluatorContext) {
        Object comparable = getComparable(spec.getCriteria(), eventsPerStream, false, exprEvaluatorContext);
        Object existing = sorted.get(comparable);
        if (existing == null) {
            return;
        }
        if (existing.equals(theEvent)) {
            sorted.remove(comparable);
            size--;
        } else if (existing instanceof ArrayDeque) {
            ArrayDeque q = (ArrayDeque) existing;
            q.remove(theEvent);
            if (q.isEmpty()) {
                sorted.remove(comparable);
            }
            size--;
        }
    }

    protected static CodegenMethodNode dereferenceRemoveFromCollCodegen(AggregationStateSortedForge forge, int stateNumber, CodegenMethodNode parent, CodegenNamedMethods namedMethods, CodegenClassScope classScope) {
        CodegenMethodNode getComparable = getComparableCodegen("getComparable_" + stateNumber, forge.getSpec().getCriteria(), namedMethods, classScope);
        CodegenExpressionRef sorted = refCol("sorted", stateNumber);

        CodegenMethodNode method = parent.makeChildWithScope(void.class, AggregationStateSortedImpl.class, CodegenSymbolProviderEmpty.INSTANCE, classScope).addParam(eu.uk.ncl.pet5o.esper.client.EventBean.class, "theEvent").addParam(eu.uk.ncl.pet5o.esper.client.EventBean[].class, NAME_EPS).addParam(ExprEvaluatorContext.class, NAME_EXPREVALCONTEXT);
        method.getBlock().declareVar(Object.class, "comparable", localMethod(getComparable, REF_EPS, constantTrue(), REF_EXPREVALCONTEXT))
                .declareVar(Object.class, "existing", exprDotMethod(sorted, "get", ref("comparable")))
                .ifRefNull("existing").blockReturnNoValue()
                .ifCondition(exprDotMethod(ref("existing"), "equals", ref("theEvent")))
                    .exprDotMethod(sorted, "remove", ref("comparable"))
                    .decrement(refCol("size", stateNumber))
                .ifElseIf(instanceOf(ref("existing"), ArrayDeque.class))
                    .declareVar(ArrayDeque.class, "q", cast(ArrayDeque.class, ref("existing")))
                    .exprDotMethod(ref("q"), "remove", ref("theEvent"))
                    .ifCondition(exprDotMethod(ref("q"), "isEmpty"))
                            .exprDotMethod(sorted, "remove", ref("comparable"))
                    .blockEnd()
                    .decrement(refCol("size", stateNumber));

        return method;
    }

    /**
     * NOTE: Code-generation-invoked method, method name and parameter order matters
     * @param value payload to check
     * @return bean
     */
    public final static eu.uk.ncl.pet5o.esper.client.EventBean checkedPayloadMayDeque(Object value) {
        if (value instanceof eu.uk.ncl.pet5o.esper.client.EventBean) {
            return (eu.uk.ncl.pet5o.esper.client.EventBean) value;
        }
        ArrayDeque<EventBean> q = (ArrayDeque<EventBean>) value;
        return q.getFirst();
    }
}
