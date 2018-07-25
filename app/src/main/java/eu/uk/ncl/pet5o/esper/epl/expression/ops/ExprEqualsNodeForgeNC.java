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

import static eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionBuilder.constantNull;
import static eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionBuilder.localMethod;

public class ExprEqualsNodeForgeNC extends ExprEqualsNodeForge {
    public ExprEqualsNodeForgeNC(ExprEqualsNodeImpl parent) {
        super(parent);
    }

    public ExprEvaluator getExprEvaluator() {
        ExprForge lhs = getForgeRenderable().getChildNodes()[0].getForge();
        ExprForge rhs = getForgeRenderable().getChildNodes()[1].getForge();
        if (!getForgeRenderable().isIs()) {
            return new ExprEqualsNodeForgeNCEvalEquals(getForgeRenderable(), lhs.getExprEvaluator(), rhs.getExprEvaluator());
        }
        return new ExprEqualsNodeForgeNCEvalIs(getForgeRenderable(), lhs.getExprEvaluator(), rhs.getExprEvaluator());
    }

    public CodegenExpression evaluateCodegen(Class requiredType, CodegenMethodScope codegenMethodScope, ExprForgeCodegenSymbol exprSymbol, CodegenClassScope codegenClassScope) {
        ExprForge lhs = getForgeRenderable().getChildNodes()[0].getForge();
        ExprForge rhs = getForgeRenderable().getChildNodes()[1].getForge();
        if (!getForgeRenderable().isIs()) {
            if (lhs.getEvaluationType() == null || rhs.getEvaluationType() == null) {
                return constantNull();
            }
            return localMethod(ExprEqualsNodeForgeNCEvalEquals.codegen(this, codegenMethodScope, exprSymbol, codegenClassScope, lhs, rhs));
        }
        return localMethod(ExprEqualsNodeForgeNCEvalIs.codegen(this, codegenMethodScope, exprSymbol, codegenClassScope, lhs, rhs));
    }

    public ExprForgeComplexityEnum getComplexity() {
        return ExprForgeComplexityEnum.INTER;
    }
}
