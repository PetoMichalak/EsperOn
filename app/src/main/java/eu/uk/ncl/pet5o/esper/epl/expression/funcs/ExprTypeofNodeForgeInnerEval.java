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

import eu.uk.ncl.pet5o.esper.codegen.base.CodegenClassScope;
import eu.uk.ncl.pet5o.esper.codegen.base.CodegenMethodNode;
import eu.uk.ncl.pet5o.esper.codegen.base.CodegenMethodScope;
import eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpression;
import eu.uk.ncl.pet5o.esper.epl.expression.codegen.ExprForgeCodegenSymbol;
import eu.uk.ncl.pet5o.esper.epl.expression.core.ExprEvaluator;
import eu.uk.ncl.pet5o.esper.epl.expression.core.ExprEvaluatorContext;
import eu.uk.ncl.pet5o.esper.epl.expression.core.ExprForgeComplexityEnum;
import eu.uk.ncl.pet5o.esper.epl.expression.core.ExprNodeRenderable;
import eu.uk.ncl.pet5o.esper.metrics.instrumentation.InstrumentationHelper;

import static eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionBuilder.*;
import static eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionBuilder.exprDotMethodChain;
import static eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionBuilder.localMethod;
import static eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionBuilder.ref;

public class ExprTypeofNodeForgeInnerEval extends ExprTypeofNodeForge {

    private final ExprTypeofNode parent;

    public ExprTypeofNodeForgeInnerEval(ExprTypeofNode parent) {
        this.parent = parent;
    }

    public ExprEvaluator getExprEvaluator() {
        return new InnerEvaluator(parent.getChildNodes()[0].getForge().getExprEvaluator());
    }

    public CodegenExpression evaluateCodegen(Class requiredType, CodegenMethodScope codegenMethodScope, ExprForgeCodegenSymbol exprSymbol, CodegenClassScope codegenClassScope) {
        CodegenMethodNode methodNode = codegenMethodScope.makeChild(String.class, ExprTypeofNodeForgeInnerEval.class, codegenClassScope);
        methodNode.getBlock()
                .declareVar(Object.class, "result", parent.getChildNodes()[0].getForge().evaluateCodegen(requiredType, methodNode, exprSymbol, codegenClassScope))
                .ifRefNullReturnNull("result")
                .methodReturn(exprDotMethodChain(ref("result")).add("getClass").add("getSimpleName"));
        return localMethod(methodNode);
    }

    public ExprNodeRenderable getForgeRenderable() {
        return parent;
    }

    public ExprForgeComplexityEnum getComplexity() {
        return ExprForgeComplexityEnum.INTER;
    }

    private static class InnerEvaluator implements ExprEvaluator {
        private final ExprEvaluator evaluator;

        InnerEvaluator(ExprEvaluator evaluator) {
            this.evaluator = evaluator;
        }

        @Override
        public Object evaluate(eu.uk.ncl.pet5o.esper.client.EventBean[] eventsPerStream, boolean isNewData, ExprEvaluatorContext context) {
            if (InstrumentationHelper.ENABLED) {
                InstrumentationHelper.get().qExprTypeof();
            }
            Object result = evaluator.evaluate(eventsPerStream, isNewData, context);
            if (result == null) {
                if (InstrumentationHelper.ENABLED) {
                    InstrumentationHelper.get().aExprTypeof(null);
                }
                return null;
            }
            if (InstrumentationHelper.ENABLED) {
                InstrumentationHelper.get().aExprTypeof(result.getClass().getSimpleName());
            }
            return result.getClass().getSimpleName();
        }
    }
}
