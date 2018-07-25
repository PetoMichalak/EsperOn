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

import eu.uk.ncl.pet5o.esper.epl.expression.core.ExprNode;

import java.util.Collections;
import java.util.List;

public class GroupByRollupNodeSingleExpr extends GroupByRollupNodeBase {

    private final ExprNode expression;

    public GroupByRollupNodeSingleExpr(ExprNode expression) {
        this.expression = expression;
    }

    public List<int[]> evaluate(GroupByRollupEvalContext context) {
        int index = context.getIndex(expression);
        return Collections.singletonList(new int[]{index});
    }
}
