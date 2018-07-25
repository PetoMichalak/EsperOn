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

import static eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionBuilder.*;
import static eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionBuilder.constantFalse;
import static eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionBuilder.constantNull;
import static eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionBuilder.constantTrue;
import static eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionBuilder.equalsNull;
import static eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionBuilder.localMethod;
import static eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionBuilder.not;
import static eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionBuilder.ref;

public class ExprAndNodeEval implements ExprEvaluator {
    private final ExprAndNodeImpl parent;
    private final ExprEvaluator[] evaluators;

    public ExprAndNodeEval(ExprAndNodeImpl parent, ExprEvaluator[] evaluators) {
        this.parent = parent;
        this.evaluators = evaluators;
    }

    public Object evaluate(eu.uk.ncl.pet5o.esper.client.EventBean[] eventsPerStream, boolean isNewData, ExprEvaluatorContext exprEvaluatorContext) {
        if (InstrumentationHelper.ENABLED) {
            InstrumentationHelper.get().qExprAnd(parent);
        }
        Boolean result = true;
        for (ExprEvaluator child : evaluators) {
            Boolean evaluated = (Boolean) child.evaluate(eventsPerStream, isNewData, exprEvaluatorContext);
            if (evaluated == null) {
                result = null;
            } else {
                if (!evaluated) {
                    if (InstrumentationHelper.ENABLED) {
                        InstrumentationHelper.get().aExprAnd(false);
                    }
                    return false;
                }
            }
        }
        if (InstrumentationHelper.ENABLED) {
            InstrumentationHelper.get().aExprAnd(result);
        }
        return result;
    }

    public static CodegenExpression codegen(ExprAndNodeImpl parent, CodegenMethodScope codegenMethodScope, ExprForgeCodegenSymbol exprSymbol, CodegenClassScope codegenClassScope) {
        CodegenMethodNode methodNode = codegenMethodScope.makeChild(Boolean.class, ExprAndNodeEval.class, codegenClassScope);
        CodegenBlock block = methodNode.getBlock()
                .declareVar(Boolean.class, "result", constantTrue());

        int count = -1;
        for (ExprNode child : parent.getChildNodes()) {
            count++;
            Class childType = child.getForge().getEvaluationType();
            if (childType.isPrimitive()) {
                block.ifCondition(not(child.getForge().evaluateCodegen(Boolean.class, methodNode, exprSymbol, codegenClassScope))).blockReturn(constantFalse());
            } else {
                String refname = "r" + count;
                block.declareVar(Boolean.class, refname, child.getForge().evaluateCodegen(Boolean.class, methodNode, exprSymbol, codegenClassScope))
                        .ifCondition(equalsNull(ref(refname)))
                        .assignRef("result", constantNull())
                        .ifElse()
                        .ifCondition(not(ref(refname))).blockReturn(constantFalse());
            }
        }
        block.methodReturn(ref("result"));
        return localMethod(methodNode);
    }
}
