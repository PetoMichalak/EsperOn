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
package eu.uk.ncl.pet5o.esper.epl.agg.service.common;

import eu.uk.ncl.pet5o.esper.core.service.StatementContext;
import eu.uk.ncl.pet5o.esper.epl.agg.access.AggregationAccessorSlotPair;
import eu.uk.ncl.pet5o.esper.epl.agg.access.AggregationAccessorSlotPairForge;
import eu.uk.ncl.pet5o.esper.epl.expression.core.ExprEvaluator;
import eu.uk.ncl.pet5o.esper.epl.expression.core.ExprForge;
import eu.uk.ncl.pet5o.esper.epl.util.ExprNodeUtilityRich;

import static eu.uk.ncl.pet5o.esper.epl.agg.service.common.AggregatorUtil.*;
import static eu.uk.ncl.pet5o.esper.epl.agg.service.common.AggregatorUtil.ACCESSAGG_EMPTY_ACCESSORS;
import static eu.uk.ncl.pet5o.esper.epl.agg.service.common.AggregatorUtil.ACCESSAGG_EMPTY_STATEFACTORY;
import static eu.uk.ncl.pet5o.esper.epl.agg.service.common.AggregatorUtil.METHODAGG_EMPTYEVALUATORS;
import static eu.uk.ncl.pet5o.esper.epl.agg.service.common.AggregatorUtil.METHODAGG_EMPTYFACTORIES;

public class AggregationRowStateForgeDesc {
    private final ExprForge[][] methodForges;
    private final AggregationMethodFactory[] methodFactories;
    private final AggregationAccessorSlotPairForge[] accessAccessorsForges;
    private final AggregationStateFactoryForge[] accessFactoriesForges;

    public AggregationRowStateForgeDesc(ExprForge[][] methodForges, AggregationMethodFactory[] methodFactories, AggregationAccessorSlotPairForge[] accessAccessorsForges, AggregationStateFactoryForge[] accessFactoriesForges) {
        this.methodForges = methodForges;
        this.methodFactories = methodFactories;
        this.accessAccessorsForges = accessAccessorsForges;
        this.accessFactoriesForges = accessFactoriesForges;
    }

    public ExprForge[][] getMethodForges() {
        return methodForges;
    }

    public AggregationMethodFactory[] getMethodFactories() {
        return methodFactories;
    }

    public AggregationAccessorSlotPairForge[] getAccessAccessorsForges() {
        return accessAccessorsForges;
    }

    public AggregationStateFactoryForge[] getAccessFactoriesForges() {
        return accessFactoriesForges;
    }

    public AggregationRowStateEvalDesc toEval(StatementContext stmtContext, boolean isFireAndForget) {
        ExprEvaluator[] methodEvals = methodForges == null ? METHODAGG_EMPTYEVALUATORS : ExprNodeUtilityRich.getEvaluatorsMayCompileWMultiValue(methodForges, stmtContext.getEngineImportService(), this.getClass(), isFireAndForget, stmtContext.getStatementName());
        AggregationStateFactory[] accessFactories = accessFactoriesForges == null ? ACCESSAGG_EMPTY_STATEFACTORY : AggregatorUtil.getAccesssFactoriesFromForges(this.accessFactoriesForges, stmtContext, isFireAndForget);
        AggregationAccessorSlotPair[] accessAccessors = accessAccessorsForges == null ? ACCESSAGG_EMPTY_ACCESSORS : AggregatorUtil.getAccessorsForForges(this.accessAccessorsForges, stmtContext.getEngineImportService(), isFireAndForget, stmtContext.getStatementName());
        return new AggregationRowStateEvalDesc(methodEvals, methodFactories == null ? METHODAGG_EMPTYFACTORIES : methodFactories, accessAccessors, accessFactories);
    }
}
