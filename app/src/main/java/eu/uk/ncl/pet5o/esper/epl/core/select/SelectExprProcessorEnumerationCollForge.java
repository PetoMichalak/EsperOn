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
package eu.uk.ncl.pet5o.esper.epl.core.select;

import eu.uk.ncl.pet5o.esper.client.EventType;
import eu.uk.ncl.pet5o.esper.codegen.base.CodegenClassScope;
import eu.uk.ncl.pet5o.esper.codegen.base.CodegenMethodScope;
import eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpression;
import eu.uk.ncl.pet5o.esper.epl.expression.codegen.ExprForgeCodegenSymbol;
import eu.uk.ncl.pet5o.esper.epl.expression.core.*;
import eu.uk.ncl.pet5o.esper.util.JavaClassHelper;

public class SelectExprProcessorEnumerationCollForge implements ExprForge {
    protected final ExprEnumerationForge enumerationForge;
    private final EventType targetType;
    private final boolean firstRowOnly;

    public SelectExprProcessorEnumerationCollForge(ExprEnumerationForge enumerationForge, EventType targetType, boolean firstRowOnly) {
        this.enumerationForge = enumerationForge;
        this.targetType = targetType;
        this.firstRowOnly = firstRowOnly;
    }

    public ExprEvaluator getExprEvaluator() {
        if (firstRowOnly) {
            return new SelectExprProcessorEnumerationCollEvalFirstRow(this, enumerationForge.getExprEvaluatorEnumeration());
        }
        return new SelectExprProcessorEnumerationCollEval(this, enumerationForge.getExprEvaluatorEnumeration());
    }

    public CodegenExpression evaluateCodegen(Class requiredType, CodegenMethodScope codegenMethodScope, ExprForgeCodegenSymbol exprSymbol, CodegenClassScope codegenClassScope) {
        if (firstRowOnly) {
            return SelectExprProcessorEnumerationCollEvalFirstRow.codegen(this, codegenMethodScope, exprSymbol, codegenClassScope);
        }
        return SelectExprProcessorEnumerationCollEval.codegen(this, codegenMethodScope, exprSymbol, codegenClassScope);
    }

    public ExprForgeComplexityEnum getComplexity() {
        return ExprForgeComplexityEnum.INTER;
    }

    public Class getEvaluationType() {
        if (firstRowOnly) {
            return targetType.getUnderlyingType();
        }
        return JavaClassHelper.getArrayType(targetType.getUnderlyingType());
    }

    public ExprNodeRenderable getForgeRenderable() {
        return enumerationForge.getForgeRenderable();
    }
}
