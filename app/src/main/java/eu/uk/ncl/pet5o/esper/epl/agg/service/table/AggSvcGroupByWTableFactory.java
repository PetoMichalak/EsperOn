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

import eu.uk.ncl.pet5o.esper.core.context.util.AgentInstanceContext;
import eu.uk.ncl.pet5o.esper.epl.agg.access.AggregationAccessorSlotPair;
import eu.uk.ncl.pet5o.esper.epl.agg.access.AggregationAgent;
import eu.uk.ncl.pet5o.esper.epl.agg.service.common.AggregationGroupByRollupDesc;
import eu.uk.ncl.pet5o.esper.epl.agg.service.common.AggregationService;
import eu.uk.ncl.pet5o.esper.epl.agg.service.common.AggregationServiceFactory;
import eu.uk.ncl.pet5o.esper.epl.core.engineimport.EngineImportService;
import eu.uk.ncl.pet5o.esper.epl.expression.core.ExprNode;
import eu.uk.ncl.pet5o.esper.epl.table.mgmt.TableColumnMethodPair;
import eu.uk.ncl.pet5o.esper.epl.table.mgmt.TableMetadata;
import eu.uk.ncl.pet5o.esper.epl.table.mgmt.TableStateInstanceGrouped;

/**
 * Implementation for handling aggregation with grouping by group-keys.
 */
public class AggSvcGroupByWTableFactory implements AggregationServiceFactory {
    private final TableMetadata tableMetadata;
    private final TableColumnMethodPair[] methodPairs;
    private final AggregationAccessorSlotPair[] accessors;
    private final boolean isJoin;
    private final int[] targetStates;
    private final ExprNode[] accessStateExpr;
    private final AggregationAgent[] agents;
    private final AggregationGroupByRollupDesc groupByRollupDesc;

    public AggSvcGroupByWTableFactory(TableMetadata tableMetadata, TableColumnMethodPair[] methodPairs, AggregationAccessorSlotPair[] accessors, boolean join, int[] targetStates, ExprNode[] accessStateExpr, AggregationAgent[] agents, AggregationGroupByRollupDesc groupByRollupDesc) {
        this.tableMetadata = tableMetadata;
        this.methodPairs = methodPairs;
        this.accessors = accessors;
        isJoin = join;
        this.targetStates = targetStates;
        this.accessStateExpr = accessStateExpr;
        this.agents = agents;
        this.groupByRollupDesc = groupByRollupDesc;
    }

    public AggregationService makeService(AgentInstanceContext agentInstanceContext, EngineImportService engineImportService, boolean isSubquery, Integer subqueryNumber) {
        TableStateInstanceGrouped tableState = (TableStateInstanceGrouped) agentInstanceContext.getStatementContext().getTableService().getState(tableMetadata.getTableName(), agentInstanceContext.getAgentInstanceId());
        if (groupByRollupDesc == null) {
            return new AggSvcGroupByWTableImpl(tableMetadata, methodPairs, accessors, isJoin,
                    tableState, targetStates, accessStateExpr, agents);
        }
        if (tableMetadata.getKeyTypes().length > 1) {
            return new AggSvcGroupByWTableRollupMultiKeyImpl(tableMetadata, methodPairs, accessors, isJoin,
                    tableState, targetStates, accessStateExpr, agents, groupByRollupDesc);
        } else {
            return new AggSvcGroupByWTableRollupSingleKeyImpl(tableMetadata, methodPairs, accessors, isJoin,
                    tableState, targetStates, accessStateExpr, agents);
        }
    }
}
