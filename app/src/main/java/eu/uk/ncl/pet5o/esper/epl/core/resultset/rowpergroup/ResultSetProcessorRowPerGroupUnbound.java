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
package eu.uk.ncl.pet5o.esper.epl.core.resultset.rowpergroup;

import eu.uk.ncl.pet5o.esper.client.EventBean;
import eu.uk.ncl.pet5o.esper.codegen.base.CodegenBlock;
import eu.uk.ncl.pet5o.esper.codegen.base.CodegenClassScope;
import eu.uk.ncl.pet5o.esper.codegen.base.CodegenMethodNode;
import eu.uk.ncl.pet5o.esper.codegen.core.CodegenInstanceAux;
import eu.uk.ncl.pet5o.esper.codegen.core.CodegenNamedParam;
import eu.uk.ncl.pet5o.esper.collection.UniformPair;
import eu.uk.ncl.pet5o.esper.core.context.util.AgentInstanceContext;
import eu.uk.ncl.pet5o.esper.epl.agg.service.common.AggregationService;
import eu.uk.ncl.pet5o.esper.epl.core.orderby.OrderByProcessor;
import eu.uk.ncl.pet5o.esper.epl.core.resultset.core.ResultSetProcessorUtil;
import eu.uk.ncl.pet5o.esper.epl.core.resultset.grouped.ResultSetProcessorGroupedUtil;
import eu.uk.ncl.pet5o.esper.epl.core.select.SelectExprProcessor;
import eu.uk.ncl.pet5o.esper.metrics.instrumentation.InstrumentationHelper;
import eu.uk.ncl.pet5o.esper.view.Viewable;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.function.Consumer;

import static eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionBuilder.*;
import static eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionBuilder.and;
import static eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionBuilder.arrayAtIndex;
import static eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionBuilder.arrayLength;
import static eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionBuilder.constant;
import static eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionBuilder.constantFalse;
import static eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionBuilder.constantNull;
import static eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionBuilder.constantTrue;
import static eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionBuilder.equalsIdentity;
import static eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionBuilder.equalsNull;
import static eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionBuilder.exprDotMethod;
import static eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionBuilder.localMethod;
import static eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionBuilder.newArrayByLength;
import static eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionBuilder.newInstance;
import static eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionBuilder.notEqualsNull;
import static eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionBuilder.or;
import static eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionBuilder.ref;
import static eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionBuilder.staticMethod;
import static eu.uk.ncl.pet5o.esper.epl.core.resultset.codegen.ResultSetProcessorCodegenNames.*;
import static eu.uk.ncl.pet5o.esper.epl.core.resultset.codegen.ResultSetProcessorCodegenNames.NAME_ISSYNTHESIZE;
import static eu.uk.ncl.pet5o.esper.epl.core.resultset.codegen.ResultSetProcessorCodegenNames.NAME_NEWDATA;
import static eu.uk.ncl.pet5o.esper.epl.core.resultset.codegen.ResultSetProcessorCodegenNames.REF_AGENTINSTANCECONTEXT;
import static eu.uk.ncl.pet5o.esper.epl.core.resultset.codegen.ResultSetProcessorCodegenNames.REF_AGGREGATIONSVC;
import static eu.uk.ncl.pet5o.esper.epl.core.resultset.codegen.ResultSetProcessorCodegenNames.REF_ISSYNTHESIZE;
import static eu.uk.ncl.pet5o.esper.epl.core.resultset.codegen.ResultSetProcessorCodegenNames.REF_NEWDATA;
import static eu.uk.ncl.pet5o.esper.epl.core.resultset.codegen.ResultSetProcessorCodegenNames.REF_OLDDATA;
import static eu.uk.ncl.pet5o.esper.epl.core.resultset.core.ResultSetProcessorUtil.METHOD_TOPAIRNULLIFALLNULL;
import static eu.uk.ncl.pet5o.esper.epl.core.resultset.grouped.ResultSetProcessorGroupedUtil.generateGroupKeySingleCodegen;
import static eu.uk.ncl.pet5o.esper.epl.expression.codegen.ExprForgeCodegenNames.NAME_EPS;
import static eu.uk.ncl.pet5o.esper.epl.expression.codegen.ExprForgeCodegenNames.REF_EPS;

public class ResultSetProcessorRowPerGroupUnbound extends ResultSetProcessorRowPerGroupImpl {

    private final ResultSetProcessorRowPerGroupUnboundHelper groupReps;

    ResultSetProcessorRowPerGroupUnbound(ResultSetProcessorRowPerGroupFactory prototype, SelectExprProcessor selectExprProcessor, OrderByProcessor orderByProcessor, AggregationService aggregationService, AgentInstanceContext agentInstanceContext) {
        super(prototype, selectExprProcessor, orderByProcessor, aggregationService, agentInstanceContext);
        groupReps = prototype.getResultSetProcessorHelperFactory().makeRSRowPerGroupUnboundGroupRep(agentInstanceContext, prototype.getGroupKeyTypes());
        aggregationService.setRemovedCallback(groupReps);
    }

    @Override
    public void applyViewResult(eu.uk.ncl.pet5o.esper.client.EventBean[] newData, eu.uk.ncl.pet5o.esper.client.EventBean[] oldData) {
        eu.uk.ncl.pet5o.esper.client.EventBean[] eventsPerStream = new eu.uk.ncl.pet5o.esper.client.EventBean[1];
        if (newData != null) {
            for (eu.uk.ncl.pet5o.esper.client.EventBean aNewData : newData) {
                eventsPerStream[0] = aNewData;
                Object mk = generateGroupKeySingle(eventsPerStream, true);
                groupReps.put(mk, aNewData);
                aggregationService.applyEnter(eventsPerStream, mk, agentInstanceContext);
            }
        }
        if (oldData != null) {
            for (eu.uk.ncl.pet5o.esper.client.EventBean anOldData : oldData) {
                eventsPerStream[0] = anOldData;
                Object mk = generateGroupKeySingle(eventsPerStream, false);
                aggregationService.applyLeave(eventsPerStream, mk, agentInstanceContext);
            }
        }
    }

    public static void applyViewResultCodegen(ResultSetProcessorRowPerGroupForge forge, CodegenClassScope classScope, CodegenMethodNode method, CodegenInstanceAux instance) {
        CodegenMethodNode generateGroupKeyViewSingle = generateGroupKeySingleCodegen(forge.getGroupKeyNodeExpressions(), classScope, instance);

        method.getBlock().declareVar(eu.uk.ncl.pet5o.esper.client.EventBean[].class, NAME_EPS, newArrayByLength(eu.uk.ncl.pet5o.esper.client.EventBean.class, constant(1)));

        {
            CodegenBlock ifNew = method.getBlock().ifCondition(notEqualsNull(REF_NEWDATA));
            {
                CodegenBlock newLoop = ifNew.forEach(eu.uk.ncl.pet5o.esper.client.EventBean.class, "aNewData", REF_NEWDATA);
                newLoop.assignArrayElement(NAME_EPS, constant(0), ref("aNewData"))
                        .declareVar(Object.class, "mk", localMethod(generateGroupKeyViewSingle, REF_EPS, constantTrue()))
                        .exprDotMethod(ref("groupReps"), "put", ref("mk"), ref("aNewData"))
                        .exprDotMethod(REF_AGGREGATIONSVC, "applyEnter", REF_EPS, ref("mk"), REF_AGENTINSTANCECONTEXT);
            }
        }

        {
            CodegenBlock ifOld = method.getBlock().ifCondition(notEqualsNull(REF_OLDDATA));
            {
                CodegenBlock oldLoop = ifOld.forEach(eu.uk.ncl.pet5o.esper.client.EventBean.class, "anOldData", REF_OLDDATA);
                oldLoop.assignArrayElement(NAME_EPS, constant(0), ref("anOldData"))
                        .declareVar(Object.class, "mk", localMethod(generateGroupKeyViewSingle, REF_EPS, constantFalse()))
                        .exprDotMethod(REF_AGGREGATIONSVC, "applyLeave", REF_EPS, ref("mk"), REF_AGENTINSTANCECONTEXT);
            }
        }
    }

    @Override
    public UniformPair<eu.uk.ncl.pet5o.esper.client.EventBean[]> processViewResult(eu.uk.ncl.pet5o.esper.client.EventBean[] newData, eu.uk.ncl.pet5o.esper.client.EventBean[] oldData, boolean isSynthesize) {

        if (InstrumentationHelper.ENABLED) {
            InstrumentationHelper.get().qResultSetProcessGroupedRowPerGroup();
        }

        if (newData != null && newData.length == 1) {
            if (oldData == null || oldData.length == 0) {
                UniformPair<eu.uk.ncl.pet5o.esper.client.EventBean[]> pair = processViewResultNewDepthOneUnbound(newData, isSynthesize);
                if (InstrumentationHelper.ENABLED) {
                    InstrumentationHelper.get().aResultSetProcessGroupedRowPerGroup(pair);
                }
                return pair;
            }
        }

        // Generate group-by keys for all events, collect all keys in a set for later event generation
        Map<Object, EventBean> keysAndEvents = new HashMap<>();
        eu.uk.ncl.pet5o.esper.client.EventBean[] eventsPerStream = new eu.uk.ncl.pet5o.esper.client.EventBean[1];

        Object[] newDataMultiKey = generateGroupKeysKeepEvent(newData, keysAndEvents, true, eventsPerStream);
        Object[] oldDataMultiKey = generateGroupKeysKeepEvent(oldData, keysAndEvents, false, eventsPerStream);

        eu.uk.ncl.pet5o.esper.client.EventBean[] selectOldEvents = null;
        if (prototype.isSelectRStream()) {
            selectOldEvents = generateOutputEventsView(keysAndEvents, false, isSynthesize, eventsPerStream);
        }

        // update aggregates
        if (newData != null) {
            // apply new data to aggregates
            for (int i = 0; i < newData.length; i++) {
                eventsPerStream[0] = newData[i];
                groupReps.put(newDataMultiKey[i], eventsPerStream[0]);
                aggregationService.applyEnter(eventsPerStream, newDataMultiKey[i], agentInstanceContext);
            }
        }
        if (oldData != null) {
            // apply old data to aggregates
            for (int i = 0; i < oldData.length; i++) {
                eventsPerStream[0] = oldData[i];
                aggregationService.applyLeave(eventsPerStream, oldDataMultiKey[i], agentInstanceContext);
            }
        }

        // generate new events using select expressions
        eu.uk.ncl.pet5o.esper.client.EventBean[] selectNewEvents = generateOutputEventsView(keysAndEvents, true, isSynthesize, eventsPerStream);

        if (InstrumentationHelper.ENABLED) {
            InstrumentationHelper.get().aResultSetProcessGroupedRowPerGroup(selectNewEvents, selectOldEvents);
        }

        return ResultSetProcessorUtil.toPairNullIfAllNull(selectNewEvents, selectOldEvents);
    }

    static void processViewResultUnboundCodegen(ResultSetProcessorRowPerGroupForge forge, CodegenClassScope classScope, CodegenMethodNode method, CodegenInstanceAux instance) {
        CodegenMethodNode generateGroupKeysKeepEvent = generateGroupKeysKeepEventCodegen(forge, classScope, instance);
        CodegenMethodNode generateOutputEventsView = generateOutputEventsViewCodegen(forge, classScope, instance);
        CodegenMethodNode processViewResultNewDepthOneUnbound = processViewResultNewDepthOneUnboundCodegen(forge, classScope, instance);

        CodegenBlock ifShortcut = method.getBlock().ifCondition(and(notEqualsNull(REF_NEWDATA), equalsIdentity(arrayLength(REF_NEWDATA), constant(1))));
        ifShortcut.ifCondition(or(equalsNull(REF_OLDDATA), equalsIdentity(arrayLength(REF_OLDDATA), constant(0))))
                .blockReturn(localMethod(processViewResultNewDepthOneUnbound, REF_NEWDATA, REF_ISSYNTHESIZE));

        method.getBlock().declareVar(Map.class, "keysAndEvents", newInstance(HashMap.class))
                .declareVar(eu.uk.ncl.pet5o.esper.client.EventBean[].class, NAME_EPS, newArrayByLength(eu.uk.ncl.pet5o.esper.client.EventBean.class, constant(1)))
                .declareVar(Object[].class, "newDataMultiKey", localMethod(generateGroupKeysKeepEvent, REF_NEWDATA, ref("keysAndEvents"), constantTrue(), REF_EPS))
                .declareVar(Object[].class, "oldDataMultiKey", localMethod(generateGroupKeysKeepEvent, REF_OLDDATA, ref("keysAndEvents"), constantFalse(), REF_EPS))
                .declareVar(eu.uk.ncl.pet5o.esper.client.EventBean[].class, "selectOldEvents", forge.isSelectRStream() ? localMethod(generateOutputEventsView, ref("keysAndEvents"), constantFalse(), REF_ISSYNTHESIZE, REF_EPS) : constantNull());

        {
            CodegenBlock ifNew = method.getBlock().ifCondition(notEqualsNull(REF_NEWDATA));
            {
                CodegenBlock newLoop = ifNew.forLoopIntSimple("i", arrayLength(REF_NEWDATA));
                newLoop.assignArrayElement(NAME_EPS, constant(0), arrayAtIndex(REF_NEWDATA, ref("i")))
                        .exprDotMethod(ref("groupReps"), "put", arrayAtIndex(ref("newDataMultiKey"), ref("i")), arrayAtIndex(REF_EPS, constant(0)))
                        .exprDotMethod(REF_AGGREGATIONSVC, "applyEnter", REF_EPS, arrayAtIndex(ref("newDataMultiKey"), ref("i")), REF_AGENTINSTANCECONTEXT);
            }
        }

        {
            CodegenBlock ifOld = method.getBlock().ifCondition(notEqualsNull(REF_OLDDATA));
            {
                CodegenBlock newLoop = ifOld.forLoopIntSimple("i", arrayLength(REF_OLDDATA));
                newLoop.assignArrayElement(NAME_EPS, constant(0), arrayAtIndex(REF_OLDDATA, ref("i")))
                        .exprDotMethod(REF_AGGREGATIONSVC, "applyLeave", REF_EPS, arrayAtIndex(ref("oldDataMultiKey"), ref("i")), REF_AGENTINSTANCECONTEXT);
            }
        }

        method.getBlock().declareVar(eu.uk.ncl.pet5o.esper.client.EventBean[].class, "selectNewEvents", localMethod(generateOutputEventsView, ref("keysAndEvents"), constantTrue(), REF_ISSYNTHESIZE, REF_EPS))
                .methodReturn(staticMethod(ResultSetProcessorUtil.class, METHOD_TOPAIRNULLIFALLNULL, ref("selectNewEvents"), ref("selectOldEvents")));
    }

    @Override
    public Iterator<EventBean> getIterator(Viewable parent) {
        if (orderByProcessor == null) {
            Iterator<EventBean> it = groupReps.valueIterator();
            return new ResultSetProcessorRowPerGroupIterator(it, this, aggregationService, agentInstanceContext);
        }
        return getIteratorSorted(groupReps.valueIterator());
    }

    public static void getIteratorViewUnboundedCodegen(ResultSetProcessorRowPerGroupForge forge, CodegenClassScope classScope, CodegenMethodNode method, CodegenInstanceAux instance) {
        if (!forge.isSorting()) {
            method.getBlock().declareVar(Iterator.class, "it", exprDotMethod(ref("groupReps"), "valueIterator"))
                    .methodReturn(newInstance(ResultSetProcessorRowPerGroupIterator.class, ref("it"), ref("this"), REF_AGGREGATIONSVC, REF_AGENTINSTANCECONTEXT));
        } else {
            CodegenMethodNode getIteratorSorted = getIteratorSortedCodegen(forge, classScope, instance);
            method.getBlock().methodReturn(localMethod(getIteratorSorted, exprDotMethod(ref("groupReps"), "valueIterator")));
        }
    }

    @Override
    public void stop() {
        super.stop();
        groupReps.destroy();
    }

    public static void stopMethodCodegenUnbound(ResultSetProcessorRowPerGroupForge forge, CodegenClassScope classScope, CodegenMethodNode method, CodegenInstanceAux instance) {
        ResultSetProcessorRowPerGroupImpl.stopMethodCodegenBound(method, instance);
        method.getBlock().exprDotMethod(ref("groupReps"), "destroy");
    }

    private UniformPair<eu.uk.ncl.pet5o.esper.client.EventBean[]> processViewResultNewDepthOneUnbound(eu.uk.ncl.pet5o.esper.client.EventBean[] newData, boolean isSynthesize) {
        Object groupKey = generateGroupKeySingle(newData, true);
        eu.uk.ncl.pet5o.esper.client.EventBean rstream = !prototype.isSelectRStream() ? null : shortcutEvalGivenKey(newData, groupKey, false, isSynthesize);   // using newdata to compute is safe here
        aggregationService.applyEnter(newData, groupKey, agentInstanceContext);
        groupReps.put(groupKey, newData[0]);
        eu.uk.ncl.pet5o.esper.client.EventBean istream = shortcutEvalGivenKey(newData, groupKey, true, isSynthesize);
        return ResultSetProcessorUtil.toPairNullIfAllNullSingle(istream, rstream);
    }

    static CodegenMethodNode processViewResultNewDepthOneUnboundCodegen(ResultSetProcessorRowPerGroupForge forge, CodegenClassScope classScope, CodegenInstanceAux instance) {
        CodegenMethodNode shortcutEvalGivenKey = shortcutEvalGivenKeyCodegen(forge.getOptionalHavingNode(), classScope, instance);
        CodegenMethodNode generateGroupKeySingle = ResultSetProcessorGroupedUtil.generateGroupKeySingleCodegen(forge.getGroupKeyNodeExpressions(), classScope, instance);

        Consumer<CodegenMethodNode> code = methodNode -> {
            methodNode.getBlock().declareVar(Object.class, "groupKey", localMethod(generateGroupKeySingle, REF_NEWDATA, constantTrue()));
            if (forge.isSelectRStream()) {
                methodNode.getBlock().declareVar(eu.uk.ncl.pet5o.esper.client.EventBean.class, "rstream", localMethod(shortcutEvalGivenKey, REF_NEWDATA, ref("groupKey"), constantFalse(), REF_ISSYNTHESIZE));
            }
            methodNode.getBlock().exprDotMethod(REF_AGGREGATIONSVC, "applyEnter", REF_NEWDATA, ref("groupKey"), REF_AGENTINSTANCECONTEXT)
                    .exprDotMethod(ref("groupReps"), "put", ref("groupKey"), arrayAtIndex(REF_NEWDATA, constant(0)))
                    .declareVar(eu.uk.ncl.pet5o.esper.client.EventBean.class, "istream", localMethod(shortcutEvalGivenKey, REF_NEWDATA, ref("groupKey"), constantTrue(), REF_ISSYNTHESIZE));
            if (forge.isSelectRStream()) {
                methodNode.getBlock().methodReturn(staticMethod(ResultSetProcessorUtil.class, "toPairNullIfAllNullSingle", ref("istream"), ref("rstream")));
            } else {
                methodNode.getBlock().methodReturn(staticMethod(ResultSetProcessorUtil.class, "toPairNullIfNullIStream", ref("istream")));
            }
        };

        return instance.getMethods().addMethod(UniformPair.class, "processViewResultNewDepthOneUnboundCodegen", CodegenNamedParam.from(eu.uk.ncl.pet5o.esper.client.EventBean[].class, NAME_NEWDATA, boolean.class, NAME_ISSYNTHESIZE), ResultSetProcessorRowPerGroupImpl.class, classScope, code);
    }
}
