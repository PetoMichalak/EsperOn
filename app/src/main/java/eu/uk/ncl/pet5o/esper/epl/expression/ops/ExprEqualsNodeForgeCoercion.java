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
import eu.uk.ncl.pet5o.esper.codegen.base.CodegenMethodNode;
import eu.uk.ncl.pet5o.esper.codegen.base.CodegenMethodScope;
import eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpression;
import eu.uk.ncl.pet5o.esper.epl.expression.codegen.ExprForgeCodegenSymbol;
import eu.uk.ncl.pet5o.esper.epl.expression.core.ExprEvaluator;
import eu.uk.ncl.pet5o.esper.epl.expression.core.ExprForgeComplexityEnum;
import eu.uk.ncl.pet5o.esper.epl.expression.core.ExprNode;
import eu.uk.ncl.pet5o.esper.util.SimpleNumberCoercer;

import static eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionBuilder.localMethod;

public class ExprEqualsNodeForgeCoercion extends ExprEqualsNodeForge {

    private final SimpleNumberCoercer numberCoercerLHS;
    private final SimpleNumberCoercer numberCoercerRHS;

    public ExprEqualsNodeForgeCoercion(ExprEqualsNodeImpl parent, SimpleNumberCoercer numberCoercerLHS, SimpleNumberCoercer numberCoercerRHS) {
        super(parent);
        this.numberCoercerLHS = numberCoercerLHS;
        this.numberCoercerRHS = numberCoercerRHS;
    }

    public ExprEvaluator getExprEvaluator() {
        ExprNode lhs = getForgeRenderable().getChildNodes()[0];
        ExprNode rhs = getForgeRenderable().getChildNodes()[1];
        return new ExprEqualsNodeForgeCoercionEval(getForgeRenderable(), lhs.getForge().getExprEvaluator(), rhs.getForge().getExprEvaluator(), numberCoercerLHS, numberCoercerRHS);
    }

    public CodegenExpression evaluateCodegen(Class requiredType, CodegenMethodScope codegenMethodScope, ExprForgeCodegenSymbol exprSymbol, CodegenClassScope codegenClassScope) {
        ExprNode lhs = getForgeRenderable().getChildNodes()[0];
        ExprNode rhs = getForgeRenderable().getChildNodes()[1];
        CodegenMethodNode method = ExprEqualsNodeForgeCoercionEval.codegen(this, codegenMethodScope, exprSymbol, codegenClassScope, lhs, rhs);
        return localMethod(method);
    }

    public ExprForgeComplexityEnum getComplexity() {
        return ExprForgeComplexityEnum.INTER;
    }

    public SimpleNumberCoercer getNumberCoercerLHS() {
        return numberCoercerLHS;
    }

    public SimpleNumberCoercer getNumberCoercerRHS() {
        return numberCoercerRHS;
    }
}
