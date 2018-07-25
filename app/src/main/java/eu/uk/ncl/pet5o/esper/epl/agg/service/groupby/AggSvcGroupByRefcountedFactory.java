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
package eu.uk.ncl.pet5o.esper.epl.agg.service.groupby;

import eu.uk.ncl.pet5o.esper.core.context.util.AgentInstanceContext;
import eu.uk.ncl.pet5o.esper.epl.agg.access.AggregationAccessorSlotPair;
import eu.uk.ncl.pet5o.esper.epl.agg.service.common.*;
import eu.uk.ncl.pet5o.esper.epl.core.engineimport.EngineImportService;
import eu.uk.ncl.pet5o.esper.epl.expression.core.ExprEvaluator;

/**
 * Implementation for handling aggregation with grouping by group-keys.
 */
public class AggSvcGroupByRefcountedFactory implements AggregationServiceFactory {
    protected final ExprEvaluator[] evaluators;
    protected final AggregationMethodFactory[] aggregators;
    protected final AggregationAccessorSlotPair[] accessors;
    protected final AggregationStateFactory[] accessAggregations;
    protected final boolean isJoin;

    public AggSvcGroupByRefcountedFactory(AggregationRowStateEvalDesc rowStateEvalDesc, boolean isJoin) {
        this.evaluators = rowStateEvalDesc.getMethodEvals();
        this.aggregators = rowStateEvalDesc.getMethodFactories();
        this.accessors = rowStateEvalDesc.getAccessAccessors();
        this.accessAggregations = rowStateEvalDesc.getAccessFactories();
        this.isJoin = isJoin;
    }

    public AggregationService makeService(AgentInstanceContext agentInstanceContext, EngineImportService engineImportService, boolean isSubquery, Integer subqueryNumber) {
        if (accessAggregations.length == 0) {
            return new AggSvcGroupByRefcountedNoAccessImpl(evaluators, aggregators);
        }
        return new AggSvcGroupByRefcountedWAccessImpl(evaluators, aggregators, accessors, accessAggregations, isJoin);
    }
}
