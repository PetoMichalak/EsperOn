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
package eu.uk.ncl.pet5o.esper.epl.datetime.reformatop;

import eu.uk.ncl.pet5o.esper.client.EventBean;
import eu.uk.ncl.pet5o.esper.client.EventType;
import eu.uk.ncl.pet5o.esper.codegen.base.CodegenClassScope;
import eu.uk.ncl.pet5o.esper.codegen.base.CodegenMember;
import eu.uk.ncl.pet5o.esper.codegen.base.CodegenMethodScope;
import eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpression;
import eu.uk.ncl.pet5o.esper.epl.datetime.eval.DatetimeMethodEnum;
import eu.uk.ncl.pet5o.esper.epl.expression.codegen.ExprForgeCodegenSymbol;
import eu.uk.ncl.pet5o.esper.codegen.base.CodegenMethodNode;
import eu.uk.ncl.pet5o.esper.epl.expression.core.ExprEvaluatorContext;
import eu.uk.ncl.pet5o.esper.epl.expression.core.ExprNode;
import eu.uk.ncl.pet5o.esper.epl.expression.dot.ExprDotNodeFilterAnalyzerInput;
import eu.uk.ncl.pet5o.esper.epl.expression.time.TimeAbacus;
import eu.uk.ncl.pet5o.esper.epl.join.plan.FilterExprAnalyzerAffector;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import static eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionBuilder.*;
import static eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionBuilder.exprDotMethod;
import static eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionBuilder.localMethodBuild;
import static eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionBuilder.member;
import static eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionBuilder.ref;

public class ReformatEvalForge implements ReformatForge, ReformatOp {

    private final CalendarEval calendarEval;
    private final LocalDateTimeEval localDateTimeEval;
    private final ZonedDateTimeEval zonedDateTimeEval;
    private final TimeZone timeZone;
    private final TimeAbacus timeAbacus;

    public ReformatEvalForge(CalendarEval calendarEval, LocalDateTimeEval localDateTimeEval, ZonedDateTimeEval zonedDateTimeEval, TimeZone timeZone, TimeAbacus timeAbacus) {
        this.calendarEval = calendarEval;
        this.localDateTimeEval = localDateTimeEval;
        this.zonedDateTimeEval = zonedDateTimeEval;
        this.timeZone = timeZone;
        this.timeAbacus = timeAbacus;
    }

    public ReformatOp getOp() {
        return this;
    }

    public Object evaluate(Long ts, EventBean[] eventsPerStream, boolean newData, ExprEvaluatorContext exprEvaluatorContext) {
        Calendar cal = Calendar.getInstance(timeZone);
        timeAbacus.calendarSet(ts, cal);
        return calendarEval.evaluateInternal(cal);
    }

    public CodegenExpression codegenLong(CodegenExpression inner, CodegenMethodScope codegenMethodScope, ExprForgeCodegenSymbol exprSymbol, CodegenClassScope codegenClassScope) {
        CodegenMember tz = codegenClassScope.makeAddMember(TimeZone.class, timeZone);
        CodegenMethodNode method = codegenMethodScope.makeChild(int.class, ReformatEvalForge.class, codegenClassScope).addParam(long.class, "ts");
        method.getBlock()
                .declareVar(Calendar.class, "cal", staticMethod(Calendar.class, "getInstance", member(tz.getMemberId())))
                .expression(timeAbacus.calendarSetCodegen(ref("ts"), ref("cal"), method, codegenClassScope))
                .methodReturn(calendarEval.codegen(ref("cal")));
        return localMethodBuild(method).pass(inner).call();
    }

    public Object evaluate(Date d, EventBean[] eventsPerStream, boolean newData, ExprEvaluatorContext exprEvaluatorContext) {
        Calendar cal = Calendar.getInstance(timeZone);
        cal.setTimeInMillis(d.getTime());
        return calendarEval.evaluateInternal(cal);
    }

    public CodegenExpression codegenDate(CodegenExpression inner, CodegenMethodScope codegenMethodScope, ExprForgeCodegenSymbol exprSymbol, CodegenClassScope codegenClassScope) {
        CodegenMember tz = codegenClassScope.makeAddMember(TimeZone.class, timeZone);
        CodegenMethodNode methodNode = codegenMethodScope.makeChild(int.class, ReformatEvalForge.class, codegenClassScope).addParam(Date.class, "d");


        methodNode.getBlock()
                .declareVar(Calendar.class, "cal", staticMethod(Calendar.class, "getInstance", member(tz.getMemberId())))
                .expression(exprDotMethod(ref("cal"), "setTimeInMillis", exprDotMethod(ref("d"), "getTime")))
                .methodReturn(calendarEval.codegen(ref("cal")));
        return localMethod(methodNode, inner);
    }

    public Object evaluate(LocalDateTime ldt, EventBean[] eventsPerStream, boolean newData, ExprEvaluatorContext exprEvaluatorContext) {
        return localDateTimeEval.evaluateInternal(ldt);
    }

    public CodegenExpression codegenLDT(CodegenExpression inner, CodegenMethodScope codegenMethodScope, ExprForgeCodegenSymbol exprSymbol, CodegenClassScope codegenClassScope) {
        return localDateTimeEval.codegen(inner);
    }

    public Object evaluate(ZonedDateTime zdt, EventBean[] eventsPerStream, boolean newData, ExprEvaluatorContext exprEvaluatorContext) {
        return zonedDateTimeEval.evaluateInternal(zdt);
    }

    public CodegenExpression codegenZDT(CodegenExpression inner, CodegenMethodScope codegenMethodScope, ExprForgeCodegenSymbol exprSymbol, CodegenClassScope codegenClassScope) {
        return zonedDateTimeEval.codegen(inner);
    }

    public Object evaluate(Calendar cal, EventBean[] eventsPerStream, boolean newData, ExprEvaluatorContext exprEvaluatorContext) {
        return calendarEval.evaluateInternal(cal);
    }

    public CodegenExpression codegenCal(CodegenExpression inner, CodegenMethodScope codegenMethodScope, ExprForgeCodegenSymbol exprSymbol, CodegenClassScope codegenClassScope) {
        return calendarEval.codegen(inner);
    }

    public Class getReturnType() {
        return Integer.class;
    }

    public FilterExprAnalyzerAffector getFilterDesc(EventType[] typesPerStream, DatetimeMethodEnum currentMethod, List<ExprNode> currentParameters, ExprDotNodeFilterAnalyzerInput inputDesc) {
        return null;
    }
}
