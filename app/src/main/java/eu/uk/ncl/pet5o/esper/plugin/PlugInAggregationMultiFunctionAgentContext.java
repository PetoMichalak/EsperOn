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
package eu.uk.ncl.pet5o.esper.plugin;

import eu.uk.ncl.pet5o.esper.epl.expression.core.ExprNode;

public class PlugInAggregationMultiFunctionAgentContext {

    private final ExprNode[] childNodes;
    private final ExprNode optionalFilterExpression;

    public PlugInAggregationMultiFunctionAgentContext(ExprNode[] childNodes, ExprNode optionalFilterExpression) {
        this.childNodes = childNodes;
        this.optionalFilterExpression = optionalFilterExpression;
    }

    public ExprNode[] getChildNodes() {
        return childNodes;
    }

    public ExprNode getOptionalFilterExpression() {
        return optionalFilterExpression;
    }
}
