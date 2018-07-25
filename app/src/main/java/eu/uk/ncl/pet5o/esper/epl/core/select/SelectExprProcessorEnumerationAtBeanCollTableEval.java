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

import com.espertech.esper.client.EventBean;
import com.espertech.esper.codegen.base.CodegenClassScope;
import com.espertech.esper.codegen.base.CodegenMember;
import com.espertech.esper.codegen.base.CodegenMethodNode;
import com.espertech.esper.codegen.base.CodegenMethodScope;
import com.espertech.esper.codegen.model.expression.CodegenExpression;
import com.espertech.esper.codegen.model.expression.CodegenExpressionRef;
import com.espertech.esper.epl.expression.codegen.ExprForgeCodegenSymbol;
import com.espertech.esper.epl.expression.core.ExprEnumerationEval;
import com.espertech.esper.epl.expression.core.ExprEvaluator;
import com.espertech.esper.epl.expression.core.ExprEvaluatorContext;
import com.espertech.esper.epl.table.mgmt.TableMetadataInternalEventToPublic;

import java.util.Collection;

import static com.espertech.esper.codegen.model.expression.CodegenExpressionBuilder.*;
import static com.espertech.esper.codegen.model.expression.CodegenExpressionBuilder.localMethod;
import static com.espertech.esper.codegen.model.expression.CodegenExpressionBuilder.member;
import static com.espertech.esper.codegen.model.expression.CodegenExpressionBuilder.ref;
import static com.espertech.esper.codegen.model.expression.CodegenExpressionBuilder.staticMethod;

public class SelectExprProcessorEnumerationAtBeanCollTableEval implements ExprEvaluator {
    private final SelectExprProcessorEnumerationAtBeanCollTableForge forge;
    private final ExprEnumerationEval enumEval;

    public SelectExprProcessorEnumerationAtBeanCollTableEval(SelectExprProcessorEnumerationAtBeanCollTableForge forge, ExprEnumerationEval enumEval) {
        this.forge = forge;
        this.enumEval = enumEval;
    }

    public Object evaluate(com.espertech.esper.client.EventBean[] eventsPerStream, boolean isNewData, ExprEvaluatorContext exprEvaluatorContext) {
        // the protocol is EventBean[]
        Object result = enumEval.evaluateGetROCollectionEvents(eventsPerStream, isNewData, exprEvaluatorContext);
        if (result == null) {
            return null;
        }
        return convertToTableType(result, forge.tableMetadata.getEventToPublic(), eventsPerStream, isNewData, exprEvaluatorContext);
    }

    public static CodegenExpression codegen(SelectExprProcessorEnumerationAtBeanCollTableForge forge, CodegenMethodScope codegenMethodScope, ExprForgeCodegenSymbol exprSymbol, CodegenClassScope codegenClassScope) {
        CodegenMember eventToPublic = codegenClassScope.makeAddMember(TableMetadataInternalEventToPublic.class, forge.tableMetadata.getEventToPublic());
        CodegenMethodNode methodNode = codegenMethodScope.makeChild(com.espertech.esper.client.EventBean[].class, SelectExprProcessorEnumerationAtBeanCollTableEval.class, codegenClassScope);

        CodegenExpressionRef refEPS = exprSymbol.getAddEPS(methodNode);
        CodegenExpression refIsNewData = exprSymbol.getAddIsNewData(methodNode);
        CodegenExpressionRef refExprEvalCtx = exprSymbol.getAddExprEvalCtx(methodNode);

        methodNode.getBlock()
                .declareVar(Object.class, "result", forge.enumerationForge.evaluateGetROCollectionEventsCodegen(methodNode, exprSymbol, codegenClassScope))
                .ifRefNullReturnNull("result")
                .methodReturn(staticMethod(SelectExprProcessorEnumerationAtBeanCollTableEval.class, "convertToTableType", ref("result"), member(eventToPublic.getMemberId()), refEPS, refIsNewData, refExprEvalCtx));
        return localMethod(methodNode);
    }

    /**
     * NOTE: Code-generation-invoked method, method name and parameter order matters
     * @param result result
     * @param eventToPublic conversion
     * @param eventsPerStream events
     * @param isNewData flag
     * @param exprEvaluatorContext context
     * @return beans
     */
    public static com.espertech.esper.client.EventBean[] convertToTableType(Object result, TableMetadataInternalEventToPublic eventToPublic, com.espertech.esper.client.EventBean[] eventsPerStream, boolean isNewData, ExprEvaluatorContext exprEvaluatorContext) {
        if (result instanceof Collection) {
            Collection<EventBean> events = (Collection<EventBean>) result;
            com.espertech.esper.client.EventBean[] out = new com.espertech.esper.client.EventBean[events.size()];
            int index = 0;
            for (com.espertech.esper.client.EventBean event : events) {
                out[index++] = eventToPublic.convert(event, eventsPerStream, isNewData, exprEvaluatorContext);
            }
            return out;
        }
        com.espertech.esper.client.EventBean[] events = (com.espertech.esper.client.EventBean[]) result;
        for (int i = 0; i < events.length; i++) {
            events[i] = eventToPublic.convert(events[i], eventsPerStream, isNewData, exprEvaluatorContext);
        }
        return events;
    }
}
