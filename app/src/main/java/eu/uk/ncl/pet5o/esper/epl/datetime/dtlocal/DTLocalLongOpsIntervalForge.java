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
import eu.uk.ncl.pet5o.esper.epl.datetime.calop.CalendarForge;
import eu.uk.ncl.pet5o.esper.epl.datetime.interval.IntervalForge;
import eu.uk.ncl.pet5o.esper.epl.expression.codegen.ExprForgeCodegenSymbol;
import eu.uk.ncl.pet5o.esper.epl.expression.time.TimeAbacus;

import java.util.List;
import java.util.TimeZone;

import static eu.uk.ncl.pet5o.esper.epl.datetime.dtlocal.DTLocalUtil.getCalendarOps;

public class DTLocalLongOpsIntervalForge extends DTLocalForgeCalOpsIntervalBase {

    protected final TimeZone timeZone;
    protected final TimeAbacus timeAbacus;

    public DTLocalLongOpsIntervalForge(List<CalendarForge> calendarForges, IntervalForge intervalForge, TimeZone timeZone, TimeAbacus timeAbacus) {
        super(calendarForges, intervalForge);
        this.timeZone = timeZone;
        this.timeAbacus = timeAbacus;
    }

    public DTLocalEvaluator getDTEvaluator() {
        return new DTLocalLongOpsIntervalEval(getCalendarOps(calendarForges), intervalForge.getOp(), timeZone, timeAbacus);
    }

    public CodegenExpression codegen(CodegenExpression inner, Class innerType, CodegenMethodScope codegenMethodScope, ExprForgeCodegenSymbol exprSymbol, CodegenClassScope codegenClassScope) {
        return DTLocalLongOpsIntervalEval.codegenPointInTime(this, inner, innerType, codegenMethodScope, exprSymbol, codegenClassScope);
    }

    public DTLocalEvaluatorIntervalComp makeEvaluatorComp() {
        return new DTLocalLongOpsIntervalEval(getCalendarOps(calendarForges), intervalForge.getOp(), timeZone, timeAbacus);
    }

    public CodegenExpression codegen(CodegenExpressionRef start, CodegenExpressionRef end, CodegenMethodScope codegenMethodScope, ExprForgeCodegenSymbol exprSymbol, CodegenClassScope codegenClassScope) {
        return DTLocalLongOpsIntervalEval.codegenStartEnd(this, start, end, codegenMethodScope, exprSymbol, codegenClassScope);
    }
}
