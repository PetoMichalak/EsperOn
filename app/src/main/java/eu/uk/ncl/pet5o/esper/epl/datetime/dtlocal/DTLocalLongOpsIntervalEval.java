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

import eu.uk.ncl.pet5o.esper.client.EventBean;
import eu.uk.ncl.pet5o.esper.codegen.base.CodegenBlock;
import eu.uk.ncl.pet5o.esper.codegen.base.CodegenClassScope;
import eu.uk.ncl.pet5o.esper.codegen.base.CodegenMember;
import eu.uk.ncl.pet5o.esper.codegen.base.CodegenMethodScope;
import eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpression;
import eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionRef;
import eu.uk.ncl.pet5o.esper.epl.datetime.calop.CalendarOp;
import eu.uk.ncl.pet5o.esper.epl.datetime.interval.IntervalOp;
import eu.uk.ncl.pet5o.esper.epl.expression.codegen.ExprForgeCodegenSymbol;
import eu.uk.ncl.pet5o.esper.codegen.base.CodegenMethodNode;
import eu.uk.ncl.pet5o.esper.epl.expression.core.ExprEvaluatorContext;
import eu.uk.ncl.pet5o.esper.epl.expression.time.TimeAbacus;

import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;

import static eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionBuilder.*;
import static eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionBuilder.localMethod;
import static eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionBuilder.member;
import static eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionBuilder.op;
import static eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionBuilder.ref;
import static eu.uk.ncl.pet5o.esper.epl.datetime.dtlocal.DTLocalUtil.evaluateCalOpsCalendarCodegen;

public class DTLocalLongOpsIntervalEval extends DTLocalEvaluatorCalOpsIntervalBase {

    private final TimeZone timeZone;
    private final TimeAbacus timeAbacus;

    public DTLocalLongOpsIntervalEval(List<CalendarOp> calendarOps, IntervalOp intervalOp, TimeZone timeZone, TimeAbacus timeAbacus) {
        super(calendarOps, intervalOp);
        this.timeZone = timeZone;
        this.timeAbacus = timeAbacus;
    }

    public Object evaluate(Object target, EventBean[] eventsPerStream, boolean isNewData, ExprEvaluatorContext exprEvaluatorContext) {
        Calendar cal = Calendar.getInstance(timeZone);
        long startRemainder = timeAbacus.calendarSet((Long) target, cal);
        DTLocalUtil.evaluateCalOpsCalendar(calendarOps, cal, eventsPerStream, isNewData, exprEvaluatorContext);
        long time = timeAbacus.calendarGet(cal, startRemainder);
        return intervalOp.evaluate(time, time, eventsPerStream, isNewData, exprEvaluatorContext);
    }

    public static CodegenExpression codegenPointInTime(DTLocalLongOpsIntervalForge forge, CodegenExpression inner, Class innerType, CodegenMethodScope codegenMethodScope, ExprForgeCodegenSymbol exprSymbol, CodegenClassScope codegenClassScope) {
        CodegenMember tz = codegenClassScope.makeAddMember(TimeZone.class, forge.timeZone);
        CodegenMethodNode methodNode = codegenMethodScope.makeChild(Boolean.class, DTLocalLongOpsIntervalEval.class, codegenClassScope).addParam(long.class, "target");

        CodegenBlock block = methodNode.getBlock()
                .declareVar(Calendar.class, "cal", staticMethod(Calendar.class, "getInstance", member(tz.getMemberId())))
                .declareVar(long.class, "startRemainder", forge.timeAbacus.calendarSetCodegen(ref("target"), ref("cal"), methodNode, codegenClassScope));
        evaluateCalOpsCalendarCodegen(block, forge.calendarForges, ref("cal"), methodNode, exprSymbol, codegenClassScope);
        block.declareVar(long.class, "time", forge.timeAbacus.calendarGetCodegen(ref("cal"), ref("startRemainder"), codegenClassScope))
                .methodReturn(forge.intervalForge.codegen(ref("time"), ref("time"), methodNode, exprSymbol, codegenClassScope));
        return localMethod(methodNode, inner);
    }

    public Object evaluate(Object startTimestamp, Object endTimestamp, EventBean[] eventsPerStream, boolean isNewData, ExprEvaluatorContext exprEvaluatorContext) {
        long startLong = (Long) startTimestamp;
        long endLong = (Long) endTimestamp;
        Calendar cal = Calendar.getInstance(timeZone);
        long startRemainder = timeAbacus.calendarSet(startLong, cal);
        DTLocalUtil.evaluateCalOpsCalendar(calendarOps, cal, eventsPerStream, isNewData, exprEvaluatorContext);
        long startTime = timeAbacus.calendarGet(cal, startRemainder);
        long endTime = startTime + (endLong - startLong);
        return intervalOp.evaluate(startTime, endTime, eventsPerStream, isNewData, exprEvaluatorContext);
    }

    public static CodegenExpression codegenStartEnd(DTLocalLongOpsIntervalForge forge, CodegenExpressionRef start, CodegenExpressionRef end, CodegenMethodScope codegenMethodScope, ExprForgeCodegenSymbol exprSymbol, CodegenClassScope codegenClassScope) {
        CodegenMember tz = codegenClassScope.makeAddMember(TimeZone.class, forge.timeZone);
        CodegenMethodNode methodNode = codegenMethodScope.makeChild(Boolean.class, DTLocalLongOpsIntervalEval.class, codegenClassScope).addParam(long.class, "startLong").addParam(long.class, "endLong");

        CodegenBlock block = methodNode.getBlock()
                .declareVar(Calendar.class, "cal", staticMethod(Calendar.class, "getInstance", member(tz.getMemberId())))
                .declareVar(long.class, "startRemainder", forge.timeAbacus.calendarSetCodegen(ref("startLong"), ref("cal"), methodNode, codegenClassScope));
        evaluateCalOpsCalendarCodegen(block, forge.calendarForges, ref("cal"), methodNode, exprSymbol, codegenClassScope);
        block.declareVar(long.class, "startTime", forge.timeAbacus.calendarGetCodegen(ref("cal"), ref("startRemainder"), codegenClassScope))
                .declareVar(long.class, "endTime", op(ref("startTime"), "+", op(ref("endLong"), "-", ref("startLong"))))
                .methodReturn(forge.intervalForge.codegen(ref("startTime"), ref("endTime"), methodNode, exprSymbol, codegenClassScope));
        return localMethod(methodNode, start, end);
    }
}
