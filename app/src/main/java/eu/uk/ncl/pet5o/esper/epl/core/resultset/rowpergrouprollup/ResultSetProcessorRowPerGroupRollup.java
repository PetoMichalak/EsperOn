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

import eu.uk.ncl.pet5o.esper.client.EventBean;
import eu.uk.ncl.pet5o.esper.epl.agg.service.common.AggregationGroupByRollupDesc;
import eu.uk.ncl.pet5o.esper.epl.agg.service.common.AggregationGroupByRollupLevel;
import eu.uk.ncl.pet5o.esper.epl.agg.service.common.AggregationRowRemovedCallback;
import eu.uk.ncl.pet5o.esper.epl.agg.service.common.AggregationService;
import eu.uk.ncl.pet5o.esper.epl.core.resultset.core.ResultSetProcessor;
import eu.uk.ncl.pet5o.esper.epl.expression.core.ExprEvaluatorContext;

import java.util.List;
import java.util.Map;

public interface ResultSetProcessorRowPerGroupRollup extends ResultSetProcessor, AggregationRowRemovedCallback {
    AggregationService getAggregationService();

    ExprEvaluatorContext getAgentInstanceContext();

    boolean isSelectRStream();

    AggregationGroupByRollupDesc getGroupByRollupDesc();

    Object generateGroupKeySingle(eu.uk.ncl.pet5o.esper.client.EventBean[] eventsPerStream, boolean isNewData);

    void generateOutputBatchedMapUnsorted(boolean join, Object mk, AggregationGroupByRollupLevel level, eu.uk.ncl.pet5o.esper.client.EventBean[] eventsPerStream, boolean isNewData, boolean isSynthesize, Map<Object, EventBean> resultEvents);

    void generateOutputBatched(Object mk, AggregationGroupByRollupLevel level, eu.uk.ncl.pet5o.esper.client.EventBean[] eventsPerStream, boolean isNewData, boolean isSynthesize, List<EventBean> resultEvents, List<Object> optSortKeys);
}

