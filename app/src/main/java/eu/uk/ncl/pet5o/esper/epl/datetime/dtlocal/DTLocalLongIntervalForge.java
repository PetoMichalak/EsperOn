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
package eu.uk.ncl.pet5o.esper.epl.datetime.dtlocal;

import eu.uk.ncl.pet5o.esper.codegen.base.CodegenClassScope;
import eu.uk.ncl.pet5o.esper.codegen.base.CodegenMethodScope;
import eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpression;
import eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionRef;
import eu.uk.ncl.pet5o.esper.epl.datetime.interval.IntervalForge;
import eu.uk.ncl.pet5o.esper.epl.expression.codegen.ExprForgeCodegenSymbol;

public class DTLocalLongIntervalForge extends DTLocalForgeIntervalBase {

    public DTLocalLongIntervalForge(IntervalForge intervalForge) {
        super(intervalForge);
    }

    public DTLocalEvaluator getDTEvaluator() {
        return new DTLocalLongIntervalEval(intervalForge.getOp());
    }

    public DTLocalEvaluatorIntervalComp makeEvaluatorComp() {
        return new DTLocalLongIntervalEval(intervalForge.getOp());
    }

    public CodegenExpression codegen(CodegenExpression inner, Class innerType, CodegenMethodScope codegenMethodScope, ExprForgeCodegenSymbol exprSymbol, CodegenClassScope codegenClassScope) {
        return intervalForge.codegen(inner, inner, codegenMethodScope, exprSymbol, codegenClassScope);
    }

    public CodegenExpression codegen(CodegenExpressionRef start, CodegenExpressionRef end, CodegenMethodScope codegenMethodScope, ExprForgeCodegenSymbol exprSymbol, CodegenClassScope codegenClassScope) {
        return intervalForge.codegen(start, end, codegenMethodScope, exprSymbol, codegenClassScope);
    }
}
