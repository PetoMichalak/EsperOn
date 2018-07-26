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
package eu.uk.ncl.pet5o.esper.epl.datetime.interval;

import eu.uk.ncl.pet5o.esper.client.EventBean;
import eu.uk.ncl.pet5o.esper.codegen.base.CodegenBlock;
import eu.uk.ncl.pet5o.esper.codegen.base.CodegenClassScope;
import eu.uk.ncl.pet5o.esper.codegen.base.CodegenMethodNode;
import eu.uk.ncl.pet5o.esper.codegen.base.CodegenMethodScope;
import eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpression;
import eu.uk.ncl.pet5o.esper.epl.expression.codegen.ExprForgeCodegenSymbol;
import eu.uk.ncl.pet5o.esper.epl.expression.core.ExprEvaluator;
import eu.uk.ncl.pet5o.esper.epl.expression.core.ExprEvaluatorContext;

import static eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionBuilder.localMethod;
import static eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionBuilder.ref;

public class IntervalForgeOp implements IntervalOp {

    private final ExprEvaluator evaluatorTimestamp;
    private final IntervalForgeImpl.IntervalOpEval intervalOpEval;

    public IntervalForgeOp(ExprEvaluator evaluatorTimestamp, IntervalForgeImpl.IntervalOpEval intervalOpEval) {
        this.evaluatorTimestamp = evaluatorTimestamp;
        this.intervalOpEval = intervalOpEval;
    }

    public Object evaluate(long startTs, long endTs, EventBean[] eventsPerStream, boolean isNewData, ExprEvaluatorContext context) {
        Object parameter = evaluatorTimestamp.evaluate(eventsPerStream, isNewData, context);
        if (parameter == null) {
            return parameter;
        }

        return intervalOpEval.evaluate(startTs, endTs, parameter, eventsPerStream, isNewData, context);
    }

    public static CodegenExpression codegen(IntervalForgeImpl forge, CodegenExpression start, CodegenExpression end, CodegenMethodScope codegenMethodScope, ExprForgeCodegenSymbol exprSymbol, CodegenClassScope codegenClassScope) {
        CodegenMethodNode methodNode = codegenMethodScope.makeChild(Boolean.class, IntervalForgeOp.class, codegenClassScope).addParam(long.class, "startTs").addParam(long.class, "endTs");

        Class evaluationType = forge.getForgeTimestamp().getEvaluationType();
        CodegenBlock block = methodNode.getBlock()
                .declareVar(evaluationType, "parameter", forge.getForgeTimestamp().evaluateCodegen(evaluationType, methodNode, exprSymbol, codegenClassScope));
        if (!forge.getForgeTimestamp().getEvaluationType().isPrimitive()) {
            block.ifRefNullReturnNull("parameter");
        }
        block.methodReturn(forge.getIntervalOpForge().codegen(ref("startTs"), ref("endTs"), ref("parameter"), forge.getForgeTimestamp().getEvaluationType(), methodNode, exprSymbol, codegenClassScope));
        return localMethod(methodNode, start, end);
    }
}
