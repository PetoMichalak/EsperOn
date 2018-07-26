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
import eu.uk.ncl.pet5o.esper.codegen.base.CodegenMethodNode;
import eu.uk.ncl.pet5o.esper.codegen.base.CodegenMethodScope;
import eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpression;
import eu.uk.ncl.pet5o.esper.epl.expression.codegen.ExprForgeCodegenSymbol;
import eu.uk.ncl.pet5o.esper.epl.expression.core.ExprEvaluator;
import eu.uk.ncl.pet5o.esper.epl.expression.core.ExprEvaluatorContext;
import eu.uk.ncl.pet5o.esper.util.SimpleNumberCoercerFactory;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoField;
import java.util.Calendar;

import static eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionBuilder.*;
import static eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionBuilder.constant;
import static eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionBuilder.enumValue;
import static eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionBuilder.exprDotMethod;
import static eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionBuilder.ref;

public class CalendarSetForgeOp implements CalendarOp {

    private final CalendarFieldEnum fieldName;
    private final ExprEvaluator valueExpr;

    public CalendarSetForgeOp(CalendarFieldEnum fieldName, ExprEvaluator valueExpr) {
        this.fieldName = fieldName;
        this.valueExpr = valueExpr;
    }

    public void evaluate(Calendar cal, EventBean[] eventsPerStream, boolean isNewData, ExprEvaluatorContext context) {
        Integer value = CalendarOpUtil.getInt(valueExpr, eventsPerStream, isNewData, context);
        if (value == null) {
            return;
        }
        cal.set(fieldName.getCalendarField(), value);
    }

    public static CodegenExpression codegenCalendar(CalendarSetForge forge, CodegenExpression cal, CodegenMethodScope codegenMethodScope, ExprForgeCodegenSymbol exprSymbol, CodegenClassScope codegenClassScope) {
        CodegenExpression calField = constant(forge.fieldName.getCalendarField());
        Class evaluationType = forge.valueExpr.getEvaluationType();
        if (evaluationType.isPrimitive()) {
            CodegenExpression valueExpr = forge.valueExpr.evaluateCodegen(evaluationType, codegenMethodScope, exprSymbol, codegenClassScope);
            return exprDotMethod(cal, "set", calField, valueExpr);
        }

        CodegenMethodNode methodNode = codegenMethodScope.makeChild(void.class, CalendarSetForgeOp.class, codegenClassScope).addParam(Calendar.class, "cal");
        CodegenExpression valueExpr = forge.valueExpr.evaluateCodegen(evaluationType, methodNode, exprSymbol, codegenClassScope);
        methodNode.getBlock()
                .declareVar(Integer.class, "value", SimpleNumberCoercerFactory.SimpleNumberCoercerInt.coerceCodegenMayNull(valueExpr, forge.valueExpr.getEvaluationType(), methodNode, codegenClassScope))
                .ifRefNullReturnNull("value")
                .expression(exprDotMethod(cal, "set", calField, ref("value")))
                .methodEnd();
        return localMethod(methodNode, cal);
    }

    public LocalDateTime evaluate(LocalDateTime ldt, EventBean[] eventsPerStream, boolean isNewData, ExprEvaluatorContext context) {
        Integer value = CalendarOpUtil.getInt(valueExpr, eventsPerStream, isNewData, context);
        if (value == null) {
            return ldt;
        }
        return ldt.with(fieldName.getChronoField(), value);
    }

    public static CodegenExpression codegenLDT(CalendarSetForge forge, CodegenExpression ldt, CodegenMethodScope codegenMethodScope, ExprForgeCodegenSymbol exprSymbol, CodegenClassScope codegenClassScope) {
        ChronoField chronoField = forge.fieldName.getChronoField();
        CodegenMethodNode methodNode = codegenMethodScope.makeChild(LocalDateTime.class, CalendarSetForgeOp.class, codegenClassScope).addParam(LocalDateTime.class, "ldt");
        Class evaluationType = forge.valueExpr.getEvaluationType();

        methodNode.getBlock()
                .declareVar(Integer.class, "value", SimpleNumberCoercerFactory.SimpleNumberCoercerInt.coerceCodegenMayNull(forge.valueExpr.evaluateCodegen(evaluationType, methodNode, exprSymbol, codegenClassScope), evaluationType, methodNode, codegenClassScope))
                .ifRefNull("value").blockReturn(ref("ldt"))
                .methodReturn(exprDotMethod(ref("ldt"), "with", enumValue(ChronoField.class, chronoField.name()), ref("value")));
        return localMethod(methodNode, ldt);
    }

    public ZonedDateTime evaluate(ZonedDateTime zdt, EventBean[] eventsPerStream, boolean isNewData, ExprEvaluatorContext context) {
        Integer value = CalendarOpUtil.getInt(valueExpr, eventsPerStream, isNewData, context);
        if (value == null) {
            return zdt;
        }
        return zdt.with(fieldName.getChronoField(), value);
    }

    public static CodegenExpression codegenZDT(CalendarSetForge forge, CodegenExpression zdt, CodegenMethodScope codegenMethodScope, ExprForgeCodegenSymbol exprSymbol, CodegenClassScope codegenClassScope) {
        ChronoField chronoField = forge.fieldName.getChronoField();
        CodegenMethodNode methodNode = codegenMethodScope.makeChild(ZonedDateTime.class, CalendarSetForgeOp.class, codegenClassScope).addParam(ZonedDateTime.class, "zdt");
        Class evaluationType = forge.valueExpr.getEvaluationType();

        methodNode.getBlock()
                .declareVar(Integer.class, "value", SimpleNumberCoercerFactory.SimpleNumberCoercerInt.coerceCodegenMayNull(forge.valueExpr.evaluateCodegen(evaluationType, methodNode, exprSymbol, codegenClassScope), evaluationType, methodNode, codegenClassScope))
                .ifRefNull("value").blockReturn(ref("zdt"))
                .methodReturn(exprDotMethod(ref("zdt"), "with", enumValue(ChronoField.class, chronoField.name()), ref("value")));
        return localMethod(methodNode, zdt);
    }
}
