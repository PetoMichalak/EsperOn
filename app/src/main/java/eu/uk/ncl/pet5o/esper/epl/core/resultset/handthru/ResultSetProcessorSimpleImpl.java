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

import eu.uk.ncl.pet5o.esper.client.EventBean;
import eu.uk.ncl.pet5o.esper.client.EventType;
import eu.uk.ncl.pet5o.esper.codegen.base.CodegenBlock;
import eu.uk.ncl.pet5o.esper.codegen.base.CodegenClassScope;
import eu.uk.ncl.pet5o.esper.codegen.base.CodegenMember;
import eu.uk.ncl.pet5o.esper.codegen.base.CodegenMethodNode;
import eu.uk.ncl.pet5o.esper.codegen.core.CodegenInstanceAux;
import eu.uk.ncl.pet5o.esper.collection.ArrayEventIterator;
import eu.uk.ncl.pet5o.esper.collection.MultiKey;
import eu.uk.ncl.pet5o.esper.collection.TransformEventIterator;
import eu.uk.ncl.pet5o.esper.collection.UniformPair;
import eu.uk.ncl.pet5o.esper.core.context.util.AgentInstanceContext;
import eu.uk.ncl.pet5o.esper.epl.core.orderby.OrderByProcessor;
import eu.uk.ncl.pet5o.esper.epl.core.resultset.core.ResultSetProcessorHelperFactory;
import eu.uk.ncl.pet5o.esper.epl.core.resultset.core.ResultSetProcessorOutputConditionType;
import eu.uk.ncl.pet5o.esper.epl.core.resultset.core.ResultSetProcessorOutputHelperVisitor;
import eu.uk.ncl.pet5o.esper.epl.core.resultset.core.ResultSetProcessorUtil;
import eu.uk.ncl.pet5o.esper.epl.core.select.SelectExprProcessor;
import eu.uk.ncl.pet5o.esper.epl.expression.core.ExprEvaluatorContext;
import eu.uk.ncl.pet5o.esper.epl.view.OutputProcessViewConditionLastAllUnord;
import eu.uk.ncl.pet5o.esper.event.EventBeanUtility;
import eu.uk.ncl.pet5o.esper.metrics.instrumentation.InstrumentationHelper;
import eu.uk.ncl.pet5o.esper.util.CollectionUtil;
import eu.uk.ncl.pet5o.esper.view.Viewable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import static eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionBuilder.*;
import static eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionBuilder.and;
import static eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionBuilder.arrayAtIndex;
import static eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionBuilder.arrayLength;
import static eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionBuilder.cast;
import static eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionBuilder.constant;
import static eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionBuilder.constantNull;
import static eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionBuilder.constantTrue;
import static eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionBuilder.equalsIdentity;
import static eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionBuilder.equalsNull;
import static eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionBuilder.exprDotMethod;
import static eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionBuilder.localMethod;
import static eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionBuilder.member;
import static eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionBuilder.newArrayByLength;
import static eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionBuilder.newInstance;
import static eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionBuilder.not;
import static eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionBuilder.notEqualsNull;
import static eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionBuilder.publicConstValue;
import static eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionBuilder.ref;
import static eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionBuilder.staticMethod;
import static eu.uk.ncl.pet5o.esper.epl.core.resultset.codegen.ResultSetProcessorCodegenNames.*;
import static eu.uk.ncl.pet5o.esper.epl.core.resultset.codegen.ResultSetProcessorCodegenNames.REF_AGENTINSTANCECONTEXT;
import static eu.uk.ncl.pet5o.esper.epl.core.resultset.codegen.ResultSetProcessorCodegenNames.REF_ISSYNTHESIZE;
import static eu.uk.ncl.pet5o.esper.epl.core.resultset.codegen.ResultSetProcessorCodegenNames.REF_JOINEVENTSSET;
import static eu.uk.ncl.pet5o.esper.epl.core.resultset.codegen.ResultSetProcessorCodegenNames.REF_JOINSET;
import static eu.uk.ncl.pet5o.esper.epl.core.resultset.codegen.ResultSetProcessorCodegenNames.REF_NEWDATA;
import static eu.uk.ncl.pet5o.esper.epl.core.resultset.codegen.ResultSetProcessorCodegenNames.REF_OLDDATA;
import static eu.uk.ncl.pet5o.esper.epl.core.resultset.codegen.ResultSetProcessorCodegenNames.REF_ORDERBYPROCESSOR;
import static eu.uk.ncl.pet5o.esper.epl.core.resultset.codegen.ResultSetProcessorCodegenNames.REF_RESULTSETVISITOR;
import static eu.uk.ncl.pet5o.esper.epl.core.resultset.codegen.ResultSetProcessorCodegenNames.REF_SELECTEXPRNONMEMBER;
import static eu.uk.ncl.pet5o.esper.epl.core.resultset.codegen.ResultSetProcessorCodegenNames.REF_SELECTEXPRPROCESSOR;
import static eu.uk.ncl.pet5o.esper.epl.core.resultset.codegen.ResultSetProcessorCodegenNames.REF_VIEWABLE;
import static eu.uk.ncl.pet5o.esper.epl.core.resultset.codegen.ResultSetProcessorCodegenNames.REF_VIEWEVENTSLIST;
import static eu.uk.ncl.pet5o.esper.epl.core.resultset.core.ResultSetProcessorUtil.METHOD_GETSELECTEVENTSNOHAVING;
import static eu.uk.ncl.pet5o.esper.event.EventBeanUtility.METHOD_FLATTENBATCHJOIN;
import static eu.uk.ncl.pet5o.esper.event.EventBeanUtility.METHOD_FLATTENBATCHSTREAM;
import static eu.uk.ncl.pet5o.esper.util.CollectionUtil.METHOD_TOARRAYEVENTS;
import static eu.uk.ncl.pet5o.esper.util.CollectionUtil.METHOD_TOARRAYOBJECTS;

/**
 * Result set processor for the simplest case: no aggregation functions used in the select clause, and no group-by.
 * <p>
 * The processor generates one row for each event entering (new event) and one row for each event leaving (old event).
 */
public class ResultSetProcessorSimpleImpl implements ResultSetProcessorSimple {
    private final static String NAME_OUTPUTALLHELPER = "outputAllHelper";
    private final static String NAME_OUTPUTLASTHELPER = "outputLastHelper";

    protected final ResultSetProcessorSimpleFactory prototype;
    private final SelectExprProcessor selectExprProcessor;
    private final OrderByProcessor orderByProcessor;
    protected ExprEvaluatorContext exprEvaluatorContext;
    private ResultSetProcessorSimpleOutputLastHelper outputLastHelper;
    private ResultSetProcessorSimpleOutputAllHelper outputAllHelper;

    ResultSetProcessorSimpleImpl(ResultSetProcessorSimpleFactory prototype, SelectExprProcessor selectExprProcessor, OrderByProcessor orderByProcessor, AgentInstanceContext agentInstanceContext) {
        this.prototype = prototype;
        this.selectExprProcessor = selectExprProcessor;
        this.orderByProcessor = orderByProcessor;
        this.exprEvaluatorContext = agentInstanceContext;
        if (prototype.isOutputLast()) { // output-last always uses this mechanism
            outputLastHelper = prototype.getResultSetProcessorHelperFactory().makeRSSimpleOutputLast(this, agentInstanceContext, prototype.getNumStreams());
        } else if (prototype.isOutputAll() && prototype.getOutputConditionType() == ResultSetProcessorOutputConditionType.POLICY_LASTALL_UNORDERED) {
            outputAllHelper = prototype.getResultSetProcessorHelperFactory().makeRSSimpleOutputAll(this, agentInstanceContext, prototype.getNumStreams());
        }
    }

    public void setAgentInstanceContext(AgentInstanceContext context) {
        exprEvaluatorContext = context;
    }

    public EventType getResultEventType() {
        return prototype.getResultEventType();
    }

    public UniformPair<eu.uk.ncl.pet5o.esper.client.EventBean[]> processJoinResult(Set<MultiKey<EventBean>> newEvents, Set<MultiKey<EventBean>> oldEvents, boolean isSynthesize) {
        if (InstrumentationHelper.ENABLED) {
            InstrumentationHelper.get().qResultSetProcessSimple();
        }

        eu.uk.ncl.pet5o.esper.client.EventBean[] selectOldEvents = null;
        eu.uk.ncl.pet5o.esper.client.EventBean[] selectNewEvents;

        if (prototype.getOptionalHavingNode() == null) {
            if (prototype.isSelectRStream()) {
                if (orderByProcessor == null) {
                    selectOldEvents = ResultSetProcessorUtil.getSelectJoinEventsNoHaving(selectExprProcessor, oldEvents, false, isSynthesize, exprEvaluatorContext);
                } else {
                    selectOldEvents = ResultSetProcessorUtil.getSelectJoinEventsNoHavingWithOrderBy(null, selectExprProcessor, orderByProcessor, oldEvents, false, isSynthesize, exprEvaluatorContext);
                }
            }

            if (orderByProcessor == null) {
                selectNewEvents = ResultSetProcessorUtil.getSelectJoinEventsNoHaving(selectExprProcessor, newEvents, true, isSynthesize, exprEvaluatorContext);
            } else {
                selectNewEvents = ResultSetProcessorUtil.getSelectJoinEventsNoHavingWithOrderBy(null, selectExprProcessor, orderByProcessor, newEvents, true, isSynthesize, exprEvaluatorContext);
            }
        } else {
            if (prototype.isSelectRStream()) {
                if (orderByProcessor == null) {
                    selectOldEvents = ResultSetProcessorUtil.getSelectJoinEventsHaving(selectExprProcessor, oldEvents, prototype.getOptionalHavingNode(), false, isSynthesize, exprEvaluatorContext);
                } else {
                    selectOldEvents = ResultSetProcessorUtil.getSelectJoinEventsHavingWithOrderBy(null, selectExprProcessor, orderByProcessor, oldEvents, prototype.getOptionalHavingNode(), false, isSynthesize, exprEvaluatorContext);
                }
            }

            if (orderByProcessor == null) {
                selectNewEvents = ResultSetProcessorUtil.getSelectJoinEventsHaving(selectExprProcessor, newEvents, prototype.getOptionalHavingNode(), true, isSynthesize, exprEvaluatorContext);
            } else {
                selectNewEvents = ResultSetProcessorUtil.getSelectJoinEventsHavingWithOrderBy(null, selectExprProcessor, orderByProcessor, newEvents, prototype.getOptionalHavingNode(), true, isSynthesize, exprEvaluatorContext);
            }
        }

        if (InstrumentationHelper.ENABLED) {
            InstrumentationHelper.get().aResultSetProcessSimple(selectNewEvents, selectOldEvents);
        }
        return new UniformPair<>(selectNewEvents, selectOldEvents); // we return a pair even if both are null
    }

    public static void processJoinResultCodegen(ResultSetProcessorSimpleForge forge, CodegenClassScope classScope, CodegenMethodNode method, CodegenInstanceAux instance) {
        method.getBlock().declareVar(eu.uk.ncl.pet5o.esper.client.EventBean[].class, "selectOldEvents", constantNull())
                .declareVarNoInit(eu.uk.ncl.pet5o.esper.client.EventBean[].class, "selectNewEvents");
        ResultSetProcessorUtil.processJoinResultCodegen(method, classScope, instance, forge.getOptionalHavingNode() != null, forge.isSelectRStream(), forge.isSorting(), false);
    }

    public UniformPair<eu.uk.ncl.pet5o.esper.client.EventBean[]> processViewResult(eu.uk.ncl.pet5o.esper.client.EventBean[] newData, eu.uk.ncl.pet5o.esper.client.EventBean[] oldData, boolean isSynthesize) {
        if (InstrumentationHelper.ENABLED) {
            InstrumentationHelper.get().qResultSetProcessSimple();
        }

        eu.uk.ncl.pet5o.esper.client.EventBean[] selectOldEvents = null;
        eu.uk.ncl.pet5o.esper.client.EventBean[] selectNewEvents;
        if (prototype.getOptionalHavingNode() == null) {
            if (prototype.isSelectRStream()) {
                if (orderByProcessor == null) {
                    selectOldEvents = ResultSetProcessorUtil.getSelectEventsNoHaving(selectExprProcessor, oldData, false, isSynthesize, exprEvaluatorContext);
                } else {
                    selectOldEvents = ResultSetProcessorUtil.getSelectEventsNoHavingWithOrderBy(null, selectExprProcessor, orderByProcessor, oldData, false, isSynthesize, exprEvaluatorContext);
                }
            }

            if (orderByProcessor == null) {
                selectNewEvents = ResultSetProcessorUtil.getSelectEventsNoHaving(selectExprProcessor, newData, true, isSynthesize, exprEvaluatorContext);
            } else {
                selectNewEvents = ResultSetProcessorUtil.getSelectEventsNoHavingWithOrderBy(null, selectExprProcessor, orderByProcessor, newData, true, isSynthesize, exprEvaluatorContext);
            }
        } else {
            if (prototype.isSelectRStream()) {
                if (orderByProcessor == null) {
                    selectOldEvents = ResultSetProcessorUtil.getSelectEventsHaving(selectExprProcessor, oldData, prototype.getOptionalHavingNode(), false, isSynthesize, exprEvaluatorContext);
                } else {
                    selectOldEvents = ResultSetProcessorUtil.getSelectEventsHavingWithOrderBy(null, selectExprProcessor, orderByProcessor, oldData, prototype.getOptionalHavingNode(), false, isSynthesize, exprEvaluatorContext);
                }
            }
            if (orderByProcessor == null) {
                selectNewEvents = ResultSetProcessorUtil.getSelectEventsHaving(selectExprProcessor, newData, prototype.getOptionalHavingNode(), true, isSynthesize, exprEvaluatorContext);
            } else {
                selectNewEvents = ResultSetProcessorUtil.getSelectEventsHavingWithOrderBy(null, selectExprProcessor, orderByProcessor, newData, prototype.getOptionalHavingNode(), true, isSynthesize, exprEvaluatorContext);
            }
        }

        if (InstrumentationHelper.ENABLED) {
            InstrumentationHelper.get().aResultSetProcessSimple(selectNewEvents, selectOldEvents);
        }
        return new UniformPair<>(selectNewEvents, selectOldEvents);  // we return a pair even if both are null
    }

    public static void processViewResultCodegen(ResultSetProcessorSimpleForge forge, CodegenClassScope classScope, CodegenMethodNode method, CodegenInstanceAux instance) {
        method.getBlock().declareVar(eu.uk.ncl.pet5o.esper.client.EventBean[].class, "selectOldEvents", constantNull())
                .declareVarNoInit(eu.uk.ncl.pet5o.esper.client.EventBean[].class, "selectNewEvents");
        ResultSetProcessorUtil.processViewResultCodegen(method, classScope, instance, forge.getOptionalHavingNode() != null, forge.isSelectRStream(), forge.isSorting(), false);
    }

    public Iterator<EventBean> getIterator(Viewable parent) {
        if (orderByProcessor == null) {
            // Return an iterator that gives row-by-row a result
            return new TransformEventIterator(parent.iterator(), new ResultSetProcessorHandtruTransform(this));
        }

        // Pull all events, generate order keys
        eu.uk.ncl.pet5o.esper.client.EventBean[] eventsPerStream = new eu.uk.ncl.pet5o.esper.client.EventBean[1];
        List<EventBean> events = new ArrayList<>();
        List<Object> orderKeys = new ArrayList<>();
        Iterator parentIterator = parent.iterator();
        if (parentIterator == null) {
            return CollectionUtil.NULL_EVENT_ITERATOR;
        }
        for (eu.uk.ncl.pet5o.esper.client.EventBean aParent : parent) {
            eventsPerStream[0] = aParent;
            Object orderKey = orderByProcessor.getSortKey(eventsPerStream, true, exprEvaluatorContext);

            eu.uk.ncl.pet5o.esper.client.EventBean[] result;
            if (prototype.getOptionalHavingNode() == null) {
                // ignore orderByProcessor
                result = ResultSetProcessorUtil.getSelectEventsNoHaving(selectExprProcessor, eventsPerStream, true, true, exprEvaluatorContext);
            } else {
                // ignore orderByProcessor
                result = ResultSetProcessorUtil.getSelectEventsHaving(selectExprProcessor, eventsPerStream, prototype.getOptionalHavingNode(), true, true, exprEvaluatorContext);
            }
            if (result != null && result.length != 0) {
                events.add(result[0]);
                orderKeys.add(orderKey);
            }
        }

        // sort
        eu.uk.ncl.pet5o.esper.client.EventBean[] outgoingEvents = CollectionUtil.toArrayEvents(events);
        Object[] orderKeysArr = CollectionUtil.toArrayObjects(orderKeys);
        eu.uk.ncl.pet5o.esper.client.EventBean[] orderedEvents = orderByProcessor.sortWOrderKeys(outgoingEvents, orderKeysArr, exprEvaluatorContext);

        return new ArrayEventIterator(orderedEvents);
    }

    public static void getIteratorViewCodegen(ResultSetProcessorSimpleForge forge, CodegenClassScope classScope, CodegenMethodNode method, CodegenInstanceAux instance) {
        if (!forge.isSorting()) {
            // Return an iterator that gives row-by-row a result
            method.getBlock().methodReturn(newInstance(TransformEventIterator.class, exprDotMethod(REF_VIEWABLE, "iterator"), newInstance(ResultSetProcessorHandtruTransform.class, ref("this"))));
            return;
        }

        // Pull all events, generate order keys
        method.getBlock().declareVar(eu.uk.ncl.pet5o.esper.client.EventBean[].class, "eventsPerStream", newArrayByLength(eu.uk.ncl.pet5o.esper.client.EventBean.class, constant(1)))
                .declareVar(List.class, "events", newInstance(ArrayList.class))
                .declareVar(List.class, "orderKeys", newInstance(ArrayList.class))
                .declareVar(Iterator.class, "parentIterator", exprDotMethod(REF_VIEWABLE, "iterator"))
                .ifCondition(equalsNull(ref("parentIterator"))).blockReturn(publicConstValue(CollectionUtil.class, "NULL_EVENT_ITERATOR"));

        {
            CodegenBlock loop = method.getBlock().forEach(eu.uk.ncl.pet5o.esper.client.EventBean.class, "aParent", REF_VIEWABLE);
            loop.assignArrayElement("eventsPerStream", constant(0), ref("aParent"))
                    .declareVar(Object.class, "orderKey", exprDotMethod(REF_ORDERBYPROCESSOR, "getSortKey", ref("eventsPerStream"), constantTrue(), REF_AGENTINSTANCECONTEXT));

            if (forge.getOptionalHavingNode() == null) {
                loop.declareVar(eu.uk.ncl.pet5o.esper.client.EventBean[].class, "result", staticMethod(ResultSetProcessorUtil.class, METHOD_GETSELECTEVENTSNOHAVING, REF_SELECTEXPRPROCESSOR, ref("eventsPerStream"), constantTrue(), constantTrue(), REF_AGENTINSTANCECONTEXT));
            } else {
                CodegenMethodNode select = ResultSetProcessorUtil.getSelectEventsHavingCodegen(classScope, instance);
                loop.declareVar(eu.uk.ncl.pet5o.esper.client.EventBean[].class, "result", localMethod(select, REF_SELECTEXPRNONMEMBER, ref("eventsPerStream"), constantTrue(), constantTrue(), REF_AGENTINSTANCECONTEXT));
            }

            loop.ifCondition(and(notEqualsNull(ref("result")), not(equalsIdentity(arrayLength(ref("result")), constant(0)))))
                    .exprDotMethod(ref("events"), "add", arrayAtIndex(ref("result"), constant(0)))
                    .exprDotMethod(ref("orderKeys"), "add", ref("orderKey"));
        }

        method.getBlock().declareVar(eu.uk.ncl.pet5o.esper.client.EventBean[].class, "outgoingEvents", staticMethod(CollectionUtil.class, METHOD_TOARRAYEVENTS, ref("events")))
                .declareVar(Object[].class, "orderKeysArr", staticMethod(CollectionUtil.class, METHOD_TOARRAYOBJECTS, ref("orderKeys")))
                .declareVar(eu.uk.ncl.pet5o.esper.client.EventBean[].class, "orderedEvents", exprDotMethod(REF_ORDERBYPROCESSOR, "sortWOrderKeys", ref("outgoingEvents"), ref("orderKeysArr"), REF_AGENTINSTANCECONTEXT))
                .methodReturn(newInstance(ArrayEventIterator.class, ref("orderedEvents")));
    }

    public Iterator<EventBean> getIterator(Set<MultiKey<EventBean>> joinSet) {
        // Process join results set as a regular join, includes sorting and having-clause filter
        UniformPair<eu.uk.ncl.pet5o.esper.client.EventBean[]> result = processJoinResult(joinSet, Collections.emptySet(), true);
        if (result == null) {
            return Collections.emptyIterator();
        }
        return new ArrayEventIterator(result.getFirst());
    }

    public static void getIteratorJoinCodegen(ResultSetProcessorSimpleForge forge, CodegenClassScope classScope, CodegenMethodNode method, CodegenInstanceAux instance) {
        method.getBlock().declareVar(UniformPair.class, "result", exprDotMethod(ref("this"), "processJoinResult", REF_JOINSET, staticMethod(Collections.class, "emptySet"), constantTrue()))
                .ifRefNull("result")
                .blockReturn(staticMethod(Collections.class, "emptyIterator"))
                .methodReturn(newInstance(ArrayEventIterator.class, cast(eu.uk.ncl.pet5o.esper.client.EventBean[].class, exprDotMethod(ref("result"), "getFirst"))));
    }

    public void clear() {
        // No need to clear state, there is no state held
    }

    public void applyViewResult(eu.uk.ncl.pet5o.esper.client.EventBean[] newData, eu.uk.ncl.pet5o.esper.client.EventBean[] oldData) {
    }

    public void applyJoinResult(Set<MultiKey<EventBean>> newEvents, Set<MultiKey<EventBean>> oldEvents) {
    }

    public void processOutputLimitedLastAllNonBufferedView(eu.uk.ncl.pet5o.esper.client.EventBean[] newData, eu.uk.ncl.pet5o.esper.client.EventBean[] oldData, boolean isGenerateSynthetic) {
        if (prototype.isOutputAll()) {
            outputAllHelper.processView(newData, oldData);
        } else {
            outputLastHelper.processView(newData, oldData);
        }
    }

    public static void processOutputLimitedLastAllNonBufferedViewCodegen(ResultSetProcessorSimpleForge forge, CodegenClassScope classScope, CodegenMethodNode method, CodegenInstanceAux instance) {
        processOutputLimitedLastAllNonBufferedCodegen(forge, "processView", classScope, method, instance);
    }

    private static void processOutputLimitedLastAllNonBufferedCodegen(ResultSetProcessorSimpleForge forge, String methodName, CodegenClassScope classScope, CodegenMethodNode method, CodegenInstanceAux instance) {
        CodegenMember factory = classScope.makeAddMember(ResultSetProcessorHelperFactory.class, forge.getResultSetProcessorHelperFactory());

        if (forge.isOutputAll()) {
            instance.addMember(NAME_OUTPUTALLHELPER, ResultSetProcessorSimpleOutputAllHelper.class);
            instance.getServiceCtor().getBlock().assignRef(NAME_OUTPUTALLHELPER, exprDotMethod(member(factory.getMemberId()), "makeRSSimpleOutputAll", ref("this"), REF_AGENTINSTANCECONTEXT, constant(forge.getNumStreams())));
            method.getBlock().exprDotMethod(ref(NAME_OUTPUTALLHELPER), methodName, REF_NEWDATA, REF_OLDDATA);
        } else if (forge.isOutputLast()) {
            instance.addMember(NAME_OUTPUTLASTHELPER, ResultSetProcessorSimpleOutputLastHelper.class);
            instance.getServiceCtor().getBlock().assignRef(NAME_OUTPUTLASTHELPER, exprDotMethod(member(factory.getMemberId()), "makeRSSimpleOutputLast", ref("this"), REF_AGENTINSTANCECONTEXT, constant(forge.getNumStreams())));
            method.getBlock().exprDotMethod(ref(NAME_OUTPUTLASTHELPER), methodName, REF_NEWDATA, REF_OLDDATA);
        }
    }

    public void processOutputLimitedLastAllNonBufferedJoin(Set<MultiKey<EventBean>> newEvents, Set<MultiKey<EventBean>> oldEvents, boolean isGenerateSynthetic) {
        if (prototype.isOutputAll()) {
            outputAllHelper.processJoin(newEvents, oldEvents);
        } else {
            outputLastHelper.processJoin(newEvents, oldEvents);
        }
    }

    public static void processOutputLimitedLastAllNonBufferedJoinCodegen(ResultSetProcessorSimpleForge forge, CodegenClassScope classScope, CodegenMethodNode method, CodegenInstanceAux instance) {
        processOutputLimitedLastAllNonBufferedCodegen(forge, "processJoin", classScope, method, instance);
    }

    public UniformPair<eu.uk.ncl.pet5o.esper.client.EventBean[]> continueOutputLimitedLastAllNonBufferedView(boolean isSynthesize) {
        if (prototype.isOutputAll()) {
            return outputAllHelper.outputView(isSynthesize);
        }
        return outputLastHelper.outputView(isSynthesize);
    }

    public static void continueOutputLimitedLastAllNonBufferedViewCodegen(ResultSetProcessorSimpleForge forge, CodegenClassScope classScope, CodegenMethodNode method, CodegenInstanceAux instance) {
        if (forge.isOutputAll()) {
            method.getBlock().methodReturn(exprDotMethod(ref(NAME_OUTPUTALLHELPER), "outputView", REF_ISSYNTHESIZE));
        } else if (forge.isOutputLast()) {
            method.getBlock().methodReturn(exprDotMethod(ref(NAME_OUTPUTLASTHELPER), "outputView", REF_ISSYNTHESIZE));
        } else {
            method.getBlock().methodReturn(constantNull());
        }
    }

    public UniformPair<eu.uk.ncl.pet5o.esper.client.EventBean[]> continueOutputLimitedLastAllNonBufferedJoin(boolean isSynthesize) {
        if (prototype.isOutputAll()) {
            return outputAllHelper.outputJoin(isSynthesize);
        }
        return outputLastHelper.outputJoin(isSynthesize);
    }

    public static void continueOutputLimitedLastAllNonBufferedJoinCodegen(ResultSetProcessorSimpleForge forge, CodegenClassScope classScope, CodegenMethodNode method, CodegenInstanceAux instance) {
        if (forge.isOutputAll()) {
            method.getBlock().methodReturn(exprDotMethod(ref(NAME_OUTPUTALLHELPER), "outputJoin", REF_ISSYNTHESIZE));
        } else if (forge.isOutputLast()) {
            method.getBlock().methodReturn(exprDotMethod(ref(NAME_OUTPUTLASTHELPER), "outputJoin", REF_ISSYNTHESIZE));
        } else {
            method.getBlock().methodReturn(constantNull());
        }
    }

    public void stop() {
        if (outputLastHelper != null) {
            outputLastHelper.destroy();
        }
        if (outputAllHelper != null) {
            outputAllHelper.destroy();
        }
    }

    public static void stopMethodCodegen(CodegenMethodNode method, CodegenInstanceAux instance) {
        if (instance.hasMember(NAME_OUTPUTLASTHELPER)) {
            method.getBlock().exprDotMethod(ref(NAME_OUTPUTLASTHELPER), "destroy");
        }
        if (instance.hasMember(NAME_OUTPUTALLHELPER)) {
            method.getBlock().exprDotMethod(ref(NAME_OUTPUTALLHELPER), "destroy");
        }
    }

    public void acceptHelperVisitor(ResultSetProcessorOutputHelperVisitor visitor) {
        if (outputLastHelper != null) {
            visitor.visit(outputLastHelper);
        }
        if (outputAllHelper != null) {
            visitor.visit(outputAllHelper);
        }
    }

    public static void acceptHelperVisitorCodegen(CodegenMethodNode method, CodegenInstanceAux instance) {
        if (instance.hasMember(NAME_OUTPUTLASTHELPER)) {
            method.getBlock().exprDotMethod(REF_RESULTSETVISITOR, "visit", ref(NAME_OUTPUTLASTHELPER));
        }
        if (instance.hasMember(NAME_OUTPUTALLHELPER)) {
            method.getBlock().exprDotMethod(REF_RESULTSETVISITOR, "visit", ref(NAME_OUTPUTALLHELPER));
        }
    }

    public UniformPair<eu.uk.ncl.pet5o.esper.client.EventBean[]> processOutputLimitedJoin(List<UniformPair<Set<MultiKey<EventBean>>>> joinEventsSet, boolean generateSynthetic) {
        if (!prototype.isOutputLast()) {
            UniformPair<Set<MultiKey<EventBean>>> flattened = EventBeanUtility.flattenBatchJoin(joinEventsSet);
            return processJoinResult(flattened.getFirst(), flattened.getSecond(), generateSynthetic);
        }

        throw new IllegalStateException("Output last is provided by " + OutputProcessViewConditionLastAllUnord.class.getSimpleName());
    }

    public static void processOutputLimitedJoinCodegen(ResultSetProcessorSimpleForge forge, CodegenMethodNode method) {
        if (!forge.isOutputLast()) {
            method.getBlock().declareVar(UniformPair.class, "pair", staticMethod(EventBeanUtility.class, METHOD_FLATTENBATCHJOIN, REF_JOINEVENTSSET))
                    .methodReturn(exprDotMethod(ref("this"), "processJoinResult",
                            cast(Set.class, exprDotMethod(ref("pair"), "getFirst")), cast(Set.class, exprDotMethod(ref("pair"), "getSecond")), REF_ISSYNTHESIZE));
            return;
        }
        method.getBlock().methodThrowUnsupported();
    }

    public UniformPair<eu.uk.ncl.pet5o.esper.client.EventBean[]> processOutputLimitedView(List<UniformPair<EventBean[]>> viewEventsList, boolean generateSynthetic) {
        if (!prototype.isOutputLast()) {
            UniformPair<eu.uk.ncl.pet5o.esper.client.EventBean[]> pair = EventBeanUtility.flattenBatchStream(viewEventsList);
            return processViewResult(pair.getFirst(), pair.getSecond(), generateSynthetic);
        }

        throw new IllegalStateException("Output last is provided by " + OutputProcessViewConditionLastAllUnord.class.getSimpleName());
    }

    public static void processOutputLimitedViewCodegen(ResultSetProcessorSimpleForge forge, CodegenMethodNode method) {
        if (!forge.isOutputLast()) {
            method.getBlock().declareVar(UniformPair.class, "pair", staticMethod(EventBeanUtility.class, METHOD_FLATTENBATCHSTREAM, REF_VIEWEVENTSLIST))
                    .methodReturn(exprDotMethod(ref("this"), "processViewResult",
                            cast(eu.uk.ncl.pet5o.esper.client.EventBean[].class, exprDotMethod(ref("pair"), "getFirst")), cast(eu.uk.ncl.pet5o.esper.client.EventBean[].class, exprDotMethod(ref("pair"), "getSecond")), REF_ISSYNTHESIZE));
            return;
        }
        method.getBlock().methodThrowUnsupported();
    }

    public boolean hasHavingClause() {
        return prototype.getOptionalHavingNode() != null;
    }

    public boolean evaluateHavingClause(eu.uk.ncl.pet5o.esper.client.EventBean[] eventsPerStream, boolean isNewData, ExprEvaluatorContext exprEvaluatorContext) {
        return ResultSetProcessorUtil.evaluateHavingClause(prototype.getOptionalHavingNode(), eventsPerStream, isNewData, exprEvaluatorContext);
    }

    public ExprEvaluatorContext getAgentInstanceContext() {
        return exprEvaluatorContext;
    }
}
