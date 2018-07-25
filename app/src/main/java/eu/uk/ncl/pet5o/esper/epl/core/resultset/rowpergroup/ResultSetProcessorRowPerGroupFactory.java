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
package eu.uk.ncl.pet5o.esper.epl.core.resultset.rowpergroup;

import eu.uk.ncl.pet5o.esper.client.EventType;
import eu.uk.ncl.pet5o.esper.core.context.util.AgentInstanceContext;
import eu.uk.ncl.pet5o.esper.epl.agg.service.common.AggregationService;
import eu.uk.ncl.pet5o.esper.epl.core.orderby.OrderByProcessor;
import eu.uk.ncl.pet5o.esper.epl.core.resultset.core.ResultSetProcessor;
import eu.uk.ncl.pet5o.esper.epl.core.resultset.core.ResultSetProcessorFactory;
import eu.uk.ncl.pet5o.esper.epl.core.resultset.core.ResultSetProcessorHelperFactory;
import eu.uk.ncl.pet5o.esper.epl.core.resultset.core.ResultSetProcessorOutputConditionType;
import eu.uk.ncl.pet5o.esper.epl.core.select.SelectExprProcessor;
import eu.uk.ncl.pet5o.esper.epl.expression.core.ExprEvaluator;
import eu.uk.ncl.pet5o.esper.epl.expression.core.ExprNode;
import eu.uk.ncl.pet5o.esper.epl.spec.OutputLimitLimitType;
import eu.uk.ncl.pet5o.esper.epl.spec.OutputLimitSpec;
import eu.uk.ncl.pet5o.esper.epl.view.OutputConditionPolledFactory;

/**
 * Result set processor prototype for the fully-grouped case:
 * there is a group-by and all non-aggregation event properties in the select clause are listed in the group by,
 * and there are aggregation functions.
 */
public class ResultSetProcessorRowPerGroupFactory implements ResultSetProcessorFactory {
    private final EventType resultEventType;
    private final SelectExprProcessor selectExprProcessor;
    private final ExprNode[] groupKeyNodeExpressions;
    private final ExprEvaluator groupKeyNode;
    private final ExprEvaluator[] groupKeyNodes;
    private final ExprEvaluator optionalHavingNode;
    private final boolean isSorting;
    private final boolean isSelectRStream;
    private final boolean isUnidirectional;
    private final OutputLimitSpec outputLimitSpec;
    private final boolean isHistoricalOnly;
    private final ResultSetProcessorHelperFactory resultSetProcessorHelperFactory;
    private final ResultSetProcessorOutputConditionType outputConditionType;
    private final int numStreams;
    private final OutputConditionPolledFactory optionalOutputFirstConditionFactory;
    private final boolean unboundedProcessor;
    private final Class[] groupKeyTypes;

    ResultSetProcessorRowPerGroupFactory(EventType resultEventType,
                                                SelectExprProcessor selectExprProcessor,
                                                ExprNode[] groupKeyNodeExpressions,
                                                ExprEvaluator groupKeyNode,
                                                ExprEvaluator[] groupKeyNodes,
                                                ExprEvaluator optionalHavingNode,
                                                boolean isSelectRStream,
                                                boolean isUnidirectional,
                                                OutputLimitSpec outputLimitSpec,
                                                boolean isSorting,
                                                boolean isHistoricalOnly,
                                                ResultSetProcessorHelperFactory resultSetProcessorHelperFactory,
                                                ResultSetProcessorOutputConditionType outputConditionType,
                                                int numStreams,
                                                OutputConditionPolledFactory optionalOutputFirstConditionFactory,
                                                boolean unboundedProcessor,
                                                Class[] groupKeyTypes) {
        this.resultEventType = resultEventType;
        this.groupKeyNodeExpressions = groupKeyNodeExpressions;
        this.selectExprProcessor = selectExprProcessor;
        this.groupKeyNodes = groupKeyNodes;
        this.groupKeyNode = groupKeyNode;
        this.optionalHavingNode = optionalHavingNode;
        this.isSorting = isSorting;
        this.isSelectRStream = isSelectRStream;
        this.isUnidirectional = isUnidirectional;
        this.outputLimitSpec = outputLimitSpec;
        this.isHistoricalOnly = isHistoricalOnly;
        this.resultSetProcessorHelperFactory = resultSetProcessorHelperFactory;
        this.outputConditionType = outputConditionType;
        this.numStreams = numStreams;
        this.optionalOutputFirstConditionFactory = optionalOutputFirstConditionFactory;
        this.unboundedProcessor = unboundedProcessor;
        this.groupKeyTypes = groupKeyTypes;
    }

    public ResultSetProcessor instantiate(OrderByProcessor orderByProcessor, AggregationService aggregationService, AgentInstanceContext agentInstanceContext) {
        if (unboundedProcessor) {
            return new ResultSetProcessorRowPerGroupUnbound(ResultSetProcessorRowPerGroupFactory.this, selectExprProcessor, orderByProcessor, aggregationService, agentInstanceContext);
        }
        return new ResultSetProcessorRowPerGroupImpl(ResultSetProcessorRowPerGroupFactory.this, selectExprProcessor, orderByProcessor, aggregationService, agentInstanceContext);
    }

    public EventType getResultEventType() {
        return resultEventType;
    }

    public ExprEvaluator[] getGroupKeyNodes() {
        return groupKeyNodes;
    }

    public ExprEvaluator getGroupKeyNode() {
        return groupKeyNode;
    }

    public ExprEvaluator getOptionalHavingNode() {
        return optionalHavingNode;
    }

    public boolean isSorting() {
        return isSorting;
    }

    public boolean isSelectRStream() {
        return isSelectRStream;
    }

    public boolean isUnidirectional() {
        return isUnidirectional;
    }

    public OutputLimitSpec getOutputLimitSpec() {
        return outputLimitSpec;
    }

    public ExprNode[] getGroupKeyNodeExpressions() {
        return groupKeyNodeExpressions;
    }

    public boolean isHistoricalOnly() {
        return isHistoricalOnly;
    }

    public boolean isOutputLast() {
        return outputLimitSpec != null && outputLimitSpec.getDisplayLimit() == OutputLimitLimitType.LAST;
    }

    public boolean isOutputAll() {
        return outputLimitSpec != null && outputLimitSpec.getDisplayLimit() == OutputLimitLimitType.ALL;
    }

    public ResultSetProcessorOutputConditionType getOutputConditionType() {
        return outputConditionType;
    }

    public int getNumStreams() {
        return numStreams;
    }

    public boolean isOutputFirst() {
        return outputLimitSpec != null && outputLimitSpec.getDisplayLimit() == OutputLimitLimitType.FIRST;
    }

    public OutputConditionPolledFactory getOptionalOutputFirstConditionFactory() {
        return optionalOutputFirstConditionFactory;
    }

    public ResultSetProcessorHelperFactory getResultSetProcessorHelperFactory() {
        return resultSetProcessorHelperFactory;
    }

    public Class[] getGroupKeyTypes() {
        return groupKeyTypes;
    }
}
