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
package eu.uk.ncl.pet5o.esper.epl.core.resultset.rowpergrouprollup;

import com.espertech.esper.client.EventBean;
import com.espertech.esper.codegen.base.CodegenClassScope;
import com.espertech.esper.codegen.base.CodegenMember;
import com.espertech.esper.codegen.base.CodegenMethodNode;
import com.espertech.esper.codegen.core.CodegenInstanceAux;
import com.espertech.esper.collection.ArrayEventIterator;
import com.espertech.esper.collection.UniformPair;
import com.espertech.esper.core.context.util.AgentInstanceContext;
import com.espertech.esper.epl.agg.service.common.AggregationService;
import com.espertech.esper.epl.core.orderby.OrderByProcessor;
import com.espertech.esper.epl.core.resultset.core.ResultSetProcessorHelperFactory;
import com.espertech.esper.epl.core.resultset.core.ResultSetProcessorUtil;
import com.espertech.esper.epl.core.resultset.grouped.ResultSetProcessorGroupedUtil;
import com.espertech.esper.metrics.instrumentation.InstrumentationHelper;
import com.espertech.esper.view.Viewable;

import java.util.Iterator;

import static com.espertech.esper.codegen.model.expression.CodegenExpressionBuilder.*;
import static com.espertech.esper.codegen.model.expression.CodegenExpressionBuilder.constant;
import static com.espertech.esper.codegen.model.expression.CodegenExpressionBuilder.constantFalse;
import static com.espertech.esper.codegen.model.expression.CodegenExpressionBuilder.constantNull;
import static com.espertech.esper.codegen.model.expression.CodegenExpressionBuilder.constantTrue;
import static com.espertech.esper.codegen.model.expression.CodegenExpressionBuilder.exprDotMethod;
import static com.espertech.esper.codegen.model.expression.CodegenExpressionBuilder.localMethod;
import static com.espertech.esper.codegen.model.expression.CodegenExpressionBuilder.member;
import static com.espertech.esper.codegen.model.expression.CodegenExpressionBuilder.newArrayByLength;
import static com.espertech.esper.codegen.model.expression.CodegenExpressionBuilder.ref;
import static com.espertech.esper.codegen.model.expression.CodegenExpressionBuilder.staticMethod;
import static com.espertech.esper.epl.core.resultset.codegen.ResultSetProcessorCodegenNames.*;
import static com.espertech.esper.epl.core.resultset.codegen.ResultSetProcessorCodegenNames.REF_AGENTINSTANCECONTEXT;
import static com.espertech.esper.epl.core.resultset.codegen.ResultSetProcessorCodegenNames.REF_AGGREGATIONSVC;
import static com.espertech.esper.epl.core.resultset.codegen.ResultSetProcessorCodegenNames.REF_ISSYNTHESIZE;
import static com.espertech.esper.epl.core.resultset.codegen.ResultSetProcessorCodegenNames.REF_NEWDATA;
import static com.espertech.esper.epl.core.resultset.codegen.ResultSetProcessorCodegenNames.REF_OLDDATA;
import static com.espertech.esper.epl.core.resultset.core.ResultSetProcessorUtil.METHOD_TOPAIRNULLIFALLNULL;
import static com.espertech.esper.epl.core.resultset.grouped.ResultSetProcessorGroupedUtil.METHOD_APPLYAGGVIEWRESULTKEYEDVIEW;

public class ResultSetProcessorRowPerGroupRollupUnbound extends ResultSetProcessorRowPerGroupRollupImpl {

    private final static String NAME_UNBOUNDHELPER = "unboundHelper";

    private final ResultSetProcessorRowPerGroupRollupUnboundHelper unboundHelper;

    ResultSetProcessorRowPerGroupRollupUnbound(ResultSetProcessorRowPerGroupRollupFactory prototype, OrderByProcessor orderByProcessor, AggregationService aggregationService, AgentInstanceContext agentInstanceContext) {
        super(prototype, orderByProcessor, aggregationService, agentInstanceContext);
        unboundHelper = prototype.getResultSetProcessorHelperFactory().makeRSRowPerGroupRollupSnapshotUnbound(agentInstanceContext, this, prototype.getGroupKeyTypes(), prototype.getNumStreams());
    }

    @Override
    public void stop() {
        super.stop();
        unboundHelper.destroy();
    }

    static void stopMethodUnboundCodegen(ResultSetProcessorRowPerGroupRollupForge forge, CodegenClassScope classScope, CodegenMethodNode method, CodegenInstanceAux instance) {
        ResultSetProcessorRowPerGroupRollupImpl.stopMethodCodegenBound(method, instance);
        method.getBlock().exprDotMethod(ref(NAME_UNBOUNDHELPER), "destroy");
    }

    @Override
    public void applyViewResult(com.espertech.esper.client.EventBean[] newData, com.espertech.esper.client.EventBean[] oldData) {
        Object[][] newDataMultiKey = generateGroupKeysView(newData, unboundHelper.getBuffer(), true);
        Object[][] oldDataMultiKey = generateGroupKeysView(oldData, unboundHelper.getBuffer(), false);
        com.espertech.esper.client.EventBean[] eventsPerStream = new com.espertech.esper.client.EventBean[1];
        ResultSetProcessorGroupedUtil.applyAggViewResultKeyedView(aggregationService, agentInstanceContext, newData, newDataMultiKey, oldData, oldDataMultiKey, eventsPerStream);
    }

    static void applyViewResultUnboundCodegen(ResultSetProcessorRowPerGroupRollupForge forge, CodegenClassScope classScope, CodegenMethodNode method, CodegenInstanceAux instance) {
        CodegenMethodNode generateGroupKeysView = generateGroupKeysViewCodegen(forge, classScope, instance);

        method.getBlock()
                .declareVar(Object[][].class, "newDataMultiKey", localMethod(generateGroupKeysView, REF_NEWDATA, exprDotMethod(ref(NAME_UNBOUNDHELPER), "getBuffer"), constantTrue()))
                .declareVar(Object[][].class, "oldDataMultiKey", localMethod(generateGroupKeysView, REF_OLDDATA, exprDotMethod(ref(NAME_UNBOUNDHELPER), "getBuffer"), constantFalse()))
                .declareVar(com.espertech.esper.client.EventBean[].class, "eventsPerStream", newArrayByLength(com.espertech.esper.client.EventBean.class, constant(1)))
                .staticMethod(ResultSetProcessorGroupedUtil.class, METHOD_APPLYAGGVIEWRESULTKEYEDVIEW, REF_AGGREGATIONSVC, REF_AGENTINSTANCECONTEXT, REF_NEWDATA, ref("newDataMultiKey"), REF_OLDDATA, ref("oldDataMultiKey"), ref("eventsPerStream"));
    }

    @Override
    public UniformPair<com.espertech.esper.client.EventBean[]> processViewResult(com.espertech.esper.client.EventBean[] newData, com.espertech.esper.client.EventBean[] oldData, boolean isSynthesize) {
        if (InstrumentationHelper.ENABLED) {
            InstrumentationHelper.get().qResultSetProcessGroupedRowPerGroup();
        }

        Object[][] newDataMultiKey = generateGroupKeysView(newData, unboundHelper.getBuffer(), true);
        Object[][] oldDataMultiKey = generateGroupKeysView(oldData, unboundHelper.getBuffer(), false);

        com.espertech.esper.client.EventBean[] selectOldEvents = null;
        if (prototype.isSelectRStream()) {
            selectOldEvents = generateOutputEventsView(unboundHelper.getBuffer(), false, isSynthesize);
        }

        com.espertech.esper.client.EventBean[] eventsPerStream = new com.espertech.esper.client.EventBean[1];
        ResultSetProcessorGroupedUtil.applyAggViewResultKeyedView(aggregationService, agentInstanceContext, newData, newDataMultiKey, oldData, oldDataMultiKey, eventsPerStream);

        com.espertech.esper.client.EventBean[] selectNewEvents = generateOutputEventsView(unboundHelper.getBuffer(), true, isSynthesize);

        if (InstrumentationHelper.ENABLED) {
            InstrumentationHelper.get().aResultSetProcessGroupedRowPerGroup(selectNewEvents, selectOldEvents);
        }

        return ResultSetProcessorUtil.toPairNullIfAllNull(selectNewEvents, selectOldEvents);
    }

    static void processViewResultUnboundCodegen(ResultSetProcessorRowPerGroupRollupForge forge, CodegenClassScope classScope, CodegenMethodNode method, CodegenInstanceAux instance) {
        CodegenMember factory = classScope.makeAddMember(ResultSetProcessorHelperFactory.class, forge.getResultSetProcessorHelperFactory());
        CodegenMember groupKeyTypes = classScope.makeAddMember(Class[].class, forge.getGroupKeyTypes());
        instance.addMember(NAME_UNBOUNDHELPER, ResultSetProcessorRowPerGroupRollupUnboundHelper.class);
        instance.getServiceCtor().getBlock().assignRef(NAME_UNBOUNDHELPER, exprDotMethod(member(factory.getMemberId()), "makeRSRowPerGroupRollupSnapshotUnbound", REF_AGENTINSTANCECONTEXT, ref("this"), member(groupKeyTypes.getMemberId()), constant(forge.getNumStreams())));

        CodegenMethodNode generateGroupKeysView = generateGroupKeysViewCodegen(forge, classScope, instance);
        CodegenMethodNode generateOutputEventsView = generateOutputEventsViewCodegen(forge, classScope, instance);

        method.getBlock()
                .declareVar(Object[][].class, "newDataMultiKey", localMethod(generateGroupKeysView, REF_NEWDATA, exprDotMethod(ref(NAME_UNBOUNDHELPER), "getBuffer"), constantTrue()))
                .declareVar(Object[][].class, "oldDataMultiKey", localMethod(generateGroupKeysView, REF_OLDDATA, exprDotMethod(ref(NAME_UNBOUNDHELPER), "getBuffer"), constantFalse()))
                .declareVar(com.espertech.esper.client.EventBean[].class, "selectOldEvents", forge.isSelectRStream() ? localMethod(generateOutputEventsView, exprDotMethod(ref(NAME_UNBOUNDHELPER), "getBuffer"), constantFalse(), REF_ISSYNTHESIZE) : constantNull())
                .declareVar(com.espertech.esper.client.EventBean[].class, "eventsPerStream", newArrayByLength(com.espertech.esper.client.EventBean.class, constant(1)))
                .staticMethod(ResultSetProcessorGroupedUtil.class, METHOD_APPLYAGGVIEWRESULTKEYEDVIEW, REF_AGGREGATIONSVC, REF_AGENTINSTANCECONTEXT, REF_NEWDATA, ref("newDataMultiKey"), REF_OLDDATA, ref("oldDataMultiKey"), ref("eventsPerStream"))
                .declareVar(com.espertech.esper.client.EventBean[].class, "selectNewEvents", localMethod(generateOutputEventsView, exprDotMethod(ref(NAME_UNBOUNDHELPER), "getBuffer"), constantTrue(), REF_ISSYNTHESIZE))
                .methodReturn(staticMethod(ResultSetProcessorUtil.class, METHOD_TOPAIRNULLIFALLNULL, ref("selectNewEvents"), ref("selectOldEvents")));
    }

    @Override
    public Iterator<EventBean> getIterator(Viewable parent) {
        com.espertech.esper.client.EventBean[] output = generateOutputEventsView(unboundHelper.getBuffer(), true, true);
        return new ArrayEventIterator(output);
    }

    static void getIteratorViewUnboundCodegen(ResultSetProcessorRowPerGroupRollupForge forge, CodegenClassScope classScope, CodegenMethodNode method, CodegenInstanceAux instance) {
        CodegenMethodNode generateOutputEventsView = generateOutputEventsViewCodegen(forge, classScope, instance);

        method.getBlock().declareVar(com.espertech.esper.client.EventBean[].class, "output", localMethod(generateOutputEventsView, exprDotMethod(ref(NAME_UNBOUNDHELPER), "getBuffer"), constantTrue(), constantTrue()))
                .methodReturn(newInstance(ArrayEventIterator.class, ref("output")));
    }
}
