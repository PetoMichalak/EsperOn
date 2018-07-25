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
package eu.uk.ncl.pet5o.esper.epl.core.orderby;

import eu.uk.ncl.pet5o.esper.codegen.core.CodegenNamedParam;
import eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionRef;
import eu.uk.ncl.pet5o.esper.core.context.util.AgentInstanceContext;
import eu.uk.ncl.pet5o.esper.epl.agg.service.common.AggregationService;
import eu.uk.ncl.pet5o.esper.epl.expression.core.ExprEvaluatorContext;

import java.util.List;

import static eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionBuilder.ref;
import static eu.uk.ncl.pet5o.esper.epl.core.resultset.codegen.ResultSetProcessorCodegenNames.REF_AGENTINSTANCECONTEXT;
import static eu.uk.ncl.pet5o.esper.epl.core.resultset.codegen.ResultSetProcessorCodegenNames.REF_AGGREGATIONSVC;
import static eu.uk.ncl.pet5o.esper.epl.core.resultset.codegen.ResultSetProcessorCodegenNames.REF_ISNEWDATA;
import static eu.uk.ncl.pet5o.esper.epl.expression.codegen.ExprForgeCodegenNames.REF_EXPREVALCONTEXT;

public class OrderByProcessorCodegenNames {
    final static String CLASSNAME_ORDERBYPROCESSORFACTORY = "OrderProcFactory";
    final static String CLASSNAME_ORDERBYPROCESSOR = "OrderProc";

    final static CodegenExpressionRef REF_OUTGOINGEVENTS = ref("orderOutgoingEvents");
    final static CodegenExpressionRef REF_GENERATINGEVENTS = ref("orderGeneratingEvents");
    final static CodegenExpressionRef REF_ORDERGROUPBYKEYS = ref("orderGroupByKeys");
    final static CodegenExpressionRef REF_ORDERCURRENTGENERATORS = ref("orderCurrentGenerators");
    final static CodegenExpressionRef REF_ORDERROLLUPLEVEL = ref("orderlevel");
    final static CodegenExpressionRef REF_ORDERKEYS = ref("orderKeys");

    final static CodegenExpressionRef REF_ORDERFIRSTEVENT = ref("first");
    final static CodegenExpressionRef REF_ORDERFIRSTSORTKEY = ref("firstSortKey");
    final static CodegenExpressionRef REF_ORDERSECONDEVENT = ref("second");
    final static CodegenExpressionRef REF_ORDERSECONDSORTKEY = ref("secondSortKey");

    final static List<CodegenNamedParam> SORTPLAIN_PARAMS = CodegenNamedParam.from(eu.uk.ncl.pet5o.esper.client.EventBean[].class, REF_OUTGOINGEVENTS.getRef(),
            eu.uk.ncl.pet5o.esper.client.EventBean[][].class, REF_GENERATINGEVENTS.getRef(),
            boolean.class, REF_ISNEWDATA.getRef(),
            ExprEvaluatorContext.class, REF_EXPREVALCONTEXT.getRef(),
            AggregationService.class, REF_AGGREGATIONSVC.getRef());

    final static List<CodegenNamedParam> SORTWGROUPKEYS_PARAMS = CodegenNamedParam.from(
            eu.uk.ncl.pet5o.esper.client.EventBean[].class, REF_OUTGOINGEVENTS.getRef(),
            eu.uk.ncl.pet5o.esper.client.EventBean[][].class, REF_GENERATINGEVENTS.getRef(),
            Object[].class, REF_ORDERGROUPBYKEYS.getRef(),
            boolean.class, REF_ISNEWDATA.getRef(),
            ExprEvaluatorContext.class, REF_EXPREVALCONTEXT.getRef(),
            AggregationService.class, REF_AGGREGATIONSVC.getRef());

    final static List<CodegenNamedParam> SORTROLLUP_PARAMS = CodegenNamedParam.from(
            eu.uk.ncl.pet5o.esper.client.EventBean[].class, REF_OUTGOINGEVENTS.getRef(),
            List.class, REF_ORDERCURRENTGENERATORS.getRef(),
            boolean.class, REF_ISNEWDATA.getRef(),
            AgentInstanceContext.class, REF_AGENTINSTANCECONTEXT.getRef(),
            AggregationService.class, REF_AGGREGATIONSVC.getRef());

    final static List<CodegenNamedParam> SORTTWOKEYS_PARAMS = CodegenNamedParam.from(
            eu.uk.ncl.pet5o.esper.client.EventBean.class, REF_ORDERFIRSTEVENT.getRef(),
            Object.class, REF_ORDERFIRSTSORTKEY.getRef(),
            eu.uk.ncl.pet5o.esper.client.EventBean.class, REF_ORDERSECONDEVENT.getRef(),
            Object.class, REF_ORDERSECONDSORTKEY.getRef());
}
