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
package eu.uk.ncl.pet5o.esper.epl.expression.core;

import eu.uk.ncl.pet5o.esper.client.EPException;
import eu.uk.ncl.pet5o.esper.core.service.ExpressionResultCacheService;
import eu.uk.ncl.pet5o.esper.core.service.StatementAgentInstanceLock;
import eu.uk.ncl.pet5o.esper.core.service.StatementType;
import eu.uk.ncl.pet5o.esper.epl.script.AgentInstanceScriptContext;
import eu.uk.ncl.pet5o.esper.epl.table.mgmt.TableExprEvaluatorContext;
import eu.uk.ncl.pet5o.esper.schedule.TimeProvider;

/**
 * Represents a minimal engine-level context for expression evaluation, not allowing for agents instances and result cache.
 */
public class ExprEvaluatorContextTimeOnly implements ExprEvaluatorContext {
    private final TimeProvider timeProvider;
    private final ExpressionResultCacheService expressionResultCacheService;

    public ExprEvaluatorContextTimeOnly(TimeProvider timeProvider) {
        this.timeProvider = timeProvider;
        this.expressionResultCacheService = new ExpressionResultCacheService(1);
    }

    /**
     * Returns the time provider.
     *
     * @return time provider
     */
    public TimeProvider getTimeProvider() {
        return timeProvider;
    }

    public ExpressionResultCacheService getExpressionResultCacheService() {
        return expressionResultCacheService;
    }

    public int getAgentInstanceId() {
        return -1;
    }

    public eu.uk.ncl.pet5o.esper.client.EventBean getContextProperties() {
        return null;
    }

    public AgentInstanceScriptContext getAllocateAgentInstanceScriptContext() {
        return null;
    }

    public String getStatementName() {
        return null;
    }

    public String getEngineURI() {
        return null;
    }

    public int getStatementId() {
        return -1;
    }

    public StatementAgentInstanceLock getAgentInstanceLock() {
        return null;
    }

    public StatementType getStatementType() {
        return null;
    }

    public TableExprEvaluatorContext getTableExprEvaluatorContext() {
        throw new EPException("Access to tables is not allowed");
    }

    public Object getStatementUserObject() {
        return null;
    }
}
