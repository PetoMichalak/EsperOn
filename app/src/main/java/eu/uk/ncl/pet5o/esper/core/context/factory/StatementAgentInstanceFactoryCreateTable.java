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

import eu.uk.ncl.pet5o.esper.core.context.util.AgentInstanceContext;
import eu.uk.ncl.pet5o.esper.epl.agg.service.common.AggregationServiceTable;
import eu.uk.ncl.pet5o.esper.epl.table.mgmt.TableMetadata;
import eu.uk.ncl.pet5o.esper.epl.table.mgmt.TableStateInstance;
import eu.uk.ncl.pet5o.esper.epl.table.mgmt.TableStateViewablePublic;
import eu.uk.ncl.pet5o.esper.util.CollectionUtil;

public class StatementAgentInstanceFactoryCreateTable implements StatementAgentInstanceFactory {
    private final TableMetadata tableMetadata;

    public StatementAgentInstanceFactoryCreateTable(TableMetadata tableMetadata) {
        this.tableMetadata = tableMetadata;
    }

    public StatementAgentInstanceFactoryCreateTableResult newContext(final AgentInstanceContext agentInstanceContext, boolean isRecoveringResilient) {
        TableStateInstance tableState = tableMetadata.getTableStateFactory().makeTableState(agentInstanceContext);
        AggregationServiceTable aggregationReportingService = new AggregationServiceTable(tableState);
        TableStateViewablePublic finalView = new TableStateViewablePublic(tableMetadata, tableState);
        return new StatementAgentInstanceFactoryCreateTableResult(finalView, CollectionUtil.STOP_CALLBACK_NONE, agentInstanceContext, aggregationReportingService);
    }

    public void assignExpressions(StatementAgentInstanceFactoryResult result) {
    }

    public void unassignExpressions() {
    }
}
