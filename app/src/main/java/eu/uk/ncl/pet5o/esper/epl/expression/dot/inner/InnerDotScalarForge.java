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
package eu.uk.ncl.pet5o.esper.epl.expression.dot.inner;

import eu.uk.ncl.pet5o.esper.client.EventType;
import eu.uk.ncl.pet5o.esper.codegen.base.CodegenClassScope;
import eu.uk.ncl.pet5o.esper.codegen.base.CodegenMethodNode;
import eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpression;
import eu.uk.ncl.pet5o.esper.epl.expression.codegen.ExprForgeCodegenSymbol;
import eu.uk.ncl.pet5o.esper.epl.expression.core.ExprForge;
import eu.uk.ncl.pet5o.esper.epl.expression.dot.ExprDotEvalRootChildInnerEval;
import eu.uk.ncl.pet5o.esper.epl.expression.dot.ExprDotEvalRootChildInnerForge;
import eu.uk.ncl.pet5o.esper.epl.rettype.EPType;
import eu.uk.ncl.pet5o.esper.epl.rettype.EPTypeHelper;

import static eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionBuilder.constantNull;

public class InnerDotScalarForge implements ExprDotEvalRootChildInnerForge {

    private ExprForge rootForge;

    public InnerDotScalarForge(ExprForge rootForge) {
        this.rootForge = rootForge;
    }

    public ExprDotEvalRootChildInnerEval getInnerEvaluator() {
        return new InnerDotScalarEval(rootForge.getExprEvaluator());
    }

    public CodegenExpression codegenEvaluate(CodegenMethodNode parentMethod, ExprForgeCodegenSymbol exprSymbol, CodegenClassScope codegenClassScope) {
        return rootForge.evaluateCodegen(rootForge.getEvaluationType(), parentMethod, exprSymbol, codegenClassScope);
    }

    public CodegenExpression evaluateGetROCollectionEventsCodegen(CodegenMethodNode parentMethod, ExprForgeCodegenSymbol exprSymbol, CodegenClassScope codegenClassScope) {
        return constantNull();
    }

    public CodegenExpression evaluateGetROCollectionScalarCodegen(CodegenMethodNode parentMethod, ExprForgeCodegenSymbol exprSymbol, CodegenClassScope codegenClassScope) {
        return constantNull();
    }

    public CodegenExpression evaluateGetEventBeanCodegen(CodegenMethodNode parentMethod, ExprForgeCodegenSymbol exprSymbol, CodegenClassScope codegenClassScope) {
        return constantNull();
    }

    public EventType getEventTypeCollection() {
        return null;
    }

    public Class getComponentTypeCollection() {
        return null;
    }

    public EventType getEventTypeSingle() {
        return null;
    }

    public EPType getTypeInfo() {
        return EPTypeHelper.singleValue(rootForge.getEvaluationType());
    }
}
