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
package eu.uk.ncl.pet5o.esper.epl.core.resultset.handthru;

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
import eu.uk.ncl.pet5o.esper.epl.spec.OutputLimitLimitType;
import eu.uk.ncl.pet5o.esper.epl.spec.OutputLimitSpec;

/**
 * Result set processor prototype for the simplest case: no aggregation functions used in the select clause, and no group-by.
 */
public class ResultSetProcessorSimpleFactory implements ResultSetProcessorFactory {
    private final EventType resultEventType;
    private final boolean isSelectRStream;
    private final SelectExprProcessor selectExprProcessor;
    private final ExprEvaluator optionalHavingExpr;
    private final OutputLimitSpec outputLimitSpec;
    private final ResultSetProcessorOutputConditionType outputConditionType;
    private final ResultSetProcessorHelperFactory resultSetProcessorHelperFactory;
    private final int numStreams;

    public ResultSetProcessorSimpleFactory(EventType resultEventType,
                                           SelectExprProcessor selectExprProcessor,
                                           ExprEvaluator optionalHavingNode,
                                           boolean isSelectRStream,
                                           OutputLimitSpec outputLimitSpec,
                                           ResultSetProcessorOutputConditionType outputConditionType,
                                           ResultSetProcessorHelperFactory resultSetProcessorHelperFactory,
                                           int numStreams) {
        this.resultEventType = resultEventType;
        this.selectExprProcessor = selectExprProcessor;
        this.optionalHavingExpr = optionalHavingNode;
        this.isSelectRStream = isSelectRStream;
        this.outputLimitSpec = outputLimitSpec;
        this.outputConditionType = outputConditionType;
        this.resultSetProcessorHelperFactory = resultSetProcessorHelperFactory;
        this.numStreams = numStreams;
    }

    public ResultSetProcessor instantiate(OrderByProcessor orderByProcessor, AggregationService aggregationService, AgentInstanceContext agentInstanceContext) {
        return new ResultSetProcessorSimpleImpl(ResultSetProcessorSimpleFactory.this, selectExprProcessor, orderByProcessor, agentInstanceContext);
    }

    public EventType getResultEventType() {
        return resultEventType;
    }

    public boolean isSelectRStream() {
        return isSelectRStream;
    }

    public ExprEvaluator getOptionalHavingNode() {
        return optionalHavingExpr;
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

    public ResultSetProcessorHelperFactory getResultSetProcessorHelperFactory() {
        return resultSetProcessorHelperFactory;
    }

    public int getNumStreams() {
        return numStreams;
    }
}
