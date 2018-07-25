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
import eu.uk.ncl.pet5o.esper.epl.expression.core.ExprForge;
import eu.uk.ncl.pet5o.esper.metrics.instrumentation.InstrumentationHelper;

import static eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionBuilder.*;
import static eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionBuilder.and;
import static eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionBuilder.equalsNull;
import static eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionBuilder.not;
import static eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionBuilder.notEqualsNull;
import static eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionBuilder.ref;

public class ExprEqualsNodeForgeNCEvalIs implements ExprEvaluator {
    private final ExprEqualsNodeImpl parent;
    private final ExprEvaluator lhs;
    private final ExprEvaluator rhs;

    public ExprEqualsNodeForgeNCEvalIs(ExprEqualsNodeImpl parent, ExprEvaluator lhs, ExprEvaluator rhs) {
        this.parent = parent;
        this.lhs = lhs;
        this.rhs = rhs;
    }

    public Object evaluate(eu.uk.ncl.pet5o.esper.client.EventBean[] eventsPerStream, boolean isNewData, ExprEvaluatorContext context) {
        if (InstrumentationHelper.ENABLED) {
            InstrumentationHelper.get().qExprIs(parent);
        }

        Object left = lhs.evaluate(eventsPerStream, isNewData, context);
        Object right = rhs.evaluate(eventsPerStream, isNewData, context);

        boolean result;
        if (left == null) {
            result = right == null;
        } else {
            result = right != null && left.equals(right);
        }
        result = result ^ parent.isNotEquals();

        if (InstrumentationHelper.ENABLED) {
            InstrumentationHelper.get().aExprIs(result);
        }
        return result;
    }

    public static CodegenMethodNode codegen(ExprEqualsNodeForgeNC forge, CodegenMethodScope codegenMethodScope, ExprForgeCodegenSymbol exprSymbol, CodegenClassScope codegenClassScope, ExprForge lhs, ExprForge rhs) {
        CodegenMethodNode methodNode = codegenMethodScope.makeChild(boolean.class, ExprEqualsNodeForgeNCEvalIs.class, codegenClassScope);
        CodegenBlock block = methodNode.getBlock()
                .declareVar(Object.class, "left", lhs.evaluateCodegen(Object.class, methodNode, exprSymbol, codegenClassScope))
                .declareVar(Object.class, "right", rhs.evaluateCodegen(Object.class, methodNode, exprSymbol, codegenClassScope));
        block.declareVarNoInit(boolean.class, "result")
                .ifRefNull("left")
                .assignRef("result", equalsNull(ref("right")))
                .ifElse()
                .assignRef("result", and(notEqualsNull(ref("right")), exprDotMethod(ref("left"), "equals", ref("right"))))
                .blockEnd();
        if (!forge.getForgeRenderable().isNotEquals()) {
            block.methodReturn(ref("result"));
        } else {
            block.methodReturn(not(ref("result")));
        }
        return methodNode;
    }
}
