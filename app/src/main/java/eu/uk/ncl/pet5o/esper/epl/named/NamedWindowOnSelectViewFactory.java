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
package eu.uk.ncl.pet5o.esper.epl.named;

import eu.uk.ncl.pet5o.esper.client.EventType;
import eu.uk.ncl.pet5o.esper.client.annotation.AuditEnum;
import eu.uk.ncl.pet5o.esper.client.soda.StreamSelector;
import eu.uk.ncl.pet5o.esper.core.context.util.AgentInstanceContext;
import eu.uk.ncl.pet5o.esper.core.service.EPStatementHandle;
import eu.uk.ncl.pet5o.esper.core.service.InternalEventRouteDest;
import eu.uk.ncl.pet5o.esper.core.service.InternalEventRouter;
import eu.uk.ncl.pet5o.esper.core.service.StatementResultService;
import eu.uk.ncl.pet5o.esper.epl.core.resultset.core.ResultSetProcessor;
import eu.uk.ncl.pet5o.esper.epl.lookup.SubordWMatchExprLookupStrategy;
import eu.uk.ncl.pet5o.esper.epl.table.mgmt.TableStateInstance;
import eu.uk.ncl.pet5o.esper.event.EventBeanReader;

/**
 * View for the on-select statement that handles selecting events from a named window.
 */
public class NamedWindowOnSelectViewFactory extends NamedWindowOnExprBaseViewFactory {
    private final InternalEventRouter internalEventRouter;
    private final boolean addToFront;
    private final EPStatementHandle statementHandle;
    private final EventBeanReader eventBeanReader;
    private final boolean isDistinct;
    private final StatementResultService statementResultService;
    private final InternalEventRouteDest internalEventRouteDest;
    private final boolean deleteAndSelect;
    private final StreamSelector optionalStreamSelector;
    private final String optionalInsertIntoTableName;

    public NamedWindowOnSelectViewFactory(EventType namedWindowEventType, InternalEventRouter internalEventRouter, boolean addToFront, EPStatementHandle statementHandle, EventBeanReader eventBeanReader, boolean distinct, StatementResultService statementResultService, InternalEventRouteDest internalEventRouteDest, boolean deleteAndSelect, StreamSelector optionalStreamSelector, String optionalInsertIntoTableName) {
        super(namedWindowEventType);
        this.internalEventRouter = internalEventRouter;
        this.addToFront = addToFront;
        this.statementHandle = statementHandle;
        this.eventBeanReader = eventBeanReader;
        isDistinct = distinct;
        this.statementResultService = statementResultService;
        this.internalEventRouteDest = internalEventRouteDest;
        this.deleteAndSelect = deleteAndSelect;
        this.optionalStreamSelector = optionalStreamSelector;
        this.optionalInsertIntoTableName = optionalInsertIntoTableName;
    }

    public NamedWindowOnExprBaseView make(SubordWMatchExprLookupStrategy lookupStrategy, NamedWindowRootViewInstance namedWindowRootViewInstance, AgentInstanceContext agentInstanceContext, ResultSetProcessor resultSetProcessor) {
        boolean audit = AuditEnum.INSERT.getAudit(agentInstanceContext.getAnnotations()) != null;
        TableStateInstance tableStateInstance = null;
        if (optionalInsertIntoTableName != null) {
            tableStateInstance = agentInstanceContext.getStatementContext().getTableService().getState(optionalInsertIntoTableName, agentInstanceContext.getAgentInstanceId());
        }
        return new NamedWindowOnSelectView(lookupStrategy, namedWindowRootViewInstance, agentInstanceContext, this, resultSetProcessor, audit, deleteAndSelect, tableStateInstance);
    }

    public InternalEventRouter getInternalEventRouter() {
        return internalEventRouter;
    }

    public boolean isAddToFront() {
        return addToFront;
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

    public StreamSelector getOptionalStreamSelector() {
        return optionalStreamSelector;
    }
}
