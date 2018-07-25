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

import eu.uk.ncl.pet5o.esper.codegen.base.*;
import eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpression;
import eu.uk.ncl.pet5o.esper.codegen.model.statement.CodegenStatementTryCatch;
import eu.uk.ncl.pet5o.esper.epl.expression.codegen.ExprForgeCodegenSymbol;
import eu.uk.ncl.pet5o.esper.epl.expression.core.ExprEvaluator;
import eu.uk.ncl.pet5o.esper.epl.expression.core.ExprEvaluatorContext;
import eu.uk.ncl.pet5o.esper.epl.rettype.EPType;
import eu.uk.ncl.pet5o.esper.util.JavaClassHelper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import static eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionBuilder.*;
import static eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionBuilder.constant;
import static eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionBuilder.constantNull;
import static eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionBuilder.exprDotMethodChain;
import static eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionBuilder.member;
import static eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionBuilder.newArrayByLength;
import static eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionBuilder.ref;

public class ExprDotMethodForgeNoDuckEvalPlain implements ExprDotEval {
    private static final Logger log = LoggerFactory.getLogger(ExprDotMethodForgeNoDuckEvalPlain.class);

    public final static String METHOD_HANDLETARGETEXCEPTION = "handleTargetException";

    protected final ExprDotMethodForgeNoDuck forge;
    private final ExprEvaluator[] parameters;

    ExprDotMethodForgeNoDuckEvalPlain(ExprDotMethodForgeNoDuck forge, ExprEvaluator[] parameters) {
        this.forge = forge;
        this.parameters = parameters;
    }

    public Object evaluate(Object target, eu.uk.ncl.pet5o.esper.client.EventBean[] eventsPerStream, boolean isNewData, ExprEvaluatorContext exprEvaluatorContext) {
        if (target == null) {
            return null;
        }

        Object[] args = new Object[parameters.length];
        for (int i = 0; i < args.length; i++) {
            args[i] = parameters[i].evaluate(eventsPerStream, isNewData, exprEvaluatorContext);
        }

        try {
            return forge.getMethod().invoke(target, args);
        } catch (InvocationTargetException e) {
            handleTargetException(forge.getStatementName(), forge.getMethod().getJavaMethod(), target.getClass().getName(), args, e.getTargetException());
        }
        return null;
    }

    public EPType getTypeInfo() {
        return forge.getTypeInfo();
    }

    public ExprDotForge getDotForge() {
        return forge;
    }

    public static CodegenExpression codegenPlain(ExprDotMethodForgeNoDuck forge, CodegenExpression inner, Class innerType, CodegenMethodScope codegenMethodScope, ExprForgeCodegenSymbol exprSymbol, CodegenClassScope codegenClassScope) {
        Class returnType = JavaClassHelper.getBoxedType(forge.getMethod().getReturnType());
        CodegenMember methodMember = codegenClassScope.makeAddMember(Method.class, forge.getMethod().getJavaMethod());
        CodegenMethodNode methodNode = codegenMethodScope.makeChild(returnType, ExprDotMethodForgeNoDuckEvalPlain.class, codegenClassScope).addParam(forge.getMethod().getDeclaringClass(), "target");

        CodegenBlock block = methodNode.getBlock();

        if (!innerType.isPrimitive() && returnType != void.class) {
            block.ifRefNullReturnNull("target");
        }
        CodegenExpression[] args = new CodegenExpression[forge.getParameters().length];
        for (int i = 0; i < forge.getParameters().length; i++) {
            String name = "p" + i;
            Class evaluationType = forge.getParameters()[i].getEvaluationType();
            block.declareVar(evaluationType, name, forge.getParameters()[i].evaluateCodegen(evaluationType, methodNode, exprSymbol, codegenClassScope));
            args[i] = ref(name);
        }
        CodegenBlock tryBlock = block.tryCatch();
        CodegenExpression invocation = exprDotMethod(ref("target"), forge.getMethod().getName(), args);
        CodegenStatementTryCatch tryCatch;
        if (returnType == void.class) {
            tryCatch = tryBlock.expression(invocation).tryEnd();
        } else {
            tryCatch = tryBlock.tryReturn(invocation);
        }
        CodegenBlock catchBlock = tryCatch.addCatch(Throwable.class, "t");
        catchBlock.declareVar(Object[].class, "args", newArrayByLength(Object.class, constant(forge.getParameters().length)));
        for (int i = 0; i < forge.getParameters().length; i++) {
            catchBlock.assignArrayElement("args", constant(i), args[i]);
        }
        catchBlock.staticMethod(ExprDotMethodForgeNoDuckEvalPlain.class, METHOD_HANDLETARGETEXCEPTION, constant(forge.getStatementName()), member(methodMember.getMemberId()),
                exprDotMethodChain(ref("target")).add("getClass").add("getName"), ref("args"), ref("t"));
        if (returnType == void.class) {
            block.methodEnd();
        } else {
            block.methodReturn(constantNull());
        }
        return localMethod(methodNode, inner);
    }

    /**
     * NOTE: Code-generation-invoked method, method name and parameter order matters
     *
     * @param statementName   name
     * @param method          method
     * @param targetClassName target class name
     * @param args            args
     * @param t               throwable
     */
    public static void handleTargetException(String statementName, Method method, String targetClassName, Object[] args, Throwable t) {
        String message = JavaClassHelper.getMessageInvocationTarget(statementName, method, targetClassName, args, t);
        log.error(message, t);
    }
}
