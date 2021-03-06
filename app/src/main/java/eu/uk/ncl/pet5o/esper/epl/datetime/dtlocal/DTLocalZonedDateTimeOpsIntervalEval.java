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
import eu.uk.ncl.pet5o.esper.codegen.base.CodegenMethodScope;
import eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpression;
import eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionRef;
import eu.uk.ncl.pet5o.esper.epl.datetime.calop.CalendarOp;
import eu.uk.ncl.pet5o.esper.epl.datetime.eval.DatetimeLongCoercerZonedDateTime;
import eu.uk.ncl.pet5o.esper.epl.datetime.interval.IntervalOp;
import eu.uk.ncl.pet5o.esper.epl.expression.codegen.ExprForgeCodegenSymbol;
import eu.uk.ncl.pet5o.esper.codegen.base.CodegenMethodNode;
import eu.uk.ncl.pet5o.esper.epl.expression.core.ExprEvaluatorContext;

import java.time.ZonedDateTime;
import java.util.List;

import static eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionBuilder.*;
import static eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionBuilder.localMethod;
import static eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionBuilder.op;
import static eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionBuilder.ref;
import static eu.uk.ncl.pet5o.esper.epl.datetime.dtlocal.DTLocalUtil.evaluateCalOpsZDT;
import static eu.uk.ncl.pet5o.esper.epl.datetime.dtlocal.DTLocalUtil.evaluateCalOpsZDTCodegen;

public class DTLocalZonedDateTimeOpsIntervalEval extends DTLocalEvaluatorCalOpsIntervalBase {

    public DTLocalZonedDateTimeOpsIntervalEval(List<CalendarOp> calendarOps, IntervalOp intervalOp) {
        super(calendarOps, intervalOp);
    }

    public Object evaluate(Object target, EventBean[] eventsPerStream, boolean isNewData, ExprEvaluatorContext exprEvaluatorContext) {
        ZonedDateTime zdt = (ZonedDateTime) target;
        zdt = evaluateCalOpsZDT(calendarOps, zdt, eventsPerStream, isNewData, exprEvaluatorContext);
        long time = DatetimeLongCoercerZonedDateTime.coerceZDTToMillis(zdt);
        return intervalOp.evaluate(time, time, eventsPerStream, isNewData, exprEvaluatorContext);
    }

    public static CodegenExpression codegen(DTLocalZonedDateTimeOpsIntervalForge forge, CodegenExpression inner, CodegenMethodScope codegenMethodScope, ExprForgeCodegenSymbol exprSymbol, CodegenClassScope codegenClassScope) {
        CodegenMethodNode methodNode = codegenMethodScope.makeChild(Boolean.class, DTLocalZonedDateTimeOpsIntervalEval.class, codegenClassScope).addParam(ZonedDateTime.class, "target");


        CodegenBlock block = methodNode.getBlock();
        evaluateCalOpsZDTCodegen(block, "target", forge.calendarForges, methodNode, exprSymbol, codegenClassScope);
        block.declareVar(long.class, "time", staticMethod(DatetimeLongCoercerZonedDateTime.class, "coerceZDTToMillis", ref("target")));
        block.methodReturn(forge.intervalForge.codegen(ref("time"), ref("time"), methodNode, exprSymbol, codegenClassScope));
        return localMethod(methodNode, inner);
    }

    public Object evaluate(Object startTimestamp, Object endTimestamp, EventBean[] eventsPerStream, boolean isNewData, ExprEvaluatorContext exprEvaluatorContext) {
        ZonedDateTime start = (ZonedDateTime) startTimestamp;
        ZonedDateTime end = (ZonedDateTime) endTimestamp;
        long deltaMSec = DatetimeLongCoercerZonedDateTime.coerceZDTToMillis(end) - DatetimeLongCoercerZonedDateTime.coerceZDTToMillis(start);
        start = evaluateCalOpsZDT(calendarOps, start, eventsPerStream, isNewData, exprEvaluatorContext);
        long startLong = DatetimeLongCoercerZonedDateTime.coerceZDTToMillis(start);
        long endTime = startLong + deltaMSec;
        return intervalOp.evaluate(startLong, endTime, eventsPerStream, isNewData, exprEvaluatorContext);
    }

    public static CodegenExpression codegen(DTLocalZonedDateTimeOpsIntervalForge forge, CodegenExpressionRef start, CodegenExpressionRef end, CodegenMethodScope codegenMethodScope, ExprForgeCodegenSymbol exprSymbol, CodegenClassScope codegenClassScope) {
        CodegenMethodNode methodNode = codegenMethodScope.makeChild(Boolean.class, DTLocalZonedDateTimeOpsIntervalEval.class, codegenClassScope).addParam(ZonedDateTime.class, "start").addParam(ZonedDateTime.class, "end");


        CodegenBlock block = methodNode.getBlock()
                .declareVar(long.class, "startMs", staticMethod(DatetimeLongCoercerZonedDateTime.class, "coerceZDTToMillis", ref("start")))
                .declareVar(long.class, "endMs", staticMethod(DatetimeLongCoercerZonedDateTime.class, "coerceZDTToMillis", ref("end")))
                .declareVar(long.class, "deltaMSec", op(ref("endMs"), "-", ref("startMs")))
                .declareVar(ZonedDateTime.class, "result", start);
        evaluateCalOpsZDTCodegen(block, "result", forge.calendarForges, methodNode, exprSymbol, codegenClassScope);
        block.declareVar(long.class, "startLong", staticMethod(DatetimeLongCoercerZonedDateTime.class, "coerceZDTToMillis", ref("result")));
        block.declareVar(long.class, "endTime", op(ref("startLong"), "+", ref("deltaMSec")));
        block.methodReturn(forge.intervalForge.codegen(ref("startLong"), ref("endTime"), methodNode, exprSymbol, codegenClassScope));
        return localMethod(methodNode, start, end);
    }
}
