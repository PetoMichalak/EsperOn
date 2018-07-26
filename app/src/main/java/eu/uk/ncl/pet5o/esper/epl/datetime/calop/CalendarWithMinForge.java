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

import eu.uk.ncl.pet5o.esper.client.EventBean;
import eu.uk.ncl.pet5o.esper.codegen.base.CodegenClassScope;
import eu.uk.ncl.pet5o.esper.codegen.base.CodegenMethodScope;
import eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpression;
import eu.uk.ncl.pet5o.esper.epl.expression.codegen.ExprForgeCodegenSymbol;
import eu.uk.ncl.pet5o.esper.epl.expression.core.ExprEvaluatorContext;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.time.temporal.ValueRange;
import java.util.Calendar;

import static eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionBuilder.constant;
import static eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionBuilder.exprDotMethod;
import static eu.uk.ncl.pet5o.esper.epl.datetime.calop.CalendarWithMaxForge.codegenLDTZDTMinMax;

public class CalendarWithMinForge implements CalendarForge, CalendarOp {

    private final CalendarFieldEnum fieldName;

    public CalendarWithMinForge(CalendarFieldEnum fieldName) {
        this.fieldName = fieldName;
    }

    public CalendarOp getEvalOp() {
        return this;
    }

    public void evaluate(Calendar cal, EventBean[] eventsPerStream, boolean isNewData, ExprEvaluatorContext context) {
        cal.set(fieldName.getCalendarField(), cal.getActualMinimum(fieldName.getCalendarField()));
    }

    public CodegenExpression codegenCalendar(CodegenExpression cal, CodegenMethodScope codegenMethodScope, ExprForgeCodegenSymbol exprSymbol, CodegenClassScope codegenClassScope) {
        CodegenExpression field = constant(fieldName.getCalendarField());
        return exprDotMethod(cal, "set", field, exprDotMethod(cal, "getActualMinimum", field));
    }

    public LocalDateTime evaluate(LocalDateTime ldt, EventBean[] eventsPerStream, boolean isNewData, ExprEvaluatorContext context) {
        ValueRange range = ldt.range(fieldName.getChronoField());
        return ldt.with(fieldName.getChronoField(), range.getMinimum());
    }

    public ZonedDateTime evaluate(ZonedDateTime zdt, EventBean[] eventsPerStream, boolean isNewData, ExprEvaluatorContext context) {
        ValueRange range = zdt.range(fieldName.getChronoField());
        return zdt.with(fieldName.getChronoField(), range.getMinimum());
    }

    public CodegenExpression codegenLDT(CodegenExpression ldt, CodegenMethodScope codegenMethodScope, ExprForgeCodegenSymbol exprSymbol, CodegenClassScope codegenClassScope) {
        return codegenLDTZDTMinMax(ldt, false, fieldName);
    }

    public CodegenExpression codegenZDT(CodegenExpression zdt, CodegenMethodScope codegenMethodScope, ExprForgeCodegenSymbol exprSymbol, CodegenClassScope codegenClassScope) {
        return codegenLDTZDTMinMax(zdt, false, fieldName);
    }
}
