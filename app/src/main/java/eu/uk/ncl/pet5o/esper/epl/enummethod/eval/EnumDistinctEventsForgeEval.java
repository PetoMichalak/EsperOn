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
import eu.uk.ncl.pet5o.esper.codegen.base.CodegenMethodNode;
import eu.uk.ncl.pet5o.esper.codegen.base.CodegenMethodScope;
import eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpression;
import eu.uk.ncl.pet5o.esper.epl.enummethod.codegen.EnumForgeCodegenNames;
import eu.uk.ncl.pet5o.esper.epl.enummethod.codegen.EnumForgeCodegenParams;
import eu.uk.ncl.pet5o.esper.epl.expression.codegen.ExprForgeCodegenSymbol;
import eu.uk.ncl.pet5o.esper.epl.expression.core.ExprEvaluator;
import eu.uk.ncl.pet5o.esper.epl.expression.core.ExprEvaluatorContext;
import eu.uk.ncl.pet5o.esper.util.JavaClassHelper;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

import static eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionBuilder.*;
import static eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionBuilder.constant;
import static eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionBuilder.exprDotMethod;
import static eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionBuilder.localMethod;
import static eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionBuilder.newInstance;
import static eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionBuilder.not;
import static eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionBuilder.ref;
import static eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionBuilder.relational;
import static eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionRelational.CodegenRelational.LE;

public class EnumDistinctEventsForgeEval implements EnumEval {

    private final EnumDistinctEventsForge forge;
    private final ExprEvaluator innerExpression;

    public EnumDistinctEventsForgeEval(EnumDistinctEventsForge forge, ExprEvaluator innerExpression) {
        this.forge = forge;
        this.innerExpression = innerExpression;
    }

    public Object evaluateEnumMethod(EventBean[] eventsLambda, Collection enumcoll, boolean isNewData, ExprEvaluatorContext context) {
        Collection<EventBean> beans = (Collection<EventBean>) enumcoll;
        if (beans.size() <= 1) {
            return beans;
        }

        Map<Comparable, EventBean> distinct = new LinkedHashMap<Comparable, EventBean>();
        for (EventBean next : beans) {
            eventsLambda[forge.streamNumLambda] = next;

            Comparable comparable = (Comparable) innerExpression.evaluate(eventsLambda, isNewData, context);
            if (!distinct.containsKey(comparable)) {
                distinct.put(comparable, next);
            }
        }

        return distinct.values();
    }

    public static CodegenExpression codegen(EnumDistinctEventsForge forge, EnumForgeCodegenParams args, CodegenMethodScope codegenMethodScope, CodegenClassScope codegenClassScope) {
        Class innerType = JavaClassHelper.getBoxedType(forge.innerExpression.getEvaluationType());

        ExprForgeCodegenSymbol scope = new ExprForgeCodegenSymbol(false, null);
        CodegenMethodNode methodNode = codegenMethodScope.makeChildWithScope(Collection.class, EnumDistinctEventsForgeEval.class, scope, codegenClassScope).addParam(EnumForgeCodegenNames.PARAMS);

        CodegenBlock block = methodNode.getBlock()
                .ifCondition(relational(exprDotMethod(EnumForgeCodegenNames.REF_ENUMCOLL, "size"), LE, constant(1)))
                .blockReturn(EnumForgeCodegenNames.REF_ENUMCOLL)
                .declareVar(Map.class, "distinct", newInstance(LinkedHashMap.class));
        block.forEach(EventBean.class, "next", EnumForgeCodegenNames.REF_ENUMCOLL)
                .assignArrayElement(EnumForgeCodegenNames.REF_EPS, constant(forge.streamNumLambda), ref("next"))
                .declareVar(innerType, "comparable", forge.innerExpression.evaluateCodegen(innerType, methodNode, scope, codegenClassScope))
                .ifCondition(not(exprDotMethod(ref("distinct"), "containsKey", ref("comparable"))))
                .expression(exprDotMethod(ref("distinct"), "put", ref("comparable"), ref("next")))
                .blockEnd();
        block.methodReturn(exprDotMethod(ref("distinct"), "values"));
        return localMethod(methodNode, args.getEps(), args.getEnumcoll(), args.getIsNewData(), args.getExprCtx());
    }
}
