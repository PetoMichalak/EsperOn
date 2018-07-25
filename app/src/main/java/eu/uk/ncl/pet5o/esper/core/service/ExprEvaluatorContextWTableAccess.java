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
package eu.uk.ncl.pet5o.esper.core.service;

import eu.uk.ncl.pet5o.esper.epl.expression.core.ExprEvaluatorContext;
import eu.uk.ncl.pet5o.esper.epl.script.AgentInstanceScriptContext;
import eu.uk.ncl.pet5o.esper.epl.table.mgmt.TableExprEvaluatorContext;
import eu.uk.ncl.pet5o.esper.epl.table.mgmt.TableService;
import eu.uk.ncl.pet5o.esper.schedule.TimeProvider;

public class ExprEvaluatorContextWTableAccess implements ExprEvaluatorContext {
    private final ExprEvaluatorContext context;
    private final TableService tableService;

    public ExprEvaluatorContextWTableAccess(ExprEvaluatorContext context, TableService tableService) {
        this.context = context;
        this.tableService = tableService;
    }

    public String getStatementName() {
        return context.getStatementName();
    }

    public String getEngineURI() {
        return context.getEngineURI();
    }

    public int getStatementId() {
        return context.getStatementId();
    }

    public StatementType getStatementType() {
        return context.getStatementType();
    }

    public TimeProvider getTimeProvider() {
        return context.getTimeProvider();
    }

    public ExpressionResultCacheService getExpressionResultCacheService() {
        return context.getExpressionResultCacheService();
    }

    public int getAgentInstanceId() {
        return context.getAgentInstanceId();
    }

    public eu.uk.ncl.pet5o.esper.client.EventBean getContextProperties() {
        return context.getContextProperties();
    }

    public AgentInstanceScriptContext getAllocateAgentInstanceScriptContext() {
        return context.getAllocateAgentInstanceScriptContext();
    }

    public StatementAgentInstanceLock getAgentInstanceLock() {
        return context.getAgentInstanceLock();
    }

    public TableExprEvaluatorContext getTableExprEvaluatorContext() {
        return tableService.getTableExprEvaluatorContext();
    }

    public Object getStatementUserObject() {
        return context.getStatementUserObject();
    }
}
