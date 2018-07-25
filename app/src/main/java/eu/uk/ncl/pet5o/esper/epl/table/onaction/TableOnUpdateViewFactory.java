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
package eu.uk.ncl.pet5o.esper.epl.table.onaction;

import eu.uk.ncl.pet5o.esper.core.context.util.AgentInstanceContext;
import eu.uk.ncl.pet5o.esper.core.service.StatementResultService;
import eu.uk.ncl.pet5o.esper.epl.core.resultset.core.ResultSetProcessor;
import eu.uk.ncl.pet5o.esper.epl.lookup.SubordWMatchExprLookupStrategy;
import eu.uk.ncl.pet5o.esper.epl.table.mgmt.TableMetadata;
import eu.uk.ncl.pet5o.esper.epl.table.mgmt.TableStateInstance;
import eu.uk.ncl.pet5o.esper.epl.table.upd.TableUpdateStrategy;
import eu.uk.ncl.pet5o.esper.epl.table.upd.TableUpdateStrategyReceiver;
import eu.uk.ncl.pet5o.esper.epl.updatehelper.EventBeanUpdateHelper;

public class TableOnUpdateViewFactory implements TableOnViewFactory, TableUpdateStrategyReceiver {
    private final StatementResultService statementResultService;
    private final TableMetadata tableMetadata;
    private final EventBeanUpdateHelper updateHelper;
    private TableUpdateStrategy tableUpdateStrategy;

    public TableOnUpdateViewFactory(StatementResultService statementResultService, TableMetadata tableMetadata, EventBeanUpdateHelper updateHelper, TableUpdateStrategy tableUpdateStrategy) {
        this.statementResultService = statementResultService;
        this.tableMetadata = tableMetadata;
        this.updateHelper = updateHelper;
        this.tableUpdateStrategy = tableUpdateStrategy;
    }

    public TableOnViewBase make(SubordWMatchExprLookupStrategy lookupStrategy, TableStateInstance tableState, AgentInstanceContext agentInstanceContext, ResultSetProcessor resultSetProcessor) {
        return new TableOnUpdateView(lookupStrategy, tableState, agentInstanceContext, tableMetadata, this);
    }

    public StatementResultService getStatementResultService() {
        return statementResultService;
    }

    public TableMetadata getTableMetadata() {
        return tableMetadata;
    }

    public EventBeanUpdateHelper getUpdateHelper() {
        return updateHelper;
    }

    public TableUpdateStrategy getTableUpdateStrategy() {
        return tableUpdateStrategy;
    }

    public void update(TableUpdateStrategy updateStrategy) {
        this.tableUpdateStrategy = updateStrategy;
    }
}
