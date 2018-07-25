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
package eu.uk.ncl.pet5o.esper.epl.core.select.eval;

import com.espertech.esper.client.EventType;
import com.espertech.esper.codegen.base.CodegenClassScope;
import com.espertech.esper.codegen.base.CodegenMember;
import com.espertech.esper.codegen.base.CodegenMethodNode;
import com.espertech.esper.codegen.base.CodegenMethodScope;
import com.espertech.esper.codegen.model.expression.CodegenExpressionRef;
import com.espertech.esper.epl.core.engineimport.EngineImportService;
import com.espertech.esper.epl.core.select.SelectExprProcessor;
import com.espertech.esper.epl.core.select.SelectExprProcessorCodegenSymbol;
import com.espertech.esper.epl.core.select.SelectExprProcessorForge;
import com.espertech.esper.epl.expression.codegen.ExprForgeCodegenSymbol;
import com.espertech.esper.epl.expression.core.ExprEvaluatorContext;
import com.espertech.esper.epl.expression.core.ExprValidationException;
import com.espertech.esper.event.EventAdapterService;
import com.espertech.esper.event.WrapperEventType;
import com.espertech.esper.util.JavaClassHelper;

import java.util.Collections;

import static com.espertech.esper.codegen.model.expression.CodegenExpressionBuilder.*;
import static com.espertech.esper.codegen.model.expression.CodegenExpressionBuilder.arrayAtIndex;
import static com.espertech.esper.codegen.model.expression.CodegenExpressionBuilder.constant;
import static com.espertech.esper.codegen.model.expression.CodegenExpressionBuilder.exprDotMethod;
import static com.espertech.esper.codegen.model.expression.CodegenExpressionBuilder.exprDotUnderlying;
import static com.espertech.esper.codegen.model.expression.CodegenExpressionBuilder.member;
import static com.espertech.esper.codegen.model.expression.CodegenExpressionBuilder.ref;
import static com.espertech.esper.codegen.model.expression.CodegenExpressionBuilder.staticMethod;

public class EvalInsertBeanWrapRecast implements SelectExprProcessor, SelectExprProcessorForge {

    private final WrapperEventType eventType;
    private final EventAdapterService eventAdapterService;
    private final int streamNumber;

    public EvalInsertBeanWrapRecast(WrapperEventType targetType, EventAdapterService eventAdapterService, int streamNumber, EventType[] typesPerStream)
            throws ExprValidationException {
        this.eventType = targetType;
        this.eventAdapterService = eventAdapterService;
        this.streamNumber = streamNumber;

        EventType sourceType = typesPerStream[streamNumber];
        Class sourceClass = sourceType.getUnderlyingType();
        Class targetClass = targetType.getUnderlyingEventType().getUnderlyingType();
        if (!JavaClassHelper.isSubclassOrImplementsInterface(sourceClass, targetClass)) {
            throw EvalInsertUtil.makeEventTypeCastException(sourceType, targetType);
        }
    }

    public com.espertech.esper.client.EventBean process(com.espertech.esper.client.EventBean[] eventsPerStream, boolean isNewData, boolean isSynthesize, ExprEvaluatorContext exprEvaluatorContext) {
        com.espertech.esper.client.EventBean theEvent = eventsPerStream[streamNumber];
        com.espertech.esper.client.EventBean recast = eventAdapterService.adapterForTypedBean(theEvent.getUnderlying(), eventType.getUnderlyingEventType());
        return eventAdapterService.adapterForTypedWrapper(recast, Collections.<String, Object>emptyMap(), eventType);
    }

    public EventType getResultEventType() {
        return eventType;
    }

    public SelectExprProcessor getSelectExprProcessor(EngineImportService engineImportService, boolean isFireAndForget, String statementName) {
        return this;
    }

    public CodegenMethodNode processCodegen(CodegenMember memberResultEventType, CodegenMember memberEventAdapterService, CodegenMethodScope codegenMethodScope, SelectExprProcessorCodegenSymbol selectSymbol, ExprForgeCodegenSymbol exprSymbol, CodegenClassScope codegenClassScope) {
        CodegenMember memberUnderlyingType = codegenClassScope.makeAddMember(EventType.class, eventType.getUnderlyingEventType());
        CodegenMethodNode methodNode = codegenMethodScope.makeChild(com.espertech.esper.client.EventBean.class, this.getClass(), codegenClassScope);
        CodegenExpressionRef refEPS = exprSymbol.getAddEPS(methodNode);
        methodNode.getBlock()
                .declareVar(com.espertech.esper.client.EventBean.class, "theEvent", arrayAtIndex(refEPS, constant(streamNumber)))
                .declareVar(com.espertech.esper.client.EventBean.class, "recast", exprDotMethod(member(memberEventAdapterService.getMemberId()), "adapterForTypedBean", exprDotUnderlying(ref("theEvent")), member(memberUnderlyingType.getMemberId())))
                .methodReturn(exprDotMethod(member(memberEventAdapterService.getMemberId()), "adapterForTypedWrapper", ref("recast"), staticMethod(Collections.class, "emptyMap"), member(memberResultEventType.getMemberId())));
        return methodNode;
    }
}
