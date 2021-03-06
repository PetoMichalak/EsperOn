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
package eu.uk.ncl.pet5o.esper.epl.expression.dot;

import eu.uk.ncl.pet5o.esper.codegen.base.CodegenBlock;
import eu.uk.ncl.pet5o.esper.codegen.base.CodegenClassScope;
import eu.uk.ncl.pet5o.esper.codegen.base.CodegenMember;
import eu.uk.ncl.pet5o.esper.codegen.base.CodegenMethodNode;
import eu.uk.ncl.pet5o.esper.codegen.base.CodegenMethodScope;
import eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpression;
import eu.uk.ncl.pet5o.esper.epl.expression.codegen.ExprForgeCodegenSymbol;
import eu.uk.ncl.pet5o.esper.epl.expression.core.ExprEvaluator;
import eu.uk.ncl.pet5o.esper.epl.expression.core.ExprEvaluatorContext;
import eu.uk.ncl.pet5o.esper.epl.variable.VariableMetaData;
import eu.uk.ncl.pet5o.esper.epl.variable.VariableReader;
import eu.uk.ncl.pet5o.esper.metrics.instrumentation.InstrumentationHelper;

import static eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionBuilder.*;
import static eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionBuilder.cast;
import static eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionBuilder.exprDotMethod;
import static eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionBuilder.localMethod;
import static eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionBuilder.member;
import static eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionBuilder.ref;

public class ExprDotNodeForgeVariableEval implements ExprEvaluator {
    private final ExprDotNodeForgeVariable forge;
    private final ExprDotEval[] chainEval;

    public ExprDotNodeForgeVariableEval(ExprDotNodeForgeVariable forge, ExprDotEval[] chainEval) {
        this.forge = forge;
        this.chainEval = chainEval;
    }

    public Object evaluate(eu.uk.ncl.pet5o.esper.client.EventBean[] eventsPerStream, boolean isNewData, ExprEvaluatorContext exprEvaluatorContext) {
        if (InstrumentationHelper.ENABLED) {
            InstrumentationHelper.get().qExprDot(forge.getForgeRenderable());
        }

        Object result = forge.getVariableReader().getValue();
        result = ExprDotNodeUtility.evaluateChainWithWrap(forge.getResultWrapLambda(), result, forge.getVariableReader().getVariableMetaData().getEventType(), forge.getVariableReader().getVariableMetaData().getType(), chainEval, forge.getChainForge(), eventsPerStream, isNewData, exprEvaluatorContext);

        if (InstrumentationHelper.ENABLED) {
            InstrumentationHelper.get().aExprDot(result);
        }
        return result;
    }

    public static CodegenExpression codegen(ExprDotNodeForgeVariable forge, CodegenMethodScope codegenMethodScope, ExprForgeCodegenSymbol exprSymbol, CodegenClassScope codegenClassScope) {

        CodegenMember variableReader = codegenClassScope.makeAddMember(VariableReader.class, forge.getVariableReader());
        Class variableType;
        VariableMetaData metaData = forge.getVariableReader().getVariableMetaData();
        if (metaData.getEventType() != null) {
            variableType = eu.uk.ncl.pet5o.esper.client.EventBean.class;
        } else {
            variableType = metaData.getType();
        }
        CodegenMethodNode methodNode = codegenMethodScope.makeChild(forge.getEvaluationType(), ExprDotNodeForgeVariableEval.class, codegenClassScope);


        CodegenBlock block = methodNode.getBlock()
                .declareVar(variableType, "result", cast(variableType, exprDotMethod(member(variableReader.getMemberId()), "getValue")));
        CodegenExpression chain = ExprDotNodeUtility.evaluateChainCodegen(methodNode, exprSymbol, codegenClassScope, ref("result"), variableType, forge.getChainForge(), forge.getResultWrapLambda());
        block.methodReturn(chain);
        return localMethod(methodNode);
    }
}
