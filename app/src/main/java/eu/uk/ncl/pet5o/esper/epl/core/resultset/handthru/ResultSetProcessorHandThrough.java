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
package eu.uk.ncl.pet5o.esper.epl.core.resultset.handthru;

import com.espertech.esper.client.EventBean;
import com.espertech.esper.client.EventType;
import com.espertech.esper.codegen.base.CodegenMethodNode;
import com.espertech.esper.codegen.model.expression.CodegenExpression;
import com.espertech.esper.collection.ArrayEventIterator;
import com.espertech.esper.collection.MultiKey;
import com.espertech.esper.collection.TransformEventIterator;
import com.espertech.esper.collection.UniformPair;
import com.espertech.esper.core.context.util.AgentInstanceContext;
import com.espertech.esper.epl.core.resultset.core.ResultSetProcessor;
import com.espertech.esper.epl.core.resultset.core.ResultSetProcessorOutputHelperVisitor;
import com.espertech.esper.epl.core.select.SelectExprProcessor;
import com.espertech.esper.epl.view.OutputProcessViewConditionLastAllUnord;
import com.espertech.esper.view.Viewable;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import static com.espertech.esper.codegen.model.expression.CodegenExpressionBuilder.*;
import static com.espertech.esper.codegen.model.expression.CodegenExpressionBuilder.cast;
import static com.espertech.esper.codegen.model.expression.CodegenExpressionBuilder.constant;
import static com.espertech.esper.codegen.model.expression.CodegenExpressionBuilder.constantNull;
import static com.espertech.esper.codegen.model.expression.CodegenExpressionBuilder.exprDotMethod;
import static com.espertech.esper.codegen.model.expression.CodegenExpressionBuilder.newInstance;
import static com.espertech.esper.codegen.model.expression.CodegenExpressionBuilder.ref;
import static com.espertech.esper.codegen.model.expression.CodegenExpressionBuilder.staticMethod;
import static com.espertech.esper.epl.core.resultset.codegen.ResultSetProcessorCodegenNames.*;
import static com.espertech.esper.epl.core.resultset.codegen.ResultSetProcessorCodegenNames.REF_AGENTINSTANCECONTEXT;
import static com.espertech.esper.epl.core.resultset.codegen.ResultSetProcessorCodegenNames.REF_ISSYNTHESIZE;
import static com.espertech.esper.epl.core.resultset.codegen.ResultSetProcessorCodegenNames.REF_JOINSET;
import static com.espertech.esper.epl.core.resultset.codegen.ResultSetProcessorCodegenNames.REF_NEWDATA;
import static com.espertech.esper.epl.core.resultset.codegen.ResultSetProcessorCodegenNames.REF_OLDDATA;
import static com.espertech.esper.epl.core.resultset.codegen.ResultSetProcessorCodegenNames.REF_SELECTEXPRPROCESSOR;
import static com.espertech.esper.epl.core.resultset.codegen.ResultSetProcessorCodegenNames.REF_VIEWABLE;
import static com.espertech.esper.epl.core.resultset.handthru.ResultSetProcessorHandThroughUtil.*;
import static com.espertech.esper.epl.core.resultset.handthru.ResultSetProcessorHandThroughUtil.METHOD_GETSELECTEVENTSNOHAVINGHANDTHRUJOIN;
import static com.espertech.esper.epl.core.resultset.handthru.ResultSetProcessorHandThroughUtil.METHOD_GETSELECTEVENTSNOHAVINGHANDTHRUVIEW;
import static com.espertech.esper.epl.core.resultset.handthru.ResultSetProcessorHandThroughUtil.getSelectEventsNoHavingHandThruJoin;
import static com.espertech.esper.epl.core.resultset.handthru.ResultSetProcessorHandThroughUtil.getSelectEventsNoHavingHandThruView;

/**
 * Result set processor for the hand-through case:
 * no aggregation functions used in the select clause, and no group-by, no having and ordering.
 */
public class ResultSetProcessorHandThrough implements ResultSetProcessor {
    private final ResultSetProcessorHandThroughFactoryForge prototype;
    private final SelectExprProcessor selectExprProcessor;
    private AgentInstanceContext agentInstanceContext;

    ResultSetProcessorHandThrough(ResultSetProcessorHandThroughFactoryForge prototype, SelectExprProcessor selectExprProcessor, AgentInstanceContext agentInstanceContext) {
        this.prototype = prototype;
        this.selectExprProcessor = selectExprProcessor;
        this.agentInstanceContext = agentInstanceContext;
    }

    public void setAgentInstanceContext(AgentInstanceContext agentInstanceContext) {
        this.agentInstanceContext = agentInstanceContext;
    }

    public EventType getResultEventType() {
        return prototype.getResultEventType();
    }

    public UniformPair<com.espertech.esper.client.EventBean[]> processJoinResult(Set<MultiKey<EventBean>> newEvents, Set<MultiKey<EventBean>> oldEvents, boolean isSynthesize) {
        com.espertech.esper.client.EventBean[] selectOldEvents = null;
        com.espertech.esper.client.EventBean[] selectNewEvents;

        if (prototype.isSelectRStream()) {
            selectOldEvents = getSelectEventsNoHavingHandThruJoin(selectExprProcessor, oldEvents, false, isSynthesize, agentInstanceContext);
        }
        selectNewEvents = getSelectEventsNoHavingHandThruJoin(selectExprProcessor, newEvents, true, isSynthesize, agentInstanceContext);

        return new UniformPair<>(selectNewEvents, selectOldEvents);
    }

    static void processJoinResultCodegen(ResultSetProcessorHandThroughFactoryForge prototype, CodegenMethodNode method) {
        CodegenExpression oldEvents = constantNull();
        if (prototype.isSelectRStream()) {
            oldEvents = staticMethod(ResultSetProcessorHandThroughUtil.class, METHOD_GETSELECTEVENTSNOHAVINGHANDTHRUJOIN, REF_SELECTEXPRPROCESSOR, REF_OLDDATA, constant(false), REF_ISSYNTHESIZE, REF_AGENTINSTANCECONTEXT);
        }
        CodegenExpression newEvents = staticMethod(ResultSetProcessorHandThroughUtil.class, METHOD_GETSELECTEVENTSNOHAVINGHANDTHRUJOIN, REF_SELECTEXPRPROCESSOR, REF_NEWDATA, constant(true), REF_ISSYNTHESIZE, REF_AGENTINSTANCECONTEXT);

        method.getBlock()
                .declareVar(com.espertech.esper.client.EventBean[].class, "selectOldEvents", oldEvents)
                .declareVar(com.espertech.esper.client.EventBean[].class, "selectNewEvents", newEvents)
                .methodReturn(newInstance(UniformPair.class, ref("selectNewEvents"), ref("selectOldEvents")));
    }

    public UniformPair<com.espertech.esper.client.EventBean[]> processViewResult(com.espertech.esper.client.EventBean[] newData, com.espertech.esper.client.EventBean[] oldData, boolean isSynthesize) {
        com.espertech.esper.client.EventBean[] selectOldEvents = null;

        if (prototype.isSelectRStream()) {
            selectOldEvents = getSelectEventsNoHavingHandThruView(selectExprProcessor, oldData, false, isSynthesize, agentInstanceContext);
        }
        com.espertech.esper.client.EventBean[] selectNewEvents = getSelectEventsNoHavingHandThruView(selectExprProcessor, newData, true, isSynthesize, agentInstanceContext);

        return new UniformPair<>(selectNewEvents, selectOldEvents);
    }

    static void processViewResultCodegen(ResultSetProcessorHandThroughFactoryForge prototype, CodegenMethodNode method) {
        CodegenExpression oldEvents = constantNull();
        if (prototype.isSelectRStream()) {
            oldEvents = staticMethod(ResultSetProcessorHandThroughUtil.class, METHOD_GETSELECTEVENTSNOHAVINGHANDTHRUVIEW, REF_SELECTEXPRPROCESSOR, REF_OLDDATA, constant(false), REF_ISSYNTHESIZE, REF_AGENTINSTANCECONTEXT);
        }
        CodegenExpression newEvents = staticMethod(ResultSetProcessorHandThroughUtil.class, METHOD_GETSELECTEVENTSNOHAVINGHANDTHRUVIEW, REF_SELECTEXPRPROCESSOR, REF_NEWDATA, constant(true), REF_ISSYNTHESIZE, REF_AGENTINSTANCECONTEXT);

        method.getBlock()
                .declareVar(com.espertech.esper.client.EventBean[].class, "selectOldEvents", oldEvents)
                .declareVar(com.espertech.esper.client.EventBean[].class, "selectNewEvents", newEvents)
                .methodReturn(newInstance(UniformPair.class, ref("selectNewEvents"), ref("selectOldEvents")));
    }

    public void clear() {
        // No need to clear state, there is no state held
    }

    public Iterator<EventBean> getIterator(Viewable parent) {
        // Return an iterator that gives row-by-row a result
        return new TransformEventIterator(parent.iterator(), new ResultSetProcessorHandtruTransform(this));
    }

    static void getIteratorViewCodegen(CodegenMethodNode methodNode) {
        methodNode.getBlock().methodReturn(newInstance(TransformEventIterator.class, exprDotMethod(REF_VIEWABLE, "iterator"), newInstance(ResultSetProcessorHandtruTransform.class, ref("this"))));
    }

    public Iterator<EventBean> getIterator(Set<MultiKey<EventBean>> joinSet) {
        // Process join results set as a regular join, includes sorting and having-clause filter
        UniformPair<com.espertech.esper.client.EventBean[]> result = processJoinResult(joinSet, Collections.emptySet(), true);
        return new ArrayEventIterator(result.getFirst());
    }

    static void getIteratorJoinCodegen(CodegenMethodNode method) {
        method.getBlock()
                .declareVar(UniformPair.class, com.espertech.esper.client.EventBean[].class, "result", exprDotMethod(ref("this"), "processJoinResult", REF_JOINSET, staticMethod(Collections.class, "emptySet"), constant(true)))
                .methodReturn(newInstance(ArrayEventIterator.class, cast(com.espertech.esper.client.EventBean[].class, exprDotMethod(ref("result"), "getFirst"))));
    }

    public void applyViewResult(com.espertech.esper.client.EventBean[] newData, com.espertech.esper.client.EventBean[] oldData) {
    }

    public void applyJoinResult(Set<MultiKey<EventBean>> newEvents, Set<MultiKey<EventBean>> oldEvents) {
    }

    public void processOutputLimitedLastAllNonBufferedView(com.espertech.esper.client.EventBean[] newData, com.espertech.esper.client.EventBean[] oldData, boolean isGenerateSynthetic) {
    }

    public void processOutputLimitedLastAllNonBufferedJoin(Set<MultiKey<EventBean>> newEvents, Set<MultiKey<EventBean>> oldEvents, boolean isGenerateSynthetic) {
    }

    public UniformPair<com.espertech.esper.client.EventBean[]> continueOutputLimitedLastAllNonBufferedView(boolean isSynthesize) {
        return null;
    }

    public UniformPair<com.espertech.esper.client.EventBean[]> continueOutputLimitedLastAllNonBufferedJoin(boolean isSynthesize) {
        return null;
    }

    public void stop() {
        // no action required
    }

    public void acceptHelperVisitor(ResultSetProcessorOutputHelperVisitor visitor) {
        // nothing to visit
    }

    public UniformPair<com.espertech.esper.client.EventBean[]> processOutputLimitedJoin(List<UniformPair<Set<MultiKey<EventBean>>>> joinEventsSet, boolean generateSynthetic) {
        throw new IllegalStateException("Output last is provided by " + OutputProcessViewConditionLastAllUnord.class.getSimpleName());
    }

    public UniformPair<com.espertech.esper.client.EventBean[]> processOutputLimitedView(List<UniformPair<EventBean[]>> viewEventsList, boolean generateSynthetic) {
        throw new IllegalStateException("Output last is provided by " + OutputProcessViewConditionLastAllUnord.class.getSimpleName());
    }
}
