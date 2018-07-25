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

import eu.uk.ncl.pet5o.esper.client.EventBean;
import eu.uk.ncl.pet5o.esper.epl.agg.service.common.AggregationRowRemovedCallback;
import eu.uk.ncl.pet5o.esper.epl.agg.service.common.AggregationService;
import eu.uk.ncl.pet5o.esper.epl.core.resultset.core.ResultSetProcessor;
import eu.uk.ncl.pet5o.esper.epl.core.select.SelectExprProcessor;
import eu.uk.ncl.pet5o.esper.epl.expression.core.ExprEvaluatorContext;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

public interface ResultSetProcessorRowPerGroup extends ResultSetProcessor, AggregationRowRemovedCallback {
    Object generateGroupKeySingle(eu.uk.ncl.pet5o.esper.client.EventBean[] eventsPerStream, boolean isNewData);

    boolean hasHavingClause();

    boolean evaluateHavingClause(eu.uk.ncl.pet5o.esper.client.EventBean[] eventsPerStream, boolean isNewData, ExprEvaluatorContext exprEvaluatorContext);

    boolean isSelectRStream();

    AggregationService getAggregationService();

    ExprEvaluatorContext getAgentInstanceContext();

    SelectExprProcessor getSelectExprProcessor();

    eu.uk.ncl.pet5o.esper.client.EventBean generateOutputBatchedNoSortWMap(boolean join, Object mk, eu.uk.ncl.pet5o.esper.client.EventBean[] eventsPerStream, boolean isNewData, boolean isSynthesize);

    void generateOutputBatchedArrFromIterator(boolean join, Iterator<Map.Entry<Object, EventBean[]>> keysAndEvents, boolean isNewData, boolean isSynthesize, List<EventBean> resultEvents, List<Object> optSortKeys);
}
