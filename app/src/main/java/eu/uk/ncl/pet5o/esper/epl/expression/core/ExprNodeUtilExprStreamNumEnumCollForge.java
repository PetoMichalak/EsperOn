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
package eu.uk.ncl.pet5o.esper.epl.expression.core;

import eu.uk.ncl.pet5o.esper.codegen.base.CodegenClassScope;
import eu.uk.ncl.pet5o.esper.codegen.base.CodegenMethodScope;
import eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpression;
import eu.uk.ncl.pet5o.esper.epl.expression.codegen.ExprForgeCodegenSymbol;

import java.util.Collection;

public class ExprNodeUtilExprStreamNumEnumCollForge implements ExprForge {
    private final ExprEnumerationForge enumeration;

    public ExprNodeUtilExprStreamNumEnumCollForge(ExprEnumerationForge enumeration) {
        this.enumeration = enumeration;
    }

    public ExprEvaluator getExprEvaluator() {
        return new ExprNodeUtilExprStreamNumEnumCollEval(enumeration.getExprEvaluatorEnumeration());
    }

    public CodegenExpression evaluateCodegen(Class requiredType, CodegenMethodScope codegenMethodScope, ExprForgeCodegenSymbol exprSymbol, CodegenClassScope codegenClassScope) {
        return enumeration.evaluateGetROCollectionEventsCodegen(codegenMethodScope, exprSymbol, codegenClassScope);
    }

    public ExprForgeComplexityEnum getComplexity() {
        return ExprForgeComplexityEnum.INTER;
    }

    public Class getEvaluationType() {
        return Collection.class;
    }

    public ExprNodeRenderable getForgeRenderable() {
        return enumeration.getForgeRenderable();
    }
}
