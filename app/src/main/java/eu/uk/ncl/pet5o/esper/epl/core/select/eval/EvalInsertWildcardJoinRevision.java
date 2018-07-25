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
import eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionBuilder;
import eu.uk.ncl.pet5o.esper.epl.core.engineimport.EngineImportService;
import eu.uk.ncl.pet5o.esper.epl.core.select.SelectExprProcessor;
import eu.uk.ncl.pet5o.esper.epl.core.select.SelectExprProcessorCodegenSymbol;
import eu.uk.ncl.pet5o.esper.epl.core.select.SelectExprProcessorForge;
import eu.uk.ncl.pet5o.esper.epl.expression.codegen.ExprForgeCodegenSymbol;
import eu.uk.ncl.pet5o.esper.epl.expression.core.ExprEvaluatorContext;
import eu.uk.ncl.pet5o.esper.event.vaevent.ValueAddEventProcessor;

import static eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionBuilder.exprDotMethod;
import static eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionBuilder.localMethod;

public class EvalInsertWildcardJoinRevision extends EvalBase implements SelectExprProcessor, SelectExprProcessorForge {

    private final SelectExprProcessorForge joinWildcardProcessorForge;
    private final ValueAddEventProcessor vaeProcessor;
    private SelectExprProcessor joinWildcardProcessor;

    public EvalInsertWildcardJoinRevision(SelectExprForgeContext selectExprForgeContext, EventType resultEventType, SelectExprProcessorForge joinWildcardProcessorForge, ValueAddEventProcessor vaeProcessor) {
        super(selectExprForgeContext, resultEventType);
        this.joinWildcardProcessorForge = joinWildcardProcessorForge;
        this.vaeProcessor = vaeProcessor;
    }

    public SelectExprProcessor getSelectExprProcessor(EngineImportService engineImportService, boolean isFireAndForget, String statementName) {
        joinWildcardProcessor = joinWildcardProcessorForge.getSelectExprProcessor(engineImportService, isFireAndForget, statementName);
        return this;
    }

    public eu.uk.ncl.pet5o.esper.client.EventBean process(eu.uk.ncl.pet5o.esper.client.EventBean[] eventsPerStream, boolean isNewData, boolean isSynthesize, ExprEvaluatorContext exprEvaluatorContext) {
        eu.uk.ncl.pet5o.esper.client.EventBean theEvent = joinWildcardProcessor.process(eventsPerStream, isNewData, isSynthesize, exprEvaluatorContext);
        return vaeProcessor.getValueAddEventBean(theEvent);
    }

    public CodegenMethodNode processCodegen(CodegenMember memberResultEventType, CodegenMember memberEventAdapterService, CodegenMethodScope codegenMethodScope, SelectExprProcessorCodegenSymbol selectSymbol, ExprForgeCodegenSymbol exprSymbol, CodegenClassScope codegenClassScope) {
        CodegenMember processor = codegenClassScope.makeAddMember(ValueAddEventProcessor.class, vaeProcessor);
        CodegenMethodNode methodNode = codegenMethodScope.makeChild(eu.uk.ncl.pet5o.esper.client.EventBean.class, this.getClass(), codegenClassScope);
        CodegenMethodNode jw = joinWildcardProcessorForge.processCodegen(memberResultEventType, memberEventAdapterService, methodNode, selectSymbol, exprSymbol, codegenClassScope);
        methodNode.getBlock().methodReturn(exprDotMethod(CodegenExpressionBuilder.member(processor.getMemberId()), "getValueAddEventBean", localMethod(jw)));
        return methodNode;
    }
}
