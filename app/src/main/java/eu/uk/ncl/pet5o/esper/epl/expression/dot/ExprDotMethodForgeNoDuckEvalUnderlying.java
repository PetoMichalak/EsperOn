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

import eu.uk.ncl.pet5o.esper.codegen.base.CodegenClassScope;
import eu.uk.ncl.pet5o.esper.codegen.base.CodegenMethodNode;
import eu.uk.ncl.pet5o.esper.codegen.base.CodegenMethodScope;
import eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpression;
import eu.uk.ncl.pet5o.esper.epl.expression.codegen.ExprForgeCodegenSymbol;
import eu.uk.ncl.pet5o.esper.epl.expression.core.ExprEvaluator;
import eu.uk.ncl.pet5o.esper.epl.expression.core.ExprEvaluatorContext;
import eu.uk.ncl.pet5o.esper.util.JavaClassHelper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionBuilder.*;
import static eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionBuilder.cast;
import static eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionBuilder.exprDotMethod;
import static eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionBuilder.ref;

public class ExprDotMethodForgeNoDuckEvalUnderlying extends ExprDotMethodForgeNoDuckEvalPlain {
    private static final Logger log = LoggerFactory.getLogger(ExprDotMethodForgeNoDuckEvalUnderlying.class);

    public ExprDotMethodForgeNoDuckEvalUnderlying(ExprDotMethodForgeNoDuck forge, ExprEvaluator[] parameters) {
        super(forge, parameters);
    }

    public Object evaluate(Object target, eu.uk.ncl.pet5o.esper.client.EventBean[] eventsPerStream, boolean isNewData, ExprEvaluatorContext exprEvaluatorContext) {
        if (target == null) {
            return null;
        }
        if (!(target instanceof eu.uk.ncl.pet5o.esper.client.EventBean)) {
            log.warn("Expected EventBean return value but received '" + target.getClass().getName() + "' for statement " + forge.getStatementName());
            return null;
        }
        eu.uk.ncl.pet5o.esper.client.EventBean bean = (eu.uk.ncl.pet5o.esper.client.EventBean) target;
        return super.evaluate(bean.getUnderlying(), eventsPerStream, isNewData, exprEvaluatorContext);
    }

    public static CodegenExpression codegenUnderlying(ExprDotMethodForgeNoDuck forge, CodegenExpression inner, Class innerType, CodegenMethodScope codegenMethodScope, ExprForgeCodegenSymbol exprSymbol, CodegenClassScope codegenClassScope) {
        Class underlyingType = forge.getMethod().getDeclaringClass();
        CodegenMethodNode methodNode = codegenMethodScope.makeChild(JavaClassHelper.getBoxedType(forge.getMethod().getReturnType()), ExprDotMethodForgeNoDuckEvalUnderlying.class, codegenClassScope).addParam(eu.uk.ncl.pet5o.esper.client.EventBean.class, "target");


        methodNode.getBlock()
                .ifRefNullReturnNull("target")
                .declareVar(underlyingType, "underlying", cast(underlyingType, exprDotMethod(ref("target"), "getUnderlying")))
                .methodReturn(ExprDotMethodForgeNoDuckEvalPlain.codegenPlain(forge, ref("underlying"), innerType, methodNode, exprSymbol, codegenClassScope));
        return localMethod(methodNode, inner);
    }
}
