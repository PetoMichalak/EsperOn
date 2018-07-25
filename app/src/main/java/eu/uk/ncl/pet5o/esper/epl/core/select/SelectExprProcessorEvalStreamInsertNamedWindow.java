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
import eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpression;
import eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionRef;
import eu.uk.ncl.pet5o.esper.epl.expression.codegen.ExprForgeCodegenSymbol;
import eu.uk.ncl.pet5o.esper.epl.expression.core.*;
import eu.uk.ncl.pet5o.esper.event.EventAdapterService;

import java.io.StringWriter;

import static eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionBuilder.*;
import static eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionBuilder.arrayAtIndex;
import static eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionBuilder.constant;
import static eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionBuilder.exprDotMethod;
import static eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionBuilder.exprDotUnderlying;
import static eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionBuilder.localMethod;
import static eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionBuilder.member;
import static eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionBuilder.ref;

public class SelectExprProcessorEvalStreamInsertNamedWindow implements ExprForge, ExprEvaluator, ExprNodeRenderable {
    private final int streamNum;
    private final EventType namedWindowAsType;
    private final Class returnType;
    private final EventAdapterService eventAdapterService;

    public SelectExprProcessorEvalStreamInsertNamedWindow(int streamNum, EventType namedWindowAsType, Class returnType, EventAdapterService eventAdapterService) {
        this.streamNum = streamNum;
        this.namedWindowAsType = namedWindowAsType;
        this.returnType = returnType;
        this.eventAdapterService = eventAdapterService;
    }

    public Object evaluate(eu.uk.ncl.pet5o.esper.client.EventBean[] eventsPerStream, boolean isNewData, ExprEvaluatorContext exprEvaluatorContext) {
        eu.uk.ncl.pet5o.esper.client.EventBean event = eventsPerStream[streamNum];
        if (event == null) {
            return null;
        }
        return eventAdapterService.adapterForType(event.getUnderlying(), namedWindowAsType);
    }

    public ExprForgeComplexityEnum getComplexity() {
        return ExprForgeComplexityEnum.SINGLE;
    }

    public int getStreamNum() {
        return streamNum;
    }

    public ExprEvaluator getExprEvaluator() {
        return this;
    }

    public CodegenExpression evaluateCodegen(Class requiredType, CodegenMethodScope codegenMethodScope, ExprForgeCodegenSymbol exprSymbol, CodegenClassScope codegenClassScope) {
        CodegenMember eventSvc = codegenClassScope.makeAddMember(EventAdapterService.class, eventAdapterService);
        CodegenMember namedWindowType = codegenClassScope.makeAddMember(EventType.class, namedWindowAsType);
        CodegenMethodNode methodNode = codegenMethodScope.makeChild(eu.uk.ncl.pet5o.esper.client.EventBean.class, SelectExprProcessorEvalStreamInsertNamedWindow.class, codegenClassScope);

        CodegenExpressionRef refEPS = exprSymbol.getAddEPS(methodNode);

        methodNode.getBlock()
                .declareVar(eu.uk.ncl.pet5o.esper.client.EventBean.class, "event", arrayAtIndex(refEPS, constant(streamNum)))
                .ifRefNullReturnNull("event")
                .methodReturn(exprDotMethod(member(eventSvc.getMemberId()), "adapterForType", exprDotUnderlying(ref("event")), member(namedWindowType.getMemberId())));
        return localMethod(methodNode);
    }

    public Class getEvaluationType() {
        return returnType;
    }

    public ExprNodeRenderable getForgeRenderable() {
        return this;
    }

    public void toEPL(StringWriter writer, ExprPrecedenceEnum parentPrecedence) {
        writer.append(this.getClass().getSimpleName());
    }
}
