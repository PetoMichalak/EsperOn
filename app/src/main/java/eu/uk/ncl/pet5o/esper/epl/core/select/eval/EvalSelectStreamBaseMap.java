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
import eu.uk.ncl.pet5o.esper.codegen.base.CodegenBlock;
import eu.uk.ncl.pet5o.esper.codegen.base.CodegenClassScope;
import eu.uk.ncl.pet5o.esper.codegen.base.CodegenMember;
import eu.uk.ncl.pet5o.esper.codegen.base.CodegenMethodNode;
import eu.uk.ncl.pet5o.esper.codegen.base.CodegenMethodScope;
import eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpression;
import eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionRef;
import eu.uk.ncl.pet5o.esper.core.service.speccompiled.SelectClauseStreamCompiledSpec;
import eu.uk.ncl.pet5o.esper.epl.core.select.SelectExprProcessor;
import eu.uk.ncl.pet5o.esper.epl.core.select.SelectExprProcessorCodegenSymbol;
import eu.uk.ncl.pet5o.esper.epl.core.select.SelectExprProcessorForge;
import eu.uk.ncl.pet5o.esper.epl.expression.codegen.CodegenLegoMayVoid;
import eu.uk.ncl.pet5o.esper.epl.expression.codegen.ExprForgeCodegenSymbol;
import eu.uk.ncl.pet5o.esper.epl.expression.core.ExprEvaluator;
import eu.uk.ncl.pet5o.esper.epl.expression.core.ExprEvaluatorContext;
import eu.uk.ncl.pet5o.esper.epl.expression.core.ExprForge;
import eu.uk.ncl.pet5o.esper.epl.table.mgmt.TableMetadataInternalEventToPublic;
import eu.uk.ncl.pet5o.esper.util.CollectionUtil;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionBuilder.*;
import static eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionBuilder.arrayAtIndex;
import static eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionBuilder.constant;
import static eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionBuilder.exprDotMethod;
import static eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionBuilder.member;
import static eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionBuilder.newInstance;
import static eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionBuilder.ref;

public abstract class EvalSelectStreamBaseMap extends EvalSelectStreamBase implements SelectExprProcessorForge, SelectExprProcessor {

    protected EvalSelectStreamBaseMap(SelectExprForgeContext selectExprForgeContext, EventType resultEventType, List<SelectClauseStreamCompiledSpec> namedStreams, boolean usingWildcard) {
        super(selectExprForgeContext, resultEventType, namedStreams, usingWildcard);
    }

    protected abstract eu.uk.ncl.pet5o.esper.client.EventBean processSpecific(Map<String, Object> props, eu.uk.ncl.pet5o.esper.client.EventBean[] eventsPerStream, boolean isNewData, ExprEvaluatorContext exprEvaluatorContext);

    protected abstract CodegenExpression processSpecificCodegen(CodegenMember memberResultEventType, CodegenMember memberEventAdapterService, CodegenExpression props, CodegenMethodScope codegenMethodScope, ExprForgeCodegenSymbol exprSymbol, CodegenClassScope codegenClassScope);

    public eu.uk.ncl.pet5o.esper.client.EventBean process(eu.uk.ncl.pet5o.esper.client.EventBean[] eventsPerStream, boolean isNewData, boolean isSynthesize, ExprEvaluatorContext exprEvaluatorContext) {
        // Evaluate all expressions and build a map of name-value pairs
        Map<String, Object> props = new HashMap<String, Object>();
        int count = 0;
        for (ExprEvaluator expressionNode : evaluators) {
            Object evalResult = expressionNode.evaluate(eventsPerStream, isNewData, exprEvaluatorContext);
            props.put(context.getColumnNames()[count], evalResult);
            count++;
        }
        for (SelectClauseStreamCompiledSpec element : namedStreams) {
            eu.uk.ncl.pet5o.esper.client.EventBean theEvent = eventsPerStream[element.getStreamNumber()];
            if (element.getTableMetadata() != null) {
                if (theEvent != null) {
                    theEvent = element.getTableMetadata().getEventToPublic().convert(theEvent, eventsPerStream, isNewData, exprEvaluatorContext);
                }
            }
            props.put(context.getColumnNames()[count], theEvent);
            count++;
        }
        if (isUsingWildcard && eventsPerStream.length > 1) {
            for (eu.uk.ncl.pet5o.esper.client.EventBean anEventsPerStream : eventsPerStream) {
                props.put(context.getColumnNames()[count], anEventsPerStream);
                count++;
            }
        }

        return processSpecific(props, eventsPerStream, isNewData, exprEvaluatorContext);
    }

    public CodegenMethodNode processCodegen(CodegenMember memberResultEventType, CodegenMember memberEventAdapterService, CodegenMethodScope codegenMethodScope, SelectExprProcessorCodegenSymbol selectSymbol, ExprForgeCodegenSymbol exprSymbol, CodegenClassScope codegenClassScope) {
        int size = this.context.getExprForges().length + namedStreams.size() + (isUsingWildcard && this.context.getNumStreams() > 1 ? this.context.getNumStreams() : 0);

        CodegenMethodNode methodNode = codegenMethodScope.makeChild(eu.uk.ncl.pet5o.esper.client.EventBean.class, this.getClass(), codegenClassScope);
        CodegenExpressionRef refEPS = exprSymbol.getAddEPS(methodNode);
        CodegenExpression refIsNewData = exprSymbol.getAddIsNewData(methodNode);
        CodegenExpressionRef refExprEvalCtx = exprSymbol.getAddExprEvalCtx(methodNode);
        CodegenBlock block = methodNode.getBlock()
                .declareVar(Map.class, "props", newInstance(HashMap.class, constant(CollectionUtil.capacityHashMap(size))));
        int count = 0;
        for (ExprForge forge : this.context.getExprForges()) {
            block.expression(exprDotMethod(ref("props"), "put", constant(this.context.getColumnNames()[count]), CodegenLegoMayVoid.expressionMayVoid(Object.class, forge, methodNode, exprSymbol, codegenClassScope)));
            count++;
        }
        for (SelectClauseStreamCompiledSpec element : namedStreams) {
            CodegenExpression theEvent = arrayAtIndex(refEPS, constant(element.getStreamNumber()));
            if (element.getTableMetadata() != null) {
                CodegenMember eventToPublic = codegenClassScope.makeAddMember(TableMetadataInternalEventToPublic.class, element.getTableMetadata().getEventToPublic());
                theEvent = exprDotMethod(member(eventToPublic.getMemberId()), "convert", theEvent, refEPS, refIsNewData, refExprEvalCtx);
            }
            block.expression(exprDotMethod(ref("props"), "put", constant(this.context.getColumnNames()[count]), theEvent));
            count++;
        }
        if (isUsingWildcard && this.context.getNumStreams() > 1) {
            for (int i = 0; i < this.context.getNumStreams(); i++) {
                block.expression(exprDotMethod(ref("props"), "put", constant(this.context.getColumnNames()[count]), arrayAtIndex(refEPS, constant(i))));
                count++;
            }
        }
        block.methodReturn(processSpecificCodegen(memberResultEventType, memberEventAdapterService, ref("props"), methodNode, exprSymbol, codegenClassScope));
        return methodNode;
    }
}
