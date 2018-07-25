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
package eu.uk.ncl.pet5o.esper.epl.table.strategy;

import eu.uk.ncl.pet5o.esper.epl.expression.core.ExprEvaluator;
import eu.uk.ncl.pet5o.esper.epl.expression.core.ExprNode;

public abstract class ExprTableExprEvaluatorBase implements ExprEvaluator {

    protected final ExprNode exprNode;
    protected final String tableName;
    protected final String subpropName;
    protected final int streamNum;
    protected final Class returnType;

    public ExprTableExprEvaluatorBase(ExprNode exprNode, String tableName, String subpropName, int streamNum, Class returnType) {
        this.exprNode = exprNode;
        this.tableName = tableName;
        this.subpropName = subpropName;
        this.streamNum = streamNum;
        this.returnType = returnType;
    }

    public Class getReturnType() {
        return returnType;
    }
}
