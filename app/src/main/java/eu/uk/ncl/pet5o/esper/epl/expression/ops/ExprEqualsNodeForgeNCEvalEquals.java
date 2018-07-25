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
import eu.uk.ncl.pet5o.esper.metrics.instrumentation.InstrumentationHelper;

import static eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionBuilder.not;
import static eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionBuilder.ref;
import static eu.uk.ncl.pet5o.esper.epl.expression.codegen.CodegenLegoCompareEquals.codegenEqualsNonNullNoCoerce;

public class ExprEqualsNodeForgeNCEvalEquals implements ExprEvaluator {
    private final ExprEqualsNodeImpl parent;
    private final ExprEvaluator lhs;
    private final ExprEvaluator rhs;

    ExprEqualsNodeForgeNCEvalEquals(ExprEqualsNodeImpl parent, ExprEvaluator lhs, ExprEvaluator rhs) {
        this.parent = parent;
        this.lhs = lhs;
        this.rhs = rhs;
    }

    public Object evaluate(eu.uk.ncl.pet5o.esper.client.EventBean[] eventsPerStream, boolean isNewData, ExprEvaluatorContext context) {
        if (InstrumentationHelper.ENABLED) {
            InstrumentationHelper.get().qExprEquals(parent);
        }

        Object left = lhs.evaluate(eventsPerStream, isNewData, context);
        Object right = rhs.evaluate(eventsPerStream, isNewData, context);

        if (left == null || right == null) { // null comparison
            if (InstrumentationHelper.ENABLED) {
                InstrumentationHelper.get().aExprEquals(null);
            }
            return null;
        }

        boolean result = left.equals(right) ^ parent.isNotEquals();
        if (InstrumentationHelper.ENABLED) {
            InstrumentationHelper.get().aExprEquals(result);
        }
        return result;
    }

    public static CodegenMethodNode codegen(ExprEqualsNodeForgeNC forge, CodegenMethodScope codegenMethodScope, ExprForgeCodegenSymbol exprSymbol, CodegenClassScope codegenClassScope, ExprForge lhs, ExprForge rhs) {
        Class lhsType = lhs.getEvaluationType();
        Class rhsType = rhs.getEvaluationType();

        CodegenMethodNode methodNode = codegenMethodScope.makeChild(Boolean.class, ExprEqualsNodeForgeNCEvalEquals.class, codegenClassScope);
        CodegenBlock block = methodNode.getBlock()
                .declareVar(lhsType, "left", lhs.evaluateCodegen(lhsType, methodNode, exprSymbol, codegenClassScope))
                .declareVar(rhsType, "right", rhs.evaluateCodegen(rhsType, methodNode, exprSymbol, codegenClassScope));

        if (!lhsType.isPrimitive()) {
            block.ifRefNullReturnNull("left");
        }
        if (!rhsType.isPrimitive()) {
            block.ifRefNullReturnNull("right");
        }
        CodegenExpression compare = codegenEqualsNonNullNoCoerce(ref("left"), lhsType, ref("right"), rhsType);
        if (!forge.getForgeRenderable().isNotEquals()) {
            block.methodReturn(compare);
        } else {
            block.methodReturn(not(compare));
        }
        return methodNode;
    }
}
