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

import java.util.TimeZone;

public class DTLocalLDTIntervalForge extends DTLocalForgeIntervalBase {

    protected final TimeZone timeZone;

    public DTLocalLDTIntervalForge(IntervalForge intervalForge, TimeZone timeZone) {
        super(intervalForge);
        this.timeZone = timeZone;
    }

    public DTLocalEvaluator getDTEvaluator() {
        return new DTLocalLDTIntervalEval(intervalForge.getOp(), timeZone);
    }

    public DTLocalEvaluatorIntervalComp makeEvaluatorComp() {
        return new DTLocalLDTIntervalEval(intervalForge.getOp(), timeZone);
    }

    public CodegenExpression codegen(CodegenExpression inner, Class innerType, CodegenMethodScope codegenMethodScope, ExprForgeCodegenSymbol exprSymbol, CodegenClassScope codegenClassScope) {
        return DTLocalLDTIntervalEval.codegen(this, inner, codegenMethodScope, exprSymbol, codegenClassScope);
    }

    public CodegenExpression codegen(CodegenExpressionRef start, CodegenExpressionRef end, CodegenMethodScope codegenMethodScope, ExprForgeCodegenSymbol exprSymbol, CodegenClassScope codegenClassScope) {
        return DTLocalLDTIntervalEval.codegen(this, start, end, codegenMethodScope, exprSymbol, codegenClassScope);
    }
}
