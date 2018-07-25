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

import eu.uk.ncl.pet5o.esper.codegen.base.*;
import eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpression;
import eu.uk.ncl.pet5o.esper.epl.expression.codegen.ExprForgeCodegenSymbol;
import eu.uk.ncl.pet5o.esper.epl.expression.core.ExprEvaluator;
import eu.uk.ncl.pet5o.esper.epl.expression.core.ExprEvaluatorContext;
import eu.uk.ncl.pet5o.esper.epl.expression.core.ExprTypableReturnEval;
import eu.uk.ncl.pet5o.esper.event.EventBeanManufacturer;

import static eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionBuilder.*;
import static eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionBuilder.constant;
import static eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionBuilder.localMethod;
import static eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionBuilder.member;
import static eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionBuilder.newArrayByLength;
import static eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionBuilder.ref;

public class SelectExprProcessorTypableSingleEval implements ExprEvaluator {
    private final SelectExprProcessorTypableSingleForge forge;
    private final ExprTypableReturnEval typable;

    public SelectExprProcessorTypableSingleEval(SelectExprProcessorTypableSingleForge forge, ExprTypableReturnEval typable) {
        this.forge = forge;
        this.typable = typable;
    }

    public Object evaluate(eu.uk.ncl.pet5o.esper.client.EventBean[] eventsPerStream, boolean isNewData, ExprEvaluatorContext exprEvaluatorContext) {
        Object[] row = typable.evaluateTypableSingle(eventsPerStream, isNewData, exprEvaluatorContext);
        if (row == null) {
            return null;
        }
        if (forge.hasWideners) {
            SelectExprProcessorHelper.applyWideners(row, forge.wideners);
        }
        return new eu.uk.ncl.pet5o.esper.client.EventBean[]{forge.factory.make(row)};
    }

    public static CodegenExpression codegen(SelectExprProcessorTypableSingleForge forge, CodegenMethodScope codegenMethodScope, ExprForgeCodegenSymbol exprSymbol, CodegenClassScope codegenClassScope) {
        CodegenMember factory = codegenClassScope.makeAddMember(EventBeanManufacturer.class, forge.factory);
        CodegenMethodNode methodNode = codegenMethodScope.makeChild(eu.uk.ncl.pet5o.esper.client.EventBean[].class, SelectExprProcessorTypableSingleEval.class, codegenClassScope);

        CodegenBlock block = methodNode.getBlock()
                .declareVar(Object[].class, "row", forge.typable.evaluateTypableSingleCodegen(methodNode, exprSymbol, codegenClassScope))
                .ifRefNullReturnNull("row");
        if (forge.hasWideners) {
            block.expression(SelectExprProcessorHelper.applyWidenersCodegen(ref("row"), forge.wideners, methodNode, codegenClassScope));
        }
        block.declareVar(eu.uk.ncl.pet5o.esper.client.EventBean[].class, "events", newArrayByLength(eu.uk.ncl.pet5o.esper.client.EventBean.class, constant(1)))
                .assignArrayElement("events", constant(0), exprDotMethod(member(factory.getMemberId()), "make", ref("row")))
                .methodReturn(ref("events"));
        return localMethod(methodNode);
    }

}
