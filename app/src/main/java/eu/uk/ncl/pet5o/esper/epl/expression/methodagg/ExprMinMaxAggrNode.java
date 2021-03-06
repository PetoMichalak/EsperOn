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

import eu.uk.ncl.pet5o.esper.core.service.StatementType;
import eu.uk.ncl.pet5o.esper.epl.agg.service.common.AggregationMethodFactory;
import eu.uk.ncl.pet5o.esper.epl.expression.baseagg.ExprAggregateNode;
import eu.uk.ncl.pet5o.esper.epl.expression.baseagg.ExprAggregateNodeBase;
import eu.uk.ncl.pet5o.esper.epl.expression.core.ExprNode;
import eu.uk.ncl.pet5o.esper.epl.expression.core.ExprValidationContext;
import eu.uk.ncl.pet5o.esper.epl.expression.core.ExprValidationException;
import eu.uk.ncl.pet5o.esper.epl.expression.core.MinMaxTypeEnum;
import eu.uk.ncl.pet5o.esper.epl.util.ExprNodeUtilityRich;

/**
 * Represents the min/max(distinct? ...) aggregate function is an expression tree.
 */
public class ExprMinMaxAggrNode extends ExprAggregateNodeBase {
    private static final long serialVersionUID = -7828413362615586145L;

    private final MinMaxTypeEnum minMaxTypeEnum;
    private final boolean isFFunc;
    private final boolean isEver;
    private boolean hasFilter;

    public ExprMinMaxAggrNode(boolean distinct, MinMaxTypeEnum minMaxTypeEnum, boolean isFFunc, boolean isEver) {
        super(distinct);
        this.minMaxTypeEnum = minMaxTypeEnum;
        this.isFFunc = isFFunc;
        this.isEver = isEver;
    }

    public AggregationMethodFactory validateAggregationChild(ExprValidationContext validationContext) throws ExprValidationException {
        if (positionalParams.length == 0 || positionalParams.length > 2) {
            throw new ExprValidationException(minMaxTypeEnum.toString() + " node must have either 1 or 2 parameters");
        }

        ExprNode child = positionalParams[0];
        boolean hasDataWindows;
        if (isEver) {
            hasDataWindows = false;
        } else {
            if (validationContext.getExprEvaluatorContext().getStatementType() == StatementType.CREATE_TABLE) {
                hasDataWindows = true;
            } else {
                hasDataWindows = ExprNodeUtilityRich.hasRemoveStreamForAggregations(child, validationContext.getStreamTypeService(), validationContext.isResettingAggregations());
            }
        }

        if (isFFunc) {
            if (positionalParams.length < 2) {
                throw new ExprValidationException(minMaxTypeEnum.toString() + "-filtered aggregation function must have a filter expression as a second parameter");
            }
            super.validateFilter(positionalParams[1].getForge());
        }

        hasFilter = positionalParams.length == 2;
        return validationContext.getEngineImportService().getAggregationFactoryFactory().makeMinMax(validationContext.getStatementExtensionSvcContext(), this, child.getForge().getEvaluationType(), hasDataWindows);
    }

    public final boolean equalsNodeAggregateMethodOnly(ExprAggregateNode node) {
        if (!(node instanceof ExprMinMaxAggrNode)) {
            return false;
        }

        ExprMinMaxAggrNode other = (ExprMinMaxAggrNode) node;
        return other.minMaxTypeEnum == this.minMaxTypeEnum && other.isEver == this.isEver;
    }

    /**
     * Returns the indicator for minimum or maximum.
     *
     * @return min/max indicator
     */
    public MinMaxTypeEnum getMinMaxTypeEnum() {
        return minMaxTypeEnum;
    }

    public boolean isHasFilter() {
        return hasFilter;
    }

    public String getAggregationFunctionName() {
        return minMaxTypeEnum.getExpressionText();
    }

    public boolean isEver() {
        return isEver;
    }

    protected boolean isFilterExpressionAsLastParameter() {
        return true;
    }
}
