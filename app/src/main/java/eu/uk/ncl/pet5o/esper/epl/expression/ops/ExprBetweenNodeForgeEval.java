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
import eu.uk.ncl.pet5o.esper.epl.expression.core.ExprForge;
import eu.uk.ncl.pet5o.esper.epl.expression.core.ExprNode;
import eu.uk.ncl.pet5o.esper.metrics.instrumentation.InstrumentationHelper;

import static eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionBuilder.*;
import static eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionBuilder.constant;
import static eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionBuilder.localMethod;
import static eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionBuilder.notOptional;
import static eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionBuilder.ref;

public class ExprBetweenNodeForgeEval implements ExprEvaluator {
    private final ExprBetweenNodeForge forge;
    private final ExprEvaluator valueEval;
    private final ExprEvaluator lowerEval;
    private final ExprEvaluator higherEval;

    public ExprBetweenNodeForgeEval(ExprBetweenNodeForge forge, ExprEvaluator valueEval, ExprEvaluator lowerEval, ExprEvaluator higherEval) {
        this.forge = forge;
        this.valueEval = valueEval;
        this.lowerEval = lowerEval;
        this.higherEval = higherEval;
    }

    public Object evaluate(eu.uk.ncl.pet5o.esper.client.EventBean[] eventsPerStream, boolean isNewData, ExprEvaluatorContext exprEvaluatorContext) {
        if (InstrumentationHelper.ENABLED) {
            InstrumentationHelper.get().qExprBetween(forge.getForgeRenderable());
        }

        // Evaluate first child which is the base value to compare to
        Object value = valueEval.evaluate(eventsPerStream, isNewData, exprEvaluatorContext);
        if (value == null) {
            if (InstrumentationHelper.ENABLED) {
                InstrumentationHelper.get().aExprBetween(false);
            }
            return false;
        }
        Object lower = lowerEval.evaluate(eventsPerStream, isNewData, exprEvaluatorContext);
        Object higher = higherEval.evaluate(eventsPerStream, isNewData, exprEvaluatorContext);

        boolean result = forge.getComputer().isBetween(value, lower, higher);
        result = result ^ forge.getForgeRenderable().isNotBetween();

        if (InstrumentationHelper.ENABLED) {
            InstrumentationHelper.get().aExprBetween(result);
        }
        return result;
    }

    public static CodegenExpression codegen(ExprBetweenNodeForge forge, CodegenMethodScope codegenMethodScope, ExprForgeCodegenSymbol exprSymbol, CodegenClassScope codegenClassScope) {
        ExprNode[] nodes = forge.getForgeRenderable().getChildNodes();
        ExprForge value = nodes[0].getForge();
        ExprForge lower = nodes[1].getForge();
        ExprForge higher = nodes[2].getForge();
        boolean isNot = forge.getForgeRenderable().isNotBetween();

        CodegenMethodNode methodNode = codegenMethodScope.makeChild(Boolean.class, ExprBetweenNodeForgeEval.class, codegenClassScope);
        CodegenBlock block = methodNode.getBlock();

        Class valueType = value.getEvaluationType();
        block.declareVar(valueType, "value", value.evaluateCodegen(valueType, methodNode, exprSymbol, codegenClassScope));
        if (!valueType.isPrimitive()) {
            block.ifRefNullReturnFalse("value");
        }

        Class lowerType = lower.getEvaluationType();
        block.declareVar(lowerType, "lower", lower.evaluateCodegen(lowerType, methodNode, exprSymbol, codegenClassScope));
        if (!lowerType.isPrimitive()) {
            block.ifRefNull("lower").blockReturn(constant(isNot));
        }

        Class higherType = higher.getEvaluationType();
        block.declareVar(higherType, "higher", higher.evaluateCodegen(higherType, methodNode, exprSymbol, codegenClassScope));
        if (!higher.getEvaluationType().isPrimitive()) {
            block.ifRefNull("higher").blockReturn(constant(isNot));
        }

        block.declareVar(boolean.class, "result", forge.getComputer().codegenNoNullCheck(ref("value"), value.getEvaluationType(), ref("lower"), lower.getEvaluationType(), ref("higher"), higher.getEvaluationType(), methodNode, codegenClassScope));
        block.methodReturn(notOptional(forge.getForgeRenderable().isNotBetween(), ref("result")));
        return localMethod(methodNode);
    }

}
