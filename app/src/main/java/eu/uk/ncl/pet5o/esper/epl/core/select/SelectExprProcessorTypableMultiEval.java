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
import static eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionBuilder.arrayAtIndex;
import static eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionBuilder.arrayLength;
import static eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionBuilder.constant;
import static eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionBuilder.equalsIdentity;
import static eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionBuilder.localMethod;
import static eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionBuilder.member;
import static eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionBuilder.newArrayByLength;
import static eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionBuilder.ref;

public class SelectExprProcessorTypableMultiEval implements ExprEvaluator {

    private final SelectExprProcessorTypableMultiForge forge;
    private final ExprTypableReturnEval typable;

    public SelectExprProcessorTypableMultiEval(SelectExprProcessorTypableMultiForge forge, ExprTypableReturnEval typable) {
        this.forge = forge;
        this.typable = typable;
    }

    public Object evaluate(eu.uk.ncl.pet5o.esper.client.EventBean[] eventsPerStream, boolean isNewData, ExprEvaluatorContext exprEvaluatorContext) {
        Object[][] rows = typable.evaluateTypableMulti(eventsPerStream, isNewData, exprEvaluatorContext);
        if (rows == null) {
            return null;
        }
        if (rows.length == 0) {
            return new eu.uk.ncl.pet5o.esper.client.EventBean[0];
        }
        if (forge.hasWideners) {
            SelectExprProcessorHelper.applyWideners(rows, forge.wideners);
        }
        eu.uk.ncl.pet5o.esper.client.EventBean[] events = new eu.uk.ncl.pet5o.esper.client.EventBean[rows.length];
        for (int i = 0; i < events.length; i++) {
            events[i] = forge.factory.make(rows[i]);
        }
        return events;
    }

    public static CodegenExpression codegen(SelectExprProcessorTypableMultiForge forge, CodegenMethodScope codegenMethodScope, ExprForgeCodegenSymbol exprSymbol, CodegenClassScope codegenClassScope) {
        CodegenMember factory = codegenClassScope.makeAddMember(EventBeanManufacturer.class, forge.factory);
        CodegenMethodNode methodNode = codegenMethodScope.makeChild(eu.uk.ncl.pet5o.esper.client.EventBean[].class, SelectExprProcessorTypableMultiEval.class, codegenClassScope);

        CodegenBlock block = methodNode.getBlock()
                .declareVar(Object[][].class, "rows", forge.typable.evaluateTypableMultiCodegen(methodNode, exprSymbol, codegenClassScope))
                .ifRefNullReturnNull("rows")
                .ifCondition(equalsIdentity(arrayLength(ref("rows")), constant(0)))
                .blockReturn(newArrayByLength(eu.uk.ncl.pet5o.esper.client.EventBean.class, constant(0)));
        if (forge.hasWideners) {
            block.expression(SelectExprProcessorHelper.applyWidenersCodegenMultirow(ref("rows"), forge.wideners, methodNode, codegenClassScope));
        }
        block.declareVar(eu.uk.ncl.pet5o.esper.client.EventBean[].class, "events", newArrayByLength(eu.uk.ncl.pet5o.esper.client.EventBean.class, arrayLength(ref("rows"))))
                .forLoopIntSimple("i", arrayLength(ref("events")))
                .assignArrayElement("events", ref("i"), exprDotMethod(member(factory.getMemberId()), "make", arrayAtIndex(ref("rows"), ref("i"))))
                .blockEnd()
                .methodReturn(ref("events"));
        return localMethod(methodNode);
    }
}
