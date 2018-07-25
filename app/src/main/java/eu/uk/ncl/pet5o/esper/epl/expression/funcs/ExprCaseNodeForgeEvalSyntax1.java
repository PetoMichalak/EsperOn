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
import eu.uk.ncl.pet5o.esper.collection.UniformPair;
import eu.uk.ncl.pet5o.esper.epl.expression.codegen.ExprForgeCodegenSymbol;
import eu.uk.ncl.pet5o.esper.epl.expression.core.ExprEvaluator;
import eu.uk.ncl.pet5o.esper.epl.expression.core.ExprEvaluatorContext;
import eu.uk.ncl.pet5o.esper.epl.expression.core.ExprNode;
import eu.uk.ncl.pet5o.esper.metrics.instrumentation.InstrumentationHelper;
import eu.uk.ncl.pet5o.esper.util.JavaClassHelper;

import java.util.List;
import java.util.Map;

import static eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionBuilder.*;
import static eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionBuilder.and;
import static eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionBuilder.constantFalse;
import static eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionBuilder.constantNull;
import static eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionBuilder.localMethod;
import static eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionBuilder.notEqualsNull;
import static eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionBuilder.ref;

public class ExprCaseNodeForgeEvalSyntax1 implements ExprEvaluator {

    private final ExprCaseNodeForge forge;
    private final List<UniformPair<ExprEvaluator>> whenThenNodeList;
    private final ExprEvaluator optionalElseExprNode;

    public ExprCaseNodeForgeEvalSyntax1(ExprCaseNodeForge forge, List<UniformPair<ExprEvaluator>> whenThenNodeList, ExprEvaluator optionalElseExprNode) {
        this.forge = forge;
        this.whenThenNodeList = whenThenNodeList;
        this.optionalElseExprNode = optionalElseExprNode;
    }

    public Object evaluate(eu.uk.ncl.pet5o.esper.client.EventBean[] eventsPerStream, boolean isNewData, ExprEvaluatorContext exprEvaluatorContext) {
        // Case 1 expression example:
        //      case when a=b then x [when c=d then y...] [else y]
        if (InstrumentationHelper.ENABLED) {
            InstrumentationHelper.get().qExprCase(forge.getForgeRenderable());
        }

        Object caseResult = null;
        boolean matched = false;
        for (UniformPair<ExprEvaluator> p : whenThenNodeList) {
            Boolean whenResult = (Boolean) p.getFirst().evaluate(eventsPerStream, isNewData, exprEvaluatorContext);

            // If the 'when'-expression returns true
            if ((whenResult != null) && whenResult) {
                caseResult = p.getSecond().evaluate(eventsPerStream, isNewData, exprEvaluatorContext);
                matched = true;
                break;
            }
        }

        if (!matched && optionalElseExprNode != null) {
            caseResult = optionalElseExprNode.evaluate(eventsPerStream, isNewData, exprEvaluatorContext);
        }

        if (caseResult == null) {
            if (InstrumentationHelper.ENABLED) {
                InstrumentationHelper.get().aExprCase(null);
            }
            return null;
        }

        if ((caseResult.getClass() != forge.getEvaluationType()) && forge.isNumericResult()) {
            caseResult = JavaClassHelper.coerceBoxed((Number) caseResult, forge.getEvaluationType());
        }

        if (InstrumentationHelper.ENABLED) {
            InstrumentationHelper.get().aExprCase(caseResult);
        }
        return caseResult;
    }

    public static CodegenExpression codegen(ExprCaseNodeForge forge, CodegenMethodScope codegenMethodScope, ExprForgeCodegenSymbol exprSymbol, CodegenClassScope codegenClassScope) {
        Class evaluationType = forge.getEvaluationType() == null ? Map.class : forge.getEvaluationType();
        CodegenMethodNode methodNode = codegenMethodScope.makeChild(evaluationType, ExprCaseNodeForgeEvalSyntax1.class, codegenClassScope);

        CodegenBlock block = methodNode.getBlock().declareVar(Boolean.class, "when", constantFalse());

        for (UniformPair<ExprNode> pair : forge.getWhenThenNodeList()) {
            block.assignRef("when", pair.getFirst().getForge().evaluateCodegen(Boolean.class, methodNode, exprSymbol, codegenClassScope));
            block.ifCondition(and(notEqualsNull(ref("when")), ref("when")))
                    .blockReturn(codegenToType(forge, pair.getSecond(), methodNode, exprSymbol, codegenClassScope));
        }
        if (forge.getOptionalElseExprNode() != null) {
            block.methodReturn(codegenToType(forge, forge.getOptionalElseExprNode(), methodNode, exprSymbol, codegenClassScope));
        } else {
            block.methodReturn(constantNull());
        }
        return localMethod(methodNode);
    }

    protected static CodegenExpression codegenToType(ExprCaseNodeForge forge, ExprNode node, CodegenMethodNode methodNode, ExprForgeCodegenSymbol exprSymbol, CodegenClassScope codegenClassScope) {
        Class nodeEvaluationType = node.getForge().getEvaluationType();
        if (nodeEvaluationType == forge.getEvaluationType() || !forge.isNumericResult()) {
            return node.getForge().evaluateCodegen(nodeEvaluationType, methodNode, exprSymbol, codegenClassScope);
        }
        if (nodeEvaluationType == null) {
            return constantNull();
        }
        return JavaClassHelper.coerceNumberToBoxedCodegen(node.getForge().evaluateCodegen(nodeEvaluationType, methodNode, exprSymbol, codegenClassScope), nodeEvaluationType, forge.getEvaluationType());
    }
}
