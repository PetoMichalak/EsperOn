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
package eu.uk.ncl.pet5o.esper.epl.expression.methodagg;

import eu.uk.ncl.pet5o.esper.epl.agg.service.common.AggregationMethodFactory;
import eu.uk.ncl.pet5o.esper.epl.expression.baseagg.ExprAggregateNode;
import eu.uk.ncl.pet5o.esper.epl.expression.baseagg.ExprAggregateNodeBase;
import eu.uk.ncl.pet5o.esper.epl.expression.core.ExprValidationContext;
import eu.uk.ncl.pet5o.esper.epl.expression.core.ExprValidationException;

/**
 * Represents the avedev(...) aggregate function is an expression tree.
 */
public class ExprAvedevNode extends ExprAggregateNodeBase {
    private static final long serialVersionUID = 252403936366503567L;

    private boolean hasFilter;

    /**
     * Ctor.
     *
     * @param distinct - flag indicating unique or non-unique value aggregation
     */
    public ExprAvedevNode(boolean distinct) {
        super(distinct);
    }

    public AggregationMethodFactory validateAggregationChild(ExprValidationContext validationContext) throws ExprValidationException {
        hasFilter = positionalParams.length > 1;
        Class childType = super.validateNumericChildAllowFilter(hasFilter);
        return validationContext.getEngineImportService().getAggregationFactoryFactory().makeAvedev(validationContext.getStatementExtensionSvcContext(), this, childType, positionalParams);
    }

    public final boolean equalsNodeAggregateMethodOnly(ExprAggregateNode node) {
        if (!(node instanceof ExprAvedevNode)) {
            return false;
        }

        return true;
    }

    public String getAggregationFunctionName() {
        return "avedev";
    }

    public boolean isHasFilter() {
        return hasFilter;
    }

    protected boolean isFilterExpressionAsLastParameter() {
        return true;
    }
}
