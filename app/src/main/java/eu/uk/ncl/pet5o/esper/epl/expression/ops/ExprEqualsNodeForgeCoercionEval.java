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
import eu.uk.ncl.pet5o.esper.util.SimpleNumberCoercer;

import static eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionBuilder.*;
import static eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionBuilder.constantFalse;
import static eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionBuilder.equalsNull;
import static eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionBuilder.not;
import static eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionBuilder.ref;

public class ExprEqualsNodeForgeCoercionEval implements ExprEvaluator {
    private final ExprEqualsNodeImpl parent;
    private final ExprEvaluator lhs;
    private final ExprEvaluator rhs;
    private final SimpleNumberCoercer numberCoercerLHS;
    private final SimpleNumberCoercer numberCoercerRHS;

    public ExprEqualsNodeForgeCoercionEval(ExprEqualsNodeImpl parent, ExprEvaluator lhs, ExprEvaluator rhs, SimpleNumberCoercer numberCoercerLHS, SimpleNumberCoercer numberCoercerRHS) {
        this.parent = parent;
        this.lhs = lhs;
        this.rhs = rhs;
        this.numberCoercerLHS = numberCoercerLHS;
        this.numberCoercerRHS = numberCoercerRHS;
    }

    public Object evaluate(eu.uk.ncl.pet5o.esper.client.EventBean[] eventsPerStream, boolean isNewData, ExprEvaluatorContext context) {
        if (InstrumentationHelper.ENABLED) {
            InstrumentationHelper.get().qExprEquals(parent);
        }
        Boolean result = evaluateInternal(eventsPerStream, isNewData, context);
        if (InstrumentationHelper.ENABLED) {
            InstrumentationHelper.get().aExprEquals(result);
        }
        return result;
    }

    private Boolean evaluateInternal(eu.uk.ncl.pet5o.esper.client.EventBean[] eventsPerStream, boolean isNewData, ExprEvaluatorContext context) {
        Object leftResult = lhs.evaluate(eventsPerStream, isNewData, context);
        Object rightResult = rhs.evaluate(eventsPerStream, isNewData, context);

        if (!parent.isIs()) {
            if (leftResult == null || rightResult == null) {
                // null comparison
                return null;
            }
        } else {
            if (leftResult == null) {
                return rightResult == null;
            }
            if (rightResult == null) {
                return false;
            }
        }

        Number left = numberCoercerLHS.coerceBoxed((Number) leftResult);
        Number right = numberCoercerRHS.coerceBoxed((Number) rightResult);
        return left.equals(right) ^ parent.isNotEquals();
    }

    public static CodegenMethodNode codegen(ExprEqualsNodeForgeCoercion forge, CodegenMethodScope codegenMethodScope, ExprForgeCodegenSymbol exprSymbol, CodegenClassScope codegenClassScope, ExprNode lhs, ExprNode rhs) {
        Class lhsType = lhs.getForge().getEvaluationType();
        Class rhsType = rhs.getForge().getEvaluationType();

        CodegenMethodNode methodNode = codegenMethodScope.makeChild(Boolean.class, ExprEqualsNodeForgeNCEvalEquals.class, codegenClassScope);
        CodegenBlock block = methodNode.getBlock()
                .declareVar(lhsType, "l", lhs.getForge().evaluateCodegen(lhsType, methodNode, exprSymbol, codegenClassScope))
                .declareVar(rhsType, "r", rhs.getForge().evaluateCodegen(rhsType, methodNode, exprSymbol, codegenClassScope));

        if (!forge.getForgeRenderable().isIs()) {
            if (!lhsType.isPrimitive()) {
                block.ifRefNullReturnNull("l");
            }
            if (!rhsType.isPrimitive()) {
                block.ifRefNullReturnNull("r");
            }
        } else {
            if (!lhsType.isPrimitive() && !rhsType.isPrimitive()) {
                block.ifRefNull("l").blockReturn(equalsNull(ref("r")));
            }
            if (!rhsType.isPrimitive()) {
                block.ifRefNull("r").blockReturn(constantFalse());
            }
        }

        block.declareVar(Number.class, "left", forge.getNumberCoercerLHS().coerceCodegen(ref("l"), lhs.getForge().getEvaluationType()));
        block.declareVar(Number.class, "right", forge.getNumberCoercerRHS().coerceCodegen(ref("r"), rhs.getForge().getEvaluationType()));
        CodegenExpression compare = exprDotMethod(ref("left"), "equals", ref("right"));
        if (!forge.getForgeRenderable().isNotEquals()) {
            block.methodReturn(compare);
        } else {
            block.methodReturn(not(compare));
        }
        return methodNode;
    }
}
