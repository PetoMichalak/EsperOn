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
package eu.uk.ncl.pet5o.esper.client.hook;

import eu.uk.ncl.pet5o.esper.codegen.base.CodegenClassScope;
import eu.uk.ncl.pet5o.esper.codegen.base.CodegenMethodNode;
import eu.uk.ncl.pet5o.esper.epl.expression.methodagg.ExprPlugInAggNode;

/**
 * Context for apply-enter/leave managed code generation
 */
public class AggregationFunctionFactoryCodegenRowApplyContextManaged {
    private final ExprPlugInAggNode parent;
    private final int column;
    private final CodegenMethodNode method;
    private final CodegenClassScope classScope;

    /**
     * Ctor.
     * @param parent expr node
     * @param column column number
     * @param method method
     * @param classScope scope
     */
    public AggregationFunctionFactoryCodegenRowApplyContextManaged(ExprPlugInAggNode parent, int column, CodegenMethodNode method, CodegenClassScope classScope) {
        this.parent = parent;
        this.column = column;
        this.method = method;
        this.classScope = classScope;
    }

    /**
     * Returns the expression node
     * @return expr node
     */
    public ExprPlugInAggNode getParent() {
        return parent;
    }

    /**
     * Returns the column number
     * @return column number
     */
    public int getColumn() {
        return column;
    }

    /**
     * Returns the method
     * @return method
     */
    public CodegenMethodNode getMethod() {
        return method;
    }

    /**
     * Returns the class scope
     * @return class scope
     */
    public CodegenClassScope getClassScope() {
        return classScope;
    }
}
