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
package eu.uk.ncl.pet5o.esper.epl.enummethod.eval;

import eu.uk.ncl.pet5o.esper.client.EventBean;
import eu.uk.ncl.pet5o.esper.codegen.base.CodegenClassScope;
import eu.uk.ncl.pet5o.esper.codegen.base.CodegenMember;
import eu.uk.ncl.pet5o.esper.codegen.base.CodegenMethodNode;
import eu.uk.ncl.pet5o.esper.codegen.base.CodegenMethodScope;
import eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpression;
import eu.uk.ncl.pet5o.esper.epl.agg.aggregator.AggregatorAvgBigDecimal;
import eu.uk.ncl.pet5o.esper.epl.enummethod.codegen.EnumForgeCodegenNames;
import eu.uk.ncl.pet5o.esper.epl.enummethod.codegen.EnumForgeCodegenParams;
import eu.uk.ncl.pet5o.esper.epl.expression.core.ExprEvaluatorContext;

import java.math.BigDecimal;
import java.math.MathContext;
import java.util.Collection;

import static eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionBuilder.*;
import static eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionBuilder.exprDotMethod;
import static eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionBuilder.member;
import static eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionBuilder.ref;

public class EnumAverageBigDecimalScalarForge extends EnumForgeBase implements EnumEval {

    private final MathContext optionalMathContext;

    public EnumAverageBigDecimalScalarForge(int streamCountIncoming, MathContext optionalMathContext) {
        super(streamCountIncoming);
        this.optionalMathContext = optionalMathContext;
    }

    public EnumEval getEnumEvaluator() {
        return this;
    }

    public Object evaluateEnumMethod(EventBean[] eventsLambda, Collection enumcoll, boolean isNewData, ExprEvaluatorContext context) {

        AggregatorAvgBigDecimal agg = new AggregatorAvgBigDecimal(optionalMathContext);

        for (Object next : enumcoll) {

            Number num = (Number) next;
            if (num == null) {
                continue;
            }
            agg.enter(num);
        }

        return agg.getValue();
    }

    public CodegenExpression codegen(EnumForgeCodegenParams args, CodegenMethodScope codegenMethodScope, CodegenClassScope codegenClassScope) {
        CodegenMember memberMathCtx = codegenClassScope.makeAddMember(MathContext.class, optionalMathContext);
        CodegenMethodNode method = codegenMethodScope.makeChild(BigDecimal.class, EnumAverageScalarForge.class, codegenClassScope).addParam(EnumForgeCodegenNames.PARAMS).getBlock()
                .declareVar(AggregatorAvgBigDecimal.class, "agg", newInstance(AggregatorAvgBigDecimal.class, member(memberMathCtx.getMemberId())))
                .forEach(Number.class, "num", EnumForgeCodegenNames.REF_ENUMCOLL)
                .ifRefNull("num").blockContinue()
                .expression(exprDotMethod(ref("agg"), "enter", ref("num")))
                .blockEnd()
                .methodReturn(exprDotMethod(ref("agg"), "getValue"));
        return localMethod(method, args.getExpressions());
    }
}
