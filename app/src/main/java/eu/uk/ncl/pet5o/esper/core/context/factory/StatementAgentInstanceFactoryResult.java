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

import eu.uk.ncl.pet5o.esper.core.context.subselect.SubSelectStrategyHolder;
import eu.uk.ncl.pet5o.esper.core.context.util.AgentInstanceContext;
import eu.uk.ncl.pet5o.esper.epl.agg.service.common.AggregationService;
import eu.uk.ncl.pet5o.esper.epl.expression.prev.ExprPreviousEvalStrategy;
import eu.uk.ncl.pet5o.esper.epl.expression.prev.ExprPreviousNode;
import eu.uk.ncl.pet5o.esper.epl.expression.prior.ExprPriorEvalStrategy;
import eu.uk.ncl.pet5o.esper.epl.expression.prior.ExprPriorNode;
import eu.uk.ncl.pet5o.esper.epl.expression.subquery.ExprSubselectNode;
import eu.uk.ncl.pet5o.esper.epl.expression.table.ExprTableAccessEvalStrategy;
import eu.uk.ncl.pet5o.esper.epl.expression.table.ExprTableAccessNode;
import eu.uk.ncl.pet5o.esper.rowregex.RegexExprPreviousEvalStrategy;
import eu.uk.ncl.pet5o.esper.util.StopCallback;
import eu.uk.ncl.pet5o.esper.view.Viewable;

import java.util.List;
import java.util.Map;

public abstract class StatementAgentInstanceFactoryResult {

    private final Viewable finalView;
    private StopCallback stopCallback;
    private final AgentInstanceContext agentInstanceContext;
    private final AggregationService optionalAggegationService;
    private final Map<ExprSubselectNode, SubSelectStrategyHolder> subselectStrategies;
    private final Map<ExprPriorNode, ExprPriorEvalStrategy> priorNodeStrategies;
    private final Map<ExprPreviousNode, ExprPreviousEvalStrategy> previousNodeStrategies;
    private final RegexExprPreviousEvalStrategy regexExprPreviousEvalStrategy;
    private final Map<ExprTableAccessNode, ExprTableAccessEvalStrategy> tableAccessStrategies;
    private final List<StatementAgentInstancePreload> preloadList;

    protected StatementAgentInstanceFactoryResult(Viewable finalView, StopCallback stopCallback, AgentInstanceContext agentInstanceContext, AggregationService optionalAggegationService, Map<ExprSubselectNode, SubSelectStrategyHolder> subselectStrategies, Map<ExprPriorNode, ExprPriorEvalStrategy> priorNodeStrategies, Map<ExprPreviousNode, ExprPreviousEvalStrategy> previousNodeStrategies, RegexExprPreviousEvalStrategy regexExprPreviousEvalStrategy, Map<ExprTableAccessNode, ExprTableAccessEvalStrategy> tableAccessStrategies, List<StatementAgentInstancePreload> preloadList) {
        this.finalView = finalView;
        this.stopCallback = stopCallback;
        this.agentInstanceContext = agentInstanceContext;
        this.optionalAggegationService = optionalAggegationService;
        this.subselectStrategies = subselectStrategies;
        this.priorNodeStrategies = priorNodeStrategies;
        this.previousNodeStrategies = previousNodeStrategies;
        this.regexExprPreviousEvalStrategy = regexExprPreviousEvalStrategy;
        this.tableAccessStrategies = tableAccessStrategies;
        this.preloadList = preloadList;
    }

    public Viewable getFinalView() {
        return finalView;
    }

    public StopCallback getStopCallback() {
        return stopCallback;
    }

    public AgentInstanceContext getAgentInstanceContext() {
        return agentInstanceContext;
    }

    public AggregationService getOptionalAggegationService() {
        return optionalAggegationService;
    }

    public Map<ExprSubselectNode, SubSelectStrategyHolder> getSubselectStrategies() {
        return subselectStrategies;
    }

    public Map<ExprPriorNode, ExprPriorEvalStrategy> getPriorNodeStrategies() {
        return priorNodeStrategies;
    }

    public Map<ExprPreviousNode, ExprPreviousEvalStrategy> getPreviousNodeStrategies() {
        return previousNodeStrategies;
    }

    public List<StatementAgentInstancePreload> getPreloadList() {
        return preloadList;
    }

    public RegexExprPreviousEvalStrategy getRegexExprPreviousEvalStrategy() {
        return regexExprPreviousEvalStrategy;
    }

    public void setStopCallback(StopCallback stopCallback) {
        this.stopCallback = stopCallback;
    }

    public Map<ExprTableAccessNode, ExprTableAccessEvalStrategy> getTableAccessEvalStrategies() {
        return tableAccessStrategies;
    }
}
