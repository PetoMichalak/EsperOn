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
package eu.uk.ncl.pet5o.esper.epl.table.mgmt;

import eu.uk.ncl.pet5o.esper.epl.expression.core.ExprEvaluator;
import eu.uk.ncl.pet5o.esper.epl.expression.core.ExprForge;
import eu.uk.ncl.pet5o.esper.epl.expression.core.ExprNode;

public class TableColumnMethodPair {
    private final ExprEvaluator evaluator;
    private final ExprForge[] forges;
    private final int targetIndex;
    private final ExprNode aggregationNode;

    public TableColumnMethodPair(ExprEvaluator evaluator, ExprForge[] forges, int targetIndex, ExprNode aggregationNode) {
        this.evaluator = evaluator;
        this.forges = forges;
        this.targetIndex = targetIndex;
        this.aggregationNode = aggregationNode;
    }

    public ExprEvaluator getEvaluator() {
        return evaluator;
    }

    public int getTargetIndex() {
        return targetIndex;
    }

    public ExprNode getAggregationNode() {
        return aggregationNode;
    }

    public ExprForge[] getForges() {
        return forges;
    }
}
