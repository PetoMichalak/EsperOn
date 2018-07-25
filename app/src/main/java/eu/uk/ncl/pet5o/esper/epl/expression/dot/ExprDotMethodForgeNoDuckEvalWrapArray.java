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
import eu.uk.ncl.pet5o.esper.epl.rettype.EPType;
import eu.uk.ncl.pet5o.esper.epl.rettype.EPTypeHelper;
import eu.uk.ncl.pet5o.esper.util.CollectionUtil;
import eu.uk.ncl.pet5o.esper.util.JavaClassHelper;

import java.util.Collection;

import static eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionBuilder.localMethod;
import static eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionBuilder.ref;

public class ExprDotMethodForgeNoDuckEvalWrapArray extends ExprDotMethodForgeNoDuckEvalPlain {
    public ExprDotMethodForgeNoDuckEvalWrapArray(ExprDotMethodForgeNoDuck forge, ExprEvaluator[] parameters) {
        super(forge, parameters);
    }

    @Override
    public Object evaluate(Object target, eu.uk.ncl.pet5o.esper.client.EventBean[] eventsPerStream, boolean isNewData, ExprEvaluatorContext exprEvaluatorContext) {
        Object result = super.evaluate(target, eventsPerStream, isNewData, exprEvaluatorContext);
        if (result == null || !result.getClass().isArray()) {
            return null;
        }
        return CollectionUtil.arrayToCollectionAllowNull(result);
    }

    @Override
    public EPType getTypeInfo() {
        return EPTypeHelper.collectionOfSingleValue(forge.getMethod().getReturnType().getComponentType());
    }

    public static CodegenExpression codegenWrapArray(ExprDotMethodForgeNoDuck forge, CodegenExpression inner, Class innerType, CodegenMethodScope codegenMethodScope, ExprForgeCodegenSymbol exprSymbol, CodegenClassScope codegenClassScope) {
        CodegenMethodNode methodNode = codegenMethodScope.makeChild(Collection.class, ExprDotMethodForgeNoDuckEvalWrapArray.class, codegenClassScope).addParam(innerType, "target");

        Class returnType = forge.getMethod().getReturnType();
        methodNode.getBlock()
                .declareVar(JavaClassHelper.getBoxedType(returnType), "array", ExprDotMethodForgeNoDuckEvalPlain.codegenPlain(forge, ref("target"), innerType, methodNode, exprSymbol, codegenClassScope))
                .methodReturn(CollectionUtil.arrayToCollectionAllowNullCodegen(methodNode, returnType, ref("array"), codegenClassScope));
        return localMethod(methodNode, inner);
    }
}
