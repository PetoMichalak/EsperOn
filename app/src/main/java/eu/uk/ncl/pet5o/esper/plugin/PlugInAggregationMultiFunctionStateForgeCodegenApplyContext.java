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

import eu.uk.ncl.pet5o.esper.codegen.base.CodegenClassScope;
import eu.uk.ncl.pet5o.esper.codegen.base.CodegenMethodNode;
import eu.uk.ncl.pet5o.esper.codegen.core.CodegenNamedMethods;
import eu.uk.ncl.pet5o.esper.epl.expression.accessagg.ExprPlugInAggMultiFunctionNodeFactory;
import eu.uk.ncl.pet5o.esper.epl.expression.codegen.ExprForgeCodegenSymbol;

public class PlugInAggregationMultiFunctionStateForgeCodegenApplyContext {
    private final ExprPlugInAggMultiFunctionNodeFactory parent;
    private final int column;
    private final CodegenMethodNode method;
    private final ExprForgeCodegenSymbol symbols;
    private final CodegenClassScope classScope;
    private final CodegenNamedMethods namedMethods;

    public PlugInAggregationMultiFunctionStateForgeCodegenApplyContext(ExprPlugInAggMultiFunctionNodeFactory parent, int column, CodegenMethodNode method, ExprForgeCodegenSymbol symbols, CodegenClassScope classScope, CodegenNamedMethods namedMethods) {
        this.parent = parent;
        this.column = column;
        this.method = method;
        this.symbols = symbols;
        this.classScope = classScope;
        this.namedMethods = namedMethods;
    }

    public ExprPlugInAggMultiFunctionNodeFactory getParent() {
        return parent;
    }

    public int getColumn() {
        return column;
    }

    public CodegenMethodNode getMethod() {
        return method;
    }

    public ExprForgeCodegenSymbol getSymbols() {
        return symbols;
    }

    public CodegenClassScope getClassScope() {
        return classScope;
    }

    public CodegenNamedMethods getNamedMethods() {
        return namedMethods;
    }
}
