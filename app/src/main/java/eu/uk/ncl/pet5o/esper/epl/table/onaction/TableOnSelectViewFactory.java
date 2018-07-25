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

import eu.uk.ncl.pet5o.esper.client.annotation.AuditEnum;
import eu.uk.ncl.pet5o.esper.core.context.util.AgentInstanceContext;
import eu.uk.ncl.pet5o.esper.core.service.EPStatementHandle;
import eu.uk.ncl.pet5o.esper.core.service.InternalEventRouteDest;
import eu.uk.ncl.pet5o.esper.core.service.InternalEventRouter;
import eu.uk.ncl.pet5o.esper.core.service.StatementResultService;
import eu.uk.ncl.pet5o.esper.epl.core.resultset.core.ResultSetProcessor;
import eu.uk.ncl.pet5o.esper.epl.lookup.SubordWMatchExprLookupStrategy;
import eu.uk.ncl.pet5o.esper.epl.table.mgmt.TableMetadata;
import eu.uk.ncl.pet5o.esper.epl.table.mgmt.TableStateInstance;
import eu.uk.ncl.pet5o.esper.event.EventBeanReader;

public class TableOnSelectViewFactory implements TableOnViewFactory {
    private final TableMetadata tableMetadata;
    private final InternalEventRouter internalEventRouter;
    private final EPStatementHandle statementHandle;
    private final EventBeanReader eventBeanReader;
    private final boolean isDistinct;
    private final StatementResultService statementResultService;
    private final InternalEventRouteDest internalEventRouteDest;
    private final boolean deleteAndSelect;

    public TableOnSelectViewFactory(TableMetadata tableMetadata, InternalEventRouter internalEventRouter, EPStatementHandle statementHandle, EventBeanReader eventBeanReader, boolean distinct, StatementResultService statementResultService, InternalEventRouteDest internalEventRouteDest, boolean deleteAndSelect) {
        this.tableMetadata = tableMetadata;
        this.internalEventRouter = internalEventRouter;
        this.statementHandle = statementHandle;
        this.eventBeanReader = eventBeanReader;
        isDistinct = distinct;
        this.statementResultService = statementResultService;
        this.internalEventRouteDest = internalEventRouteDest;
        this.deleteAndSelect = deleteAndSelect;
    }

    public TableOnViewBase make(SubordWMatchExprLookupStrategy lookupStrategy, TableStateInstance tableState, AgentInstanceContext agentInstanceContext, ResultSetProcessor resultSetProcessor) {
        boolean audit = AuditEnum.INSERT.getAudit(agentInstanceContext.getAnnotations()) != null;
        return new TableOnSelectView(lookupStrategy, tableState, agentInstanceContext, tableMetadata, this, resultSetProcessor, audit, deleteAndSelect);
    }

    public InternalEventRouter getInternalEventRouter() {
        return internalEventRouter;
    }

    public EPStatementHandle getStatementHandle() {
        return statementHandle;
    }

    public EventBeanReader getEventBeanReader() {
        return eventBeanReader;
    }

    public boolean isDistinct() {
        return isDistinct;
    }

    public StatementResultService getStatementResultService() {
        return statementResultService;
    }

    public InternalEventRouteDest getInternalEventRouteDest() {
        return internalEventRouteDest;
    }
}
