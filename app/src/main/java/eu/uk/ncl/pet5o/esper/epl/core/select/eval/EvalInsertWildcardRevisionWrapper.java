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
import eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpression;
import eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionRef;
import eu.uk.ncl.pet5o.esper.epl.core.engineimport.EngineImportService;
import eu.uk.ncl.pet5o.esper.epl.core.select.SelectExprProcessor;
import eu.uk.ncl.pet5o.esper.epl.core.select.SelectExprProcessorCodegenSymbol;
import eu.uk.ncl.pet5o.esper.epl.expression.codegen.ExprForgeCodegenSymbol;
import eu.uk.ncl.pet5o.esper.epl.expression.core.ExprEvaluatorContext;
import eu.uk.ncl.pet5o.esper.event.vaevent.ValueAddEventProcessor;

import java.util.Map;

import static eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionBuilder.*;
import static eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionBuilder.arrayAtIndex;
import static eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionBuilder.constant;
import static eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionBuilder.exprDotMethod;
import static eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionBuilder.member;
import static eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionBuilder.ref;

public class EvalInsertWildcardRevisionWrapper extends EvalBaseMap implements SelectExprProcessor {

    private final ValueAddEventProcessor vaeProcessor;
    private final EventType wrappingEventType;

    public EvalInsertWildcardRevisionWrapper(SelectExprForgeContext selectExprForgeContext, EventType resultEventType, ValueAddEventProcessor vaeProcessor, EventType wrappingEventType) {
        super(selectExprForgeContext, resultEventType);
        this.vaeProcessor = vaeProcessor;
        this.wrappingEventType = wrappingEventType;
    }

    protected void initSelectExprProcessorSpecific(EngineImportService engineImportService, boolean isFireAndForget, String statementName) {
    }

    public eu.uk.ncl.pet5o.esper.client.EventBean processSpecific(Map<String, Object> props, eu.uk.ncl.pet5o.esper.client.EventBean[] eventsPerStream, boolean isNewData, boolean isSynthesize, ExprEvaluatorContext exprEvaluatorContext) {
        eu.uk.ncl.pet5o.esper.client.EventBean underlying = eventsPerStream[0];
        eu.uk.ncl.pet5o.esper.client.EventBean wrapped = super.getEventAdapterService().adapterForTypedWrapper(underlying, props, wrappingEventType);
        return vaeProcessor.getValueAddEventBean(wrapped);
    }

    protected CodegenExpression processSpecificCodegen(CodegenMember memberResultEventType, CodegenMember memberEventAdapterService, CodegenExpression props, CodegenMethodNode methodNode, SelectExprProcessorCodegenSymbol selectEnv, ExprForgeCodegenSymbol exprSymbol, CodegenClassScope codegenClassScope) {
        CodegenMember processor = codegenClassScope.makeAddMember(ValueAddEventProcessor.class, vaeProcessor);
        CodegenMember innerType = codegenClassScope.makeAddMember(EventType.class, wrappingEventType);
        CodegenExpressionRef refEPS = exprSymbol.getAddEPS(methodNode);
        CodegenExpression wrapped = exprDotMethod(member(memberEventAdapterService.getMemberId()), "adapterForTypedWrapper", arrayAtIndex(refEPS, constant(0)), ref("props"), member(innerType.getMemberId()));
        return exprDotMethod(member(processor.getMemberId()), "getValueAddEventBean", wrapped);
    }
}
