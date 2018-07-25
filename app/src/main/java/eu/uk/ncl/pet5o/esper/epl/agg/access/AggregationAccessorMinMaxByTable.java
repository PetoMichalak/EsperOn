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

import eu.uk.ncl.pet5o.esper.codegen.base.CodegenMember;
import eu.uk.ncl.pet5o.esper.epl.core.engineimport.EngineImportService;
import eu.uk.ncl.pet5o.esper.epl.expression.core.ExprEvaluatorContext;
import eu.uk.ncl.pet5o.esper.epl.table.mgmt.TableMetadata;
import eu.uk.ncl.pet5o.esper.epl.table.mgmt.TableMetadataInternalEventToPublic;
import eu.uk.ncl.pet5o.esper.plugin.PlugInAggregationMultiFunctionCodegenType;

import static eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionBuilder.*;
import static eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionBuilder.exprDotMethod;
import static eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionBuilder.member;
import static eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionBuilder.ref;
import static eu.uk.ncl.pet5o.esper.epl.expression.codegen.ExprForgeCodegenNames.REF_EPS;
import static eu.uk.ncl.pet5o.esper.epl.expression.codegen.ExprForgeCodegenNames.REF_EXPREVALCONTEXT;
import static eu.uk.ncl.pet5o.esper.epl.expression.codegen.ExprForgeCodegenNames.REF_ISNEWDATA;

/**
 * Represents the aggregation accessor that provides the result for the "maxBy" aggregation function.
 */
public class AggregationAccessorMinMaxByTable extends AggregationAccessorMinMaxByBase implements AggregationAccessorForge {
    private final TableMetadata tableMetadata;

    public AggregationAccessorMinMaxByTable(boolean max, TableMetadata tableMetadata) {
        super(max);
        this.tableMetadata = tableMetadata;
    }

    public PlugInAggregationMultiFunctionCodegenType getPluginCodegenType() {
        return PlugInAggregationMultiFunctionCodegenType.CODEGEN_ALL;
    }

    public Object getValue(AggregationState state, eu.uk.ncl.pet5o.esper.client.EventBean[] eventsPerStream, boolean isNewData, ExprEvaluatorContext context) {
        eu.uk.ncl.pet5o.esper.client.EventBean event = getEnumerableEvent(state, eventsPerStream, isNewData, context);
        if (event == null) {
            return null;
        }
        return tableMetadata.getEventToPublic().convertToUnd(event, eventsPerStream, isNewData, context);
    }

    public AggregationAccessor getAccessor(EngineImportService engineImportService, boolean isFireAndForget, String statementName) {
        return this;
    }

    public void getValueCodegen(AggregationAccessorForgeGetCodegenContext context) {
        CodegenMember eventToPublic = context.getClassScope().makeAddMember(TableMetadataInternalEventToPublic.class, tableMetadata.getEventToPublic());
        AggregationStateSortedCodegen forge = (AggregationStateSortedCodegen) context.getAccessStateForge();
        context.getMethod().getBlock().declareVar(eu.uk.ncl.pet5o.esper.client.EventBean.class, "event", max ? forge.getLastValueCodegen(context.getColumn(), context.getClassScope(), context.getMethod()) : forge.getFirstValueCodegen(context.getColumn(), context.getClassScope(), context.getMethod()))
                .ifRefNullReturnNull("event")
                .methodReturn(exprDotMethod(member(eventToPublic.getMemberId()), "convertToUnd", ref("event"), REF_EPS, REF_ISNEWDATA, REF_EXPREVALCONTEXT));
    }
}
