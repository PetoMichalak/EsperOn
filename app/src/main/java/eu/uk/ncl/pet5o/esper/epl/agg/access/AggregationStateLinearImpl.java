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
import com.espertech.esper.codegen.base.CodegenMembersColumnized;
import com.espertech.esper.codegen.base.CodegenMethodNode;
import com.espertech.esper.codegen.base.CodegenSymbolProviderEmpty;
import com.espertech.esper.codegen.core.CodegenCtor;
import com.espertech.esper.codegen.model.expression.CodegenExpression;
import com.espertech.esper.codegen.model.expression.CodegenExpressionRef;
import com.espertech.esper.codegen.model.expression.CodegenExpressionRefWCol;
import com.espertech.esper.epl.agg.factory.AggregationStateLinearForge;
import com.espertech.esper.epl.expression.codegen.ExprForgeCodegenSymbol;
import com.espertech.esper.epl.expression.core.ExprEvaluatorContext;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import static com.espertech.esper.codegen.model.expression.CodegenExpressionBuilder.*;
import static com.espertech.esper.codegen.model.expression.CodegenExpressionBuilder.arrayAtIndex;
import static com.espertech.esper.codegen.model.expression.CodegenExpressionBuilder.cast;
import static com.espertech.esper.codegen.model.expression.CodegenExpressionBuilder.constant;
import static com.espertech.esper.codegen.model.expression.CodegenExpressionBuilder.constantNull;
import static com.espertech.esper.codegen.model.expression.CodegenExpressionBuilder.exprDotMethod;
import static com.espertech.esper.codegen.model.expression.CodegenExpressionBuilder.localMethod;
import static com.espertech.esper.codegen.model.expression.CodegenExpressionBuilder.newInstance;
import static com.espertech.esper.codegen.model.expression.CodegenExpressionBuilder.op;
import static com.espertech.esper.codegen.model.expression.CodegenExpressionBuilder.ref;
import static com.espertech.esper.codegen.model.expression.CodegenExpressionBuilder.refCol;
import static com.espertech.esper.codegen.model.expression.CodegenExpressionBuilder.relational;
import static com.espertech.esper.codegen.model.expression.CodegenExpressionRelational.CodegenRelational.GE;
import static com.espertech.esper.codegen.model.expression.CodegenExpressionRelational.CodegenRelational.LT;
import static com.espertech.esper.epl.agg.aggregator.AggregatorCodegenUtil.prefixWithFilterCheck;

/**
 * Implementation of access function for single-stream (not joins).
 */
public class AggregationStateLinearImpl implements AggregationStateWithSize, AggregationStateLinear {
    protected int streamId;
    protected ArrayList<EventBean> events = new ArrayList<>();

    /**
     * Ctor.
     *
     * @param streamId stream id
     */
    public AggregationStateLinearImpl(int streamId) {
        this.streamId = streamId;
    }

    public static void rowMemberCodegen(int stateNumber, CodegenCtor ctor, CodegenMembersColumnized membersColumnized) {
        membersColumnized.addMember(stateNumber, ArrayList.class, "events");
        CodegenExpressionRefWCol events = new CodegenExpressionRefWCol("events", stateNumber);
        ctor.getBlock().assignRef(events, newInstance(ArrayList.class));
    }

    public void applyEnter(com.espertech.esper.client.EventBean[] eventsPerStream, ExprEvaluatorContext exprEvaluatorContext) {
        com.espertech.esper.client.EventBean theEvent = eventsPerStream[streamId];
        if (theEvent == null) {
            return;
        }
        events.add(theEvent);
    }

    public static void applyEnterCodegen(AggregationStateLinearForge forge, int stateNumber, CodegenMethodNode method, ExprForgeCodegenSymbol symbols, CodegenClassScope classScope) {
        if (forge.getOptionalFilter() != null) {
            prefixWithFilterCheck(forge.getOptionalFilter(), method, symbols, classScope);
        }
        CodegenExpressionRef events = refCol("events", stateNumber);
        CodegenExpressionRef eps = symbols.getAddEPS(method);
        method.getBlock()
                .declareVar(com.espertech.esper.client.EventBean.class, "theEvent", arrayAtIndex(eps, constant(forge.getStreamNum())))
                .ifRefNull("theEvent").blockReturnNoValue()
                .exprDotMethod(events, "add", ref("theEvent"));
    }

    public void applyLeave(com.espertech.esper.client.EventBean[] eventsPerStream, ExprEvaluatorContext exprEvaluatorContext) {
        com.espertech.esper.client.EventBean theEvent = eventsPerStream[streamId];
        if (theEvent == null) {
            return;
        }
        events.remove(theEvent);
    }

    public static void applyLeaveCodegen(AggregationStateLinearForge forge, int stateNumber, CodegenMethodNode method, ExprForgeCodegenSymbol symbols, CodegenClassScope classScope) {
        if (forge.getOptionalFilter() != null) {
            prefixWithFilterCheck(forge.getOptionalFilter(), method, symbols, classScope);
        }
        CodegenExpressionRef events = refCol("events", stateNumber);
        CodegenExpressionRef eps = symbols.getAddEPS(method);
        method.getBlock()
                .declareVar(com.espertech.esper.client.EventBean.class, "theEvent", arrayAtIndex(eps, constant(forge.getStreamNum())))
                .ifRefNull("theEvent").blockReturnNoValue()
                .exprDotMethod(events, "remove", ref("theEvent"));
    }

    public void clear() {
        events.clear();
    }

    public static void clearCodegen(int stateNumber, CodegenMethodNode method) {
        method.getBlock().exprDotMethod(refCol("events", stateNumber), "clear");
    }

    public com.espertech.esper.client.EventBean getFirstNthValue(int index) {
        if (index < 0) {
            return null;
        }
        if (index >= events.size()) {
            return null;
        }
        return events.get(index);
    }

    public static CodegenExpression getFirstNthValueCodegen(CodegenExpressionRef index, int column, CodegenClassScope classScope, CodegenMethodNode parentMethod) {
        CodegenExpressionRef events = refCol("events", column);
        CodegenMethodNode method = parentMethod.makeChildWithScope(com.espertech.esper.client.EventBean.class, AggregationStateLinearImpl.class, CodegenSymbolProviderEmpty.INSTANCE, classScope).addParam(int.class, "index");
        method.getBlock().ifCondition(relational(ref("index"), LT, constant(0))).blockReturn(constantNull())
                .ifCondition(relational(ref("index"), GE, exprDotMethod(events, "size"))).blockReturn(constantNull())
                .methodReturn(cast(com.espertech.esper.client.EventBean.class, exprDotMethod(events, "get", ref("index"))));
        return localMethod(method, index);
    }

    public com.espertech.esper.client.EventBean getLastNthValue(int index) {
        if (index < 0) {
            return null;
        }
        if (index >= events.size()) {
            return null;
        }
        return events.get(events.size() - index - 1);
    }

    public static CodegenExpression getLastNthValueCodegen(CodegenExpressionRef index, int column, CodegenClassScope classScope, CodegenMethodNode parentMethod) {
        CodegenExpressionRef events = refCol("events", column);
        CodegenMethodNode method = parentMethod.makeChildWithScope(com.espertech.esper.client.EventBean.class, AggregationStateLinearImpl.class, CodegenSymbolProviderEmpty.INSTANCE, classScope).addParam(int.class, "index");
        method.getBlock().ifCondition(relational(ref("index"), LT, constant(0))).blockReturn(constantNull())
                .ifCondition(relational(ref("index"), GE, exprDotMethod(events, "size"))).blockReturn(constantNull())
                .methodReturn(cast(com.espertech.esper.client.EventBean.class, exprDotMethod(events, "get", op(op(exprDotMethod(events, "size"), "-", ref("index")), "-", constant(1)))));
        return localMethod(method, index);
    }

    public com.espertech.esper.client.EventBean getFirstValue() {
        if (events.isEmpty()) {
            return null;
        }
        return events.get(0);
    }

    public static CodegenExpression codegenGetFirstValue(int column, CodegenClassScope classScope, CodegenMethodNode parentMethod) {
        CodegenExpressionRef events = refCol("events", column);
        CodegenMethodNode method = parentMethod.makeChildWithScope(com.espertech.esper.client.EventBean.class, AggregationStateLinearImpl.class, CodegenSymbolProviderEmpty.INSTANCE, classScope);
        method.getBlock().ifCondition(exprDotMethod(events, "isEmpty")).blockReturn(constantNull())
                .methodReturn(cast(com.espertech.esper.client.EventBean.class, exprDotMethod(events, "get", constant(0))));
        return localMethod(method);
    }

    public com.espertech.esper.client.EventBean getLastValue() {
        if (events.isEmpty()) {
            return null;
        }
        return events.get(events.size() - 1);
    }

    public static CodegenExpression codegenGetLastValue(int column, CodegenClassScope classScope, CodegenMethodNode parentMethod) {
        CodegenExpressionRef events = refCol("events", column);
        CodegenMethodNode method = parentMethod.makeChildWithScope(com.espertech.esper.client.EventBean.class, AggregationStateLinearImpl.class, CodegenSymbolProviderEmpty.INSTANCE, classScope);
        method.getBlock().ifCondition(exprDotMethod(events, "isEmpty")).blockReturn(constantNull())
                .methodReturn(cast(com.espertech.esper.client.EventBean.class, exprDotMethod(events, "get", op(exprDotMethod(events, "size"), "-", constant(1)))));
        return localMethod(method);
    }

    public Iterator<EventBean> iterator() {
        return events.iterator();
    }

    public static CodegenExpression codegenIterator(int slot) {
        CodegenExpressionRefWCol events = new CodegenExpressionRefWCol("events", slot);
        return exprDotMethod(events, "iterator");
    }

    public Collection<EventBean> collectionReadOnly() {
        return events;
    }

    public static CodegenExpression collectionReadOnlyCodegen(int column) {
        return refCol("events", column);
    }

    public int size() {
        return events.size();
    }

    public static CodegenExpression codegenSize(int slot) {
        return exprDotMethod(new CodegenExpressionRefWCol("events", slot), "size");
    }

    public ArrayList<EventBean> getEvents() {
        return events;
    }
}
