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
package eu.uk.ncl.pet5o.esper.epl.datetime.calop;

import eu.uk.ncl.pet5o.esper.codegen.base.CodegenClassScope;
import eu.uk.ncl.pet5o.esper.codegen.base.CodegenMethodScope;
import eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpression;
import eu.uk.ncl.pet5o.esper.epl.expression.codegen.ExprForgeCodegenSymbol;
import eu.uk.ncl.pet5o.esper.epl.expression.core.ExprForge;

public class CalendarSetForge implements CalendarForge {

    protected final CalendarFieldEnum fieldName;
    protected final ExprForge valueExpr;

    public CalendarSetForge(CalendarFieldEnum fieldName, ExprForge valueExpr) {
        this.fieldName = fieldName;
        this.valueExpr = valueExpr;
    }

    public CalendarOp getEvalOp() {
        return new CalendarSetForgeOp(fieldName, valueExpr.getExprEvaluator());
    }

    public CodegenExpression codegenCalendar(CodegenExpression cal, CodegenMethodScope codegenMethodScope, ExprForgeCodegenSymbol exprSymbol, CodegenClassScope codegenClassScope) {
        return CalendarSetForgeOp.codegenCalendar(this, cal, codegenMethodScope, exprSymbol, codegenClassScope);
    }

    public CodegenExpression codegenLDT(CodegenExpression ldt, CodegenMethodScope codegenMethodScope, ExprForgeCodegenSymbol exprSymbol, CodegenClassScope codegenClassScope) {
        return CalendarSetForgeOp.codegenLDT(this, ldt, codegenMethodScope, exprSymbol, codegenClassScope);
    }

    public CodegenExpression codegenZDT(CodegenExpression zdt, CodegenMethodScope codegenMethodScope, ExprForgeCodegenSymbol exprSymbol, CodegenClassScope codegenClassScope) {
        return CalendarSetForgeOp.codegenZDT(this, zdt, codegenMethodScope, exprSymbol, codegenClassScope);
    }
}