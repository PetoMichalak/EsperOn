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
package eu.uk.ncl.pet5o.esper.epl.core.resultset.rowforall;

import eu.uk.ncl.pet5o.esper.epl.agg.service.common.AggregationService;
import eu.uk.ncl.pet5o.esper.epl.core.resultset.core.ResultSetProcessor;
import eu.uk.ncl.pet5o.esper.epl.expression.core.ExprEvaluatorContext;

public interface ResultSetProcessorRowForAll extends ResultSetProcessor {
    eu.uk.ncl.pet5o.esper.client.EventBean[] getSelectListEventsAsArray(boolean isNewData, boolean isSynthesize, boolean join);

    boolean isSelectRStream();

    AggregationService getAggregationService();

    ExprEvaluatorContext getExprEvaluatorContext();
}
