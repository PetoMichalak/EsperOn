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

public class SelectExprProcessorEnumerationAtBeanSingleForge implements ExprForge {
    protected final ExprEnumerationForge enumerationForge;
    private final EventType eventTypeSingle;

    public SelectExprProcessorEnumerationAtBeanSingleForge(ExprEnumerationForge enumerationForge, EventType eventTypeSingle) {
        this.enumerationForge = enumerationForge;
        this.eventTypeSingle = eventTypeSingle;
    }

    public ExprEvaluator getExprEvaluator() {
        return new SelectExprProcessorEnumerationAtBeanSingleEval(this, enumerationForge.getExprEvaluatorEnumeration());
    }

    public CodegenExpression evaluateCodegen(Class requiredType, CodegenMethodScope codegenMethodScope, ExprForgeCodegenSymbol exprSymbol, CodegenClassScope codegenClassScope) {
        return SelectExprProcessorEnumerationAtBeanSingleEval.codegen(this, codegenMethodScope, exprSymbol, codegenClassScope);
    }

    public ExprForgeComplexityEnum getComplexity() {
        return ExprForgeComplexityEnum.INTER;
    }

    public Class getEvaluationType() {
        return eventTypeSingle.getUnderlyingType();
    }

    public ExprNodeRenderable getForgeRenderable() {
        return enumerationForge.getForgeRenderable();
    }
}
