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
package eu.uk.ncl.pet5o.esper.epl.core.select;

import com.espertech.esper.codegen.base.CodegenClassScope;
import com.espertech.esper.codegen.base.CodegenMethodNode;
import com.espertech.esper.codegen.base.CodegenMethodScope;
import com.espertech.esper.codegen.model.expression.CodegenExpression;
import com.espertech.esper.codegen.model.expression.CodegenExpressionRef;
import com.espertech.esper.epl.expression.codegen.ExprForgeCodegenSymbol;
import com.espertech.esper.epl.expression.core.*;
import com.espertech.esper.metrics.instrumentation.InstrumentationHelper;

import static com.espertech.esper.codegen.model.expression.CodegenExpressionBuilder.*;
import static com.espertech.esper.codegen.model.expression.CodegenExpressionBuilder.arrayAtIndex;
import static com.espertech.esper.codegen.model.expression.CodegenExpressionBuilder.constant;
import static com.espertech.esper.codegen.model.expression.CodegenExpressionBuilder.constantNull;
import static com.espertech.esper.codegen.model.expression.CodegenExpressionBuilder.equalsNull;
import static com.espertech.esper.codegen.model.expression.CodegenExpressionBuilder.localMethod;

public class SelectExprProcessorEvalStreamInsertUnd implements ExprForge, ExprEvaluator {
    private final ExprStreamUnderlyingNode undNode;
    private final int streamNum;
    private final Class returnType;

    public SelectExprProcessorEvalStreamInsertUnd(ExprStreamUnderlyingNode undNode, int streamNum, Class returnType) {
        this.undNode = undNode;
        this.streamNum = streamNum;
        this.returnType = returnType;
    }

    public Object evaluate(com.espertech.esper.client.EventBean[] eventsPerStream, boolean isNewData, ExprEvaluatorContext exprEvaluatorContext) {
        if (InstrumentationHelper.ENABLED) {
            InstrumentationHelper.get().qExprStreamUndSelectClause(undNode);
            com.espertech.esper.client.EventBean event = eventsPerStream == null ? null : eventsPerStream[streamNum];
            InstrumentationHelper.get().aExprStreamUndSelectClause(event);
            return event;
        }
        return eventsPerStream == null ? null : eventsPerStream[streamNum];
    }

    public CodegenExpression evaluateCodegen(Class requiredType, CodegenMethodScope codegenMethodScope, ExprForgeCodegenSymbol exprSymbol, CodegenClassScope codegenClassScope) {
        CodegenMethodNode methodNode = codegenMethodScope.makeChild(com.espertech.esper.client.EventBean.class, SelectExprProcessorEvalStreamInsertUnd.class, codegenClassScope);

        CodegenExpressionRef refEPS = exprSymbol.getAddEPS(methodNode);
        methodNode.getBlock()
                .ifCondition(equalsNull(refEPS)).blockReturn(constantNull())
                .methodReturn(arrayAtIndex(refEPS, constant(streamNum)));
        return localMethod(methodNode);
    }

    public ExprForgeComplexityEnum getComplexity() {
        return ExprForgeComplexityEnum.NONE;
    }

    public int getStreamNum() {
        return streamNum;
    }

    public ExprEvaluator getExprEvaluator() {
        return this;
    }

    public Class getEvaluationType() {
        return com.espertech.esper.client.EventBean.class;
    }

    public Class getUnderlyingReturnType() {
        return returnType;
    }

    public ExprNodeRenderable getForgeRenderable() {
        return undNode;
    }
}
