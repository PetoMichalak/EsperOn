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

import eu.uk.ncl.pet5o.esper.client.EventType;
import eu.uk.ncl.pet5o.esper.codegen.base.CodegenClassScope;
import eu.uk.ncl.pet5o.esper.codegen.base.CodegenMethodNode;
import eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpression;
import eu.uk.ncl.pet5o.esper.epl.expression.codegen.ExprForgeCodegenSymbol;
import eu.uk.ncl.pet5o.esper.epl.rettype.EPType;

public interface ExprDotEvalRootChildInnerForge {
    EventType getEventTypeCollection();
    EventType getEventTypeSingle();
    Class getComponentTypeCollection();
    EPType getTypeInfo();

    ExprDotEvalRootChildInnerEval getInnerEvaluator();
    CodegenExpression codegenEvaluate(CodegenMethodNode parentMethod, ExprForgeCodegenSymbol exprSymbol, CodegenClassScope codegenClassScope);

    CodegenExpression evaluateGetROCollectionEventsCodegen(CodegenMethodNode parentMethod, ExprForgeCodegenSymbol exprSymbol, CodegenClassScope codegenClassScope);
    CodegenExpression evaluateGetROCollectionScalarCodegen(CodegenMethodNode parentMethod, ExprForgeCodegenSymbol exprSymbol, CodegenClassScope codegenClassScope);
    CodegenExpression evaluateGetEventBeanCodegen(CodegenMethodNode parentMethod, ExprForgeCodegenSymbol exprSymbol, CodegenClassScope codegenClassScope);
}
