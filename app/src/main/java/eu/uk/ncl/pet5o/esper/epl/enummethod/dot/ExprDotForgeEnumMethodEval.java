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
package eu.uk.ncl.pet5o.esper.epl.enummethod.dot;

import eu.uk.ncl.pet5o.esper.client.EventBean;
import eu.uk.ncl.pet5o.esper.codegen.base.CodegenBlock;
import eu.uk.ncl.pet5o.esper.codegen.base.CodegenClassScope;
import eu.uk.ncl.pet5o.esper.codegen.base.CodegenMember;
import eu.uk.ncl.pet5o.esper.codegen.base.CodegenMethodNode;
import eu.uk.ncl.pet5o.esper.codegen.base.CodegenMethodScope;
import eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpression;
import eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionRef;
import eu.uk.ncl.pet5o.esper.core.service.ExpressionResultCacheEntryLongArrayAndObj;
import eu.uk.ncl.pet5o.esper.core.service.ExpressionResultCacheForEnumerationMethod;
import eu.uk.ncl.pet5o.esper.epl.enummethod.codegen.EnumForgeCodegenParams;
import eu.uk.ncl.pet5o.esper.epl.enummethod.eval.EnumEval;
import eu.uk.ncl.pet5o.esper.epl.expression.codegen.ExprForgeCodegenSymbol;
import eu.uk.ncl.pet5o.esper.epl.expression.core.ExprEvaluatorContext;
import eu.uk.ncl.pet5o.esper.epl.expression.dot.ExprDotEval;
import eu.uk.ncl.pet5o.esper.epl.expression.dot.ExprDotForge;
import eu.uk.ncl.pet5o.esper.epl.rettype.EPType;
import eu.uk.ncl.pet5o.esper.epl.rettype.EPTypeHelper;
import eu.uk.ncl.pet5o.esper.event.EventBeanUtility;

import java.util.Collection;
import java.util.Collections;
import java.util.concurrent.atomic.AtomicLong;

import static eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionBuilder.*;
import static eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionBuilder.cast;
import static eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionBuilder.constant;
import static eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionBuilder.exprDotMethod;
import static eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionBuilder.exprDotMethodChain;
import static eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionBuilder.member;
import static eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionBuilder.notEqualsNull;
import static eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionBuilder.ref;
import static eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionBuilder.staticMethod;

public class ExprDotForgeEnumMethodEval implements ExprDotEval {

    private final ExprDotForgeEnumMethodBase forge;
    private final EnumEval enumEval;
    private final boolean cache;
    private final int enumEvalNumRequiredEvents;

    private long contextNumber = 0;

    public ExprDotForgeEnumMethodEval(ExprDotForgeEnumMethodBase forge, EnumEval enumEval, boolean cache, int enumEvalNumRequiredEvents) {
        this.forge = forge;
        this.enumEval = enumEval;
        this.cache = cache;
        this.enumEvalNumRequiredEvents = enumEvalNumRequiredEvents;
    }

    public Object evaluate(Object target, EventBean[] eventsPerStream, boolean isNewData, ExprEvaluatorContext exprEvaluatorContext) {
        if (target instanceof EventBean) {
            target = Collections.singletonList((EventBean) target);
        }
        ExpressionResultCacheForEnumerationMethod cache = exprEvaluatorContext.getExpressionResultCacheService().getAllocateEnumerationMethod();
        if (this.cache) {
            ExpressionResultCacheEntryLongArrayAndObj cacheValue = cache.getEnumerationMethodLastValue(this);
            if (cacheValue != null) {
                return cacheValue.getResult();
            }
            Collection coll = (Collection) target;
            if (coll == null) {
                return null;
            }
            EventBean[] eventsLambda = allocateCopyEventLambda(eventsPerStream, enumEvalNumRequiredEvents);
            Object result = enumEval.evaluateEnumMethod(eventsLambda, coll, isNewData, exprEvaluatorContext);
            cache.saveEnumerationMethodLastValue(this, result);
            return result;
        }

        contextNumber++;
        try {
            cache.pushContext(contextNumber);
            Collection coll = (Collection) target;
            if (coll == null) {
                return null;
            }
            EventBean[] eventsLambda = allocateCopyEventLambda(eventsPerStream, enumEvalNumRequiredEvents);
            return enumEval.evaluateEnumMethod(eventsLambda, coll, isNewData, exprEvaluatorContext);
        } finally {
            cache.popContext();
        }
    }

    public static CodegenExpression codegen(ExprDotForgeEnumMethodBase forge, CodegenExpression inner, Class innerType, CodegenMethodScope codegenMethodScope, ExprForgeCodegenSymbol exprSymbol, CodegenClassScope codegenClassScope) {
        Class returnType = EPTypeHelper.getCodegenReturnType(forge.getTypeInfo());
        CodegenMethodNode methodNode = codegenMethodScope.makeChild(returnType, ExprDotForgeEnumMethodEval.class, codegenClassScope).addParam(innerType, "param");

        CodegenExpressionRef refEPS = exprSymbol.getAddEPS(methodNode);
        CodegenExpression refIsNewData = exprSymbol.getAddIsNewData(methodNode);
        CodegenExpressionRef refExprEvalCtx = exprSymbol.getAddExprEvalCtx(methodNode);

        CodegenMember forgeMember = codegenClassScope.makeAddMember(ExprDotForgeEnumMethodBase.class, forge);
        CodegenBlock block = methodNode.getBlock();
        if (innerType == EventBean.class) {
            block.declareVar(Collection.class, "coll", staticMethod(Collections.class, "singletonList", ref("param")));
        } else {
            block.declareVar(Collection.class, "coll", ref("param"));
        }
        block.declareVar(ExpressionResultCacheForEnumerationMethod.class, "cache", exprDotMethodChain(refExprEvalCtx).add("getExpressionResultCacheService").add("getAllocateEnumerationMethod"));
        EnumForgeCodegenParams premade = new EnumForgeCodegenParams(ref("eventsLambda"), ref("coll"), refIsNewData, refExprEvalCtx);
        if (forge.cache) {
            block.declareVar(ExpressionResultCacheEntryLongArrayAndObj.class, "cacheValue", exprDotMethod(ref("cache"), "getEnumerationMethodLastValue", member(forgeMember.getMemberId())))
                    .ifCondition(notEqualsNull(ref("cacheValue")))
                    .blockReturn(cast(returnType, exprDotMethod(ref("cacheValue"), "getResult")))
                    .ifRefNullReturnNull("coll")
                    .declareVar(EventBean[].class, "eventsLambda", staticMethod(ExprDotForgeEnumMethodEval.class, "allocateCopyEventLambda", refEPS, constant(forge.enumEvalNumRequiredEvents)))
                    .declareVar(EPTypeHelper.getCodegenReturnType(forge.getTypeInfo()), "result", forge.enumForge.codegen(premade, methodNode, codegenClassScope))
                    .expression(exprDotMethod(ref("cache"), "saveEnumerationMethodLastValue", member(forgeMember.getMemberId()), ref("result")))
                    .methodReturn(ref("result"));
        } else {
            AtomicLong contextNumber = new AtomicLong();
            CodegenMember contextNumberMember = codegenClassScope.makeAddMember(AtomicLong.class, contextNumber);
            block.declareVar(long.class, "contextNumber", exprDotMethod(member(contextNumberMember.getMemberId()), "getAndIncrement"))
                    .tryCatch()
                    .expression(exprDotMethod(ref("cache"), "pushContext", ref("contextNumber")))
                    .ifRefNullReturnNull("coll")
                    .declareVar(EventBean[].class, "eventsLambda", staticMethod(ExprDotForgeEnumMethodEval.class, "allocateCopyEventLambda", refEPS, constant(forge.enumEvalNumRequiredEvents)))
                    .tryReturn(forge.enumForge.codegen(premade, methodNode, codegenClassScope))
                    .tryFinally()
                    .expression(exprDotMethod(ref("cache"), "popContext"))
                    .blockEnd()
                    .methodEnd();
        }
        return localMethod(methodNode, inner);
    }

    public EPType getTypeInfo() {
        return forge.getTypeInfo();
    }

    public ExprDotForge getDotForge() {
        return forge;
    }

    /**
     * NOTE: Code-generation-invoked method, method name and parameter order matters
     *
     * @param eventsPerStream           events
     * @param enumEvalNumRequiredEvents width
     * @return allocated
     */
    public static EventBean[] allocateCopyEventLambda(EventBean[] eventsPerStream, int enumEvalNumRequiredEvents) {
        EventBean[] eventsLambda = new EventBean[enumEvalNumRequiredEvents];
        EventBeanUtility.safeArrayCopy(eventsPerStream, eventsLambda);
        return eventsLambda;
    }
}
