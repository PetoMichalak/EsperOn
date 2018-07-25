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

import eu.uk.ncl.pet5o.esper.core.context.activator.ViewableActivationResult;
import eu.uk.ncl.pet5o.esper.core.context.activator.ViewableActivator;
import eu.uk.ncl.pet5o.esper.core.context.subselect.SubSelectStrategyCollection;
import eu.uk.ncl.pet5o.esper.core.context.subselect.SubSelectStrategyHolder;
import eu.uk.ncl.pet5o.esper.core.context.util.AgentInstanceContext;
import eu.uk.ncl.pet5o.esper.core.context.util.StatementAgentInstanceUtil;
import eu.uk.ncl.pet5o.esper.core.service.EPServicesContext;
import eu.uk.ncl.pet5o.esper.core.service.StatementContext;
import eu.uk.ncl.pet5o.esper.core.service.speccompiled.StatementSpecCompiled;
import eu.uk.ncl.pet5o.esper.core.start.EPStatementStartMethodCreateWindow;
import eu.uk.ncl.pet5o.esper.core.start.EPStatementStartMethodHelperSubselect;
import eu.uk.ncl.pet5o.esper.core.start.EPStatementStartMethodHelperTableAccess;
import eu.uk.ncl.pet5o.esper.epl.agg.service.common.AggregationService;
import eu.uk.ncl.pet5o.esper.epl.expression.subquery.ExprSubselectNode;
import eu.uk.ncl.pet5o.esper.epl.expression.table.ExprTableAccessEvalStrategy;
import eu.uk.ncl.pet5o.esper.epl.expression.table.ExprTableAccessNode;
import eu.uk.ncl.pet5o.esper.pattern.EvalRootState;
import eu.uk.ncl.pet5o.esper.util.StopCallback;
import eu.uk.ncl.pet5o.esper.view.View;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public abstract class StatementAgentInstanceFactoryOnTriggerBase implements StatementAgentInstanceFactory {
    private static final Logger log = LoggerFactory.getLogger(EPStatementStartMethodCreateWindow.class);

    protected final StatementContext statementContext;
    protected final StatementSpecCompiled statementSpec;
    protected final EPServicesContext services;
    private final ViewableActivator activator;
    private final SubSelectStrategyCollection subSelectStrategyCollection;

    public abstract OnExprViewResult determineOnExprView(AgentInstanceContext agentInstanceContext, List<StopCallback> stopCallbacks, boolean isRecoveringReslient);

    public abstract View determineFinalOutputView(AgentInstanceContext agentInstanceContext, View onExprView);

    public StatementAgentInstanceFactoryOnTriggerBase(StatementContext statementContext, StatementSpecCompiled statementSpec, EPServicesContext services, ViewableActivator activator, SubSelectStrategyCollection subSelectStrategyCollection) {
        this.statementContext = statementContext;
        this.statementSpec = statementSpec;
        this.services = services;
        this.activator = activator;
        this.subSelectStrategyCollection = subSelectStrategyCollection;
    }

    public StatementAgentInstanceFactoryOnTriggerResult newContext(final AgentInstanceContext agentInstanceContext, boolean isRecoveringResilient) {
        List<StopCallback> stopCallbacks = new ArrayList<StopCallback>();
        View view;
        Map<ExprSubselectNode, SubSelectStrategyHolder> subselectStrategies;
        AggregationService aggregationService;
        EvalRootState optPatternRoot;
        Map<ExprTableAccessNode, ExprTableAccessEvalStrategy> tableAccessStrategies;
        final ViewableActivationResult activationResult;

        try {
            OnExprViewResult onExprViewResult = determineOnExprView(agentInstanceContext, stopCallbacks, isRecoveringResilient);
            view = onExprViewResult.getOnExprView();
            aggregationService = onExprViewResult.getOptionalAggregationService();

            // attach stream to view
            activationResult = activator.activate(agentInstanceContext, false, isRecoveringResilient);
            activationResult.getViewable().addView(view);
            stopCallbacks.add(activationResult.getStopCallback());
            optPatternRoot = activationResult.getOptionalPatternRoot();

            // determine final output view
            view = determineFinalOutputView(agentInstanceContext, view);

            // start subselects
            subselectStrategies = EPStatementStartMethodHelperSubselect.startSubselects(services, subSelectStrategyCollection, agentInstanceContext, stopCallbacks, isRecoveringResilient);

            // plan table access
            tableAccessStrategies = EPStatementStartMethodHelperTableAccess.attachTableAccess(services, agentInstanceContext, statementSpec.getTableNodes(), false);
        } catch (RuntimeException ex) {
            StopCallback stopCallback = StatementAgentInstanceUtil.getStopCallback(stopCallbacks, agentInstanceContext);
            StatementAgentInstanceUtil.stopSafe(stopCallback, statementContext);
            throw ex;
        }

        StatementAgentInstanceFactoryOnTriggerResult onTriggerResult = new StatementAgentInstanceFactoryOnTriggerResult(view, null, agentInstanceContext, aggregationService, subselectStrategies, optPatternRoot, tableAccessStrategies, activationResult);
        if (statementContext.getStatementExtensionServicesContext() != null) {
            statementContext.getStatementExtensionServicesContext().contributeStopCallback(onTriggerResult, stopCallbacks);
        }

        log.debug(".start Statement start completed");
        StopCallback stopCallback = StatementAgentInstanceUtil.getStopCallback(stopCallbacks, agentInstanceContext);
        onTriggerResult.setStopCallback(stopCallback);

        return onTriggerResult;
    }

    public void assignExpressions(StatementAgentInstanceFactoryResult result) {
    }

    public void unassignExpressions() {
    }

    public static class OnExprViewResult {
        private final View onExprView;
        private final AggregationService optionalAggregationService;

        public OnExprViewResult(View onExprView, AggregationService optionalAggregationService) {
            this.onExprView = onExprView;
            this.optionalAggregationService = optionalAggregationService;
        }

        public View getOnExprView() {
            return onExprView;
        }

        public AggregationService getOptionalAggregationService() {
            return optionalAggregationService;
        }
    }
}
