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
package eu.uk.ncl.pet5o.esper.core.context.factory;

import eu.uk.ncl.pet5o.esper.client.EventType;
import eu.uk.ncl.pet5o.esper.collection.Pair;
import eu.uk.ncl.pet5o.esper.core.context.activator.ViewableActivator;
import eu.uk.ncl.pet5o.esper.core.context.subselect.SubSelectStrategyCollection;
import eu.uk.ncl.pet5o.esper.core.context.util.AgentInstanceContext;
import eu.uk.ncl.pet5o.esper.core.service.EPServicesContext;
import eu.uk.ncl.pet5o.esper.core.service.StatementContext;
import eu.uk.ncl.pet5o.esper.core.service.speccompiled.StatementSpecCompiled;
import eu.uk.ncl.pet5o.esper.core.start.EPStatementStartMethodHelperUtil;
import eu.uk.ncl.pet5o.esper.epl.agg.service.common.AggregationService;
import eu.uk.ncl.pet5o.esper.epl.core.resultset.core.ResultSetProcessor;
import eu.uk.ncl.pet5o.esper.epl.core.resultset.core.ResultSetProcessorFactoryDesc;
import eu.uk.ncl.pet5o.esper.epl.expression.core.ExprNode;
import eu.uk.ncl.pet5o.esper.epl.expression.core.ExprValidationException;
import eu.uk.ncl.pet5o.esper.epl.join.hint.ExcludePlanHint;
import eu.uk.ncl.pet5o.esper.epl.join.hint.IndexHint;
import eu.uk.ncl.pet5o.esper.epl.join.table.EventTable;
import eu.uk.ncl.pet5o.esper.epl.lookup.SubordWMatchExprLookupStrategy;
import eu.uk.ncl.pet5o.esper.epl.lookup.SubordinateQueryPlanner;
import eu.uk.ncl.pet5o.esper.epl.lookup.SubordinateQueryPlannerUtil;
import eu.uk.ncl.pet5o.esper.epl.lookup.SubordinateWMatchExprQueryPlanResult;
import eu.uk.ncl.pet5o.esper.epl.named.*;
import eu.uk.ncl.pet5o.esper.epl.spec.OnTriggerType;
import eu.uk.ncl.pet5o.esper.epl.spec.OnTriggerWindowDesc;
import eu.uk.ncl.pet5o.esper.epl.view.OutputProcessViewFactory;
import eu.uk.ncl.pet5o.esper.util.StopCallback;
import eu.uk.ncl.pet5o.esper.view.View;

import java.util.List;

public class StatementAgentInstanceFactoryOnTriggerNamedWindow extends StatementAgentInstanceFactoryOnTriggerBase {
    private final ResultSetProcessorFactoryDesc resultSetProcessorPrototype;
    private final ResultSetProcessorFactoryDesc outputResultSetProcessorPrototype;
    private final NamedWindowOnExprFactory onExprFactory;
    private final OutputProcessViewFactory outputProcessViewFactory;
    private final NamedWindowProcessor processor;

    private final SubordinateWMatchExprQueryPlanResult queryPlan;

    public StatementAgentInstanceFactoryOnTriggerNamedWindow(final StatementContext statementContext, StatementSpecCompiled statementSpec, EPServicesContext services, ViewableActivator activator, SubSelectStrategyCollection subSelectStrategyCollection, ResultSetProcessorFactoryDesc resultSetProcessorPrototype, ExprNode validatedJoin, ResultSetProcessorFactoryDesc outputResultSetProcessorPrototype, NamedWindowOnExprFactory onExprFactory, OutputProcessViewFactory outputProcessViewFactory, EventType activatorResultEventType, final NamedWindowProcessor processor, List<StopCallback> stopCallbacks)
            throws ExprValidationException {
        super(statementContext, statementSpec, services, activator, subSelectStrategyCollection);
        this.resultSetProcessorPrototype = resultSetProcessorPrototype;
        this.outputResultSetProcessorPrototype = outputResultSetProcessorPrototype;
        this.onExprFactory = onExprFactory;
        this.outputProcessViewFactory = outputProcessViewFactory;
        this.processor = processor;

        IndexHintPair pair = getIndexHintPair(statementContext, statementSpec);
        IndexHint indexHint = pair.getIndexHint();
        ExcludePlanHint excludePlanHint = pair.getExcludePlanHint();

        queryPlan = SubordinateQueryPlanner.planOnExpression(
                validatedJoin, activatorResultEventType, indexHint, processor.isEnableSubqueryIndexShare(), -1, excludePlanHint,
                processor.isVirtualDataWindow(), processor.getEventTableIndexMetadataRepo(), processor.getNamedWindowType(),
                processor.getOptionalUniqueKeyProps(), false, statementContext.getStatementName(), statementContext.getStatementId(), statementContext.getAnnotations(), statementContext.getEngineImportService());
        if (queryPlan.getIndexDescs() != null) {
            SubordinateQueryPlannerUtil.addIndexMetaAndRef(queryPlan.getIndexDescs(), processor.getEventTableIndexMetadataRepo(), statementContext.getStatementName());
            stopCallbacks.add(new StopCallback() {
                public void stop() {
                    for (int i = 0; i < queryPlan.getIndexDescs().length; i++) {
                        boolean last = processor.getEventTableIndexMetadataRepo().removeIndexReference(queryPlan.getIndexDescs()[i].getIndexMultiKey(), statementContext.getStatementName());
                        if (last) {
                            processor.getEventTableIndexMetadataRepo().removeIndex(queryPlan.getIndexDescs()[i].getIndexMultiKey());
                            processor.removeAllInstanceIndexes(queryPlan.getIndexDescs()[i].getIndexMultiKey());
                        }
                    }
                }
            });
        }
        SubordinateQueryPlannerUtil.queryPlanLogOnExpr(processor.getRootView().isQueryPlanLogging(), NamedWindowRootView.getQueryPlanLog(),
                queryPlan, statementContext.getAnnotations(), statementContext.getEngineImportService());
    }

    public OnExprViewResult determineOnExprView(AgentInstanceContext agentInstanceContext, List<StopCallback> stopCallbacks, boolean isRecoveringReslient) {
        // get result set processor and aggregation services
        Pair<ResultSetProcessor, AggregationService> pair = EPStatementStartMethodHelperUtil.startResultSetAndAggregation(resultSetProcessorPrototype, agentInstanceContext, false, null);

        // get named window processor instance
        NamedWindowProcessorInstance processorInstance = processor.getProcessorInstance(agentInstanceContext);

        // obtain on-expr view
        EventTable[] indexes = null;
        if (queryPlan.getIndexDescs() != null) {
            indexes = SubordinateQueryPlannerUtil.realizeTables(queryPlan.getIndexDescs(), processor.getNamedWindowType(), processorInstance.getRootViewInstance().getIndexRepository(), processorInstance.getRootViewInstance().getDataWindowContents(), processorInstance.getTailViewInstance().getAgentInstanceContext(), isRecoveringReslient);
        }
        SubordWMatchExprLookupStrategy strategy = queryPlan.getFactory().realize(indexes, agentInstanceContext, processorInstance.getRootViewInstance().getDataWindowContents(), processorInstance.getRootViewInstance().getVirtualDataWindow());
        NamedWindowOnExprView onExprBaseView = onExprFactory.make(strategy, processorInstance.getRootViewInstance(), agentInstanceContext, pair.getFirst());

        return new OnExprViewResult(onExprBaseView, pair.getSecond());
    }

    public void assignExpressions(StatementAgentInstanceFactoryResult result) {
    }

    public void unassignExpressions() {
    }

    public View determineFinalOutputView(AgentInstanceContext agentInstanceContext, View onExprView) {

        if ((statementSpec.getOnTriggerDesc().getOnTriggerType() == OnTriggerType.ON_DELETE) ||
                (statementSpec.getOnTriggerDesc().getOnTriggerType() == OnTriggerType.ON_UPDATE) ||
                (statementSpec.getOnTriggerDesc().getOnTriggerType() == OnTriggerType.ON_MERGE)) {

            ResultSetProcessor outputResultSetProcessor = outputResultSetProcessorPrototype.getResultSetProcessorFactory().instantiate(null, null, agentInstanceContext);
            View outputView = outputProcessViewFactory.makeView(outputResultSetProcessor, agentInstanceContext);
            onExprView.addView(outputView);
            return outputView;
        }

        return onExprView;
    }

    protected static IndexHintPair getIndexHintPair(StatementContext statementContext, StatementSpecCompiled statementSpec)
            throws ExprValidationException {
        IndexHint indexHint = IndexHint.getIndexHint(statementContext.getAnnotations());
        ExcludePlanHint excludePlanHint = null;
        if (statementSpec.getOnTriggerDesc() instanceof OnTriggerWindowDesc) {
            OnTriggerWindowDesc onTriggerWindowDesc = (OnTriggerWindowDesc) statementSpec.getOnTriggerDesc();
            String[] streamNames = {onTriggerWindowDesc.getOptionalAsName(), statementSpec.getStreamSpecs()[0].getOptionalStreamName()};
            excludePlanHint = ExcludePlanHint.getHint(streamNames, statementContext);
        }
        return new IndexHintPair(indexHint, excludePlanHint);
    }

    public static class IndexHintPair {
        private final IndexHint indexHint;
        private final ExcludePlanHint excludePlanHint;

        public IndexHintPair(IndexHint indexHint, ExcludePlanHint excludePlanHint) {
            this.indexHint = indexHint;
            this.excludePlanHint = excludePlanHint;
        }

        public IndexHint getIndexHint() {
            return indexHint;
        }

        public ExcludePlanHint getExcludePlanHint() {
            return excludePlanHint;
        }
    }
}
