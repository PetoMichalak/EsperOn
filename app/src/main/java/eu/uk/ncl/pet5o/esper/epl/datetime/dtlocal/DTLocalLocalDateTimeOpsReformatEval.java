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
import eu.uk.ncl.pet5o.esper.epl.datetime.calop.CalendarOp;
import eu.uk.ncl.pet5o.esper.epl.datetime.reformatop.ReformatOp;
import eu.uk.ncl.pet5o.esper.epl.expression.codegen.ExprForgeCodegenSymbol;
import eu.uk.ncl.pet5o.esper.codegen.base.CodegenMethodNode;
import eu.uk.ncl.pet5o.esper.epl.expression.core.ExprEvaluatorContext;

import java.time.LocalDateTime;
import java.util.List;

import static eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionBuilder.localMethod;
import static eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionBuilder.ref;

public class DTLocalLocalDateTimeOpsReformatEval extends DTLocalEvaluatorCalopReformatBase {

    public DTLocalLocalDateTimeOpsReformatEval(List<CalendarOp> calendarOps, ReformatOp reformatOp) {
        super(calendarOps, reformatOp);
    }

    public Object evaluate(Object target, EventBean[] eventsPerStream, boolean isNewData, ExprEvaluatorContext exprEvaluatorContext) {
        LocalDateTime ldt = (LocalDateTime) target;
        ldt = DTLocalUtil.evaluateCalOpsLDT(calendarOps, ldt, eventsPerStream, isNewData, exprEvaluatorContext);
        return reformatOp.evaluate(ldt, eventsPerStream, isNewData, exprEvaluatorContext);
    }

    public static CodegenExpression codegen(DTLocalLocalDateTimeOpsReformatForge forge, CodegenExpression inner, CodegenMethodScope codegenMethodScope, ExprForgeCodegenSymbol exprSymbol, CodegenClassScope codegenClassScope) {
        CodegenMethodNode methodNode = codegenMethodScope.makeChild(forge.reformatForge.getReturnType(), DTLocalLocalDateTimeOpsReformatEval.class, codegenClassScope).addParam(LocalDateTime.class, "ldt");

        CodegenBlock block = methodNode.getBlock();
        DTLocalUtil.evaluateCalOpsLDTCodegen(block, "ldt", forge.calendarForges, methodNode, exprSymbol, codegenClassScope);
        block.methodReturn(forge.reformatForge.codegenLDT(ref("ldt"), methodNode, exprSymbol, codegenClassScope));
        return localMethod(methodNode, inner);
    }
}
