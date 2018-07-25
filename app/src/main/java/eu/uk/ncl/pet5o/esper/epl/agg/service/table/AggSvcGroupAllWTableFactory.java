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
import eu.uk.ncl.pet5o.esper.epl.agg.service.common.AggregationService;
import eu.uk.ncl.pet5o.esper.epl.agg.service.common.AggregationServiceFactory;
import eu.uk.ncl.pet5o.esper.epl.core.engineimport.EngineImportService;
import eu.uk.ncl.pet5o.esper.epl.expression.core.ExprNode;
import eu.uk.ncl.pet5o.esper.epl.table.mgmt.TableColumnMethodPair;
import eu.uk.ncl.pet5o.esper.epl.table.mgmt.TableMetadata;
import eu.uk.ncl.pet5o.esper.epl.table.mgmt.TableStateInstanceUngrouped;

/**
 * Implementation for handling aggregation without any grouping (no group-by).
 */
public class AggSvcGroupAllWTableFactory implements AggregationServiceFactory {
    private final TableMetadata tableMetadata;
    private final TableColumnMethodPair[] methodPairs;
    protected final AggregationAccessorSlotPair[] accessors;
    protected final boolean isJoin;
    private final int[] targetStates;
    private final ExprNode[] accessStateExpr;
    private final AggregationAgent[] agents;

    public AggSvcGroupAllWTableFactory(TableMetadata tableMetadata, TableColumnMethodPair[] methodPairs, AggregationAccessorSlotPair[] accessors, boolean isJoin, int[] targetStates, ExprNode[] accessStateExpr, AggregationAgent[] agents) {
        this.tableMetadata = tableMetadata;
        this.methodPairs = methodPairs;
        this.accessors = accessors;
        this.isJoin = isJoin;
        this.targetStates = targetStates;
        this.accessStateExpr = accessStateExpr;
        this.agents = agents;
    }

    public AggregationService makeService(AgentInstanceContext agentInstanceContext, EngineImportService engineImportService, boolean isSubquery, Integer subqueryNumber) {
        TableStateInstanceUngrouped tableState = (TableStateInstanceUngrouped) agentInstanceContext.getStatementContext().getTableService().getState(tableMetadata.getTableName(), agentInstanceContext.getAgentInstanceId());
        return new AggSvcGroupAllWTableImpl(tableState, methodPairs,
                accessors, targetStates, accessStateExpr, agents);
    }
}
