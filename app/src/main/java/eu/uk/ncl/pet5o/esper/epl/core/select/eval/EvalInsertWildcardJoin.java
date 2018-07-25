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
import eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionBuilder;
import eu.uk.ncl.pet5o.esper.epl.core.engineimport.EngineImportService;
import eu.uk.ncl.pet5o.esper.epl.core.select.SelectExprProcessor;
import eu.uk.ncl.pet5o.esper.epl.core.select.SelectExprProcessorCodegenSymbol;
import eu.uk.ncl.pet5o.esper.epl.core.select.SelectExprProcessorForge;
import eu.uk.ncl.pet5o.esper.epl.expression.codegen.ExprForgeCodegenSymbol;
import eu.uk.ncl.pet5o.esper.epl.expression.core.ExprEvaluatorContext;

import java.util.Map;

import static eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionBuilder.exprDotMethod;
import static eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionBuilder.localMethod;

public class EvalInsertWildcardJoin extends EvalBaseMap implements SelectExprProcessor {

    private final SelectExprProcessorForge joinWildcardProcessorForge;
    private SelectExprProcessor joinWildcardProcessor;

    public EvalInsertWildcardJoin(SelectExprForgeContext selectExprForgeContext, EventType resultEventType, SelectExprProcessorForge joinWildcardProcessorForge) {
        super(selectExprForgeContext, resultEventType);
        this.joinWildcardProcessorForge = joinWildcardProcessorForge;
    }

    public eu.uk.ncl.pet5o.esper.client.EventBean processSpecific(Map<String, Object> props, eu.uk.ncl.pet5o.esper.client.EventBean[] eventsPerStream, boolean isNewData, boolean isSynthesize, ExprEvaluatorContext exprEvaluatorContext) {
        eu.uk.ncl.pet5o.esper.client.EventBean theEvent = joinWildcardProcessor.process(eventsPerStream, isNewData, isSynthesize, exprEvaluatorContext);
        // Using a wrapper bean since we cannot use the same event type else same-type filters match.
        // Wrapping it even when not adding properties is very inexpensive.
        return super.getEventAdapterService().adapterForTypedWrapper(theEvent, props, super.getResultEventType());
    }

    protected void initSelectExprProcessorSpecific(EngineImportService engineImportService, boolean isFireAndForget, String statementName) {
        joinWildcardProcessor = joinWildcardProcessorForge.getSelectExprProcessor(engineImportService, isFireAndForget, statementName);
    }

    protected CodegenExpression processSpecificCodegen(CodegenMember memberResultEventType, CodegenMember memberEventAdapterService, CodegenExpression props, CodegenMethodNode methodNode, SelectExprProcessorCodegenSymbol selectEnv, ExprForgeCodegenSymbol exprSymbol, CodegenClassScope codegenClassScope) {
        CodegenMethodNode inner = joinWildcardProcessorForge.processCodegen(memberResultEventType, memberEventAdapterService, methodNode, selectEnv, exprSymbol, codegenClassScope);
        return exprDotMethod(CodegenExpressionBuilder.member(memberEventAdapterService.getMemberId()), "adapterForTypedWrapper", localMethod(inner), props, CodegenExpressionBuilder.member(memberResultEventType.getMemberId()));
    }
}
