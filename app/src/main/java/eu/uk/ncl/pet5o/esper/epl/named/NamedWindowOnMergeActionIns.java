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

import eu.uk.ncl.pet5o.esper.client.EventBean;
import eu.uk.ncl.pet5o.esper.collection.OneEventCollection;
import eu.uk.ncl.pet5o.esper.core.service.EPStatementHandle;
import eu.uk.ncl.pet5o.esper.core.service.InternalEventRouteDest;
import eu.uk.ncl.pet5o.esper.core.service.InternalEventRouter;
import eu.uk.ncl.pet5o.esper.epl.core.select.SelectExprProcessor;
import eu.uk.ncl.pet5o.esper.epl.expression.core.ExprEvaluator;
import eu.uk.ncl.pet5o.esper.epl.expression.core.ExprEvaluatorContext;
import eu.uk.ncl.pet5o.esper.epl.table.mgmt.TableService;
import eu.uk.ncl.pet5o.esper.epl.table.mgmt.TableStateInstance;
import eu.uk.ncl.pet5o.esper.util.AuditPath;

public class NamedWindowOnMergeActionIns extends NamedWindowOnMergeAction {
    private final SelectExprProcessor insertHelper;
    private final InternalEventRouter internalEventRouter;
    private final String insertIntoTableName;
    private final TableService tableService;
    private final EPStatementHandle statementHandle;
    private final InternalEventRouteDest internalEventRouteDest;
    private final boolean audit;

    public NamedWindowOnMergeActionIns(ExprEvaluator optionalFilter, SelectExprProcessor insertHelper, InternalEventRouter internalEventRouter, String insertIntoTableName, TableService tableService, EPStatementHandle statementHandle, InternalEventRouteDest internalEventRouteDest, boolean audit) {
        super(optionalFilter);
        this.insertHelper = insertHelper;
        this.internalEventRouter = internalEventRouter;
        this.insertIntoTableName = insertIntoTableName;
        this.tableService = tableService;
        this.statementHandle = statementHandle;
        this.internalEventRouteDest = internalEventRouteDest;
        this.audit = audit;
    }

    public void apply(EventBean matchingEvent, EventBean[] eventsPerStream, OneEventCollection newData, OneEventCollection oldData, ExprEvaluatorContext exprEvaluatorContext) {
        EventBean theEvent = insertHelper.process(eventsPerStream, true, true, exprEvaluatorContext);

        if (insertIntoTableName != null) {
            TableStateInstance tableStateInstance = tableService.getState(insertIntoTableName, exprEvaluatorContext.getAgentInstanceId());
            if (audit) {
                AuditPath.auditInsertInto(tableStateInstance.getAgentInstanceContext().getEngineURI(), statementHandle.getStatementName(), theEvent);
            }
            tableStateInstance.addEventUnadorned(theEvent);
            return;
        }

        if (internalEventRouter == null) {
            newData.add(theEvent);
            return;
        }

        if (audit) {
            AuditPath.auditInsertInto(internalEventRouteDest.getEngineURI(), statementHandle.getStatementName(), theEvent);
        }
        internalEventRouter.route(theEvent, statementHandle, internalEventRouteDest, exprEvaluatorContext, false);
    }

    public String getName() {
        return internalEventRouter != null ? "insert-into" : "select";
    }
}
