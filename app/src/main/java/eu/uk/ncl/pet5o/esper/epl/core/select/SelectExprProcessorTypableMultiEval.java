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
package eu.uk.ncl.pet5o.esper.epl.core.select;

import com.espertech.esper.codegen.base.*;
import com.espertech.esper.codegen.model.expression.CodegenExpression;
import com.espertech.esper.epl.expression.codegen.ExprForgeCodegenSymbol;
import com.espertech.esper.epl.expression.core.ExprEvaluator;
import com.espertech.esper.epl.expression.core.ExprEvaluatorContext;
import com.espertech.esper.epl.expression.core.ExprTypableReturnEval;
import com.espertech.esper.event.EventBeanManufacturer;

import static com.espertech.esper.codegen.model.expression.CodegenExpressionBuilder.*;
import static com.espertech.esper.codegen.model.expression.CodegenExpressionBuilder.arrayAtIndex;
import static com.espertech.esper.codegen.model.expression.CodegenExpressionBuilder.arrayLength;
import static com.espertech.esper.codegen.model.expression.CodegenExpressionBuilder.constant;
import static com.espertech.esper.codegen.model.expression.CodegenExpressionBuilder.equalsIdentity;
import static com.espertech.esper.codegen.model.expression.CodegenExpressionBuilder.localMethod;
import static com.espertech.esper.codegen.model.expression.CodegenExpressionBuilder.member;
import static com.espertech.esper.codegen.model.expression.CodegenExpressionBuilder.newArrayByLength;
import static com.espertech.esper.codegen.model.expression.CodegenExpressionBuilder.ref;

public class SelectExprProcessorTypableMultiEval implements ExprEvaluator {

    private final SelectExprProcessorTypableMultiForge forge;
    private final ExprTypableReturnEval typable;

    public SelectExprProcessorTypableMultiEval(SelectExprProcessorTypableMultiForge forge, ExprTypableReturnEval typable) {
        this.forge = forge;
        this.typable = typable;
    }

    public Object evaluate(com.espertech.esper.client.EventBean[] eventsPerStream, boolean isNewData, ExprEvaluatorContext exprEvaluatorContext) {
        Object[][] rows = typable.evaluateTypableMulti(eventsPerStream, isNewData, exprEvaluatorContext);
        if (rows == null) {
            return null;
        }
        if (rows.length == 0) {
            return new com.espertech.esper.client.EventBean[0];
        }
        if (forge.hasWideners) {
            SelectExprProcessorHelper.applyWideners(rows, forge.wideners);
        }
        com.espertech.esper.client.EventBean[] events = new com.espertech.esper.client.EventBean[rows.length];
        for (int i = 0; i < events.length; i++) {
            events[i] = forge.factory.make(rows[i]);
        }
        return events;
    }

    public static CodegenExpression codegen(SelectExprProcessorTypableMultiForge forge, CodegenMethodScope codegenMethodScope, ExprForgeCodegenSymbol exprSymbol, CodegenClassScope codegenClassScope) {
        CodegenMember factory = codegenClassScope.makeAddMember(EventBeanManufacturer.class, forge.factory);
        CodegenMethodNode methodNode = codegenMethodScope.makeChild(com.espertech.esper.client.EventBean[].class, SelectExprProcessorTypableMultiEval.class, codegenClassScope);

        CodegenBlock block = methodNode.getBlock()
                .declareVar(Object[][].class, "rows", forge.typable.evaluateTypableMultiCodegen(methodNode, exprSymbol, codegenClassScope))
                .ifRefNullReturnNull("rows")
                .ifCondition(equalsIdentity(arrayLength(ref("rows")), constant(0)))
                .blockReturn(newArrayByLength(com.espertech.esper.client.EventBean.class, constant(0)));
        if (forge.hasWideners) {
            block.expression(SelectExprProcessorHelper.applyWidenersCodegenMultirow(ref("rows"), forge.wideners, methodNode, codegenClassScope));
        }
        block.declareVar(com.espertech.esper.client.EventBean[].class, "events", newArrayByLength(com.espertech.esper.client.EventBean.class, arrayLength(ref("rows"))))
                .forLoopIntSimple("i", arrayLength(ref("events")))
                .assignArrayElement("events", ref("i"), exprDotMethod(member(factory.getMemberId()), "make", arrayAtIndex(ref("rows"), ref("i"))))
                .blockEnd()
                .methodReturn(ref("events"));
        return localMethod(methodNode);
    }
}
