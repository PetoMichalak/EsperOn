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

import eu.uk.ncl.pet5o.esper.client.hook.EPLMethodInvocationContext;
import eu.uk.ncl.pet5o.esper.client.hook.EventBeanService;
import eu.uk.ncl.pet5o.esper.codegen.base.CodegenClassScope;
import eu.uk.ncl.pet5o.esper.codegen.base.CodegenMember;
import eu.uk.ncl.pet5o.esper.codegen.base.CodegenMethodNode;
import eu.uk.ncl.pet5o.esper.codegen.base.CodegenMethodScope;
import eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpression;
import eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionRef;
import eu.uk.ncl.pet5o.esper.epl.expression.codegen.ExprForgeCodegenSymbol;

import java.io.StringWriter;

import static eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionBuilder.*;
import static eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionBuilder.constant;
import static eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionBuilder.equalsNull;
import static eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionBuilder.exprDotMethod;
import static eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionBuilder.localMethod;
import static eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionBuilder.member;
import static eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionBuilder.newInstance;

public class ExprNodeUtilExprMethodContext implements ExprForge, ExprEvaluator, ExprNodeRenderable {

    private final EPLMethodInvocationContext defaultContextForFilters;

    public ExprNodeUtilExprMethodContext(String engineURI, String functionName, EventBeanService eventBeanService) {
        this.defaultContextForFilters = new EPLMethodInvocationContext(null, -1, engineURI, functionName, null, eventBeanService);
    }

    public Object evaluate(eu.uk.ncl.pet5o.esper.client.EventBean[] eventsPerStream, boolean isNewData, ExprEvaluatorContext context) {
        if (context == null) {
            return defaultContextForFilters;
        }
        return new EPLMethodInvocationContext(context.getStatementName(),
                context.getAgentInstanceId(),
                defaultContextForFilters.getEngineURI(),
                defaultContextForFilters.getFunctionName(),
                context.getStatementUserObject(),
                defaultContextForFilters.getEventBeanService());
    }

    public ExprEvaluator getExprEvaluator() {
        return this;
    }

    public CodegenExpression evaluateCodegen(Class requiredType, CodegenMethodScope codegenMethodScope, ExprForgeCodegenSymbol exprSymbol, CodegenClassScope codegenClassScope) {
        CodegenMethodNode methodNode = codegenMethodScope.makeChild(EPLMethodInvocationContext.class, ExprNodeUtilExprMethodContext.class, codegenClassScope);
        CodegenExpressionRef refExprEvalCtx = exprSymbol.getAddExprEvalCtx(methodNode);

        CodegenExpression stmtName = exprDotMethod(refExprEvalCtx, "getStatementName");
        CodegenExpression cpid = exprDotMethod(refExprEvalCtx, "getAgentInstanceId");
        CodegenExpression engineURI = constant(defaultContextForFilters.getEngineURI());
        CodegenExpression functionName = constant(defaultContextForFilters.getFunctionName());
        CodegenExpression userObject = exprDotMethod(refExprEvalCtx, "getStatementUserObject");
        CodegenMember defaultCtx = codegenClassScope.makeAddMember(EPLMethodInvocationContext.class, defaultContextForFilters);
        methodNode.getBlock()
                .ifCondition(equalsNull(refExprEvalCtx))
                .blockReturn(member(defaultCtx.getMemberId()))
                .methodReturn(newInstance(EPLMethodInvocationContext.class, stmtName, cpid, engineURI, functionName, userObject,
                        exprDotMethod(member(defaultCtx.getMemberId()), "getEventBeanService")));
        return localMethod(methodNode);
    }

    public Class getEvaluationType() {
        return EPLMethodInvocationContext.class;
    }

    public ExprForgeComplexityEnum getComplexity() {
        return ExprForgeComplexityEnum.SINGLE;
    }

    public ExprNodeRenderable getForgeRenderable() {
        return this;
    }

    public void toEPL(StringWriter writer, ExprPrecedenceEnum parentPrecedence) {
        writer.append(ExprNodeUtilExprMethodContext.class.getSimpleName());
    }
}
