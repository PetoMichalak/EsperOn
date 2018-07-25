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
import eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpression;
import eu.uk.ncl.pet5o.esper.epl.core.engineimport.EngineImportService;
import eu.uk.ncl.pet5o.esper.epl.core.select.SelectExprProcessor;
import eu.uk.ncl.pet5o.esper.epl.core.select.SelectExprProcessorCodegenSymbol;
import eu.uk.ncl.pet5o.esper.epl.core.select.SelectExprProcessorForge;
import eu.uk.ncl.pet5o.esper.epl.expression.codegen.CodegenLegoMayVoid;
import eu.uk.ncl.pet5o.esper.epl.expression.codegen.ExprForgeCodegenSymbol;
import eu.uk.ncl.pet5o.esper.epl.expression.core.ExprEvaluator;
import eu.uk.ncl.pet5o.esper.epl.expression.core.ExprEvaluatorContext;
import eu.uk.ncl.pet5o.esper.epl.util.ExprNodeUtilityRich;
import eu.uk.ncl.pet5o.esper.event.EventAdapterService;
import eu.uk.ncl.pet5o.esper.util.CollectionUtil;

import java.util.HashMap;
import java.util.Map;

import static eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionBuilder.*;
import static eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionBuilder.constant;
import static eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionBuilder.exprDotMethod;
import static eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionBuilder.member;
import static eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionBuilder.newInstance;
import static eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionBuilder.ref;

public class EvalSelectNoWildcardMap implements SelectExprProcessor, SelectExprProcessorForge {

    private final SelectExprForgeContext selectContext;
    private final EventType resultEventType;
    private ExprEvaluator[] evaluators;

    public EvalSelectNoWildcardMap(SelectExprForgeContext selectContext, EventType resultEventType) {
        this.selectContext = selectContext;
        this.resultEventType = resultEventType;
    }

    public SelectExprProcessor getSelectExprProcessor(EngineImportService engineImportService, boolean isFireAndForget, String statementName) {
        if (evaluators == null) {
            evaluators = ExprNodeUtilityRich.getEvaluatorsMayCompile(selectContext.getExprForges(), engineImportService, this.getClass(), isFireAndForget, statementName);
        }
        return this;
    }

    public eu.uk.ncl.pet5o.esper.client.EventBean process(eu.uk.ncl.pet5o.esper.client.EventBean[] eventsPerStream, boolean isNewData, boolean isSynthesize, ExprEvaluatorContext exprEvaluatorContext) {
        String[] columnNames = selectContext.getColumnNames();
        EventAdapterService eventAdapterService = selectContext.getEventAdapterService();

        // Evaluate all expressions and build a map of name-value pairs
        Map<String, Object> props = new HashMap<String, Object>(CollectionUtil.capacityHashMap(selectContext.getColumnNames().length));
        for (int i = 0; i < evaluators.length; i++) {
            Object evalResult = evaluators[i].evaluate(eventsPerStream, isNewData, exprEvaluatorContext);
            props.put(columnNames[i], evalResult);
        }

        return eventAdapterService.adapterForTypedMap(props, resultEventType);
    }

    public CodegenMethodNode processCodegen(CodegenMember memberResultEventType, CodegenMember memberEventAdapterService, CodegenMethodScope codegenMethodScope, SelectExprProcessorCodegenSymbol selectSymbol, ExprForgeCodegenSymbol exprSymbol, CodegenClassScope codegenClassScope) {
        CodegenMethodNode methodNode = codegenMethodScope.makeChild(eu.uk.ncl.pet5o.esper.client.EventBean.class, this.getClass(), codegenClassScope);
        methodNode.getBlock().declareVar(Map.class, "props", newInstance(HashMap.class, constant(CollectionUtil.capacityHashMap(selectContext.getColumnNames().length))));
        for (int i = 0; i < selectContext.getColumnNames().length; i++) {
            CodegenExpression expression = CodegenLegoMayVoid.expressionMayVoid(Object.class, selectContext.getExprForges()[i], methodNode, exprSymbol, codegenClassScope);
            methodNode.getBlock().expression(exprDotMethod(ref("props"), "put", constant(selectContext.getColumnNames()[i]), expression));
        }
        methodNode.getBlock().methodReturn(exprDotMethod(member(memberEventAdapterService.getMemberId()), "adapterForTypedMap", ref("props"), member(memberResultEventType.getMemberId())));
        return methodNode;
    }

    public EventType getResultEventType() {
        return resultEventType;
    }
}
