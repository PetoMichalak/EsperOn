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
package eu.uk.ncl.pet5o.esper.epl.enummethod.dot;

import eu.uk.ncl.pet5o.esper.client.EventBean;
import eu.uk.ncl.pet5o.esper.client.EventType;
import eu.uk.ncl.pet5o.esper.codegen.base.CodegenClassScope;
import eu.uk.ncl.pet5o.esper.codegen.base.CodegenMember;
import eu.uk.ncl.pet5o.esper.codegen.base.CodegenMethodScope;
import eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpression;
import eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionBuilder;
import eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionRef;
import eu.uk.ncl.pet5o.esper.epl.expression.codegen.ExprForgeCodegenSymbol;
import eu.uk.ncl.pet5o.esper.epl.expression.core.ExprEvaluatorContext;
import eu.uk.ncl.pet5o.esper.epl.expression.dot.ExprDotEval;
import eu.uk.ncl.pet5o.esper.epl.expression.dot.ExprDotEvalVisitor;
import eu.uk.ncl.pet5o.esper.epl.expression.dot.ExprDotForge;
import eu.uk.ncl.pet5o.esper.epl.rettype.EPType;
import eu.uk.ncl.pet5o.esper.epl.rettype.EPTypeHelper;
import eu.uk.ncl.pet5o.esper.epl.table.mgmt.TableMetadata;
import eu.uk.ncl.pet5o.esper.epl.table.mgmt.TableMetadataInternalEventToPublic;

import java.util.ArrayDeque;
import java.util.Collection;

import static eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionBuilder.staticMethod;

public class ExprDotForgeUnpackCollEventBeanTable implements ExprDotForge, ExprDotEval {

    private final EPType typeInfo;
    private final TableMetadata tableMetadata;

    public ExprDotForgeUnpackCollEventBeanTable(EventType type, TableMetadata tableMetadata) {
        this.typeInfo = EPTypeHelper.collectionOfSingleValue(tableMetadata.getPublicEventType().getUnderlyingType());
        this.tableMetadata = tableMetadata;
    }

    public Object evaluate(Object target, EventBean[] eventsPerStream, boolean isNewData, ExprEvaluatorContext exprEvaluatorContext) {
        return convertToTableUnderling(target, tableMetadata.getEventToPublic(), eventsPerStream, isNewData, exprEvaluatorContext);
    }

    public CodegenExpression codegen(CodegenExpression inner, Class innerType, CodegenMethodScope codegenMethodScope, ExprForgeCodegenSymbol exprSymbol, CodegenClassScope codegenClassScope) {
        CodegenMember eventToPublic = codegenClassScope.makeAddMember(TableMetadataInternalEventToPublic.class, tableMetadata.getEventToPublic());
        CodegenExpressionRef refEPS = exprSymbol.getAddEPS(codegenMethodScope);
        CodegenExpression refIsNewData = exprSymbol.getAddIsNewData(codegenMethodScope);
        CodegenExpressionRef refExprEvalCtx = exprSymbol.getAddExprEvalCtx(codegenMethodScope);
        return staticMethod(ExprDotForgeUnpackCollEventBeanTable.class, "convertToTableUnderling", inner, CodegenExpressionBuilder.member(eventToPublic.getMemberId()), refEPS, refIsNewData, refExprEvalCtx);
    }

    /**
     * NOTE: Code-generation-invoked method, method name and parameter order matters
     * @param target target
     * @param eventToPublic conversion
     * @param eventsPerStream events
     * @param isNewData new data flow
     * @param exprEvaluatorContext context
     * @return events
     */
    public static Collection<Object[]> convertToTableUnderling(Object target, TableMetadataInternalEventToPublic eventToPublic, EventBean[] eventsPerStream, boolean isNewData, ExprEvaluatorContext exprEvaluatorContext) {
        if (target == null) {
            return null;
        }
        Collection<EventBean> events = (Collection<EventBean>) target;
        ArrayDeque<Object[]> underlyings = new ArrayDeque<>(events.size());
        for (EventBean event : events) {
            underlyings.add(eventToPublic.convertToUnd(event, eventsPerStream, isNewData, exprEvaluatorContext));
        }
        return underlyings;
    }

    public EPType getTypeInfo() {
        return typeInfo;
    }

    public void visit(ExprDotEvalVisitor visitor) {
        visitor.visitUnderlyingEventColl();
    }

    public ExprDotEval getDotEvaluator() {
        return this;
    }

    public ExprDotForge getDotForge() {
        return this;
    }
}
