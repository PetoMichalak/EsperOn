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
package eu.uk.ncl.pet5o.esper.epl.expression.ops;

import eu.uk.ncl.pet5o.esper.codegen.base.CodegenClassScope;
import eu.uk.ncl.pet5o.esper.codegen.base.CodegenMethodScope;
import eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpression;
import eu.uk.ncl.pet5o.esper.epl.expression.codegen.ExprForgeCodegenSymbol;
import eu.uk.ncl.pet5o.esper.epl.expression.core.ExprEvaluator;
import eu.uk.ncl.pet5o.esper.epl.expression.core.ExprForge;
import eu.uk.ncl.pet5o.esper.epl.expression.core.ExprForgeComplexityEnum;
import eu.uk.ncl.pet5o.esper.type.RelationalOpEnum;

/**
 * Represents a lesser or greater then (&lt;/&lt;=/&gt;/&gt;=) expression in a filter expression tree.
 */
public class ExprRelationalOpNodeForge implements ExprForge {
    private final ExprRelationalOpNodeImpl parent;
    private final RelationalOpEnum.Computer computer;

    public ExprRelationalOpNodeForge(ExprRelationalOpNodeImpl parent, RelationalOpEnum.Computer computer) {
        this.parent = parent;
        this.computer = computer;
    }

    public ExprEvaluator getExprEvaluator() {
        return new ExprRelationalOpNodeForgeEval(this, parent.getChildNodes()[0].getForge().getExprEvaluator(), parent.getChildNodes()[1].getForge().getExprEvaluator());
    }

    public CodegenExpression evaluateCodegen(Class requiredType, CodegenMethodScope codegenMethodScope, ExprForgeCodegenSymbol exprSymbol, CodegenClassScope codegenClassScope) {
        return ExprRelationalOpNodeForgeEval.codegen(this, codegenMethodScope, exprSymbol, codegenClassScope);
    }

    public ExprForgeComplexityEnum getComplexity() {
        return ExprForgeComplexityEnum.INTER;
    }

    public Class getEvaluationType() {
        return Boolean.class;
    }

    public ExprRelationalOpNodeImpl getForgeRenderable() {
        return parent;
    }

    public RelationalOpEnum.Computer getComputer() {
        return computer;
    }
}
