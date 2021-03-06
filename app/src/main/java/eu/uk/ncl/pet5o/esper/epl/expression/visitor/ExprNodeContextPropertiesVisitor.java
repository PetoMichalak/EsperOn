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
package eu.uk.ncl.pet5o.esper.epl.expression.visitor;

import eu.uk.ncl.pet5o.esper.epl.expression.core.ExprContextPropertyNode;
import eu.uk.ncl.pet5o.esper.epl.expression.core.ExprNode;

/**
 * Visitor that early-exists when it finds a context partition property.
 */
public class ExprNodeContextPropertiesVisitor implements ExprNodeVisitor {
    private boolean found;

    public boolean isVisit(ExprNode exprNode) {
        return !found;
    }

    public void visit(ExprNode exprNode) {
        if (!(exprNode instanceof ExprContextPropertyNode)) {
            return;
        }
        found = true;
    }

    public boolean isFound() {
        return found;
    }
}
