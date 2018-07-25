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
package eu.uk.ncl.pet5o.esper.epl.expression.funcs;

import eu.uk.ncl.pet5o.esper.codegen.base.CodegenClassScope;
import eu.uk.ncl.pet5o.esper.codegen.base.CodegenMethodNode;
import eu.uk.ncl.pet5o.esper.codegen.base.CodegenMethodScope;
import eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpression;
import eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionRef;
import eu.uk.ncl.pet5o.esper.epl.expression.codegen.ExprForgeCodegenSymbol;
import eu.uk.ncl.pet5o.esper.epl.expression.core.ExprEvaluator;
import eu.uk.ncl.pet5o.esper.epl.expression.core.ExprEvaluatorContext;
import eu.uk.ncl.pet5o.esper.epl.expression.core.ExprForgeComplexityEnum;
import eu.uk.ncl.pet5o.esper.epl.expression.core.ExprNode;
import eu.uk.ncl.pet5o.esper.event.EventPropertyGetterSPI;
import eu.uk.ncl.pet5o.esper.metrics.instrumentation.InstrumentationHelper;

import static eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionBuilder.*;
import static eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionBuilder.arrayAtIndex;
import static eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionBuilder.constant;
import static eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionBuilder.constantNull;
import static eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionBuilder.exprDotMethodChain;
import static eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionBuilder.localMethod;
import static eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionBuilder.ref;

public class ExprTypeofNodeForgeFragmentType extends ExprTypeofNodeForge implements ExprEvaluator {

    private final ExprTypeofNode parent;
    private final int streamId;
    private final EventPropertyGetterSPI getter;
    private final String fragmentType;

    public ExprTypeofNodeForgeFragmentType(ExprTypeofNode parent, int streamId, EventPropertyGetterSPI getter, String fragmentType) {
        this.parent = parent;
        this.streamId = streamId;
        this.getter = getter;
        this.fragmentType = fragmentType;
    }

    public Object evaluate(eu.uk.ncl.pet5o.esper.client.EventBean[] eventsPerStream, boolean isNewData, ExprEvaluatorContext context) {
        if (InstrumentationHelper.ENABLED) {
            InstrumentationHelper.get().qExprTypeof();
        }
        eu.uk.ncl.pet5o.esper.client.EventBean event = eventsPerStream[streamId];
        if (event == null) {
            if (InstrumentationHelper.ENABLED) {
                InstrumentationHelper.get().aExprTypeof(null);
            }
            return null;
        }
        Object fragment = getter.getFragment(event);
        if (fragment == null) {
            if (InstrumentationHelper.ENABLED) {
                InstrumentationHelper.get().aExprTypeof(null);
            }
            return null;
        }
        if (fragment instanceof eu.uk.ncl.pet5o.esper.client.EventBean) {
            eu.uk.ncl.pet5o.esper.client.EventBean bean = (eu.uk.ncl.pet5o.esper.client.EventBean) fragment;
            if (InstrumentationHelper.ENABLED) {
                InstrumentationHelper.get().aExprTypeof(bean.getEventType().getName());
            }
            return bean.getEventType().getName();
        }
        if (fragment.getClass().isArray()) {
            String type = fragmentType + "[]";
            if (InstrumentationHelper.ENABLED) {
                InstrumentationHelper.get().aExprTypeof(type);
            }
            return type;
        }
        if (InstrumentationHelper.ENABLED) {
            InstrumentationHelper.get().aExprTypeof(null);
        }
        return null;
    }

    public CodegenExpression evaluateCodegen(Class requiredType, CodegenMethodScope codegenMethodScope, ExprForgeCodegenSymbol exprSymbol, CodegenClassScope codegenClassScope) {
        CodegenMethodNode methodNode = codegenMethodScope.makeChild(String.class, ExprTypeofNodeForgeFragmentType.class, codegenClassScope);

        CodegenExpressionRef refEPS = exprSymbol.getAddEPS(methodNode);
        methodNode.getBlock()
                .declareVar(eu.uk.ncl.pet5o.esper.client.EventBean.class, "event", arrayAtIndex(refEPS, constant(streamId)))
                .ifRefNullReturnNull("event")
                .declareVar(Object.class, "fragment", getter.eventBeanFragmentCodegen(ref("event"), methodNode, codegenClassScope))
                .ifRefNullReturnNull("fragment")
                .ifInstanceOf("fragment", eu.uk.ncl.pet5o.esper.client.EventBean.class)
                .blockReturn(exprDotMethodChain(cast(eu.uk.ncl.pet5o.esper.client.EventBean.class, ref("fragment"))).add("getEventType").add("getName"))
                .ifCondition(exprDotMethodChain(ref("fragment")).add("getClass").add("isArray"))
                .blockReturn(constant(fragmentType + "[]"))
                .methodReturn(constantNull());
        return localMethod(methodNode);
    }

    public ExprForgeComplexityEnum getComplexity() {
        return ExprForgeComplexityEnum.SINGLE;
    }

    public ExprEvaluator getExprEvaluator() {
        return this;
    }

    public ExprNode getForgeRenderable() {
        return parent;
    }
}
