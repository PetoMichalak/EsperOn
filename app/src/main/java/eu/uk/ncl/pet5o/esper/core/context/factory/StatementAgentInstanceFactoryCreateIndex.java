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
package eu.uk.ncl.pet5o.esper.core.context.factory;

import eu.uk.ncl.pet5o.esper.client.EPException;
import eu.uk.ncl.pet5o.esper.core.context.util.AgentInstanceContext;
import eu.uk.ncl.pet5o.esper.core.service.EPServicesContext;
import eu.uk.ncl.pet5o.esper.epl.expression.core.ExprValidationException;
import eu.uk.ncl.pet5o.esper.epl.join.plan.QueryPlanIndexItem;
import eu.uk.ncl.pet5o.esper.epl.named.NamedWindowProcessor;
import eu.uk.ncl.pet5o.esper.epl.named.NamedWindowProcessorInstance;
import eu.uk.ncl.pet5o.esper.epl.spec.CreateIndexDesc;
import eu.uk.ncl.pet5o.esper.epl.table.mgmt.TableStateInstance;
import eu.uk.ncl.pet5o.esper.epl.virtualdw.VirtualDWView;
import eu.uk.ncl.pet5o.esper.util.StopCallback;
import eu.uk.ncl.pet5o.esper.view.Viewable;

public class StatementAgentInstanceFactoryCreateIndex implements StatementAgentInstanceFactory {

    private final EPServicesContext services;
    private final CreateIndexDesc spec;
    private final Viewable finalView;
    private final NamedWindowProcessor namedWindowProcessor;
    private final String tableName;
    private final String contextName;
    private final QueryPlanIndexItem explicitIndexDesc;

    public StatementAgentInstanceFactoryCreateIndex(EPServicesContext services, CreateIndexDesc spec, Viewable finalView, NamedWindowProcessor namedWindowProcessor, String tableName, String contextName, QueryPlanIndexItem explicitIndexDesc) {
        this.services = services;
        this.spec = spec;
        this.finalView = finalView;
        this.namedWindowProcessor = namedWindowProcessor;
        this.tableName = tableName;
        this.contextName = contextName;
        this.explicitIndexDesc = explicitIndexDesc;
    }

    public StatementAgentInstanceFactoryCreateIndexResult newContext(AgentInstanceContext agentInstanceContext, boolean isRecoveringResilient) {
        StopCallback stopCallback;
        final int agentInstanceId = agentInstanceContext.getAgentInstanceId();

        if (namedWindowProcessor != null) {
            // handle named window index
            final NamedWindowProcessorInstance processorInstance = namedWindowProcessor.getProcessorInstance(agentInstanceContext);

            if (namedWindowProcessor.isVirtualDataWindow()) {
                final VirtualDWView virtualDWView = processorInstance.getRootViewInstance().getVirtualDataWindow();
                virtualDWView.handleStartIndex(spec);
                stopCallback = new StopCallback() {
                    public void stop() {
                        virtualDWView.handleStopIndex(spec);
                    }
                };
            } else {
                try {
                    processorInstance.getRootViewInstance().addExplicitIndex(spec.getIndexName(), explicitIndexDesc, isRecoveringResilient);
                } catch (ExprValidationException e) {
                    throw new EPException("Failed to create index: " + e.getMessage(), e);
                }

                stopCallback = new StopCallback() {
                    public void stop() {
                        // we remove the index when context partitioned.
                        // when not context partition the index gets removed when the last reference to the named window gets destroyed.
                        if (contextName != null) {
                            NamedWindowProcessorInstance instance = namedWindowProcessor.getProcessorInstance(agentInstanceId);
                            if (instance != null) {
                                instance.removeExplicitIndex(spec.getIndexName());
                            }
                        }
                    }
                };
            }
        } else {
            // handle table access
            try {
                TableStateInstance instance = services.getTableService().getState(tableName, agentInstanceContext.getAgentInstanceId());
                instance.addExplicitIndex(spec.getIndexName(), explicitIndexDesc, isRecoveringResilient, contextName != null);
            } catch (ExprValidationException ex) {
                throw new EPException("Failed to create index: " + ex.getMessage(), ex);
            }

            stopCallback = new StopCallback() {
                public void stop() {
                    // we remove the index when context partitioned.
                    // when not context partition the index gets removed when the last reference to the table gets destroyed.
                    if (contextName != null) {
                        TableStateInstance instance = services.getTableService().getState(tableName, agentInstanceId);
                        if (instance != null) {
                            instance.removeExplicitIndex(spec.getIndexName());
                        }
                    }
                }
            };
        }

        return new StatementAgentInstanceFactoryCreateIndexResult(finalView, stopCallback, agentInstanceContext);
    }

    public void assignExpressions(StatementAgentInstanceFactoryResult result) {
    }

    public void unassignExpressions() {
    }
}
