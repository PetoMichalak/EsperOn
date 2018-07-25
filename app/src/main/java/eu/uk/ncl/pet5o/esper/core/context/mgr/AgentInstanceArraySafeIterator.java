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
package eu.uk.ncl.pet5o.esper.core.context.mgr;

import eu.uk.ncl.pet5o.esper.client.SafeIterator;
import eu.uk.ncl.pet5o.esper.core.context.util.AgentInstanceContext;
import eu.uk.ncl.pet5o.esper.core.service.StatementAgentInstanceLock;

public class AgentInstanceArraySafeIterator extends AgentInstanceArrayIterator implements SafeIterator<eu.uk.ncl.pet5o.esper.client.EventBean> {

    public AgentInstanceArraySafeIterator(AgentInstance[] instances) {
        super(instances);
        for (AgentInstance instance : instances) {
            StatementAgentInstanceLock instanceLock = instance.getAgentInstanceContext().getEpStatementAgentInstanceHandle().getStatementAgentInstanceLock();
            instanceLock.acquireWriteLock();
        }
    }

    public void close() {
        for (AgentInstance instance : instances) {
            AgentInstanceContext agentInstanceContext = instance.getAgentInstanceContext();
            StatementAgentInstanceLock instanceLock = agentInstanceContext.getEpStatementAgentInstanceHandle().getStatementAgentInstanceLock();
            if (agentInstanceContext.getStatementContext().getEpStatementHandle().isHasTableAccess()) {
                agentInstanceContext.getTableExprEvaluatorContext().releaseAcquiredLocks();
            }
            instanceLock.releaseWriteLock();
        }
    }
}
