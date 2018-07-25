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
package eu.uk.ncl.pet5o.esper.core.context.activator;

import eu.uk.ncl.pet5o.esper.client.EventType;
import eu.uk.ncl.pet5o.esper.core.context.util.AgentInstanceContext;
import eu.uk.ncl.pet5o.esper.core.service.EPServicesContext;
import eu.uk.ncl.pet5o.esper.core.service.EPStatementHandleCallback;
import eu.uk.ncl.pet5o.esper.filter.FilterHandleCallback;
import eu.uk.ncl.pet5o.esper.filter.FilterServiceEntry;
import eu.uk.ncl.pet5o.esper.filterspec.FilterSpecCompiled;
import eu.uk.ncl.pet5o.esper.filterspec.FilterValueSet;
import eu.uk.ncl.pet5o.esper.filterspec.FilterValueSetParam;
import eu.uk.ncl.pet5o.esper.metrics.instrumentation.InstrumentationAgent;
import eu.uk.ncl.pet5o.esper.metrics.instrumentation.InstrumentationHelper;
import eu.uk.ncl.pet5o.esper.view.EventStream;
import eu.uk.ncl.pet5o.esper.view.ZeroDepthStreamIterable;
import eu.uk.ncl.pet5o.esper.view.ZeroDepthStreamNoIterate;
import eu.uk.ncl.pet5o.esper.view.stream.EventStreamProxy;

import java.lang.annotation.Annotation;
import java.util.Collection;

public class ViewableActivatorFilterProxy implements ViewableActivator {

    private final EPServicesContext services;
    private final FilterSpecCompiled filterSpec;
    private final Annotation[] annotations;
    private final boolean isSubSelect;
    private final InstrumentationAgent instrumentationAgent;
    private final boolean isCanIterate;

    protected ViewableActivatorFilterProxy(EPServicesContext services, FilterSpecCompiled filterSpec, Annotation[] annotations, boolean subSelect, InstrumentationAgent instrumentationAgent, boolean isCanIterate) {
        this.services = services;
        this.filterSpec = filterSpec;
        this.annotations = annotations;
        isSubSelect = subSelect;
        this.instrumentationAgent = instrumentationAgent;
        this.isCanIterate = isCanIterate;
    }

    public ViewableActivationResult activate(final AgentInstanceContext agentInstanceContext, boolean isSubselect, boolean isRecoveringResilient) {

        // New event stream
        EventType resultEventType = filterSpec.getResultEventType();
        EventStream zeroDepthStream = isCanIterate ? new ZeroDepthStreamIterable(resultEventType) : new ZeroDepthStreamNoIterate(resultEventType);

        // audit proxy
        EventStream inputStream = EventStreamProxy.getAuditProxy(agentInstanceContext.getStatementContext().getEngineURI(), agentInstanceContext.getEpStatementAgentInstanceHandle().getStatementHandle().getStatementName(), annotations, filterSpec, zeroDepthStream);

        final EventStream eventStream = inputStream;
        final int statementId = agentInstanceContext.getStatementContext().getStatementId();

        FilterHandleCallback filterCallback;
        if (filterSpec.getOptionalPropertyEvaluator() != null) {
            filterCallback = new FilterHandleCallback() {
                public int getStatementId() {
                    return statementId;
                }

                public void matchFound(eu.uk.ncl.pet5o.esper.client.EventBean theEvent, Collection<FilterHandleCallback> allStmtMatches) {
                    eu.uk.ncl.pet5o.esper.client.EventBean[] result = filterSpec.getOptionalPropertyEvaluator().getProperty(theEvent, agentInstanceContext);
                    if (result == null) {
                        return;
                    }
                    eventStream.insert(result);
                }

                public boolean isSubSelect() {
                    return isSubSelect;
                }
            };
        } else {
            filterCallback = new FilterHandleCallback() {
                public int getStatementId() {
                    return statementId;
                }

                public void matchFound(eu.uk.ncl.pet5o.esper.client.EventBean theEvent, Collection<FilterHandleCallback> allStmtMatches) {
                    if (InstrumentationHelper.ENABLED) {
                        instrumentationAgent.indicateQ();
                    }
                    eventStream.insert(theEvent);
                    if (InstrumentationHelper.ENABLED) {
                        instrumentationAgent.indicateA();
                    }
                }

                public boolean isSubSelect() {
                    return isSubSelect;
                }
            };
        }
        EPStatementHandleCallback filterHandle = new EPStatementHandleCallback(agentInstanceContext.getEpStatementAgentInstanceHandle(), filterCallback);

        FilterValueSetParam[][] addendum = null;
        if (agentInstanceContext.getAgentInstanceFilterProxy() != null) {
            addendum = agentInstanceContext.getAgentInstanceFilterProxy().getAddendumFilters(filterSpec);
        }
        FilterValueSet filterValueSet = filterSpec.getValueSet(null, addendum, agentInstanceContext, agentInstanceContext.getEngineImportService(), agentInstanceContext.getAnnotations());
        FilterServiceEntry filterServiceEntry = services.getFilterService().add(filterValueSet, filterHandle);

        ViewableActivatorFilterProxyStopCallback stopCallback = new ViewableActivatorFilterProxyStopCallback(this, filterHandle, filterServiceEntry);
        return new ViewableActivationResult(inputStream, stopCallback, null, null, null, false, false, null);
    }

    public EPServicesContext getServices() {
        return services;
    }

    public FilterSpecCompiled getFilterSpec() {
        return filterSpec;
    }
}
