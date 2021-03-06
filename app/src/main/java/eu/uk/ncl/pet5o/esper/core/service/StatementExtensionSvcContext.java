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

import eu.uk.ncl.pet5o.esper.core.context.factory.StatementAgentInstanceFactoryResult;
import eu.uk.ncl.pet5o.esper.core.service.resource.StatementResourceHolder;
import eu.uk.ncl.pet5o.esper.core.service.resource.StatementResourceService;
import eu.uk.ncl.pet5o.esper.core.start.EPStatementStartMethodSelectDesc;
import eu.uk.ncl.pet5o.esper.util.StopCallback;

import java.util.List;

/**
 * Statement-level extension services.
 */
public interface StatementExtensionSvcContext {
    StatementResourceService getStmtResources();

    StatementResourceHolder extractStatementResourceHolder(StatementAgentInstanceFactoryResult resultOfStart);

    void preStartWalk(EPStatementStartMethodSelectDesc selectDesc);

    void postProcessStart(StatementAgentInstanceFactoryResult resultOfStart, boolean isRecoveringResilient);

    void contributeStopCallback(StatementAgentInstanceFactoryResult selectResult, List<StopCallback> stopCallbacks);
}
