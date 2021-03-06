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
package eu.uk.ncl.pet5o.esper.epl.core.orderby;

import eu.uk.ncl.pet5o.esper.epl.expression.core.ExprNode;

public class OrderByElementForge {
    private ExprNode exprNode;
    private boolean isDescending;

    public OrderByElementForge(ExprNode exprNode, boolean isDescending) {
        this.exprNode = exprNode;
        this.isDescending = isDescending;
    }

    public ExprNode getExprNode() {
        return exprNode;
    }

    public boolean isDescending() {
        return isDescending;
    }
}
