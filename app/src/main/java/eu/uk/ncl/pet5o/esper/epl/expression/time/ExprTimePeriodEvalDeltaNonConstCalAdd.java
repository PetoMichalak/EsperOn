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
package eu.uk.ncl.pet5o.esper.epl.expression.time;

import eu.uk.ncl.pet5o.esper.codegen.base.CodegenBlock;
import eu.uk.ncl.pet5o.esper.codegen.base.CodegenClassScope;
import eu.uk.ncl.pet5o.esper.codegen.base.CodegenMember;
import eu.uk.ncl.pet5o.esper.codegen.base.CodegenMethodNode;
import eu.uk.ncl.pet5o.esper.codegen.base.CodegenMethodScope;
import eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpression;
import eu.uk.ncl.pet5o.esper.epl.expression.codegen.ExprForgeCodegenSymbol;
import eu.uk.ncl.pet5o.esper.epl.expression.core.ExprEvaluator;
import eu.uk.ncl.pet5o.esper.epl.expression.core.ExprEvaluatorContext;
import eu.uk.ncl.pet5o.esper.schedule.TimeProvider;

import java.util.Calendar;
import java.util.TimeZone;

import static eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionBuilder.*;
import static eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionBuilder.constant;
import static eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionBuilder.localMethod;
import static eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionBuilder.member;
import static eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionBuilder.op;
import static eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionBuilder.ref;

public class ExprTimePeriodEvalDeltaNonConstCalAdd implements ExprTimePeriodEvalDeltaNonConst {
    private final Calendar cal;
    private final ExprTimePeriodForge forge;
    private final int indexMicroseconds;

    public ExprTimePeriodEvalDeltaNonConstCalAdd(TimeZone timeZone, ExprTimePeriodForge forge) {
        this.forge = forge;
        this.cal = Calendar.getInstance(timeZone);
        this.indexMicroseconds = ExprTimePeriodUtil.findIndexMicroseconds(forge.getAdders());
    }

    public synchronized long deltaAdd(long currentTime, eu.uk.ncl.pet5o.esper.client.EventBean[] eventsPerStream, boolean isNewData, ExprEvaluatorContext context) {
        return addSubtract(currentTime, 1, eventsPerStream, isNewData, context);
    }

    public CodegenExpression deltaAddCodegen(CodegenExpression reference, CodegenMethodScope codegenMethodScope, ExprForgeCodegenSymbol exprSymbol, CodegenClassScope codegenClassScope) {
        return addSubtractCodegen(reference, constant(1), codegenMethodScope, exprSymbol, codegenClassScope);
    }

    public synchronized long deltaSubtract(long currentTime, eu.uk.ncl.pet5o.esper.client.EventBean[] eventsPerStream, boolean isNewData, ExprEvaluatorContext context) {
        return addSubtract(currentTime, -1, eventsPerStream, isNewData, context);
    }

    public synchronized long deltaUseEngineTime(eu.uk.ncl.pet5o.esper.client.EventBean[] eventsPerStream, ExprEvaluatorContext exprEvaluatorContext, TimeProvider timeProvider) {
        long currentTime = timeProvider.getTime();
        return addSubtract(currentTime, 1, eventsPerStream, true, exprEvaluatorContext);
    }

    public synchronized ExprTimePeriodEvalDeltaResult deltaAddWReference(long current, long reference, eu.uk.ncl.pet5o.esper.client.EventBean[] eventsPerStream, boolean isNewData, ExprEvaluatorContext context) {
        // find the next-nearest reference higher then the current time, compute delta, return reference one lower
        if (reference > current) {
            while (reference > current) {
                reference = reference - deltaSubtract(reference, eventsPerStream, isNewData, context);
            }
        }

        long next = reference;
        long last;
        do {
            last = next;
            next = next + deltaAdd(last, eventsPerStream, isNewData, context);
        }
        while (next <= current);
        return new ExprTimePeriodEvalDeltaResult(next - current, last);
    }

    private long addSubtract(long currentTime, int factor, eu.uk.ncl.pet5o.esper.client.EventBean[] eventsPerStream, boolean newData, ExprEvaluatorContext context) {
        long remainder = forge.getTimeAbacus().calendarSet(currentTime, cal);

        ExprTimePeriodAdder.TimePeriodAdder[] adders = forge.getAdders();
        ExprEvaluator[] evaluators = forge.getEvaluators();
        int usec = 0;
        for (int i = 0; i < adders.length; i++) {
            int value = ((Number) evaluators[i].evaluate(eventsPerStream, newData, context)).intValue();
            if (i == indexMicroseconds) {
                usec = value;
            } else {
                adders[i].add(cal, factor * value);
            }
        }

        long result = forge.getTimeAbacus().calendarGet(cal, remainder);
        if (indexMicroseconds != -1) {
            result += factor * usec;
        }
        return result - currentTime;
    }

    private CodegenExpression addSubtractCodegen(CodegenExpression reference, CodegenExpression constant, CodegenMethodScope codegenMethodScope, ExprForgeCodegenSymbol exprSymbol, CodegenClassScope codegenClassScope) {
        CodegenMember calMember = codegenClassScope.makeAddMember(Calendar.class, cal);

        CodegenMethodNode methodNode = codegenMethodScope.makeChild(long.class, ExprTimePeriodEvalDeltaNonConstCalAdd.class, codegenClassScope);
        methodNode.addParam(long.class, "currentTime");
        methodNode.addParam(int.class, "factor");

        CodegenBlock block = methodNode.getBlock()
                .declareVarNoInit(long.class, "result")
                .synchronizedOn(member(calMember.getMemberId()))
                .declareVar(long.class, "remainder", forge.getTimeAbacus().calendarSetCodegen(ref("currentTime"), member(calMember.getMemberId()), methodNode, codegenClassScope))
                .declareVar(int.class, "usec", constant(0));
        for (int i = 0; i < forge.getAdders().length; i++) {
            String refname = "r" + i;
            block.declareVar(int.class, refname, forge.getForgeRenderable().getChildNodes()[i].getForge().evaluateCodegen(int.class, methodNode, exprSymbol, codegenClassScope));
            if (i == indexMicroseconds) {
                block.assignRef("usec", ref(refname));
            } else {
                block.expression(forge.getAdders()[i].addCodegen(member(calMember.getMemberId()), op(ref("factor"), "*", ref(refname))));
            }
        }
        block.assignRef("result", forge.getTimeAbacus().calendarGetCodegen(member(calMember.getMemberId()), ref("remainder"), codegenClassScope));
        if (indexMicroseconds != -1) {
            block.assignRef("result", op(ref("result"), "+", op(ref("factor"), "*", ref("usec"))));
        }
        block.blockEnd().methodReturn(op(ref("result"), "-", ref("currentTime")));
        return localMethod(methodNode, reference, constant);
    }
}
