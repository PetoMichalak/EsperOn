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

import eu.uk.ncl.pet5o.esper.codegen.base.CodegenClassScope;
import eu.uk.ncl.pet5o.esper.codegen.base.CodegenMember;
import eu.uk.ncl.pet5o.esper.codegen.base.CodegenMethodNode;
import eu.uk.ncl.pet5o.esper.codegen.base.CodegenMethodScope;
import eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpression;
import eu.uk.ncl.pet5o.esper.epl.expression.codegen.ExprForgeCodegenSymbol;
import eu.uk.ncl.pet5o.esper.epl.expression.core.ExprEvaluator;
import eu.uk.ncl.pet5o.esper.epl.expression.core.ExprEvaluatorContext;
import eu.uk.ncl.pet5o.esper.epl.expression.core.ExprNode;
import eu.uk.ncl.pet5o.esper.metrics.instrumentation.InstrumentationHelper;

import java.util.regex.Pattern;

import static eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionBuilder.*;
import static eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionBuilder.exprDotMethod;
import static eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionBuilder.exprDotMethodChain;
import static eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionBuilder.member;
import static eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionBuilder.not;
import static eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionBuilder.ref;

/**
 * Like-Node Form-1: string input, constant pattern and no or constant escape character
 */
public class ExprRegexpNodeForgeConstEval implements ExprEvaluator {
    private final ExprRegexpNodeForgeConst forge;
    private final ExprEvaluator lhsEval;

    ExprRegexpNodeForgeConstEval(ExprRegexpNodeForgeConst forge, ExprEvaluator lhsEval) {
        this.forge = forge;
        this.lhsEval = lhsEval;
    }

    public Object evaluate(eu.uk.ncl.pet5o.esper.client.EventBean[] eventsPerStream, boolean isNewData, ExprEvaluatorContext context) {
        if (InstrumentationHelper.ENABLED) {
            InstrumentationHelper.get().qExprRegexp(forge.getForgeRenderable());
        }

        Object value = lhsEval.evaluate(eventsPerStream, isNewData, context);
        if (value == null) {
            if (InstrumentationHelper.ENABLED) {
                InstrumentationHelper.get().aExprRegexp(null);
            }
            return null;
        }

        if (forge.isNumericValue()) {
            value = value.toString();
        }

        boolean result = forge.getForgeRenderable().isNot() ^ forge.getPattern().matcher((CharSequence) value).matches();

        if (InstrumentationHelper.ENABLED) {
            InstrumentationHelper.get().aExprRegexp(result);
        }
        return result;
    }

    public static CodegenMethodNode codegen(ExprRegexpNodeForgeConst forge, ExprNode lhs, CodegenMethodScope codegenMethodScope, ExprForgeCodegenSymbol exprSymbol, CodegenClassScope codegenClassScope) {
        CodegenMember mPattern = codegenClassScope.makeAddMember(Pattern.class, forge.getPattern());
        CodegenMethodNode methodNode = codegenMethodScope.makeChild(Boolean.class, ExprRegexpNodeForgeConstEval.class, codegenClassScope);

        if (!forge.isNumericValue()) {
            methodNode.getBlock()
                    .declareVar(String.class, "value", lhs.getForge().evaluateCodegen(String.class, methodNode, exprSymbol, codegenClassScope))
                    .ifRefNullReturnNull("value")
                    .methodReturn(getRegexpCode(forge, member(mPattern.getMemberId()), ref("value")));
        } else {
            methodNode.getBlock()
                    .declareVar(Object.class, "value", lhs.getForge().evaluateCodegen(Object.class, methodNode, exprSymbol, codegenClassScope))
                    .ifRefNullReturnNull("value")
                    .methodReturn(getRegexpCode(forge, member(mPattern.getMemberId()), exprDotMethod(ref("value"), "toString")));
        }
        return methodNode;
    }

    static CodegenExpression getRegexpCode(ExprRegexpNodeForge forge, CodegenExpression pattern, CodegenExpression stringExpr) {
        CodegenExpression eval = exprDotMethodChain(pattern).add("matcher", stringExpr).add("matches");
        return !forge.getForgeRenderable().isNot() ? eval : not(eval);
    }
}
