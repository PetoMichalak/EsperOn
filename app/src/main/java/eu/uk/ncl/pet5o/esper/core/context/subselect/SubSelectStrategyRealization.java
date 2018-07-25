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
package eu.uk.ncl.pet5o.esper.core.context.subselect;

import eu.uk.ncl.pet5o.esper.core.context.factory.StatementAgentInstancePostLoad;
import eu.uk.ncl.pet5o.esper.epl.agg.service.common.AggregationService;
import eu.uk.ncl.pet5o.esper.epl.expression.prev.ExprPreviousEvalStrategy;
import eu.uk.ncl.pet5o.esper.epl.expression.prev.ExprPreviousNode;
import eu.uk.ncl.pet5o.esper.epl.expression.prior.ExprPriorEvalStrategy;
import eu.uk.ncl.pet5o.esper.epl.expression.prior.ExprPriorNode;
import eu.uk.ncl.pet5o.esper.epl.lookup.SubordTableLookupStrategy;
import eu.uk.ncl.pet5o.esper.epl.subquery.SubselectAggregationPreprocessorBase;
import eu.uk.ncl.pet5o.esper.view.Viewable;

import java.util.Map;

/**
 * Entry holding lookup resource references for use by {@link eu.uk.ncl.pet5o.esper.core.context.subselect.SubSelectActivationCollection}.
 */
public class SubSelectStrategyRealization {
    private final SubordTableLookupStrategy strategy;
    private final SubselectAggregationPreprocessorBase subselectAggregationPreprocessor;
    private final AggregationService subselectAggregationService;
    private final Map<ExprPriorNode, ExprPriorEvalStrategy> priorNodeStrategies;
    private final Map<ExprPreviousNode, ExprPreviousEvalStrategy> previousNodeStrategies;
    private final Viewable subselectView;
    private final StatementAgentInstancePostLoad postLoad;

    public SubSelectStrategyRealization(SubordTableLookupStrategy strategy, SubselectAggregationPreprocessorBase subselectAggregationPreprocessor, AggregationService subselectAggregationService, Map<ExprPriorNode, ExprPriorEvalStrategy> priorNodeStrategies, Map<ExprPreviousNode, ExprPreviousEvalStrategy> previousNodeStrategies, Viewable subselectView, StatementAgentInstancePostLoad postLoad) {
        this.strategy = strategy;
        this.subselectAggregationPreprocessor = subselectAggregationPreprocessor;
        this.subselectAggregationService = subselectAggregationService;
        this.priorNodeStrategies = priorNodeStrategies;
        this.previousNodeStrategies = previousNodeStrategies;
        this.subselectView = subselectView;
        this.postLoad = postLoad;
    }

    public SubordTableLookupStrategy getStrategy() {
        return strategy;
    }

    public SubselectAggregationPreprocessorBase getSubselectAggregationPreprocessor() {
        return subselectAggregationPreprocessor;
    }

    public AggregationService getSubselectAggregationService() {
        return subselectAggregationService;
    }

    public Map<ExprPriorNode, ExprPriorEvalStrategy> getPriorNodeStrategies() {
        return priorNodeStrategies;
    }

    public Map<ExprPreviousNode, ExprPreviousEvalStrategy> getPreviousNodeStrategies() {
        return previousNodeStrategies;
    }

    public Viewable getSubselectView() {
        return subselectView;
    }

    public StatementAgentInstancePostLoad getPostLoad() {
        return postLoad;
    }
}
