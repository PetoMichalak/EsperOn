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
import eu.uk.ncl.pet5o.esper.pattern.EvalRootState;
import eu.uk.ncl.pet5o.esper.rowregex.RegexExprPreviousEvalStrategy;
import eu.uk.ncl.pet5o.esper.util.StopCallback;
import eu.uk.ncl.pet5o.esper.view.Viewable;

import java.util.List;
import java.util.Map;

public class StatementAgentInstanceFactorySelectResult extends StatementAgentInstanceFactoryResult {

    private final EvalRootState[] patternRoots;
    private final StatementAgentInstancePostLoad optionalPostLoadJoin;
    private final Viewable[] topViews;
    private final Viewable[] eventStreamViewables;
    private final ViewableActivationResult[] viewableActivationResults;

    public StatementAgentInstanceFactorySelectResult(Viewable finalView,
                                                     StopCallback stopCallback,
                                                     AgentInstanceContext agentInstanceContext,
                                                     AggregationService optionalAggegationService,
                                                     Map<ExprSubselectNode, SubSelectStrategyHolder> subselectStrategies,
                                                     Map<ExprPriorNode, ExprPriorEvalStrategy> priorNodeStrategies,
                                                     Map<ExprPreviousNode, ExprPreviousEvalStrategy> previousNodeStrategies,
                                                     RegexExprPreviousEvalStrategy regexExprPreviousEvalStrategy,
                                                     Map<ExprTableAccessNode, ExprTableAccessEvalStrategy> tableAccessStrategies,
                                                     List<StatementAgentInstancePreload> preloadList,
                                                     EvalRootState[] patternRoots,
                                                     StatementAgentInstancePostLoad optionalPostLoadJoin,
                                                     Viewable[] topViews,
                                                     Viewable[] eventStreamViewables,
                                                     ViewableActivationResult[] viewableActivationResults) {
        super(finalView, stopCallback, agentInstanceContext, optionalAggegationService, subselectStrategies, priorNodeStrategies, previousNodeStrategies, regexExprPreviousEvalStrategy, tableAccessStrategies, preloadList);
        this.topViews = topViews;
        this.patternRoots = patternRoots;
        this.optionalPostLoadJoin = optionalPostLoadJoin;
        this.eventStreamViewables = eventStreamViewables;
        this.viewableActivationResults = viewableActivationResults;
    }

    public Viewable[] getTopViews() {
        return topViews;
    }

    public EvalRootState[] getPatternRoots() {
        return patternRoots;
    }

    public StatementAgentInstancePostLoad getOptionalPostLoadJoin() {
        return optionalPostLoadJoin;
    }

    public Viewable[] getEventStreamViewables() {
        return eventStreamViewables;
    }

    public ViewableActivationResult[] getViewableActivationResults() {
        return viewableActivationResults;
    }
}
