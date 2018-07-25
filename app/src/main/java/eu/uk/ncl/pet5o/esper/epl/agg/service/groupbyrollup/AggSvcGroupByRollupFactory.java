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
package eu.uk.ncl.pet5o.esper.epl.agg.service.groupbyrollup;

import eu.uk.ncl.pet5o.esper.core.context.util.AgentInstanceContext;
import eu.uk.ncl.pet5o.esper.epl.agg.access.AggregationAccessorSlotPair;
import eu.uk.ncl.pet5o.esper.epl.agg.access.AggregationState;
import eu.uk.ncl.pet5o.esper.epl.agg.aggregator.AggregationMethod;
import eu.uk.ncl.pet5o.esper.epl.agg.service.common.*;
import eu.uk.ncl.pet5o.esper.epl.core.engineimport.EngineImportService;
import eu.uk.ncl.pet5o.esper.epl.expression.core.ExprEvaluator;

/**
 * Implementation for handling aggregation with grouping by group-keys.
 */
public class AggSvcGroupByRollupFactory implements AggregationServiceFactory {
    protected final ExprEvaluator[] evaluators;
    protected final AggregationMethodFactory[] aggregators;
    protected final AggregationAccessorSlotPair[] accessors;
    protected final AggregationStateFactory[] accessAggregations;
    protected final boolean isJoin;
    protected final AggregationGroupByRollupDesc groupByRollupDesc;

    public AggSvcGroupByRollupFactory(AggregationRowStateEvalDesc eval, boolean isJoin, AggregationGroupByRollupDesc groupByRollupDesc) {
        this.evaluators = eval.getMethodEvals();
        this.aggregators = eval.getMethodFactories();
        this.accessors = eval.getAccessAccessors();
        this.accessAggregations = eval.getAccessFactories();
        this.isJoin = isJoin;
        this.groupByRollupDesc = groupByRollupDesc;
    }

    public AggregationService makeService(AgentInstanceContext agentInstanceContext, EngineImportService engineImportService, boolean isSubquery, Integer subqueryNumber) {
        AggregationState[] topStates = AggSvcGroupByUtil.newAccesses(agentInstanceContext.getAgentInstanceId(), isJoin, accessAggregations, null, null);
        AggregationMethod[] topMethods = AggSvcGroupByUtil.newAggregators(aggregators);
        return new AggSvcGroupByRollupImpl(evaluators, aggregators, accessors, accessAggregations, isJoin, groupByRollupDesc, topMethods, topStates);
    }
}
