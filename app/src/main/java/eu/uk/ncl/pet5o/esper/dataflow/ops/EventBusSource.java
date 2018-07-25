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
package eu.uk.ncl.pet5o.esper.dataflow.ops;

import eu.uk.ncl.pet5o.esper.client.EPException;
import eu.uk.ncl.pet5o.esper.client.EventBean;
import eu.uk.ncl.pet5o.esper.client.EventType;
import eu.uk.ncl.pet5o.esper.client.dataflow.EPDataFlowEventBeanCollector;
import eu.uk.ncl.pet5o.esper.client.dataflow.EPDataFlowEventBeanCollectorContext;
import eu.uk.ncl.pet5o.esper.core.context.util.AgentInstanceContext;
import eu.uk.ncl.pet5o.esper.core.context.util.EPStatementAgentInstanceHandle;
import eu.uk.ncl.pet5o.esper.core.service.EPStatementHandleCallback;
import eu.uk.ncl.pet5o.esper.core.service.StatementAgentInstanceFilterVersion;
import eu.uk.ncl.pet5o.esper.dataflow.annotations.DataFlowContext;
import eu.uk.ncl.pet5o.esper.dataflow.annotations.DataFlowOpParameter;
import eu.uk.ncl.pet5o.esper.dataflow.annotations.DataFlowOperator;
import eu.uk.ncl.pet5o.esper.dataflow.interfaces.*;
import eu.uk.ncl.pet5o.esper.epl.core.streamtype.StreamTypeServiceImpl;
import eu.uk.ncl.pet5o.esper.epl.expression.core.ExprNode;
import eu.uk.ncl.pet5o.esper.epl.expression.core.ExprValidationException;
import eu.uk.ncl.pet5o.esper.filter.*;
import eu.uk.ncl.pet5o.esper.filterspec.FilterSpecCompiled;
import eu.uk.ncl.pet5o.esper.filterspec.FilterValueSet;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.LinkedBlockingDeque;

@DataFlowOperator
public class EventBusSource implements DataFlowSourceOperator, DataFlowOpLifecycle, FilterHandleCallback {

    @DataFlowOpParameter
    protected ExprNode filter;

    @DataFlowOpParameter
    protected EPDataFlowEventBeanCollector collector;

    @DataFlowContext
    protected EPDataFlowEmitter graphContext;

    protected EventType eventType;
    protected AgentInstanceContext agentInstanceContext;
    protected EPStatementHandleCallback callbackHandle;
    protected FilterServiceEntry filterServiceEntry;
    protected LinkedBlockingDeque<Object> emittables = new LinkedBlockingDeque<Object>();
    protected boolean submitEventBean;

    private ThreadLocal<EPDataFlowEventBeanCollectorContext> collectorDataTL = new ThreadLocal<EPDataFlowEventBeanCollectorContext>() {
        protected synchronized EPDataFlowEventBeanCollectorContext initialValue() {
            return null;
        }
    };

    public DataFlowOpInitializeResult initialize(DataFlowOpInitializateContext context) throws Exception {
        if (context.getOutputPorts().size() != 1) {
            throw new IllegalArgumentException("EventBusSource operator requires one output stream but produces " + context.getOutputPorts().size() + " streams");
        }

        DataFlowOpOutputPort portZero = context.getOutputPorts().get(0);
        if (portZero.getOptionalDeclaredType() == null || portZero.getOptionalDeclaredType().getEventType() == null) {
            throw new IllegalArgumentException("EventBusSource operator requires an event type declated for the output stream");
        }

        if (!portZero.getOptionalDeclaredType().isUnderlying()) {
            submitEventBean = true;
        }
        this.eventType = portZero.getOptionalDeclaredType().getEventType();
        this.agentInstanceContext = context.getAgentInstanceContext();

        return new DataFlowOpInitializeResult();
    }

    public void next() throws InterruptedException {
        Object next = emittables.take();
        graphContext.submit(next);
    }

    public void matchFound(EventBean theEvent, Collection<FilterHandleCallback> allStmtMatches) {
        if (collector != null) {
            EPDataFlowEventBeanCollectorContext holder = collectorDataTL.get();
            if (holder == null) {
                holder = new EPDataFlowEventBeanCollectorContext(graphContext, submitEventBean, theEvent);
                collectorDataTL.set(holder);
            } else {
                holder.setEvent(theEvent);
            }
            collector.collect(holder);
        } else if (submitEventBean) {
            emittables.add(theEvent);
        } else {
            emittables.add(theEvent.getUnderlying());
        }
    }

    public boolean isSubSelect() {
        return false;
    }

    public int getStatementId() {
        return agentInstanceContext.getStatementId();
    }

    public void open(DataFlowOpOpenContext openContext) {
        FilterValueSet valueSet;
        try {
            List<ExprNode> filters = Collections.emptyList();
            if (filter != null) {
                filters = Collections.singletonList(filter);
            }
            FilterSpecCompiled spec = FilterSpecCompiler.makeFilterSpec(eventType, eventType.getName(), filters, null,
                    null, null, new StreamTypeServiceImpl(eventType, eventType.getName(), true, agentInstanceContext.getEngineURI()), null, agentInstanceContext.getStatementContext(), new ArrayList<Integer>());
            valueSet = spec.getValueSet(null, null, agentInstanceContext, agentInstanceContext.getEngineImportService(), agentInstanceContext.getAnnotations());
        } catch (ExprValidationException ex) {
            throw new EPException("Failed to open filter: " + ex.getMessage(), ex);
        }

        EPStatementAgentInstanceHandle handle = new EPStatementAgentInstanceHandle(agentInstanceContext.getStatementContext().getEpStatementHandle(), agentInstanceContext.getAgentInstanceLock(), 0, new StatementAgentInstanceFilterVersion(), agentInstanceContext.getStatementContext().getFilterFaultHandlerFactory());
        callbackHandle = new EPStatementHandleCallback(handle, this);
        filterServiceEntry = agentInstanceContext.getStatementContext().getFilterService().add(valueSet, callbackHandle);
    }

    public synchronized void close(DataFlowOpCloseContext openContext) {
        if (callbackHandle != null) {
            agentInstanceContext.getStatementContext().getFilterService().remove(callbackHandle, filterServiceEntry);
            callbackHandle = null;
            filterServiceEntry = null;
        }
    }
}
