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
package eu.uk.ncl.pet5o.esper.epl.agg.access;

import eu.uk.ncl.pet5o.esper.client.EventBean;
import eu.uk.ncl.pet5o.esper.codegen.base.CodegenMember;
import eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpression;
import eu.uk.ncl.pet5o.esper.epl.agg.factory.AggregationStateSortedForge;
import eu.uk.ncl.pet5o.esper.epl.core.engineimport.EngineImportService;
import eu.uk.ncl.pet5o.esper.epl.expression.core.ExprEvaluatorContext;
import eu.uk.ncl.pet5o.esper.epl.table.mgmt.TableMetadata;
import eu.uk.ncl.pet5o.esper.epl.table.mgmt.TableMetadataInternalEventToPublic;
import eu.uk.ncl.pet5o.esper.plugin.PlugInAggregationMultiFunctionCodegenType;
import eu.uk.ncl.pet5o.esper.util.JavaClassHelper;

import java.lang.reflect.Array;
import java.util.Collection;
import java.util.Iterator;

import static eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionBuilder.*;
import static eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionBuilder.cast;
import static eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionBuilder.constant;
import static eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionBuilder.constantNull;
import static eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionBuilder.equalsIdentity;
import static eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionBuilder.exprDotMethod;
import static eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionBuilder.member;
import static eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionBuilder.newArrayByLength;
import static eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionBuilder.ref;
import static eu.uk.ncl.pet5o.esper.epl.expression.codegen.ExprForgeCodegenNames.*;
import static eu.uk.ncl.pet5o.esper.epl.expression.codegen.ExprForgeCodegenNames.REF_EPS;
import static eu.uk.ncl.pet5o.esper.epl.expression.codegen.ExprForgeCodegenNames.REF_EXPREVALCONTEXT;
import static eu.uk.ncl.pet5o.esper.epl.expression.codegen.ExprForgeCodegenNames.REF_ISNEWDATA;

/**
 * Represents the aggregation accessor that provides the result for the "maxBy" aggregation function.
 */
public class AggregationAccessorSortedTable implements AggregationAccessor, AggregationAccessorForge {
    private final boolean max;
    private final Class componentType;
    private final TableMetadata tableMetadata;

    public AggregationAccessorSortedTable(boolean max, Class componentType, TableMetadata tableMetadata) {
        this.max = max;
        this.componentType = componentType;
        this.tableMetadata = tableMetadata;
    }

    public PlugInAggregationMultiFunctionCodegenType getPluginCodegenType() {
        return PlugInAggregationMultiFunctionCodegenType.CODEGEN_ALL;
    }

    public Object getValue(AggregationState state, eu.uk.ncl.pet5o.esper.client.EventBean[] eventsPerStream, boolean isNewData, ExprEvaluatorContext exprEvaluatorContext) {
        AggregationStateSorted sorted = (AggregationStateSorted) state;
        if (sorted.size() == 0) {
            return null;
        }
        Object array = Array.newInstance(componentType, sorted.size());

        Iterator<EventBean> it;
        if (max) {
            it = sorted.getReverseIterator();
        } else {
            it = sorted.iterator();
        }

        int count = 0;
        for (; it.hasNext(); ) {
            eu.uk.ncl.pet5o.esper.client.EventBean bean = it.next();
            Object und = tableMetadata.getEventToPublic().convertToUnd(bean, eventsPerStream, isNewData, exprEvaluatorContext);
            Array.set(array, count++, und);
        }
        return array;
    }

    public void getValueCodegen(AggregationAccessorForgeGetCodegenContext context) {
        CodegenMember eventToPublic = context.getClassScope().makeAddMember(TableMetadataInternalEventToPublic.class, tableMetadata.getEventToPublic());
        AggregationStateSortedForge sorted = (AggregationStateSortedForge) context.getAccessStateForge();
        CodegenExpression size = sorted.sizeCodegen(context.getColumn());
        CodegenExpression iterator = max ? sorted.getReverseIteratorCodegen(context.getColumn()) : sorted.iteratorCodegen(context.getColumn());

        context.getMethod().getBlock().ifCondition(equalsIdentity(size, constant(0))).blockReturn(constantNull())
                .declareVar(JavaClassHelper.getArrayType(componentType), "array", newArrayByLength(componentType, size))
                .declareVar(int.class, "count", constant(0))
                .declareVar(Iterator.class, "it", iterator)
                .whileLoop(exprDotMethod(ref("it"), "hasNext"))
                .declareVar(eu.uk.ncl.pet5o.esper.client.EventBean.class, "bean", cast(eu.uk.ncl.pet5o.esper.client.EventBean.class, exprDotMethod(ref("it"), "next")))
                .assignArrayElement(ref("array"), ref("count"), exprDotMethod(member(eventToPublic.getMemberId()), "convertToUnd", ref("bean"), REF_EPS, REF_ISNEWDATA, REF_EXPREVALCONTEXT))
                .increment("count")
                .blockEnd()
                .methodReturn(ref("array"));
    }

    public Collection<EventBean> getEnumerableEvents(AggregationState state, eu.uk.ncl.pet5o.esper.client.EventBean[] eventsPerStream, boolean isNewData, ExprEvaluatorContext exprEvaluatorContext) {
        return ((AggregationStateSorted) state).collectionReadOnly();
    }

    public void getEnumerableEventsCodegen(AggregationAccessorForgeGetCodegenContext context) {
        AggregationStateSortedForge sorted = (AggregationStateSortedForge) context.getAccessStateForge();
        context.getMethod().getBlock().methodReturn(sorted.collectionReadOnlyCodegen(context.getColumn()));
    }

    public Collection<Object> getEnumerableScalar(AggregationState state, eu.uk.ncl.pet5o.esper.client.EventBean[] eventsPerStream, boolean isNewData, ExprEvaluatorContext exprEvaluatorContext) {
        return null;
    }

    public void getEnumerableScalarCodegen(AggregationAccessorForgeGetCodegenContext context) {
        context.getMethod().getBlock().methodReturn(constantNull());
    }

    public eu.uk.ncl.pet5o.esper.client.EventBean getEnumerableEvent(AggregationState state, eu.uk.ncl.pet5o.esper.client.EventBean[] eventsPerStream, boolean isNewData, ExprEvaluatorContext exprEvaluatorContext) {
        return null;
    }

    public void getEnumerableEventCodegen(AggregationAccessorForgeGetCodegenContext context) {
        context.getMethod().getBlock().methodReturn(constantNull());
    }

    public AggregationAccessor getAccessor(EngineImportService engineImportService, boolean isFireAndForget, String statementName) {
        return this;
    }
}
