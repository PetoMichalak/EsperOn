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
import eu.uk.ncl.pet5o.esper.epl.expression.core.ExprNode;
import eu.uk.ncl.pet5o.esper.epl.expression.core.ExprNodeUtilityCore;
import eu.uk.ncl.pet5o.esper.epl.expression.core.ExprValidationContext;
import eu.uk.ncl.pet5o.esper.epl.expression.core.ExprValidationException;

/**
 * Represents the nth(...) and aggregate function is an expression tree.
 */
public class ExprNthAggNode extends ExprAggregateNodeBase {
    private static final long serialVersionUID = -843689785630260527L;

    /**
     * Ctor.
     *
     * @param distinct - flag indicating unique or non-unique value aggregation
     */
    public ExprNthAggNode(boolean distinct) {
        super(distinct);
    }

    public AggregationMethodFactory validateAggregationChild(ExprValidationContext validationContext) throws ExprValidationException {
        String message = "The nth aggregation function requires two parameters, an expression returning aggregation values and a numeric index constant";
        if (this.positionalParams.length != 2) {
            throw new ExprValidationException(message);
        }

        ExprNode first = this.positionalParams[0];
        ExprNode second = this.positionalParams[1];
        if (!second.isConstantResult()) {
            throw new ExprValidationException(message);
        }

        Number num = (Number) second.getForge().getExprEvaluator().evaluate(null, true, validationContext.getExprEvaluatorContext());
        int size = num.intValue();

        if (optionalFilter != null) {
            this.positionalParams = ExprNodeUtilityCore.addExpression(positionalParams, optionalFilter);
        }

        return validationContext.getEngineImportService().getAggregationFactoryFactory().makeNth(validationContext.getStatementExtensionSvcContext(), this, first.getForge().getEvaluationType(), size);
    }

    public String getAggregationFunctionName() {
        return "nth";
    }

    public final boolean equalsNodeAggregateMethodOnly(ExprAggregateNode node) {
        return node instanceof ExprNthAggNode;
    }

    protected boolean isFilterExpressionAsLastParameter() {
        return false;
    }
}
