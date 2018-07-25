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
import eu.uk.ncl.pet5o.esper.epl.datetime.calop.CalendarOp;
import eu.uk.ncl.pet5o.esper.epl.datetime.reformatop.ReformatOp;
import eu.uk.ncl.pet5o.esper.epl.expression.codegen.ExprForgeCodegenSymbol;
import eu.uk.ncl.pet5o.esper.codegen.base.CodegenMethodNode;
import eu.uk.ncl.pet5o.esper.epl.expression.core.ExprEvaluatorContext;
import eu.uk.ncl.pet5o.esper.epl.expression.time.TimeAbacus;

import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;

import static eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionBuilder.*;
import static eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionBuilder.member;
import static eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionBuilder.ref;

public class DTLocalLongOpsReformatEval extends DTLocalEvaluatorCalopReformatBase {

    private final TimeZone timeZone;
    private final TimeAbacus timeAbacus;

    public DTLocalLongOpsReformatEval(List<CalendarOp> calendarOps, ReformatOp reformatOp, TimeZone timeZone, TimeAbacus timeAbacus) {
        super(calendarOps, reformatOp);
        this.timeZone = timeZone;
        this.timeAbacus = timeAbacus;
    }

    public Object evaluate(Object target, EventBean[] eventsPerStream, boolean isNewData, ExprEvaluatorContext exprEvaluatorContext) {
        Calendar cal = Calendar.getInstance(timeZone);
        timeAbacus.calendarSet((Long) target, cal);
        DTLocalUtil.evaluateCalOpsCalendar(calendarOps, cal, eventsPerStream, isNewData, exprEvaluatorContext);
        return reformatOp.evaluate(cal, eventsPerStream, isNewData, exprEvaluatorContext);
    }

    public static CodegenExpression codegen(DTLocalLongOpsReformatForge forge, CodegenExpression inner, CodegenMethodScope codegenMethodScope, ExprForgeCodegenSymbol exprSymbol, CodegenClassScope codegenClassScope) {
        CodegenMember tz = codegenClassScope.makeAddMember(TimeZone.class, forge.timeZone);
        CodegenMethodNode methodNode = codegenMethodScope.makeChild(forge.reformatForge.getReturnType(), DTLocalLongOpsReformatEval.class, codegenClassScope).addParam(long.class, "target");

        CodegenBlock block = methodNode.getBlock()
                .declareVar(Calendar.class, "cal", staticMethod(Calendar.class, "getInstance", member(tz.getMemberId())))
                .expression(forge.timeAbacus.calendarSetCodegen(ref("target"), ref("cal"), methodNode, codegenClassScope));
        DTLocalUtil.evaluateCalOpsCalendarCodegen(block, forge.calendarForges, ref("cal"), methodNode, exprSymbol, codegenClassScope);
        block.methodReturn(forge.reformatForge.codegenCal(ref("cal"), methodNode, exprSymbol, codegenClassScope));
        return localMethod(methodNode, inner);
    }
}
