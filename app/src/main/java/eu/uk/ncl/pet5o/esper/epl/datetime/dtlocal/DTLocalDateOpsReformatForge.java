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
import eu.uk.ncl.pet5o.esper.epl.datetime.reformatop.ReformatForge;
import eu.uk.ncl.pet5o.esper.epl.expression.codegen.ExprForgeCodegenSymbol;

import java.util.List;
import java.util.TimeZone;

import static eu.uk.ncl.pet5o.esper.epl.datetime.dtlocal.DTLocalUtil.getCalendarOps;

public class DTLocalDateOpsReformatForge extends DTLocalForgeCalopReformatBase {

    protected final TimeZone timeZone;

    public DTLocalDateOpsReformatForge(List<CalendarForge> calendarForges, ReformatForge reformatForge, TimeZone timeZone) {
        super(calendarForges, reformatForge);
        this.timeZone = timeZone;
    }

    public DTLocalEvaluator getDTEvaluator() {
        return new DTLocalDateOpsReformatEval(getCalendarOps(calendarForges), reformatForge.getOp(), timeZone);
    }

    public CodegenExpression codegen(CodegenExpression inner, Class innerType, CodegenMethodScope codegenMethodScope, ExprForgeCodegenSymbol exprSymbol, CodegenClassScope codegenClassScope) {
        return DTLocalDateOpsReformatEval.codegen(this, inner, codegenMethodScope, exprSymbol, codegenClassScope);
    }
}