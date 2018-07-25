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
package eu.uk.ncl.pet5o.esper.epl.expression.funcs;

import eu.uk.ncl.pet5o.esper.codegen.base.CodegenBlock;
import eu.uk.ncl.pet5o.esper.codegen.base.CodegenClassScope;
import eu.uk.ncl.pet5o.esper.codegen.base.CodegenMethodNode;
import eu.uk.ncl.pet5o.esper.codegen.base.CodegenMethodScope;
import eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpression;
import eu.uk.ncl.pet5o.esper.epl.expression.codegen.ExprForgeCodegenSymbol;
import eu.uk.ncl.pet5o.esper.epl.expression.core.ExprEvaluator;
import eu.uk.ncl.pet5o.esper.epl.expression.core.ExprEvaluatorContext;
import eu.uk.ncl.pet5o.esper.epl.expression.core.ExprTypableReturnEval;

import java.util.Map;

import static eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionBuilder.*;
import static eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionBuilder.constant;
import static eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionBuilder.exprDotMethod;
import static eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionBuilder.localMethod;
import static eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionBuilder.newArrayByLength;
import static eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionBuilder.ref;

public class ExprCaseNodeForgeEvalTypable implements ExprTypableReturnEval {

    private final ExprCaseNodeForge forge;
    private final ExprEvaluator evaluator;

    public ExprCaseNodeForgeEvalTypable(ExprCaseNodeForge forge) {
        this.forge = forge;
        this.evaluator = forge.getExprEvaluator();
    }

    public Object evaluate(eu.uk.ncl.pet5o.esper.client.EventBean[] eventsPerStream, boolean isNewData, ExprEvaluatorContext context) {
        return evaluator.evaluate(eventsPerStream, isNewData, context);
    }

    public Object[] evaluateTypableSingle(eu.uk.ncl.pet5o.esper.client.EventBean[] eventsPerStream, boolean isNewData, ExprEvaluatorContext context) {
        Map<String, Object> map = (Map<String, Object>) evaluator.evaluate(eventsPerStream, isNewData, context);
        Object[] row = new Object[map.size()];
        int index = -1;
        for (Map.Entry<String, Object> entry : forge.mapResultType.entrySet()) {
            index++;
            row[index] = map.get(entry.getKey());
        }
        return row;
    }

    public static CodegenExpression codegenTypeableSingle(ExprCaseNodeForge forge, CodegenMethodScope codegenMethodScope, ExprForgeCodegenSymbol exprSymbol, CodegenClassScope codegenClassScope) {
        CodegenMethodNode methodNode = codegenMethodScope.makeChild(Object[].class, ExprCaseNodeForgeEvalTypable.class, codegenClassScope);

        CodegenBlock block = methodNode.getBlock()
                .declareVar(Map.class, "map", cast(Map.class, forge.evaluateCodegen(Map.class, methodNode, exprSymbol, codegenClassScope)))
                .declareVar(Object[].class, "row", newArrayByLength(Object.class, exprDotMethod(ref("map"), "size")));
        int index = -1;
        for (Map.Entry<String, Object> entry : forge.mapResultType.entrySet()) {
            index++;
            block.assignArrayElement(ref("row"), constant(index), exprDotMethod(ref("map"), "get", constant(entry.getKey())));
        }
        block.methodReturn(ref("row"));
        return localMethod(methodNode);
    }

    public Object[][] evaluateTypableMulti(eu.uk.ncl.pet5o.esper.client.EventBean[] eventsPerStream, boolean isNewData, ExprEvaluatorContext context) {
        return null;    // always single-row
    }
}
