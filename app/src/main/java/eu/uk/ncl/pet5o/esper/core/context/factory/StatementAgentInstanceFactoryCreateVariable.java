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
import eu.uk.ncl.pet5o.esper.client.EventType;
import eu.uk.ncl.pet5o.esper.core.context.mgr.ContextPropertyRegistryImpl;
import eu.uk.ncl.pet5o.esper.core.context.util.AgentInstanceContext;
import eu.uk.ncl.pet5o.esper.core.service.EPServicesContext;
import eu.uk.ncl.pet5o.esper.core.service.StatementContext;
import eu.uk.ncl.pet5o.esper.core.service.speccompiled.StatementSpecCompiled;
import eu.uk.ncl.pet5o.esper.core.start.EPStatementStartMethodHelperAssignExpr;
import eu.uk.ncl.pet5o.esper.epl.core.resultset.core.ResultSetProcessor;
import eu.uk.ncl.pet5o.esper.epl.core.resultset.core.ResultSetProcessorFactoryDesc;
import eu.uk.ncl.pet5o.esper.epl.core.resultset.core.ResultSetProcessorFactoryFactory;
import eu.uk.ncl.pet5o.esper.epl.core.streamtype.StreamTypeService;
import eu.uk.ncl.pet5o.esper.epl.core.streamtype.StreamTypeServiceImpl;
import eu.uk.ncl.pet5o.esper.epl.expression.core.ExprValidationException;
import eu.uk.ncl.pet5o.esper.epl.spec.CreateVariableDesc;
import eu.uk.ncl.pet5o.esper.epl.spec.SelectClauseElementWildcard;
import eu.uk.ncl.pet5o.esper.epl.spec.SelectClauseStreamSelectorEnum;
import eu.uk.ncl.pet5o.esper.epl.variable.CreateVariableView;
import eu.uk.ncl.pet5o.esper.epl.variable.VariableMetaData;
import eu.uk.ncl.pet5o.esper.epl.view.OutputProcessViewBase;
import eu.uk.ncl.pet5o.esper.epl.view.OutputProcessViewFactory;
import eu.uk.ncl.pet5o.esper.epl.view.OutputProcessViewFactoryFactory;
import eu.uk.ncl.pet5o.esper.util.StopCallback;
import eu.uk.ncl.pet5o.esper.view.StatementStopCallback;

public class StatementAgentInstanceFactoryCreateVariable extends StatementAgentInstanceFactoryBase {
    private final CreateVariableDesc createDesc;
    private final StatementSpecCompiled statementSpec;
    private final StatementContext statementContext;
    private final EPServicesContext services;
    private final VariableMetaData variableMetaData;
    private final EventType eventType;

    public StatementAgentInstanceFactoryCreateVariable(CreateVariableDesc createDesc, StatementSpecCompiled statementSpec, StatementContext statementContext, EPServicesContext services, VariableMetaData variableMetaData, EventType eventType) {
        super(statementContext.getAnnotations());
        this.createDesc = createDesc;
        this.statementSpec = statementSpec;
        this.statementContext = statementContext;
        this.services = services;
        this.variableMetaData = variableMetaData;
        this.eventType = eventType;
    }

    public StatementAgentInstanceFactoryCreateVariableResult newContextInternal(final AgentInstanceContext agentInstanceContext, boolean isRecoveringResilient) {
        StopCallback stopCallback = new StopCallback() {
            public void stop() {
                services.getVariableService().deallocateVariableState(variableMetaData.getVariableName(), agentInstanceContext.getAgentInstanceId());
            }
        };
        services.getVariableService().allocateVariableState(variableMetaData.getVariableName(), agentInstanceContext.getAgentInstanceId(), statementContext.getStatementExtensionServicesContext(), isRecoveringResilient);

        final CreateVariableView createView = new CreateVariableView(statementContext.getStatementId(), services.getEventAdapterService(), services.getVariableService(), createDesc.getVariableName(), statementContext.getStatementResultService(), agentInstanceContext.getAgentInstanceId());

        services.getVariableService().registerCallback(createDesc.getVariableName(), agentInstanceContext.getAgentInstanceId(), createView);
        statementContext.getStatementStopService().addSubscriber(new StatementStopCallback() {
            public void statementStopped() {
                services.getVariableService().unregisterCallback(createDesc.getVariableName(), 0, createView);
            }
        });

        // Create result set processor, use wildcard selection
        statementSpec.getSelectClauseSpec().setSelectExprList(new SelectClauseElementWildcard());
        statementSpec.setSelectStreamDirEnum(SelectClauseStreamSelectorEnum.RSTREAM_ISTREAM_BOTH);
        StreamTypeService typeService = new StreamTypeServiceImpl(new EventType[]{createView.getEventType()}, new String[]{"create_variable"}, new boolean[]{true}, services.getEngineURI(), false, false);
        OutputProcessViewBase outputViewBase;
        try {
            ResultSetProcessorFactoryDesc resultSetProcessorPrototype = ResultSetProcessorFactoryFactory.getProcessorPrototype(
                    statementSpec, statementContext, typeService, null, new boolean[0], true, ContextPropertyRegistryImpl.EMPTY_REGISTRY, null, services.getConfigSnapshot(), services.getResultSetProcessorHelperFactory(), false, false);
            ResultSetProcessor resultSetProcessor = EPStatementStartMethodHelperAssignExpr.getAssignResultSetProcessor(agentInstanceContext, resultSetProcessorPrototype, false, null, false);

            // Attach output view
            OutputProcessViewFactory outputViewFactory = OutputProcessViewFactoryFactory.make(statementSpec, services.getInternalEventRouter(), agentInstanceContext.getStatementContext(), resultSetProcessorPrototype.getResultEventType(), null, services.getTableService(), resultSetProcessorPrototype.getResultSetProcessorType(), services.getResultSetProcessorHelperFactory(), services.getStatementVariableRefService());
            outputViewBase = outputViewFactory.makeView(resultSetProcessor, agentInstanceContext);
            createView.addView(outputViewBase);
        } catch (ExprValidationException ex) {
            throw new EPException("Unexpected exception in create-variable context allocation: " + ex.getMessage(), ex);
        }

        return new StatementAgentInstanceFactoryCreateVariableResult(outputViewBase, stopCallback, agentInstanceContext);
    }

    public void assignExpressions(StatementAgentInstanceFactoryResult result) {
    }

    public void unassignExpressions() {
    }
}
