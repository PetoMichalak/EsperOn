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
import eu.uk.ncl.pet5o.esper.epl.datetime.calop.CalendarForge;
import eu.uk.ncl.pet5o.esper.epl.expression.codegen.ExprForgeCodegenSymbol;
import eu.uk.ncl.pet5o.esper.epl.expression.time.TimeAbacus;

import java.util.List;
import java.util.TimeZone;

import static eu.uk.ncl.pet5o.esper.epl.datetime.dtlocal.DTLocalUtil.getCalendarOps;

public class DTLocalCalOpsLongForge extends DTLocalForgeCalOpsCalBase implements DTLocalForge {

    protected final TimeZone timeZone;
    protected final TimeAbacus timeAbacus;

    public DTLocalCalOpsLongForge(List<CalendarForge> calendarForges, TimeZone timeZone, TimeAbacus timeAbacus) {
        super(calendarForges);
        this.timeZone = timeZone;
        this.timeAbacus = timeAbacus;
    }

    public DTLocalEvaluator getDTEvaluator() {
        return new DTLocalCalOpsLongEval(getCalendarOps(calendarForges), timeZone, timeAbacus);
    }

    public CodegenExpression codegen(CodegenExpression inner, Class innerType, CodegenMethodScope codegenMethodScope, ExprForgeCodegenSymbol exprSymbol, CodegenClassScope codegenClassScope) {
        return DTLocalCalOpsLongEval.codegen(this, inner, codegenMethodScope, exprSymbol, codegenClassScope);
    }
}
