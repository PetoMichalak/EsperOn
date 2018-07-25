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

import eu.uk.ncl.pet5o.esper.client.EventType;
import eu.uk.ncl.pet5o.esper.codegen.base.CodegenClassScope;
import eu.uk.ncl.pet5o.esper.codegen.base.CodegenMember;
import eu.uk.ncl.pet5o.esper.codegen.base.CodegenMethodNode;
import eu.uk.ncl.pet5o.esper.codegen.base.CodegenMethodScope;
import eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionRef;
import eu.uk.ncl.pet5o.esper.epl.core.engineimport.EngineImportService;
import eu.uk.ncl.pet5o.esper.epl.core.select.SelectExprProcessor;
import eu.uk.ncl.pet5o.esper.epl.core.select.SelectExprProcessorCodegenSymbol;
import eu.uk.ncl.pet5o.esper.epl.core.select.SelectExprProcessorForge;
import eu.uk.ncl.pet5o.esper.epl.expression.codegen.ExprForgeCodegenSymbol;
import eu.uk.ncl.pet5o.esper.epl.expression.core.ExprEvaluatorContext;
import eu.uk.ncl.pet5o.esper.epl.expression.core.ExprValidationException;
import eu.uk.ncl.pet5o.esper.event.EventAdapterService;
import eu.uk.ncl.pet5o.esper.event.WrapperEventType;
import eu.uk.ncl.pet5o.esper.util.JavaClassHelper;

import java.util.Collections;

import static eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionBuilder.*;
import static eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionBuilder.arrayAtIndex;
import static eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionBuilder.constant;
import static eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionBuilder.exprDotMethod;
import static eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionBuilder.exprDotUnderlying;
import static eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionBuilder.member;
import static eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionBuilder.ref;
import static eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionBuilder.staticMethod;

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

    public eu.uk.ncl.pet5o.esper.client.EventBean process(eu.uk.ncl.pet5o.esper.client.EventBean[] eventsPerStream, boolean isNewData, boolean isSynthesize, ExprEvaluatorContext exprEvaluatorContext) {
        eu.uk.ncl.pet5o.esper.client.EventBean theEvent = eventsPerStream[streamNumber];
        eu.uk.ncl.pet5o.esper.client.EventBean recast = eventAdapterService.adapterForTypedBean(theEvent.getUnderlying(), eventType.getUnderlyingEventType());
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
        CodegenMethodNode methodNode = codegenMethodScope.makeChild(eu.uk.ncl.pet5o.esper.client.EventBean.class, this.getClass(), codegenClassScope);
        CodegenExpressionRef refEPS = exprSymbol.getAddEPS(methodNode);
        methodNode.getBlock()
                .declareVar(eu.uk.ncl.pet5o.esper.client.EventBean.class, "theEvent", arrayAtIndex(refEPS, constant(streamNumber)))
                .declareVar(eu.uk.ncl.pet5o.esper.client.EventBean.class, "recast", exprDotMethod(member(memberEventAdapterService.getMemberId()), "adapterForTypedBean", exprDotUnderlying(ref("theEvent")), member(memberUnderlyingType.getMemberId())))
                .methodReturn(exprDotMethod(member(memberEventAdapterService.getMemberId()), "adapterForTypedWrapper", ref("recast"), staticMethod(Collections.class, "emptyMap"), member(memberResultEventType.getMemberId())));
        return methodNode;
    }
}
