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
package eu.uk.ncl.pet5o.esper.epl.index.quadtree;

import eu.uk.ncl.pet5o.esper.epl.expression.core.ExprEvaluator;
import eu.uk.ncl.pet5o.esper.epl.lookup.EventAdvancedIndexConfigStatement;

public class AdvancedIndexConfigStatementPointRegionQuadtree implements EventAdvancedIndexConfigStatement {
    private final ExprEvaluator xEval;
    private final ExprEvaluator yEval;

    public AdvancedIndexConfigStatementPointRegionQuadtree(ExprEvaluator xEval, ExprEvaluator yEval) {
        this.xEval = xEval;
        this.yEval = yEval;
    }

    public ExprEvaluator getxEval() {
        return xEval;
    }

    public ExprEvaluator getyEval() {
        return yEval;
    }
}
