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
import eu.uk.ncl.pet5o.esper.util.StopCallback;
import eu.uk.ncl.pet5o.esper.view.Viewable;

import java.util.Collections;
import java.util.Map;

public class StatementAgentInstanceFactoryOnTriggerResult extends StatementAgentInstanceFactoryResult {

    private final EvalRootState optPatternRoot;
    private final ViewableActivationResult viewableActivationResult;

    public StatementAgentInstanceFactoryOnTriggerResult(Viewable finalView, StopCallback stopCallback, AgentInstanceContext agentInstanceContext, AggregationService aggregationService, Map<ExprSubselectNode, SubSelectStrategyHolder> subselectStrategies,
                                                        EvalRootState optPatternRoot, Map<ExprTableAccessNode, ExprTableAccessEvalStrategy> tableAccessStrategies, ViewableActivationResult viewableActivationResult) {
        super(finalView, stopCallback, agentInstanceContext, aggregationService, subselectStrategies,
                Collections.<ExprPriorNode, ExprPriorEvalStrategy>emptyMap(),
                Collections.<ExprPreviousNode, ExprPreviousEvalStrategy>emptyMap(),
                null,
                tableAccessStrategies,
                Collections.<StatementAgentInstancePreload>emptyList());
        this.optPatternRoot = optPatternRoot;
        this.viewableActivationResult = viewableActivationResult;
    }

    public EvalRootState getOptPatternRoot() {
        return optPatternRoot;
    }

    public ViewableActivationResult getViewableActivationResult() {
        return viewableActivationResult;
    }
}
