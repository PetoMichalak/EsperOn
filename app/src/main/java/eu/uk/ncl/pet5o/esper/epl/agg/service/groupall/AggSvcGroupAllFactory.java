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
package eu.uk.ncl.pet5o.esper.epl.agg.service.groupall;

import eu.uk.ncl.pet5o.esper.core.context.util.AgentInstanceContext;
import eu.uk.ncl.pet5o.esper.epl.agg.access.AggregationAccessorSlotPair;
import eu.uk.ncl.pet5o.esper.epl.agg.access.AggregationState;
import eu.uk.ncl.pet5o.esper.epl.agg.aggregator.AggregationMethod;
import eu.uk.ncl.pet5o.esper.epl.agg.service.common.*;
import eu.uk.ncl.pet5o.esper.epl.core.engineimport.EngineImportService;
import eu.uk.ncl.pet5o.esper.epl.expression.core.ExprEvaluator;

/**
 * Aggregation service for use when only first/last/window aggregation functions are used an none other.
 */
public class AggSvcGroupAllFactory implements AggregationServiceFactory {
    protected final ExprEvaluator[] evaluators;
    protected final AggregationMethodFactory[] aggregators;
    protected final AggregationAccessorSlotPair[] accessors;
    protected final AggregationStateFactory[] accessAggSpecs;
    protected final boolean isJoin;

    public AggSvcGroupAllFactory(AggregationRowStateEvalDesc evalDesc, boolean isJoin) {
        this.evaluators = evalDesc.getMethodEvals();
        this.aggregators = evalDesc.getMethodFactories();
        this.accessors = evalDesc.getAccessAccessors();
        this.accessAggSpecs = evalDesc.getAccessFactories();
        this.isJoin = isJoin;
    }

    public AggregationService makeService(AgentInstanceContext agentInstanceContext, EngineImportService engineImportService, boolean isSubquery, Integer subqueryNumber) {
        if (aggregators.length > 0) {
            AggregationMethod[] row = AggSvcGroupByUtil.newAggregators(aggregators);
            if (accessAggSpecs.length == 0) {
                return new AggSvcGroupAllImplNoAccess(this, row);
            } else {
                AggregationState[] states = AggSvcGroupByUtil.newAccesses(agentInstanceContext.getAgentInstanceId(), isJoin, accessAggSpecs, null, null);
                return new AggSvcGroupAllImplMixedAccess(this, row, states);
            }
        } else {
            AggregationState[] states = AggSvcGroupByUtil.newAccesses(agentInstanceContext.getAgentInstanceId(), isJoin, accessAggSpecs, null, null);
            return new AggSvcGroupAllImplAccessOnly(this, states);
        }
    }
}
