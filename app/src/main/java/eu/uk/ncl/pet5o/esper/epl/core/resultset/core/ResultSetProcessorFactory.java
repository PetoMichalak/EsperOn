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
package eu.uk.ncl.pet5o.esper.epl.core.resultset.core;

import eu.uk.ncl.pet5o.esper.core.context.util.AgentInstanceContext;
import eu.uk.ncl.pet5o.esper.epl.agg.service.common.AggregationService;
import eu.uk.ncl.pet5o.esper.epl.core.orderby.OrderByProcessor;

/**
 * Processor prototype for result sets for instances that apply the select-clause, group-by-clause and having-clauses
 * as supplied.
 */
public interface ResultSetProcessorFactory {

    ResultSetProcessor instantiate(OrderByProcessor orderByProcessor, AggregationService aggregationService, AgentInstanceContext agentInstanceContext);
}
