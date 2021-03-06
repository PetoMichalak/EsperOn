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

import eu.uk.ncl.pet5o.esper.client.EPException;
import eu.uk.ncl.pet5o.esper.codegen.base.CodegenBlock;
import eu.uk.ncl.pet5o.esper.codegen.base.CodegenClassScope;
import eu.uk.ncl.pet5o.esper.codegen.base.CodegenMethodNode;
import eu.uk.ncl.pet5o.esper.codegen.base.CodegenMethodScope;
import eu.uk.ncl.pet5o.esper.epl.expression.codegen.ExprForgeCodegenSymbol;
import eu.uk.ncl.pet5o.esper.epl.expression.core.ExprEvaluator;
import eu.uk.ncl.pet5o.esper.epl.expression.core.ExprEvaluatorContext;
import eu.uk.ncl.pet5o.esper.epl.expression.core.ExprNode;
import eu.uk.ncl.pet5o.esper.metrics.instrumentation.InstrumentationHelper;

import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import static eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionBuilder.*;
import static eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionBuilder.exprDotMethod;
import static eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionBuilder.ref;
import static eu.uk.ncl.pet5o.esper.epl.expression.ops.ExprRegexpNodeForgeConstEval.getRegexpCode;

public class ExprRegexpNodeForgeNonconstEval implements ExprEvaluator {
    private final ExprRegexpNodeForgeNonconst forge;
    private final ExprEvaluator lhsEval;
    private final ExprEvaluator patternEval;

    public ExprRegexpNodeForgeNonconstEval(ExprRegexpNodeForgeNonconst forge, ExprEvaluator lhsEval, ExprEvaluator patternEval) {
        this.forge = forge;
        this.lhsEval = lhsEval;
        this.patternEval = patternEval;
    }

    /**
     * NOTE: Code-generation-invoked method, method name and parameter order matters
     *
     * @param text regex pattern
     * @return pattern
     */
    public static Pattern exprRegexNodeCompilePattern(String text) {
        try {
            return Pattern.compile(text);
        } catch (PatternSyntaxException ex) {
            throw new EPException("Error compiling regex pattern '" + text + "': " + ex.getMessage(), ex);
        }
    }

    public Object evaluate(eu.uk.ncl.pet5o.esper.client.EventBean[] eventsPerStream, boolean isNewData, ExprEvaluatorContext context) {
        if (InstrumentationHelper.ENABLED) {
            InstrumentationHelper.get().qExprRegexp(forge.getForgeRenderable());
        }

        String patternText = (String) patternEval.evaluate(eventsPerStream, isNewData, context);
        if (patternText == null) {
            if (InstrumentationHelper.ENABLED) {
                InstrumentationHelper.get().aExprRegexp(null);
            }
            return null;
        }

        Pattern pattern = exprRegexNodeCompilePattern(patternText);

        Object evalValue = lhsEval.evaluate(eventsPerStream, isNewData, context);
        if (evalValue == null) {
            if (InstrumentationHelper.ENABLED) {
                InstrumentationHelper.get().aExprRegexp(null);
            }
            return null;
        }

        if (forge.isNumericValue()) {
            evalValue = evalValue.toString();
        }

        boolean result = forge.getForgeRenderable().isNot() ^ pattern.matcher((CharSequence) evalValue).matches();

        if (InstrumentationHelper.ENABLED) {
            InstrumentationHelper.get().aExprRegexp(result);
        }
        return result;
    }

    public static CodegenMethodNode codegen(ExprRegexpNodeForgeNonconst forge, ExprNode lhs, ExprNode pattern, CodegenMethodScope codegenMethodScope, ExprForgeCodegenSymbol exprSymbol, CodegenClassScope codegenClassScope) {
        CodegenMethodNode methodNode = codegenMethodScope.makeChild(Boolean.class, ExprRegexpNodeForgeNonconstEval.class, codegenClassScope);
        CodegenBlock blockMethod = methodNode.getBlock()
                .declareVar(String.class, "patternText", pattern.getForge().evaluateCodegen(String.class, methodNode, exprSymbol, codegenClassScope))
                .ifRefNullReturnNull("patternText");

        // initial like-setup
        blockMethod.declareVar(Pattern.class, "pattern", staticMethod(ExprRegexpNodeForgeNonconstEval.class, "exprRegexNodeCompilePattern", ref("patternText")));

        if (!forge.isNumericValue()) {
            blockMethod.declareVar(String.class, "value", lhs.getForge().evaluateCodegen(String.class, methodNode, exprSymbol, codegenClassScope))
                    .ifRefNullReturnNull("value")
                    .methodReturn(getRegexpCode(forge, ref("pattern"), ref("value")));
        } else {
            blockMethod.declareVar(Object.class, "value", lhs.getForge().evaluateCodegen(Object.class, methodNode, exprSymbol, codegenClassScope))
                    .ifRefNullReturnNull("value")
                    .methodReturn(getRegexpCode(forge, ref("pattern"), exprDotMethod(ref("value"), "toString")));
        }
        return methodNode;
    }

}
