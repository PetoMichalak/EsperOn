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
package eu.uk.ncl.pet5o.esper.epl.enummethod.dot;

import eu.uk.ncl.pet5o.esper.client.EventBean;
import eu.uk.ncl.pet5o.esper.codegen.base.CodegenClassScope;
import eu.uk.ncl.pet5o.esper.codegen.base.CodegenMethodNode;
import eu.uk.ncl.pet5o.esper.codegen.base.CodegenMethodScope;
import eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpression;
import eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionRef;
import eu.uk.ncl.pet5o.esper.epl.expression.codegen.ExprForgeCodegenSymbol;
import eu.uk.ncl.pet5o.esper.epl.expression.core.ExprEvaluator;
import eu.uk.ncl.pet5o.esper.epl.expression.core.ExprEvaluatorContext;

import static eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionBuilder.*;
import static eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionBuilder.arrayAtIndex;
import static eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionBuilder.constant;
import static eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionBuilder.localMethod;
import static eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionBuilder.ref;

public class PropertyDotNonLambdaIndexedForgeEval implements ExprEvaluator {

    private final PropertyDotNonLambdaIndexedForge forge;
    private final ExprEvaluator paramEval;

    public PropertyDotNonLambdaIndexedForgeEval(PropertyDotNonLambdaIndexedForge forge, ExprEvaluator paramEval) {
        this.forge = forge;
        this.paramEval = paramEval;
    }

    public Object evaluate(EventBean[] eventsPerStream, boolean isNewData, ExprEvaluatorContext context) {
        EventBean event = eventsPerStream[forge.getStreamId()];
        if (event == null) {
            return null;
        }
        Integer key = (Integer) paramEval.evaluate(eventsPerStream, isNewData, context);
        return forge.getIndexedGetter().get(event, key);
    }

    public static CodegenExpression codegen(PropertyDotNonLambdaIndexedForge forge, CodegenMethodScope codegenMethodScope, ExprForgeCodegenSymbol exprSymbol, CodegenClassScope codegenClassScope) {
        CodegenMethodNode methodNode = codegenMethodScope.makeChild(forge.getEvaluationType(), PropertyDotNonLambdaIndexedForgeEval.class, codegenClassScope);

        CodegenExpressionRef refEPS = exprSymbol.getAddEPS(methodNode);
        Class evaluationType = forge.getParamForge().getEvaluationType();
        methodNode.getBlock()
                .declareVar(EventBean.class, "event", arrayAtIndex(refEPS, constant(forge.getStreamId())))
                .ifRefNullReturnNull("event")
                .declareVar(evaluationType, "key", forge.getParamForge().evaluateCodegen(evaluationType, methodNode, exprSymbol, codegenClassScope))
                .methodReturn(forge.getIndexedGetter().eventBeanGetIndexedCodegen(methodNode, codegenClassScope, ref("event"), ref("key")));
        return localMethod(methodNode);
    }
}
