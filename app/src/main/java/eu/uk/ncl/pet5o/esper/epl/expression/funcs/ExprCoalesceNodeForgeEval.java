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
import eu.uk.ncl.pet5o.esper.util.SimpleNumberCoercer;
import eu.uk.ncl.pet5o.esper.util.SimpleNumberCoercerFactory;

import static eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionBuilder.*;
import static eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionBuilder.constantNull;
import static eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionBuilder.localMethod;
import static eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionBuilder.notEqualsNull;
import static eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionBuilder.ref;

public class ExprCoalesceNodeForgeEval implements ExprEvaluator {
    private final ExprCoalesceNodeForge forge;
    private final ExprEvaluator[] evaluators;

    ExprCoalesceNodeForgeEval(ExprCoalesceNodeForge forge, ExprEvaluator[] evaluators) {
        this.forge = forge;
        this.evaluators = evaluators;
    }

    public Object evaluate(eu.uk.ncl.pet5o.esper.client.EventBean[] eventsPerStream, boolean isNewData, ExprEvaluatorContext exprEvaluatorContext) {
        if (InstrumentationHelper.ENABLED) {
            InstrumentationHelper.get().qExprCoalesce(forge.getForgeRenderable());
        }
        Object value;

        // Look for the first non-null return value
        for (int i = 0; i < evaluators.length; i++) {
            value = evaluators[i].evaluate(eventsPerStream, isNewData, exprEvaluatorContext);

            if (value != null) {
                // Check if we need to coerce
                if (forge.getIsNumericCoercion()[i]) {
                    value = JavaClassHelper.coerceBoxed((Number) value, forge.getEvaluationType());
                }
                if (InstrumentationHelper.ENABLED) {
                    InstrumentationHelper.get().aExprCoalesce(value);
                }
                return value;
            }
        }

        if (InstrumentationHelper.ENABLED) {
            InstrumentationHelper.get().aExprCoalesce(null);
        }
        return null;
    }

    public static CodegenExpression codegen(ExprCoalesceNodeForge forge, CodegenMethodScope codegenMethodScope, ExprForgeCodegenSymbol exprSymbol, CodegenClassScope codegenClassScope) {
        if (forge.getEvaluationType() == null) {
            return constantNull();
        }
        CodegenMethodNode methodNode = codegenMethodScope.makeChild(forge.getEvaluationType(), ExprCoalesceNodeForgeEval.class, codegenClassScope);


        CodegenBlock block = methodNode.getBlock();
        int num = 0;
        boolean doneWithReturn = false;
        for (ExprNode node : forge.getForgeRenderable().getChildNodes()) {
            Class reftype = node.getForge().getEvaluationType();
            if (reftype != null) {
                String refname = "r" + num;
                block.declareVar(reftype, refname, node.getForge().evaluateCodegen(reftype, methodNode, exprSymbol, codegenClassScope));

                if (reftype.isPrimitive()) {
                    if (!forge.getIsNumericCoercion()[num]) {
                        block.methodReturn(ref(refname));
                        doneWithReturn = true;
                    } else {
                        SimpleNumberCoercer coercer = SimpleNumberCoercerFactory.getCoercer(reftype, forge.getEvaluationType());
                        block.methodReturn(coercer.coerceCodegen(ref(refname), reftype));
                        doneWithReturn = true;
                    }
                    break;
                }

                CodegenBlock blockIf = block.ifCondition(notEqualsNull(ref(refname)));
                if (!forge.getIsNumericCoercion()[num]) {
                    blockIf.blockReturn(ref(refname));
                } else {
                    blockIf.blockReturn(JavaClassHelper.coerceNumberBoxedToBoxedCodegen(ref(refname), reftype, forge.getEvaluationType()));
                }
            }
            num++;
        }

        if (!doneWithReturn) {
            block.methodReturn(constantNull());
        }
        return localMethod(methodNode);
    }
}
