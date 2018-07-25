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
import static com.espertech.esper.codegen.model.expression.CodegenExpressionBuilder.constant;
import static com.espertech.esper.codegen.model.expression.CodegenExpressionBuilder.localMethod;
import static com.espertech.esper.codegen.model.expression.CodegenExpressionBuilder.member;
import static com.espertech.esper.codegen.model.expression.CodegenExpressionBuilder.newArrayByLength;
import static com.espertech.esper.codegen.model.expression.CodegenExpressionBuilder.ref;

public class SelectExprProcessorTypableSingleEval implements ExprEvaluator {
    private final SelectExprProcessorTypableSingleForge forge;
    private final ExprTypableReturnEval typable;

    public SelectExprProcessorTypableSingleEval(SelectExprProcessorTypableSingleForge forge, ExprTypableReturnEval typable) {
        this.forge = forge;
        this.typable = typable;
    }

    public Object evaluate(com.espertech.esper.client.EventBean[] eventsPerStream, boolean isNewData, ExprEvaluatorContext exprEvaluatorContext) {
        Object[] row = typable.evaluateTypableSingle(eventsPerStream, isNewData, exprEvaluatorContext);
        if (row == null) {
            return null;
        }
        if (forge.hasWideners) {
            SelectExprProcessorHelper.applyWideners(row, forge.wideners);
        }
        return new com.espertech.esper.client.EventBean[]{forge.factory.make(row)};
    }

    public static CodegenExpression codegen(SelectExprProcessorTypableSingleForge forge, CodegenMethodScope codegenMethodScope, ExprForgeCodegenSymbol exprSymbol, CodegenClassScope codegenClassScope) {
        CodegenMember factory = codegenClassScope.makeAddMember(EventBeanManufacturer.class, forge.factory);
        CodegenMethodNode methodNode = codegenMethodScope.makeChild(com.espertech.esper.client.EventBean[].class, SelectExprProcessorTypableSingleEval.class, codegenClassScope);

        CodegenBlock block = methodNode.getBlock()
                .declareVar(Object[].class, "row", forge.typable.evaluateTypableSingleCodegen(methodNode, exprSymbol, codegenClassScope))
                .ifRefNullReturnNull("row");
        if (forge.hasWideners) {
            block.expression(SelectExprProcessorHelper.applyWidenersCodegen(ref("row"), forge.wideners, methodNode, codegenClassScope));
        }
        block.declareVar(com.espertech.esper.client.EventBean[].class, "events", newArrayByLength(com.espertech.esper.client.EventBean.class, constant(1)))
                .assignArrayElement("events", constant(0), exprDotMethod(member(factory.getMemberId()), "make", ref("row")))
                .methodReturn(ref("events"));
        return localMethod(methodNode);
    }

}
