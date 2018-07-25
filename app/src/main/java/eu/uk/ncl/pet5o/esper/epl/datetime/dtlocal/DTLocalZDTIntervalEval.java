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
import eu.uk.ncl.pet5o.esper.codegen.base.CodegenClassScope;
import eu.uk.ncl.pet5o.esper.codegen.base.CodegenMethodScope;
import eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpression;
import eu.uk.ncl.pet5o.esper.epl.datetime.eval.DatetimeLongCoercerZonedDateTime;
import eu.uk.ncl.pet5o.esper.epl.datetime.interval.IntervalOp;
import eu.uk.ncl.pet5o.esper.epl.expression.codegen.ExprForgeCodegenSymbol;
import eu.uk.ncl.pet5o.esper.codegen.base.CodegenMethodNode;
import eu.uk.ncl.pet5o.esper.epl.expression.core.ExprEvaluatorContext;

import java.time.ZonedDateTime;

import static eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionBuilder.*;
import static eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionBuilder.localMethod;
import static eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionBuilder.ref;

class DTLocalZDTIntervalEval extends DTLocalEvaluatorIntervalBase {

    public DTLocalZDTIntervalEval(IntervalOp intervalOp) {
        super(intervalOp);
    }

    public Object evaluate(Object target, EventBean[] eventsPerStream, boolean isNewData, ExprEvaluatorContext exprEvaluatorContext) {
        long time = DatetimeLongCoercerZonedDateTime.coerceZDTToMillis((ZonedDateTime) target);
        return intervalOp.evaluate(time, time, eventsPerStream, isNewData, exprEvaluatorContext);
    }

    public static CodegenExpression codegen(DTLocalZDTIntervalForge forge, CodegenExpression inner, Class innerType, CodegenMethodScope codegenMethodScope, ExprForgeCodegenSymbol exprSymbol, CodegenClassScope codegenClassScope) {
        CodegenMethodNode methodNode = codegenMethodScope.makeChild(Boolean.class, DTLocalZDTIntervalEval.class, codegenClassScope).addParam(ZonedDateTime.class, "target");

        methodNode.getBlock()
                .declareVar(long.class, "time", staticMethod(DatetimeLongCoercerZonedDateTime.class, "coerceZDTToMillis", ref("target")))
                .methodReturn(forge.intervalForge.codegen(ref("time"), ref("time"), methodNode, exprSymbol, codegenClassScope));
        return localMethod(methodNode, inner);
    }

    public Object evaluate(Object startTimestamp, Object endTimestamp, EventBean[] eventsPerStream, boolean isNewData, ExprEvaluatorContext exprEvaluatorContext) {
        long start = DatetimeLongCoercerZonedDateTime.coerceZDTToMillis((ZonedDateTime) startTimestamp);
        long end = DatetimeLongCoercerZonedDateTime.coerceZDTToMillis((ZonedDateTime) endTimestamp);
        return intervalOp.evaluate(start, end, eventsPerStream, isNewData, exprEvaluatorContext);
    }

    public static CodegenExpression codegen(DTLocalZDTIntervalForge forge, CodegenExpression start, CodegenExpression end, CodegenMethodScope codegenMethodScope, ExprForgeCodegenSymbol exprSymbol, CodegenClassScope codegenClassScope) {
        CodegenMethodNode methodNode = codegenMethodScope.makeChild(Boolean.class, DTLocalZDTIntervalEval.class, codegenClassScope).addParam(ZonedDateTime.class, "startTimestamp").addParam(ZonedDateTime.class, "endTimestamp");

        methodNode.getBlock()
                .declareVar(long.class, "start", staticMethod(DatetimeLongCoercerZonedDateTime.class, "coerceZDTToMillis", ref("startTimestamp")))
                .declareVar(long.class, "end", staticMethod(DatetimeLongCoercerZonedDateTime.class, "coerceZDTToMillis", ref("endTimestamp")))
                .methodReturn(forge.intervalForge.codegen(ref("start"), ref("end"), methodNode, exprSymbol, codegenClassScope));
        return localMethod(methodNode, start, end);
    }
}
