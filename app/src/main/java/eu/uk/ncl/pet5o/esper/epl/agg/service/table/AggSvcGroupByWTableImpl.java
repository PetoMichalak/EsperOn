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
package eu.uk.ncl.pet5o.esper.epl.agg.service.table;

import eu.uk.ncl.pet5o.esper.epl.agg.access.AggregationAccessorSlotPair;
import eu.uk.ncl.pet5o.esper.epl.agg.access.AggregationAgent;
import eu.uk.ncl.pet5o.esper.epl.expression.core.ExprEvaluatorContext;
import eu.uk.ncl.pet5o.esper.epl.expression.core.ExprNode;
import eu.uk.ncl.pet5o.esper.epl.table.mgmt.TableColumnMethodPair;
import eu.uk.ncl.pet5o.esper.epl.table.mgmt.TableMetadata;
import eu.uk.ncl.pet5o.esper.epl.table.mgmt.TableStateInstanceGrouped;

/**
 * Implementation for handling aggregation with grouping by group-keys.
 */
public class AggSvcGroupByWTableImpl extends AggSvcGroupByWTableBase {
    public AggSvcGroupByWTableImpl(TableMetadata tableMetadata, TableColumnMethodPair[] methodPairs, AggregationAccessorSlotPair[] accessors, boolean join, TableStateInstanceGrouped tableStateInstance, int[] targetStates, ExprNode[] accessStateExpr, AggregationAgent[] agents) {
        super(tableMetadata, methodPairs, accessors, join, tableStateInstance, targetStates, accessStateExpr, agents);
    }

    public void applyEnterInternal(eu.uk.ncl.pet5o.esper.client.EventBean[] eventsPerStream, Object groupByKey, ExprEvaluatorContext exprEvaluatorContext) {
        applyEnterGroupKey(eventsPerStream, groupByKey, exprEvaluatorContext);
    }

    public void applyLeaveInternal(eu.uk.ncl.pet5o.esper.client.EventBean[] eventsPerStream, Object groupByKey, ExprEvaluatorContext exprEvaluatorContext) {
        applyLeaveGroupKey(eventsPerStream, groupByKey, exprEvaluatorContext);
    }
}
