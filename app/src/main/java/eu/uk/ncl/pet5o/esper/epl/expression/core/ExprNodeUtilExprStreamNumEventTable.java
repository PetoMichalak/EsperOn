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
package eu.uk.ncl.pet5o.esper.epl.expression.core;

import eu.uk.ncl.pet5o.esper.codegen.base.CodegenClassScope;
import eu.uk.ncl.pet5o.esper.codegen.base.CodegenMember;
import eu.uk.ncl.pet5o.esper.codegen.base.CodegenMethodScope;
import eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpression;
import eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionRef;
import eu.uk.ncl.pet5o.esper.epl.expression.codegen.ExprForgeCodegenSymbol;
import eu.uk.ncl.pet5o.esper.epl.table.mgmt.TableMetadata;
import eu.uk.ncl.pet5o.esper.epl.table.mgmt.TableMetadataInternalEventToPublic;

import java.io.StringWriter;

import static eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionBuilder.*;
import static eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionBuilder.constant;
import static eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionBuilder.member;
import static eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionBuilder.staticMethod;

public class ExprNodeUtilExprStreamNumEventTable implements ExprForge, ExprEvaluator, ExprNodeRenderable {
    private final int streamNum;
    private final TableMetadata tableMetadata;

    public ExprNodeUtilExprStreamNumEventTable(int streamNum, TableMetadata tableMetadata) {
        this.streamNum = streamNum;
        this.tableMetadata = tableMetadata;
    }

    public Object evaluate(eu.uk.ncl.pet5o.esper.client.EventBean[] eventsPerStream, boolean isNewData, ExprEvaluatorContext context) {
        return evaluateConvertTableEvent(streamNum, tableMetadata.getEventToPublic(), eventsPerStream, isNewData, context);
    }

    public CodegenExpression evaluateCodegen(Class requiredType, CodegenMethodScope codegenMethodScope, ExprForgeCodegenSymbol exprSymbol, CodegenClassScope codegenClassScope) {
        CodegenExpressionRef refEPS = exprSymbol.getAddEPS(codegenMethodScope);
        CodegenExpression refIsNewData = exprSymbol.getAddIsNewData(codegenMethodScope);
        CodegenExpressionRef refExprEvalCtx = exprSymbol.getAddExprEvalCtx(codegenMethodScope);
        CodegenMember eventToPublic = codegenClassScope.makeAddMember(TableMetadataInternalEventToPublic.class, tableMetadata.getEventToPublic());
        return staticMethod(ExprNodeUtilExprStreamNumEventTable.class, "evaluateConvertTableEvent", constant(streamNum), member(eventToPublic.getMemberId()), refEPS, refIsNewData, refExprEvalCtx);
    }

    public ExprForgeComplexityEnum getComplexity() {
        return ExprForgeComplexityEnum.SINGLE;
    }

    /**
     * NOTE: Code-generation-invoked method, method name and parameter order matters
     *
     * @param streamNum       stream
     * @param eventToPublic   conversion
     * @param eventsPerStream events
     * @param isNewData       flag
     * @param context         context
     * @return event
     */
    public static eu.uk.ncl.pet5o.esper.client.EventBean evaluateConvertTableEvent(int streamNum, TableMetadataInternalEventToPublic eventToPublic, eu.uk.ncl.pet5o.esper.client.EventBean[] eventsPerStream, boolean isNewData, ExprEvaluatorContext context) {
        eu.uk.ncl.pet5o.esper.client.EventBean event = eventsPerStream[streamNum];
        if (event == null) {
            return null;
        }
        return eventToPublic.convert(event, eventsPerStream, isNewData, context);
    }

    public ExprEvaluator getExprEvaluator() {
        return this;
    }

    public Class getEvaluationType() {
        return eu.uk.ncl.pet5o.esper.client.EventBean.class;
    }

    public ExprNodeRenderable getForgeRenderable() {
        return this;
    }

    public void toEPL(StringWriter writer, ExprPrecedenceEnum parentPrecedence) {
        writer.append(this.getClass().getSimpleName());
    }
}
