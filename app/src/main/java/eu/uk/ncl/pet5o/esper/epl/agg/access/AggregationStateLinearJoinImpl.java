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
import eu.uk.ncl.pet5o.esper.codegen.base.CodegenMembersColumnized;
import eu.uk.ncl.pet5o.esper.codegen.base.CodegenMethodNode;
import eu.uk.ncl.pet5o.esper.codegen.base.CodegenSymbolProviderEmpty;
import eu.uk.ncl.pet5o.esper.codegen.core.CodegenCtor;
import eu.uk.ncl.pet5o.esper.codegen.core.CodegenNamedMethods;
import eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpression;
import eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionRef;
import eu.uk.ncl.pet5o.esper.collection.ArrayEventIterator;
import eu.uk.ncl.pet5o.esper.epl.agg.factory.AggregationStateLinearForge;
import eu.uk.ncl.pet5o.esper.epl.expression.codegen.ExprForgeCodegenSymbol;
import eu.uk.ncl.pet5o.esper.epl.expression.core.ExprEvaluatorContext;
import eu.uk.ncl.pet5o.esper.util.CollectionUtil;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;

import static eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionBuilder.*;
import static eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionBuilder.arrayAtIndex;
import static eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionBuilder.arrayLength;
import static eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionBuilder.cast;
import static eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionBuilder.constant;
import static eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionBuilder.constantNull;
import static eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionBuilder.equalsIdentity;
import static eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionBuilder.equalsNull;
import static eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionBuilder.exprDotMethod;
import static eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionBuilder.exprDotMethodChain;
import static eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionBuilder.localMethod;
import static eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionBuilder.newInstance;
import static eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionBuilder.op;
import static eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionBuilder.ref;
import static eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionBuilder.refCol;
import static eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionBuilder.relational;
import static eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionRelational.CodegenRelational.GE;
import static eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionRelational.CodegenRelational.LT;
import static eu.uk.ncl.pet5o.esper.epl.agg.aggregator.AggregatorCodegenUtil.prefixWithFilterCheck;
import static eu.uk.ncl.pet5o.esper.util.CollectionUtil.METHOD_TOARRAYEVENTS;

/**
 * Implementation of access function for joins.
 */
public class AggregationStateLinearJoinImpl implements AggregationStateWithSize, AggregationStateLinear {
    protected int streamId;
    protected LinkedHashMap<EventBean, Integer> refSet = new LinkedHashMap<EventBean, Integer>();
    private eu.uk.ncl.pet5o.esper.client.EventBean[] array;

    /**
     * Ctor.
     *
     * @param streamId stream id
     */
    public AggregationStateLinearJoinImpl(int streamId) {
        this.streamId = streamId;
    }

    public static void rowMemberCodegen(int stateNumber, CodegenCtor ctor, CodegenMembersColumnized membersColumnized) {
        membersColumnized.addMember(stateNumber, LinkedHashMap.class, "refSet");
        membersColumnized.addMember(stateNumber, eu.uk.ncl.pet5o.esper.client.EventBean[].class, "array");
        ctor.getBlock().assignRef(refCol("refSet", stateNumber), newInstance(LinkedHashMap.class));
    }

    public void applyEnter(eu.uk.ncl.pet5o.esper.client.EventBean[] eventsPerStream, ExprEvaluatorContext exprEvaluatorContext) {
        eu.uk.ncl.pet5o.esper.client.EventBean theEvent = eventsPerStream[streamId];
        if (theEvent == null) {
            return;
        }
        addEvent(theEvent);
    }

    public static void applyEnterCodegen(AggregationStateLinearForge forge, int stateNumber, CodegenMethodNode method, ExprForgeCodegenSymbol symbols, CodegenClassScope classScope) {
        if (forge.getOptionalFilter() != null) {
            prefixWithFilterCheck(forge.getOptionalFilter(), method, symbols, classScope);
        }
        CodegenExpression eps = symbols.getAddEPS(method);
        CodegenMethodNode addEvent = addEventCodegen(stateNumber, method, classScope);
        method.getBlock().declareVar(eu.uk.ncl.pet5o.esper.client.EventBean.class, "theEvent", arrayAtIndex(eps, constant(forge.getStreamNum())))
                .ifRefNull("theEvent").blockReturnNoValue()
                .localMethod(addEvent, ref("theEvent"));
    }

    public void applyLeave(eu.uk.ncl.pet5o.esper.client.EventBean[] eventsPerStream, ExprEvaluatorContext exprEvaluatorContext) {
        eu.uk.ncl.pet5o.esper.client.EventBean theEvent = eventsPerStream[streamId];
        if (theEvent == null) {
            return;
        }
        removeEvent(theEvent);
    }

    public static void applyLeaveCodegen(AggregationStateLinearForge forge, int stateNumber, CodegenMethodNode method, ExprForgeCodegenSymbol symbols, CodegenClassScope classScope) {
        if (forge.getOptionalFilter() != null) {
            prefixWithFilterCheck(forge.getOptionalFilter(), method, symbols, classScope);
        }
        CodegenExpression eps = symbols.getAddEPS(method);
        CodegenMethodNode removeEvent = removeEventCodegen(stateNumber, method, classScope);
        method.getBlock().declareVar(eu.uk.ncl.pet5o.esper.client.EventBean.class, "theEvent", arrayAtIndex(eps, constant(forge.getStreamNum())))
                .ifRefNull("theEvent").blockReturnNoValue()
                .localMethod(removeEvent, ref("theEvent"));
    }

    public void clear() {
        refSet.clear();
        array = null;
    }

    public static void clearCodegen(int stateNumber, CodegenMethodNode method) {
        method.getBlock().exprDotMethod(refCol("refSet", stateNumber), "clear")
                .assignRef(refCol("array", stateNumber), constantNull());
    }

    public eu.uk.ncl.pet5o.esper.client.EventBean getFirstNthValue(int index) {
        if (index < 0) {
            return null;
        }
        if (refSet.isEmpty()) {
            return null;
        }
        if (index >= refSet.size()) {
            return null;
        }
        if (array == null) {
            initArray();
        }
        return array[index];
    }

    public static CodegenExpression getFirstNthValueCodegen(CodegenExpressionRef index, int slot, CodegenClassScope classScope, CodegenMethodNode parentMethod, CodegenNamedMethods namedMethods) {
        CodegenExpressionRef refSet = refCol("refSet", slot);
        CodegenExpressionRef array = refCol("array", slot);
        CodegenMethodNode initArray = initArrayCodegen(slot, namedMethods, classScope);
        CodegenMethodNode method = parentMethod.makeChildWithScope(eu.uk.ncl.pet5o.esper.client.EventBean.class, AggregationStateLinearJoinImpl.class, CodegenSymbolProviderEmpty.INSTANCE, classScope).addParam(int.class, "index");
        method.getBlock().ifCondition(relational(ref("index"), LT, constant(0))).blockReturn(constantNull())
                .ifCondition(exprDotMethod(refSet, "isEmpty")).blockReturn(constantNull())
                .ifCondition(relational(ref("index"), GE, exprDotMethod(refSet, "size"))).blockReturn(constantNull())
                .ifCondition(equalsNull(array)).localMethod(initArray).blockEnd()
                .methodReturn(arrayAtIndex(array, ref("index")));
        return localMethod(method, index);
    }

    public eu.uk.ncl.pet5o.esper.client.EventBean getLastNthValue(int index) {
        if (index < 0) {
            return null;
        }
        if (refSet.isEmpty()) {
            return null;
        }
        if (index >= refSet.size()) {
            return null;
        }
        if (array == null) {
            initArray();
        }
        return array[array.length - index - 1];
    }

    public static CodegenExpression getLastNthValueCodegen(CodegenExpressionRef index, int slot, CodegenClassScope classScope, CodegenMethodNode parentMethod, CodegenNamedMethods namedMethods) {
        CodegenExpressionRef refSet = refCol("refSet", slot);
        CodegenExpressionRef array = refCol("array", slot);
        CodegenMethodNode initArray = initArrayCodegen(slot, namedMethods, classScope);
        CodegenMethodNode method = parentMethod.makeChildWithScope(eu.uk.ncl.pet5o.esper.client.EventBean.class, AggregationStateLinearJoinImpl.class, CodegenSymbolProviderEmpty.INSTANCE, classScope).addParam(int.class, "index");
        method.getBlock().ifCondition(relational(ref("index"), LT, constant(0))).blockReturn(constantNull())
                .ifCondition(exprDotMethod(refSet, "isEmpty")).blockReturn(constantNull())
                .ifCondition(relational(ref("index"), GE, exprDotMethod(refSet, "size"))).blockReturn(constantNull())
                .ifCondition(equalsNull(array)).localMethod(initArray).blockEnd()
                .methodReturn(arrayAtIndex(array, op(op(arrayLength(array), "-", ref("index")), "-", constant(1))));
        return localMethod(method, index);
    }

    public eu.uk.ncl.pet5o.esper.client.EventBean getFirstValue() {
        if (refSet.isEmpty()) {
            return null;
        }
        return refSet.entrySet().iterator().next().getKey();
    }

    public static CodegenExpression codegenGetFirstValue(int slot, CodegenClassScope classScope, CodegenMethodNode parentMethod) {
        CodegenExpressionRef refSet = refCol("refSet", slot);
        CodegenMethodNode method = parentMethod.makeChildWithScope(eu.uk.ncl.pet5o.esper.client.EventBean.class, AggregationStateLinearJoinImpl.class, CodegenSymbolProviderEmpty.INSTANCE, classScope);
        method.getBlock().ifCondition(exprDotMethod(refSet, "isEmpty")).blockReturn(constantNull())
                .declareVar(Map.Entry.class, "entry", cast(Map.Entry.class, exprDotMethodChain(refSet).add("entrySet").add("iterator").add("next")))
                .methodReturn(cast(eu.uk.ncl.pet5o.esper.client.EventBean.class, exprDotMethod(ref("entry"), "getKey")));
        return localMethod(method);
    }

    public eu.uk.ncl.pet5o.esper.client.EventBean getLastValue() {
        if (refSet.isEmpty()) {
            return null;
        }
        if (array == null) {
            initArray();
        }
        return array[array.length - 1];
    }

    public static CodegenExpression codegenGetLastValue(int slot, CodegenClassScope classScope, CodegenMethodNode parentMethod, CodegenNamedMethods namedMethods) {
        CodegenExpressionRef refSet = refCol("refSet", slot);
        CodegenExpressionRef array = refCol("array", slot);
        CodegenMethodNode initArray = initArrayCodegen(slot, namedMethods, classScope);
        CodegenMethodNode method = parentMethod.makeChildWithScope(eu.uk.ncl.pet5o.esper.client.EventBean.class, AggregationStateLinearJoinImpl.class, CodegenSymbolProviderEmpty.INSTANCE, classScope);
        method.getBlock().ifCondition(exprDotMethod(refSet, "isEmpty")).blockReturn(constantNull())
                .ifCondition(equalsNull(array)).localMethod(initArray).blockEnd()
                .methodReturn(arrayAtIndex(array, op(arrayLength(array), "-", constant(1))));
        return localMethod(method);
    }

    public Iterator<EventBean> iterator() {
        if (array == null) {
            initArray();
        }
        return new ArrayEventIterator(array);
    }

    public static CodegenExpression iteratorCodegen(int slot, CodegenClassScope classScope, CodegenMethodNode parentMethod, CodegenNamedMethods namedMethods) {
        CodegenMethodNode initArray = initArrayCodegen(slot, namedMethods, classScope);
        CodegenMethodNode method = parentMethod.makeChildWithScope(Iterator.class, AggregationStateLinearJoinImpl.class, CodegenSymbolProviderEmpty.INSTANCE, classScope);
        method.getBlock().ifRefNull(refCol("array", slot))
                .localMethod(initArray)
                .blockEnd()
                .methodReturn(newInstance(ArrayEventIterator.class, refCol("array", slot)));
        return localMethod(method);
    }

    public Collection<EventBean> collectionReadOnly() {
        if (array == null) {
            initArray();
        }
        return Arrays.asList(array);
    }

    public static CodegenExpression collectionReadOnlyCodegen(int column, CodegenMethodNode parentMethod, CodegenClassScope classScope, CodegenNamedMethods namedMethods) {
        CodegenMethodNode initArray = initArrayCodegen(column, namedMethods, classScope);
        CodegenMethodNode method = parentMethod.makeChildWithScope(Collection.class, AggregationStateLinearJoinImpl.class, CodegenSymbolProviderEmpty.INSTANCE, classScope);
        method.getBlock().ifRefNull(refCol("array", column))
                .localMethod(initArray)
                .blockEnd()
                .methodReturn(staticMethod(Arrays.class, "asList", refCol("array", column)));
        return localMethod(method);
    }

    public int size() {
        return refSet.size();
    }

    public static CodegenExpression sizeCodegen(AggregationStateLinearForge forge, int slot) {
        return exprDotMethod(refCol("refSet", slot), "size");
    }

    public LinkedHashMap<EventBean, Integer> getRefSet() {
        return refSet;
    }

    protected void addEvent(eu.uk.ncl.pet5o.esper.client.EventBean theEvent) {
        array = null;
        Integer value = refSet.get(theEvent);
        if (value == null) {
            refSet.put(theEvent, 1);
            return;
        }

        value++;
        refSet.put(theEvent, value);
    }

    private static CodegenMethodNode addEventCodegen(int stateNumber, CodegenMethodNode parent, CodegenClassScope classScope) {
        CodegenExpressionRef refSet = refCol("refSet", stateNumber);
        CodegenMethodNode method = parent.makeChildWithScope(void.class, AggregationStateLinearJoinImpl.class, CodegenSymbolProviderEmpty.INSTANCE, classScope).addParam(eu.uk.ncl.pet5o.esper.client.EventBean.class, "theEvent");
        method.getBlock().assignRef(refCol("array", stateNumber), constantNull())
                .declareVar(Integer.class, "value", cast(Integer.class, exprDotMethod(refSet, "get", ref("theEvent"))))
                .ifRefNull("value")
                    .exprDotMethod(refSet, "put", ref("theEvent"), constant(1))
                    .blockReturnNoValue()
                .increment("value")
                .exprDotMethod(refSet, "put", ref("theEvent"), ref("value"));
        return method;
    }

    protected void removeEvent(eu.uk.ncl.pet5o.esper.client.EventBean theEvent) {
        array = null;

        Integer value = refSet.get(theEvent);
        if (value == null) {
            return;
        }

        if (value == 1) {
            refSet.remove(theEvent);
            return;
        }

        value--;
        refSet.put(theEvent, value);
    }

    private static CodegenMethodNode removeEventCodegen(int stateNumber, CodegenMethodNode parent, CodegenClassScope classScope) {
        CodegenExpressionRef refSet = refCol("refSet", stateNumber);
        CodegenMethodNode method = parent.makeChildWithScope(void.class, AggregationStateLinearJoinImpl.class, CodegenSymbolProviderEmpty.INSTANCE, classScope).addParam(eu.uk.ncl.pet5o.esper.client.EventBean.class, "theEvent");
        method.getBlock().assignRef(refCol("array", stateNumber), constantNull())
                .declareVar(Integer.class, "value", cast(Integer.class, exprDotMethod(refSet, "get", ref("theEvent"))))
                .ifRefNull("value").blockReturnNoValue()
                .ifCondition(equalsIdentity(ref("value"), constant(1)))
                    .exprDotMethod(refSet, "remove", ref("theEvent"))
                    .blockReturnNoValue()
                .decrement("value")
                .exprDotMethod(refSet, "put", ref("theEvent"), ref("value"));
        return method;
    }

    private void initArray() {
        Set<EventBean> events = refSet.keySet();
        array = CollectionUtil.toArrayEvents(events);
    }

    private static CodegenMethodNode initArrayCodegen(int slot, CodegenNamedMethods namedMethods, CodegenClassScope classScope) {
        Consumer<CodegenMethodNode> code = method -> {
            method.getBlock().declareVar(Set.class, eu.uk.ncl.pet5o.esper.client.EventBean.class, "events", exprDotMethod(refCol("refSet", slot), "keySet"))
                    .assignRef(refCol("array", slot), staticMethod(CollectionUtil.class, METHOD_TOARRAYEVENTS, ref("events")));
        };
        return namedMethods.addMethod(void.class, "initArray_" + slot, Collections.emptyList(), AggregationStateLinearJoinImpl.class, classScope, code);
    }
}
