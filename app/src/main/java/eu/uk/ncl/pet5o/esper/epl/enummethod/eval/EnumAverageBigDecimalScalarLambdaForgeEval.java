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
import eu.uk.ncl.pet5o.esper.codegen.base.CodegenBlock;
import eu.uk.ncl.pet5o.esper.codegen.base.CodegenClassScope;
import eu.uk.ncl.pet5o.esper.codegen.base.CodegenMember;
import eu.uk.ncl.pet5o.esper.codegen.base.CodegenMethodNode;
import eu.uk.ncl.pet5o.esper.codegen.base.CodegenMethodScope;
import eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpression;
import eu.uk.ncl.pet5o.esper.epl.agg.aggregator.AggregatorAvgBigDecimal;
import eu.uk.ncl.pet5o.esper.epl.enummethod.codegen.EnumForgeCodegenNames;
import eu.uk.ncl.pet5o.esper.epl.enummethod.codegen.EnumForgeCodegenParams;
import eu.uk.ncl.pet5o.esper.epl.expression.codegen.ExprForgeCodegenSymbol;
import eu.uk.ncl.pet5o.esper.epl.expression.core.ExprEvaluator;
import eu.uk.ncl.pet5o.esper.epl.expression.core.ExprEvaluatorContext;
import eu.uk.ncl.pet5o.esper.event.arr.ObjectArrayEventBean;
import eu.uk.ncl.pet5o.esper.event.arr.ObjectArrayEventType;

import java.math.BigDecimal;
import java.math.MathContext;
import java.util.Collection;

import static eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionBuilder.*;
import static eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionBuilder.constant;
import static eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionBuilder.exprDotMethod;
import static eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionBuilder.localMethod;
import static eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionBuilder.member;
import static eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionBuilder.newArrayByLength;
import static eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionBuilder.newInstance;
import static eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionBuilder.ref;

public class EnumAverageBigDecimalScalarLambdaForgeEval implements EnumEval {

    private final EnumAverageBigDecimalScalarLambdaForge forge;
    private final ExprEvaluator innerExpression;

    public EnumAverageBigDecimalScalarLambdaForgeEval(EnumAverageBigDecimalScalarLambdaForge forge, ExprEvaluator innerExpression) {
        this.forge = forge;
        this.innerExpression = innerExpression;
    }

    public Object evaluateEnumMethod(EventBean[] eventsLambda, Collection enumcoll, boolean isNewData, ExprEvaluatorContext context) {

        AggregatorAvgBigDecimal agg = new AggregatorAvgBigDecimal(forge.optionalMathContext);
        ObjectArrayEventBean resultEvent = new ObjectArrayEventBean(new Object[1], forge.resultEventType);
        eventsLambda[forge.streamNumLambda] = resultEvent;
        Object[] props = resultEvent.getProperties();

        Collection<Object> values = (Collection<Object>) enumcoll;
        for (Object next : values) {

            props[0] = next;

            Number num = (Number) innerExpression.evaluate(eventsLambda, isNewData, context);
            if (num == null) {
                continue;
            }
            agg.enter(num);
        }

        return agg.getValue();
    }

    public static CodegenExpression codegen(EnumAverageBigDecimalScalarLambdaForge forge, EnumForgeCodegenParams args, CodegenMethodScope codegenMethodScope, CodegenClassScope codegenClassScope) {
        Class innerType = forge.innerExpression.getEvaluationType();
        CodegenMember typeMember = codegenClassScope.makeAddMember(ObjectArrayEventType.class, forge.resultEventType);
        CodegenMember mathCtxMember = codegenClassScope.makeAddMember(MathContext.class, forge.optionalMathContext);

        ExprForgeCodegenSymbol scope = new ExprForgeCodegenSymbol(false, null);
        CodegenMethodNode methodNode = codegenMethodScope.makeChildWithScope(BigDecimal.class, EnumAverageBigDecimalScalarLambdaForgeEval.class, scope, codegenClassScope).addParam(EnumForgeCodegenNames.PARAMS);

        CodegenBlock block = methodNode.getBlock();
        block.declareVar(AggregatorAvgBigDecimal.class, "agg", newInstance(AggregatorAvgBigDecimal.class, member(mathCtxMember.getMemberId())))
                .declareVar(ObjectArrayEventBean.class, "resultEvent", newInstance(ObjectArrayEventBean.class, newArrayByLength(Object.class, constant(1)), member(typeMember.getMemberId())))
                .assignArrayElement(EnumForgeCodegenNames.REF_EPS, constant(forge.streamNumLambda), ref("resultEvent"))
                .declareVar(Object[].class, "props", exprDotMethod(ref("resultEvent"), "getProperties"));

        CodegenBlock forEach = block.forEach(Object.class, "next", EnumForgeCodegenNames.REF_ENUMCOLL)
                .assignArrayElement("props", constant(0), ref("next"))
                .declareVar(innerType, "num", forge.innerExpression.evaluateCodegen(Number.class, methodNode, scope, codegenClassScope));
        if (!innerType.isPrimitive()) {
            forEach.ifRefNull("num").blockContinue();
        }
        forEach.expression(exprDotMethod(ref("agg"), "enter", ref("num")));
        block.methodReturn(exprDotMethod(ref("agg"), "getValue"));
        return localMethod(methodNode, args.getEps(), args.getEnumcoll(), args.getIsNewData(), args.getExprCtx());
    }
}
