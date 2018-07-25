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

import com.espertech.esper.codegen.base.CodegenClassScope;
import com.espertech.esper.codegen.base.CodegenMember;
import com.espertech.esper.codegen.base.CodegenMethodNode;
import com.espertech.esper.codegen.base.CodegenMethodScope;
import com.espertech.esper.codegen.model.expression.CodegenExpression;
import com.espertech.esper.epl.expression.codegen.ExprForgeCodegenSymbol;
import com.espertech.esper.epl.table.mgmt.TableMetadata;
import com.espertech.esper.epl.table.mgmt.TableMetadataInternalEventToPublic;

import java.io.StringWriter;

import static com.espertech.esper.codegen.model.expression.CodegenExpressionBuilder.*;
import static com.espertech.esper.codegen.model.expression.CodegenExpressionBuilder.arrayAtIndex;
import static com.espertech.esper.codegen.model.expression.CodegenExpressionBuilder.constant;
import static com.espertech.esper.codegen.model.expression.CodegenExpressionBuilder.exprDotMethod;
import static com.espertech.esper.codegen.model.expression.CodegenExpressionBuilder.localMethod;
import static com.espertech.esper.codegen.model.expression.CodegenExpressionBuilder.member;
import static com.espertech.esper.codegen.model.expression.CodegenExpressionBuilder.ref;

public class ExprNodeUtilUnderlyingEvaluatorTable implements ExprEvaluator, ExprForge {
    private final int streamNum;
    private final Class resultType;
    private final TableMetadata tableMetadata;

    public ExprNodeUtilUnderlyingEvaluatorTable(int streamNum, Class resultType, TableMetadata tableMetadata) {
        this.streamNum = streamNum;
        this.resultType = resultType;
        this.tableMetadata = tableMetadata;
    }

    public ExprEvaluator getExprEvaluator() {
        return this;
    }

    public Class getEvaluationType() {
        return resultType;
    }

    public ExprForgeComplexityEnum getComplexity() {
        return ExprForgeComplexityEnum.SINGLE;
    }

    public ExprNodeRenderable getForgeRenderable() {
        return new ExprNodeRenderable() {
            public void toEPL(StringWriter writer, ExprPrecedenceEnum parentPrecedence) {
                writer.append(this.getClass().getSimpleName());
            }
        };
    }

    public Object evaluate(com.espertech.esper.client.EventBean[] eventsPerStream, boolean isNewData, ExprEvaluatorContext context) {
        if (eventsPerStream == null) {
            return null;
        }
        com.espertech.esper.client.EventBean event = eventsPerStream[streamNum];
        if (event == null) {
            return null;
        }
        return tableMetadata.getEventToPublic().convertToUnd(event, eventsPerStream, isNewData, context);
    }

    public CodegenExpression evaluateCodegen(Class requiredType, CodegenMethodScope parent, ExprForgeCodegenSymbol exprSymbol, CodegenClassScope codegenClassScope) {
        CodegenMember eventToPublic = codegenClassScope.makeAddMember(TableMetadataInternalEventToPublic.class, tableMetadata.getEventToPublic());
        CodegenMethodNode method = parent.makeChild(Object[].class, ExprNodeUtilUnderlyingEvaluatorTable.class, codegenClassScope);
        method.getBlock().ifRefNullReturnNull(exprSymbol.getAddEPS(method))
                .declareVar(com.espertech.esper.client.EventBean.class, "event", arrayAtIndex(exprSymbol.getAddEPS(method), constant(streamNum)))
                .ifRefNullReturnNull("event")
                .methodReturn(exprDotMethod(member(eventToPublic.getMemberId()), "convertToUnd", ref("event"), exprSymbol.getAddEPS(method), exprSymbol.getAddIsNewData(method), exprSymbol.getAddExprEvalCtx(method)));
        return localMethod(method);
    }
}
