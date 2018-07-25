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
package eu.uk.ncl.pet5o.esper.epl.expression.dot;

import com.espertech.esper.codegen.base.CodegenClassScope;
import com.espertech.esper.codegen.base.CodegenMethodNode;
import com.espertech.esper.codegen.base.CodegenMethodScope;
import com.espertech.esper.codegen.model.expression.CodegenExpression;
import com.espertech.esper.codegen.model.expression.CodegenExpressionRef;
import com.espertech.esper.epl.expression.codegen.CodegenLegoCast;
import com.espertech.esper.epl.expression.codegen.ExprForgeCodegenSymbol;
import com.espertech.esper.epl.expression.core.ExprEvaluator;
import com.espertech.esper.epl.expression.core.ExprEvaluatorContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.espertech.esper.codegen.model.expression.CodegenExpressionBuilder.*;
import static com.espertech.esper.codegen.model.expression.CodegenExpressionBuilder.arrayAtIndex;
import static com.espertech.esper.codegen.model.expression.CodegenExpressionBuilder.constant;
import static com.espertech.esper.codegen.model.expression.CodegenExpressionBuilder.localMethod;
import static com.espertech.esper.codegen.model.expression.CodegenExpressionBuilder.ref;

public class ExprDotNodeForgePropertyExprEvalMapped implements ExprEvaluator {
    private static final Logger log = LoggerFactory.getLogger(ExprDotNodeForgePropertyExprEvalMapped.class);

    private final ExprDotNodeForgePropertyExpr forge;
    private final ExprEvaluator exprEvaluator;

    public ExprDotNodeForgePropertyExprEvalMapped(ExprDotNodeForgePropertyExpr forge, ExprEvaluator exprEvaluator) {
        this.forge = forge;
        this.exprEvaluator = exprEvaluator;
    }

    public Object evaluate(com.espertech.esper.client.EventBean[] eventsPerStream, boolean isNewData, ExprEvaluatorContext context) {
        com.espertech.esper.client.EventBean event = eventsPerStream[forge.getStreamNum()];
        if (event == null) {
            return null;
        }
        Object result = exprEvaluator.evaluate(eventsPerStream, isNewData, context);
        if (result != null && (!(result instanceof String))) {
            log.warn(forge.getWarningText("string", result));
            return null;
        }
        return forge.getMappedGetter().get(event, (String) result);
    }

    public static CodegenExpression codegen(ExprDotNodeForgePropertyExpr forge, CodegenMethodScope codegenMethodScope, ExprForgeCodegenSymbol exprSymbol, CodegenClassScope codegenClassScope) {
        CodegenMethodNode methodNode = codegenMethodScope.makeChild(forge.getEvaluationType(), ExprDotNodeForgePropertyExprEvalMapped.class, codegenClassScope);

        CodegenExpressionRef refEPS = exprSymbol.getAddEPS(methodNode);
        methodNode.getBlock()
                .declareVar(com.espertech.esper.client.EventBean.class, "event", arrayAtIndex(refEPS, constant(forge.getStreamNum())))
                .ifRefNullReturnNull("event")
                .declareVar(String.class, "result", forge.getExprForge().evaluateCodegen(String.class, methodNode, exprSymbol, codegenClassScope))
                .ifRefNullReturnNull("result")
                .methodReturn(CodegenLegoCast.castSafeFromObjectType(forge.getEvaluationType(), forge.getMappedGetter().eventBeanGetMappedCodegen(methodNode, codegenClassScope, ref("event"), ref("result"))));

        return localMethod(methodNode);
    }

}
