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
package eu.uk.ncl.pet5o.esper.epl.expression.core;

import com.espertech.esper.codegen.base.CodegenClassScope;
import com.espertech.esper.codegen.base.CodegenMethodNode;
import com.espertech.esper.codegen.base.CodegenMethodScope;
import com.espertech.esper.codegen.model.expression.CodegenExpression;
import com.espertech.esper.codegen.model.expression.CodegenExpressionRef;
import com.espertech.esper.epl.expression.codegen.CodegenLegoCast;
import com.espertech.esper.epl.expression.codegen.ExprForgeCodegenSymbol;
import com.espertech.esper.event.EventPropertyGetterSPI;

import static com.espertech.esper.codegen.model.expression.CodegenExpressionBuilder.*;
import static com.espertech.esper.codegen.model.expression.CodegenExpressionBuilder.constantNull;
import static com.espertech.esper.codegen.model.expression.CodegenExpressionBuilder.exprDotMethod;
import static com.espertech.esper.codegen.model.expression.CodegenExpressionBuilder.localMethod;
import static com.espertech.esper.codegen.model.expression.CodegenExpressionBuilder.notEqualsNull;

public class ExprIdentNodeEvaluatorContext implements ExprIdentNodeEvaluator {

    private final int streamNum;
    private final Class resultType;
    private final EventPropertyGetterSPI getter;

    public ExprIdentNodeEvaluatorContext(int streamNum, Class resultType, EventPropertyGetterSPI getter) {
        this.streamNum = streamNum;
        this.resultType = resultType;
        this.getter = getter;
    }

    public boolean evaluatePropertyExists(com.espertech.esper.client.EventBean[] eventsPerStream, boolean isNewData) {
        return true;
    }

    public int getStreamNum() {
        return streamNum;
    }

    public Object evaluate(com.espertech.esper.client.EventBean[] eventsPerStream, boolean isNewData, ExprEvaluatorContext context) {
        if (context.getContextProperties() != null) {
            return getter.get(context.getContextProperties());
        }
        return null;
    }

    public CodegenExpression codegen(Class requiredType, CodegenMethodScope codegenMethodScope, ExprForgeCodegenSymbol exprSymbol, CodegenClassScope codegenClassScope) {
        CodegenMethodNode methodNode = codegenMethodScope.makeChild(resultType, this.getClass(), codegenClassScope);
        CodegenExpressionRef refExprEvalCtx = exprSymbol.getAddExprEvalCtx(methodNode);

        methodNode.getBlock()
                .ifCondition(notEqualsNull(refExprEvalCtx))
                .blockReturn(CodegenLegoCast.castSafeFromObjectType(resultType, getter.eventBeanGetCodegen(exprDotMethod(refExprEvalCtx, "getContextProperties"), methodNode, codegenClassScope)))
                .methodReturn(constantNull());
        return localMethod(methodNode);
    }

    public Class getEvaluationType() {
        return resultType;
    }

    public EventPropertyGetterSPI getGetter() {
        return getter;
    }

    public boolean isContextEvaluated() {
        return true;
    }

}
