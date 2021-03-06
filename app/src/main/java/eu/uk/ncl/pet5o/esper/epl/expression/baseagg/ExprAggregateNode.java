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
package eu.uk.ncl.pet5o.esper.epl.expression.baseagg;

import eu.uk.ncl.pet5o.esper.epl.agg.service.common.AggregationMethodFactory;
import eu.uk.ncl.pet5o.esper.epl.agg.service.common.AggregationResultFuture;
import eu.uk.ncl.pet5o.esper.epl.expression.core.ExprEvaluator;
import eu.uk.ncl.pet5o.esper.epl.expression.core.ExprForge;
import eu.uk.ncl.pet5o.esper.epl.expression.core.ExprNode;
import eu.uk.ncl.pet5o.esper.epl.expression.core.ExprValidationException;

/**
 * Base expression node that represents an aggregation function such as 'sum' or 'count'.
 */
public interface ExprAggregateNode extends ExprEvaluator, ExprForge, ExprNode {
    public AggregationMethodFactory getFactory();

    public void setAggregationResultFuture(AggregationResultFuture aggregationResultFuture, int column);

    public boolean isDistinct();

    public ExprAggregateLocalGroupByDesc getOptionalLocalGroupBy();

    public void validatePositionals() throws ExprValidationException;

    public ExprNode[] getPositionalParams();

    public ExprNode getOptionalFilter();
}
