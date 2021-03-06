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
package eu.uk.ncl.pet5o.esper.epl.expression.ops;

import eu.uk.ncl.pet5o.esper.codegen.base.CodegenBlock;
import eu.uk.ncl.pet5o.esper.codegen.base.CodegenClassScope;
import eu.uk.ncl.pet5o.esper.codegen.base.CodegenMethodNode;
import eu.uk.ncl.pet5o.esper.codegen.base.CodegenMethodScope;
import eu.uk.ncl.pet5o.esper.epl.expression.codegen.ExprForgeCodegenSymbol;
import eu.uk.ncl.pet5o.esper.epl.expression.core.ExprEvaluator;
import eu.uk.ncl.pet5o.esper.epl.expression.core.ExprEvaluatorContext;
import eu.uk.ncl.pet5o.esper.epl.expression.core.ExprNode;
import eu.uk.ncl.pet5o.esper.metrics.instrumentation.InstrumentationHelper;

import static eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionBuilder.ref;

public class ExprMathNodeForgeEval implements ExprEvaluator {
    private final ExprMathNodeForge forge;
    private final ExprEvaluator evaluatorLeft;
    private final ExprEvaluator evaluatorRight;

    public ExprMathNodeForgeEval(ExprMathNodeForge forge, ExprEvaluator evaluatorLeft, ExprEvaluator evaluatorRight) {
        this.forge = forge;
        this.evaluatorLeft = evaluatorLeft;
        this.evaluatorRight = evaluatorRight;
    }

    public Object evaluate(eu.uk.ncl.pet5o.esper.client.EventBean[] eventsPerStream, boolean isNewData, ExprEvaluatorContext exprEvaluatorContext) {
        if (InstrumentationHelper.ENABLED) {
            InstrumentationHelper.get().qExprMath(forge.getForgeRenderable(), forge.getForgeRenderable().getMathArithTypeEnum().getExpressionText());
        }

        Object left = evaluatorLeft.evaluate(eventsPerStream, isNewData, exprEvaluatorContext);
        if (left == null) {
            if (InstrumentationHelper.ENABLED) {
                InstrumentationHelper.get().aExprMath(null);
            }
            return null;
        }

        Object right = evaluatorRight.evaluate(eventsPerStream, isNewData, exprEvaluatorContext);
        if (right == null) {
            if (InstrumentationHelper.ENABLED) {
                InstrumentationHelper.get().aExprMath(null);
            }
            return null;
        }

        Object result = forge.getArithTypeEnumComputer().compute((Number) left, (Number) right);

        if (InstrumentationHelper.ENABLED) {
            InstrumentationHelper.get().aExprMath(result);
        }

        return result;
    }

    public static CodegenMethodNode codegen(ExprMathNodeForge forge, CodegenMethodScope codegenMethodScope, ExprForgeCodegenSymbol exprSymbol, CodegenClassScope codegenClassScope, ExprNode lhs, ExprNode rhs) {
        CodegenMethodNode methodNode = codegenMethodScope.makeChild(forge.getEvaluationType(), ExprMathNodeForgeEval.class, codegenClassScope);
        Class lhsType = lhs.getForge().getEvaluationType();
        Class rhsType = rhs.getForge().getEvaluationType();
        CodegenBlock block = methodNode.getBlock()
                .declareVar(lhsType, "left", lhs.getForge().evaluateCodegen(lhsType, methodNode, exprSymbol, codegenClassScope));
        if (!lhsType.isPrimitive()) {
            block.ifRefNullReturnNull("left");
        }
        block.declareVar(rhsType, "right", rhs.getForge().evaluateCodegen(rhsType, methodNode, exprSymbol, codegenClassScope));
        if (!rhsType.isPrimitive()) {
            block.ifRefNullReturnNull("right");
        }
        block.methodReturn(forge.getArithTypeEnumComputer().codegen(methodNode, codegenClassScope, ref("left"), ref("right"), lhsType, rhsType));
        return methodNode;
    }
}
