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
package eu.uk.ncl.pet5o.esper.epl.agg.rollup;

import eu.uk.ncl.pet5o.esper.epl.agg.service.common.AggregationGroupByRollupDesc;
import eu.uk.ncl.pet5o.esper.epl.expression.core.ExprNode;

public class GroupByRollupPlanDesc {
    private final ExprNode[] expressions;
    private final AggregationGroupByRollupDesc rollupDesc;

    public GroupByRollupPlanDesc(ExprNode[] expressions, AggregationGroupByRollupDesc rollupDesc) {
        this.expressions = expressions;
        this.rollupDesc = rollupDesc;
    }

    public ExprNode[] getExpressions() {
        return expressions;
    }

    public AggregationGroupByRollupDesc getRollupDesc() {
        return rollupDesc;
    }
}
