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

import eu.uk.ncl.pet5o.esper.codegen.base.CodegenMembersColumnized;
import eu.uk.ncl.pet5o.esper.codegen.core.CodegenCtor;
import eu.uk.ncl.pet5o.esper.epl.expression.methodagg.ExprPlugInAggNode;

/**
 * Context for row-member code generation
 */
public class AggregationFunctionFactoryCodegenRowMemberContext {
    private final ExprPlugInAggNode parent;
    private final int column;
    private final CodegenCtor ctor;
    private final CodegenMembersColumnized membersColumnized;

    /**
     * Ctor.
     * @param parent expr node
     * @param column column number
     * @param membersColumnized members
     * @param ctor ctor
     */
    public AggregationFunctionFactoryCodegenRowMemberContext(ExprPlugInAggNode parent, int column, CodegenCtor ctor, CodegenMembersColumnized membersColumnized) {
        this.parent = parent;
        this.column = column;
        this.ctor = ctor;
        this.membersColumnized = membersColumnized;
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
     * Returns the ctor.
     * @return ctor
     */
    public CodegenCtor getCtor() {
        return ctor;
    }

    /**
     * Returns member access
     * @return members
     */
    public CodegenMembersColumnized getMembersColumnized() {
        return membersColumnized;
    }
}
