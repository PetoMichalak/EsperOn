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
import eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpression;
import eu.uk.ncl.pet5o.esper.epl.expression.codegen.ExprForgeCodegenSymbol;
import eu.uk.ncl.pet5o.esper.epl.expression.core.ExprEvaluator;
import eu.uk.ncl.pet5o.esper.epl.expression.core.ExprEvaluatorContext;
import eu.uk.ncl.pet5o.esper.epl.expression.core.ExprNode;
import eu.uk.ncl.pet5o.esper.metrics.instrumentation.InstrumentationHelper;
import eu.uk.ncl.pet5o.esper.util.JavaClassHelper;

import static eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionBuilder.*;
import static eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionBuilder.cast;
import static eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionBuilder.localMethod;
import static eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionBuilder.op;
import static eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionBuilder.ref;

public class ExprBitWiseNodeForgeEval implements ExprEvaluator {
    private final ExprBitWiseNodeForge forge;
    private final ExprEvaluator lhs;
    private final ExprEvaluator rhs;

    ExprBitWiseNodeForgeEval(ExprBitWiseNodeForge forge, ExprEvaluator lhs, ExprEvaluator rhs) {
        this.forge = forge;
        this.lhs = lhs;
        this.rhs = rhs;
    }

    public Object evaluate(eu.uk.ncl.pet5o.esper.client.EventBean[] eventsPerStream, boolean isNewData, ExprEvaluatorContext exprEvaluatorContext) {
        if (InstrumentationHelper.ENABLED) {
            InstrumentationHelper.get().qExprBitwise(forge.getForgeRenderable(), forge.getForgeRenderable().getBitWiseOpEnum());
        }

        Object left = lhs.evaluate(eventsPerStream, isNewData, exprEvaluatorContext);
        Object right = rhs.evaluate(eventsPerStream, isNewData, exprEvaluatorContext);

        if ((left == null) || (right == null)) {
            if (InstrumentationHelper.ENABLED) {
                InstrumentationHelper.get().aExprBitwise(null);
            }
            return null;
        }

        Object result = forge.getComputer().compute(left, right);

        if (InstrumentationHelper.ENABLED) {
            InstrumentationHelper.get().aExprBitwise(result);
        }
        return result;
    }

    public static CodegenExpression codegen(ExprBitWiseNodeForge forge, Class requiredType, CodegenMethodScope codegenMethodScope, ExprForgeCodegenSymbol exprSymbol, CodegenClassScope codegenClassScope, ExprNode lhs, ExprNode rhs) {
        CodegenMethodNode methodNode = codegenMethodScope.makeChild(forge.getEvaluationType(), ExprBitWiseNodeForgeEval.class, codegenClassScope);

        Class leftType = lhs.getForge().getEvaluationType();
        Class rightType = rhs.getForge().getEvaluationType();
        CodegenBlock block = methodNode.getBlock()
                .declareVar(leftType, "left", lhs.getForge().evaluateCodegen(leftType, methodNode, exprSymbol, codegenClassScope))
                .declareVar(rightType, "right", rhs.getForge().evaluateCodegen(rightType, methodNode, exprSymbol, codegenClassScope));
        if (!leftType.isPrimitive()) {
            block.ifRefNullReturnNull("left");
        }
        if (!rhs.getForge().getEvaluationType().isPrimitive()) {
            block.ifRefNullReturnNull("right");
        }
        Class primitive = JavaClassHelper.getPrimitiveType(forge.getEvaluationType());
        block.declareVar(primitive, "l", ref("left"))
                .declareVar(primitive, "r", ref("right"));

        block.methodReturn(cast(primitive, op(ref("l"), forge.getForgeRenderable().getBitWiseOpEnum().getExpressionText(), ref("r"))));
        return localMethod(methodNode);
    }
}
