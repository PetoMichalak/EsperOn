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
import eu.uk.ncl.pet5o.esper.epl.core.engineimport.EngineImportService;
import eu.uk.ncl.pet5o.esper.epl.expression.codegen.ExprForgeCodegenSymbol;
import eu.uk.ncl.pet5o.esper.epl.expression.core.ExprEvaluatorContext;
import eu.uk.ncl.pet5o.esper.event.EventAdapterService;
import eu.uk.ncl.pet5o.esper.util.CollectionUtil;

import java.util.HashMap;
import java.util.Map;

import static eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionBuilder.*;
import static eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionBuilder.arrayAtIndex;
import static eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionBuilder.constant;
import static eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionBuilder.exprDotMethod;
import static eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionBuilder.member;
import static eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionBuilder.newInstance;
import static eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionBuilder.ref;

/**
 * Processor for select-clause expressions that handles wildcards. Computes results based on matching events.
 */
public class SelectExprJoinWildcardProcessorMap implements SelectExprProcessor, SelectExprProcessorForge {
    private final String[] streamNames;
    private final EventType resultEventType;
    private final EventAdapterService eventAdapterService;

    public SelectExprJoinWildcardProcessorMap(String[] streamNames, EventType resultEventType, EventAdapterService eventAdapterService) {
        this.streamNames = streamNames;
        this.resultEventType = resultEventType;
        this.eventAdapterService = eventAdapterService;
    }

    public eu.uk.ncl.pet5o.esper.client.EventBean process(eu.uk.ncl.pet5o.esper.client.EventBean[] eventsPerStream, boolean isNewData, boolean isSynthesize, ExprEvaluatorContext exprEvaluatorContext) {
        Map<String, Object> tuple = new HashMap<>(CollectionUtil.capacityHashMap(streamNames.length));
        for (int i = 0; i < streamNames.length; i++) {
            if (streamNames[i] == null) {
                throw new IllegalStateException("Event name for stream " + i + " is null");
            }
            tuple.put(streamNames[i], eventsPerStream[i]);
        }

        return eventAdapterService.adapterForTypedMap(tuple, resultEventType);
    }

    public CodegenMethodNode processCodegen(CodegenMember memberResultEventType, CodegenMember memberEventAdapterService, CodegenMethodScope codegenMethodScope, SelectExprProcessorCodegenSymbol selectSymbol, ExprForgeCodegenSymbol exprSymbol, CodegenClassScope codegenClassScope) {
        CodegenMethodNode methodNode = codegenMethodScope.makeChild(eu.uk.ncl.pet5o.esper.client.EventBean.class, this.getClass(), codegenClassScope);
        CodegenExpressionRef refEPS = exprSymbol.getAddEPS(methodNode);
        methodNode.getBlock()
                .declareVar(Map.class, "tuple", newInstance(HashMap.class, constant(CollectionUtil.capacityHashMap(streamNames.length))));
        for (int i = 0; i < streamNames.length; i++) {
            methodNode.getBlock().expression(exprDotMethod(ref("tuple"), "put", constant(streamNames[i]), arrayAtIndex(refEPS, constant(i))));
        }
        methodNode.getBlock().methodReturn(exprDotMethod(member(memberEventAdapterService.getMemberId()), "adapterForTypedMap", ref("tuple"), member(memberResultEventType.getMemberId())));
        return methodNode;
    }

    public EventType getResultEventType() {
        return resultEventType;
    }

    public SelectExprProcessor getSelectExprProcessor(EngineImportService engineImportService, boolean isFireAndForget, String statementName) {
        return this;
    }
}
