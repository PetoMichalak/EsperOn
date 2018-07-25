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

import eu.uk.ncl.pet5o.esper.client.EventBean;
import eu.uk.ncl.pet5o.esper.codegen.base.CodegenClassScope;
import eu.uk.ncl.pet5o.esper.codegen.base.CodegenMember;
import eu.uk.ncl.pet5o.esper.codegen.base.CodegenMethodNode;
import eu.uk.ncl.pet5o.esper.codegen.base.CodegenMethodScope;
import eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpression;
import eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionRef;
import eu.uk.ncl.pet5o.esper.epl.expression.codegen.ExprForgeCodegenSymbol;
import eu.uk.ncl.pet5o.esper.epl.expression.core.ExprEnumerationEval;
import eu.uk.ncl.pet5o.esper.epl.expression.core.ExprEvaluator;
import eu.uk.ncl.pet5o.esper.epl.expression.core.ExprEvaluatorContext;
import eu.uk.ncl.pet5o.esper.epl.table.mgmt.TableMetadataInternalEventToPublic;

import java.util.Collection;

import static eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionBuilder.*;
import static eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionBuilder.localMethod;
import static eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionBuilder.member;
import static eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionBuilder.ref;
import static eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionBuilder.staticMethod;

public class SelectExprProcessorEnumerationAtBeanCollTableEval implements ExprEvaluator {
    private final SelectExprProcessorEnumerationAtBeanCollTableForge forge;
    private final ExprEnumerationEval enumEval;

    public SelectExprProcessorEnumerationAtBeanCollTableEval(SelectExprProcessorEnumerationAtBeanCollTableForge forge, ExprEnumerationEval enumEval) {
        this.forge = forge;
        this.enumEval = enumEval;
    }

    public Object evaluate(eu.uk.ncl.pet5o.esper.client.EventBean[] eventsPerStream, boolean isNewData, ExprEvaluatorContext exprEvaluatorContext) {
        // the protocol is EventBean[]
        Object result = enumEval.evaluateGetROCollectionEvents(eventsPerStream, isNewData, exprEvaluatorContext);
        if (result == null) {
            return null;
        }
        return convertToTableType(result, forge.tableMetadata.getEventToPublic(), eventsPerStream, isNewData, exprEvaluatorContext);
    }

    public static CodegenExpression codegen(SelectExprProcessorEnumerationAtBeanCollTableForge forge, CodegenMethodScope codegenMethodScope, ExprForgeCodegenSymbol exprSymbol, CodegenClassScope codegenClassScope) {
        CodegenMember eventToPublic = codegenClassScope.makeAddMember(TableMetadataInternalEventToPublic.class, forge.tableMetadata.getEventToPublic());
        CodegenMethodNode methodNode = codegenMethodScope.makeChild(eu.uk.ncl.pet5o.esper.client.EventBean[].class, SelectExprProcessorEnumerationAtBeanCollTableEval.class, codegenClassScope);

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
    public static eu.uk.ncl.pet5o.esper.client.EventBean[] convertToTableType(Object result, TableMetadataInternalEventToPublic eventToPublic, eu.uk.ncl.pet5o.esper.client.EventBean[] eventsPerStream, boolean isNewData, ExprEvaluatorContext exprEvaluatorContext) {
        if (result instanceof Collection) {
            Collection<EventBean> events = (Collection<EventBean>) result;
            eu.uk.ncl.pet5o.esper.client.EventBean[] out = new eu.uk.ncl.pet5o.esper.client.EventBean[events.size()];
            int index = 0;
            for (eu.uk.ncl.pet5o.esper.client.EventBean event : events) {
                out[index++] = eventToPublic.convert(event, eventsPerStream, isNewData, exprEvaluatorContext);
            }
            return out;
        }
        eu.uk.ncl.pet5o.esper.client.EventBean[] events = (eu.uk.ncl.pet5o.esper.client.EventBean[]) result;
        for (int i = 0; i < events.length; i++) {
            events[i] = eventToPublic.convert(events[i], eventsPerStream, isNewData, exprEvaluatorContext);
        }
        return events;
    }
}
