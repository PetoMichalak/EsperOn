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
package eu.uk.ncl.pet5o.esper.epl.expression.dot;

import eu.uk.ncl.pet5o.esper.codegen.base.CodegenClassScope;
import eu.uk.ncl.pet5o.esper.codegen.base.CodegenMethodScope;
import eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpression;
import eu.uk.ncl.pet5o.esper.epl.expression.codegen.ExprForgeCodegenSymbol;
import eu.uk.ncl.pet5o.esper.epl.expression.core.ExprForge;
import eu.uk.ncl.pet5o.esper.epl.rettype.EPType;
import eu.uk.ncl.pet5o.esper.epl.rettype.EPTypeHelper;

public class ExprDotForgeArrayGet implements ExprDotForge {
    private final EPType typeInfo;
    private final ExprForge indexExpression;

    public ExprDotForgeArrayGet(ExprForge index, Class componentType) {
        this.indexExpression = index;
        this.typeInfo = EPTypeHelper.singleValue(componentType);
    }

    public EPType getTypeInfo() {
        return typeInfo;
    }

    public void visit(ExprDotEvalVisitor visitor) {
        visitor.visitArraySingleItemSource();
    }

    public ExprDotEval getDotEvaluator() {
        return new ExprDotForgeArrayGetEval(this, indexExpression.getExprEvaluator());
    }

    public CodegenExpression codegen(CodegenExpression inner, Class innerType, CodegenMethodScope codegenMethodScope, ExprForgeCodegenSymbol exprSymbol, CodegenClassScope codegenClassScope) {
        return ExprDotForgeArrayGetEval.codegen(this, inner, innerType, codegenMethodScope, exprSymbol, codegenClassScope);
    }

    public ExprForge getIndexExpression() {
        return indexExpression;
    }
}
