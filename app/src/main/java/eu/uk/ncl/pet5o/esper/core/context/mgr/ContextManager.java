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

import eu.uk.ncl.pet5o.esper.client.EventType;
import eu.uk.ncl.pet5o.esper.client.context.ContextPartitionDescriptor;
import eu.uk.ncl.pet5o.esper.client.context.ContextPartitionSelector;
import eu.uk.ncl.pet5o.esper.client.context.ContextPartitionStateListener;
import eu.uk.ncl.pet5o.esper.core.context.util.ContextDescriptor;
import eu.uk.ncl.pet5o.esper.epl.expression.core.ExprFilterSpecLookupable;
import eu.uk.ncl.pet5o.esper.epl.expression.core.ExprValidationException;
import eu.uk.ncl.pet5o.esper.filter.FilterFaultHandler;

import java.util.Collection;
import java.util.Iterator;
import java.util.Map;

public interface ContextManager extends FilterFaultHandler {
    public ContextDescriptor getContextDescriptor();

    public int getNumNestingLevels();

    public ContextStateCache getContextStateCache();

    public void addStatement(ContextControllerStatementBase statement, boolean isRecoveringResilient) throws ExprValidationException;

    public void stopStatement(String statementName, int statementId);

    public void destroyStatement(String statementName, int statementId);

    public void safeDestroy();

    public ExprFilterSpecLookupable getFilterLookupable(EventType eventType);

    public ContextStatePathDescriptor extractPaths(ContextPartitionSelector contextPartitionSelector);

    public ContextStatePathDescriptor extractStopPaths(ContextPartitionSelector contextPartitionSelector);

    public ContextStatePathDescriptor extractDestroyPaths(ContextPartitionSelector contextPartitionSelector);

    public void importStartPaths(ContextControllerState state, AgentInstanceSelector agentInstanceSelector);

    public Map<Integer, ContextPartitionDescriptor> startPaths(ContextPartitionSelector contextPartitionSelector);

    public Collection<Integer> getAgentInstanceIds(ContextPartitionSelector contextPartitionSelector);

    public Map<Integer, ContextControllerStatementDesc> getStatements();

    public void addListener(ContextPartitionStateListener listener);

    public void removeListener(ContextPartitionStateListener listener);

    public Iterator<ContextPartitionStateListener> getListeners();

    public void removeListeners();

    public Map<String, Object> getContextProperties(int contextPartitionId);
}
