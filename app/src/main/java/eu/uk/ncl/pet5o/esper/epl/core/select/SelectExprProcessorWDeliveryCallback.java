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

import eu.uk.ncl.pet5o.esper.client.EventType;
import eu.uk.ncl.pet5o.esper.codegen.base.CodegenClassScope;
import eu.uk.ncl.pet5o.esper.codegen.base.CodegenMember;
import eu.uk.ncl.pet5o.esper.codegen.base.CodegenMethodNode;
import eu.uk.ncl.pet5o.esper.codegen.base.CodegenMethodScope;
import eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionBuilder;
import eu.uk.ncl.pet5o.esper.epl.core.engineimport.EngineImportService;
import eu.uk.ncl.pet5o.esper.epl.expression.codegen.ExprForgeCodegenSymbol;
import eu.uk.ncl.pet5o.esper.epl.expression.core.ExprEvaluatorContext;

import static eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionBuilder.exprDotMethod;
import static eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionBuilder.localMethod;

/**
 * Interface for processors of select-clause items, implementors are computing results based on matching events.
 */
public class SelectExprProcessorWDeliveryCallback implements SelectExprProcessor, SelectExprProcessorForge {
    private final EventType eventType;
    private final BindProcessorForge bindProcessorForge;
    private final SelectExprProcessorDeliveryCallback selectExprProcessorCallback;
    private BindProcessor bindProcessor;

    public SelectExprProcessorWDeliveryCallback(EventType eventType, BindProcessorForge bindProcessorForge, SelectExprProcessorDeliveryCallback selectExprProcessorCallback) {
        this.eventType = eventType;
        this.bindProcessorForge = bindProcessorForge;
        this.selectExprProcessorCallback = selectExprProcessorCallback;
    }

    public EventType getResultEventType() {
        return eventType;
    }

    public eu.uk.ncl.pet5o.esper.client.EventBean process(eu.uk.ncl.pet5o.esper.client.EventBean[] eventsPerStream, boolean isNewData, boolean isSynthesize, ExprEvaluatorContext exprEvaluatorContext) {
        Object[] columns = bindProcessor.process(eventsPerStream, isNewData, exprEvaluatorContext);
        return selectExprProcessorCallback.selected(columns);
    }

    public SelectExprProcessor getSelectExprProcessor(EngineImportService engineImportService, boolean isFireAndForget, String statementName) {
        if (bindProcessor == null) {
            bindProcessor = bindProcessorForge.getBindProcessor(engineImportService, isFireAndForget, statementName);
        }
        return this;
    }

    public CodegenMethodNode processCodegen(CodegenMember memberResultEventType, CodegenMember memberEventAdapterService, CodegenMethodScope codegenMethodScope, SelectExprProcessorCodegenSymbol selectSymbol, ExprForgeCodegenSymbol exprSymbol, CodegenClassScope codegenClassScope) {
        CodegenMember memberCallback = codegenClassScope.makeAddMember(SelectExprProcessorDeliveryCallback.class, selectExprProcessorCallback);
        CodegenMethodNode methodNode = codegenMethodScope.makeChild(eu.uk.ncl.pet5o.esper.client.EventBean.class, this.getClass(), codegenClassScope);
        CodegenMethodNode bindMethod = bindProcessorForge.processCodegen(methodNode, exprSymbol, codegenClassScope);
        methodNode.getBlock().methodReturn(exprDotMethod(CodegenExpressionBuilder.member(memberCallback.getMemberId()), "selected", localMethod(bindMethod)));
        return methodNode;
    }
}
