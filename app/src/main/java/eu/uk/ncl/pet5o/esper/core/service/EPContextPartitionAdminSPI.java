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

import eu.uk.ncl.pet5o.esper.client.context.ContextPartitionSelector;
import eu.uk.ncl.pet5o.esper.client.context.EPContextPartitionAdmin;
import eu.uk.ncl.pet5o.esper.core.context.mgr.AgentInstanceSelector;

public interface EPContextPartitionAdminSPI extends EPContextPartitionAdmin {
    public boolean isSupportsExtract();

    public EPContextPartitionExtract extractDestroyPaths(String contextName, ContextPartitionSelector selector);

    public EPContextPartitionExtract extractStopPaths(String contextName, ContextPartitionSelector selector);

    public EPContextPartitionExtract extractPaths(String contextName, ContextPartitionSelector selector);

    public EPContextPartitionImportResult importStartPaths(String contextName, EPContextPartitionImportable importable, AgentInstanceSelector agentInstanceSelector);
}
