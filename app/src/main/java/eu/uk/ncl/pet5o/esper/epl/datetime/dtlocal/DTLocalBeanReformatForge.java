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
package eu.uk.ncl.pet5o.esper.epl.datetime.dtlocal;

import eu.uk.ncl.pet5o.esper.client.EventBean;
import eu.uk.ncl.pet5o.esper.codegen.base.CodegenBlock;
import eu.uk.ncl.pet5o.esper.codegen.base.CodegenClassScope;
import eu.uk.ncl.pet5o.esper.codegen.base.CodegenMethodNode;
import eu.uk.ncl.pet5o.esper.codegen.base.CodegenMethodScope;
import eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpression;
import eu.uk.ncl.pet5o.esper.epl.expression.codegen.ExprForgeCodegenSymbol;
import eu.uk.ncl.pet5o.esper.event.EventPropertyGetterSPI;

import static eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionBuilder.localMethod;
import static eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionBuilder.ref;

public class DTLocalBeanReformatForge implements DTLocalForge {
    private final EventPropertyGetterSPI getter;
    private final Class getterResultType;
    private final DTLocalForge inner;
    private final Class returnType;

    public DTLocalBeanReformatForge(EventPropertyGetterSPI getter, Class getterResultType, DTLocalForge inner, Class returnType) {
        this.getter = getter;
        this.getterResultType = getterResultType;
        this.inner = inner;
        this.returnType = returnType;
    }

    public DTLocalEvaluator getDTEvaluator() {
        return new DTLocalBeanReformatEval(getter, inner.getDTEvaluator());
    }

    public CodegenExpression codegen(CodegenExpression target, Class targetType, CodegenMethodScope codegenMethodScope, ExprForgeCodegenSymbol exprSymbol, CodegenClassScope codegenClassScope) {
        CodegenMethodNode methodNode = codegenMethodScope.makeChild(returnType, DTLocalBeanReformatForge.class, codegenClassScope).addParam(EventBean.class, "target");

        CodegenBlock block = methodNode.getBlock()
                .declareVar(getterResultType, "timestamp", getter.eventBeanGetCodegen(ref("target"), methodNode, codegenClassScope));
        if (!getterResultType.isPrimitive()) {
            block.ifRefNullReturnNull("timestamp");
        }
        block.methodReturn(inner.codegen(ref("timestamp"), getterResultType, methodNode, exprSymbol, codegenClassScope));
        return localMethod(methodNode, target);
    }
}
