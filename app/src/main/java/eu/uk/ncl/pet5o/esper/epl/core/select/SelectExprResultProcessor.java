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
import eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionRef;
import eu.uk.ncl.pet5o.esper.core.service.StatementResultService;
import eu.uk.ncl.pet5o.esper.epl.core.engineimport.EngineImportService;
import eu.uk.ncl.pet5o.esper.epl.expression.codegen.ExprForgeCodegenSymbol;
import eu.uk.ncl.pet5o.esper.epl.expression.core.ExprEvaluatorContext;
import eu.uk.ncl.pet5o.esper.event.NaturalEventBean;
import eu.uk.ncl.pet5o.esper.metrics.instrumentation.InstrumentationHelper;

import static eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionBuilder.*;
import static eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionBuilder.constantNull;
import static eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionBuilder.exprDotMethod;
import static eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionBuilder.localMethod;
import static eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionBuilder.member;
import static eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionBuilder.newInstance;
import static eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionBuilder.not;
import static eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionBuilder.or;
import static eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionBuilder.ref;

/**
 * A select expression processor that check what type of result (synthetic and natural) event is expected and
 * produces.
 */
public class SelectExprResultProcessor implements SelectExprProcessor, SelectExprProcessorForge {
    private final StatementResultService statementResultService;
    private final SelectExprProcessorForge syntheticProcessorForge;
    private final BindProcessorForge bindProcessorForge;

    private SelectExprProcessor syntheticProcessor;
    private BindProcessor bindProcessor;

    /**
     * Ctor.
     *
     * @param statementResultService for awareness of listeners and subscribers handles output results
     * @param syntheticProcessor     is the processor generating synthetic events according to the select clause
     * @param bindProcessorForge          for generating natural object column results
     */
    public SelectExprResultProcessor(StatementResultService statementResultService,
                                     SelectExprProcessorForge syntheticProcessor,
                                     BindProcessorForge bindProcessorForge) {
        this.statementResultService = statementResultService;
        this.syntheticProcessorForge = syntheticProcessor;
        this.bindProcessorForge = bindProcessorForge;
    }

    public SelectExprProcessor getSelectExprProcessor(EngineImportService engineImportService, boolean isFireAndForget, String statementName) {
        if (syntheticProcessor == null) {
            syntheticProcessor = syntheticProcessorForge.getSelectExprProcessor(engineImportService, isFireAndForget, statementName);
            bindProcessor = bindProcessorForge.getBindProcessor(engineImportService, isFireAndForget, statementName);
        }
        return this;
    }

    public EventType getResultEventType() {
        return syntheticProcessorForge.getResultEventType();
    }

    public eu.uk.ncl.pet5o.esper.client.EventBean process(eu.uk.ncl.pet5o.esper.client.EventBean[] eventsPerStream, boolean isNewData, boolean isSynthesize, ExprEvaluatorContext exprEvaluatorContext) {
        if (InstrumentationHelper.ENABLED) {
            InstrumentationHelper.get().qSelectClause(eventsPerStream, isNewData, isSynthesize, exprEvaluatorContext);
        }

        boolean makeNatural = statementResultService.isMakeNatural();
        boolean synthesize = statementResultService.isMakeSynthetic() || isSynthesize;

        if (!makeNatural) {
            if (synthesize) {
                eu.uk.ncl.pet5o.esper.client.EventBean syntheticEvent = syntheticProcessor.process(eventsPerStream, isNewData, isSynthesize, exprEvaluatorContext);
                if (InstrumentationHelper.ENABLED) {
                    InstrumentationHelper.get().aSelectClause(isNewData, syntheticEvent, null);
                }
                return syntheticEvent;
            }
            return null;
        }

        eu.uk.ncl.pet5o.esper.client.EventBean syntheticEvent = null;
        EventType syntheticEventType = null;

        if (synthesize) {
            syntheticEvent = syntheticProcessor.process(eventsPerStream, isNewData, isSynthesize, exprEvaluatorContext);
            syntheticEventType = syntheticProcessorForge.getResultEventType();
        }

        Object[] parameters = bindProcessor.process(eventsPerStream, isNewData, exprEvaluatorContext);
        if (InstrumentationHelper.ENABLED) {
            InstrumentationHelper.get().aSelectClause(isNewData, syntheticEvent, parameters);
        }
        return new NaturalEventBean(syntheticEventType, parameters, syntheticEvent);
    }

    public CodegenMethodNode processCodegen(CodegenMember memberResultEventType, CodegenMember memberEventAdapterService, CodegenMethodScope codegenMethodScope, SelectExprProcessorCodegenSymbol selectSymbol, ExprForgeCodegenSymbol exprSymbol, CodegenClassScope codegenClassScope) {
        CodegenMethodNode processMethod = codegenMethodScope.makeChild(eu.uk.ncl.pet5o.esper.client.EventBean.class, this.getClass(), codegenClassScope);
        CodegenExpressionRef isSythesize = selectSymbol.getAddSynthesize(processMethod);

        CodegenMethodNode syntheticMethod = syntheticProcessorForge.processCodegen(memberResultEventType, memberEventAdapterService, processMethod, selectSymbol, exprSymbol, codegenClassScope);
        CodegenMethodNode bindMethod = bindProcessorForge.processCodegen(processMethod, exprSymbol, codegenClassScope);

        CodegenMember stmtResultSvc = codegenClassScope.makeAddMember(StatementResultService.class, statementResultService);
        processMethod.getBlock()
                .declareVar(boolean.class, "makeNatural", exprDotMethod(member(stmtResultSvc.getMemberId()), "isMakeNatural"))
                .declareVar(boolean.class, "synthesize", or(exprDotMethod(member(stmtResultSvc.getMemberId()), "isMakeSynthetic"), isSythesize))
                .ifCondition(not(ref("makeNatural")))
                .ifCondition(ref("synthesize"))
                .blockReturn(localMethod(syntheticMethod))
                .blockReturn(constantNull())
                .declareVar(eu.uk.ncl.pet5o.esper.client.EventBean.class, "syntheticEvent", constantNull())
                .ifCondition(ref("synthesize"))
                .assignRef("syntheticEvent", localMethod(syntheticMethod))
                .blockEnd()
                .declareVar(Object[].class, "parameters", localMethod(bindMethod))
                .methodReturn(newInstance(NaturalEventBean.class, member(memberResultEventType.getMemberId()), ref("parameters"), ref("syntheticEvent")));

        return processMethod;
    }
}
