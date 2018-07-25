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

import eu.uk.ncl.pet5o.esper.core.context.activator.ViewableActivator;
import eu.uk.ncl.pet5o.esper.core.context.subselect.SubSelectStrategyCollection;
import eu.uk.ncl.pet5o.esper.core.context.util.AgentInstanceContext;
import eu.uk.ncl.pet5o.esper.core.service.EPServicesContext;
import eu.uk.ncl.pet5o.esper.core.service.StatementContext;
import eu.uk.ncl.pet5o.esper.core.service.speccompiled.StatementSpecCompiled;
import eu.uk.ncl.pet5o.esper.epl.core.resultset.core.ResultSetProcessor;
import eu.uk.ncl.pet5o.esper.epl.core.resultset.core.ResultSetProcessorFactoryDesc;
import eu.uk.ncl.pet5o.esper.epl.variable.OnSetVariableView;
import eu.uk.ncl.pet5o.esper.epl.variable.OnSetVariableViewFactory;
import eu.uk.ncl.pet5o.esper.epl.view.OutputProcessViewFactory;
import eu.uk.ncl.pet5o.esper.util.StopCallback;
import eu.uk.ncl.pet5o.esper.view.View;

import java.util.List;

public class StatementAgentInstanceFactoryOnTriggerSetVariable extends StatementAgentInstanceFactoryOnTriggerBase {
    private final OnSetVariableViewFactory onSetVariableViewFactory;
    private final ResultSetProcessorFactoryDesc outputResultSetProcessorPrototype;
    private final OutputProcessViewFactory outputProcessViewFactory;

    public StatementAgentInstanceFactoryOnTriggerSetVariable(StatementContext statementContext, StatementSpecCompiled statementSpec, EPServicesContext services, ViewableActivator activator, SubSelectStrategyCollection subSelectStrategyCollection, OnSetVariableViewFactory onSetVariableViewFactory, ResultSetProcessorFactoryDesc outputResultSetProcessorPrototype, OutputProcessViewFactory outputProcessViewFactory) {
        super(statementContext, statementSpec, services, activator, subSelectStrategyCollection);
        this.onSetVariableViewFactory = onSetVariableViewFactory;
        this.outputResultSetProcessorPrototype = outputResultSetProcessorPrototype;
        this.outputProcessViewFactory = outputProcessViewFactory;
    }

    public OnExprViewResult determineOnExprView(AgentInstanceContext agentInstanceContext, List<StopCallback> stopCallbacks, boolean isRecoveringReslient) {
        OnSetVariableView view = onSetVariableViewFactory.instantiate(agentInstanceContext);
        return new OnExprViewResult(view, null);
    }

    public View determineFinalOutputView(AgentInstanceContext agentInstanceContext, View onExprView) {
        ResultSetProcessor outputResultSetProcessor = outputResultSetProcessorPrototype.getResultSetProcessorFactory().instantiate(null, null, agentInstanceContext);
        View outputView = outputProcessViewFactory.makeView(outputResultSetProcessor, agentInstanceContext);
        onExprView.addView(outputView);
        return outputView;
    }
}
