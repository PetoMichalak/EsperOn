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
package eu.uk.ncl.pet5o.esper.core.context.subselect;

import eu.uk.ncl.pet5o.esper.core.context.util.AgentInstanceContext;
import eu.uk.ncl.pet5o.esper.core.service.EPServicesContext;
import eu.uk.ncl.pet5o.esper.util.StopCallback;
import eu.uk.ncl.pet5o.esper.view.Viewable;

import java.util.List;

/**
 * Entry holding lookup resource references for use by {@link SubSelectActivationCollection}.
 */
public interface SubSelectStrategyFactory {
    public SubSelectStrategyRealization instantiate(EPServicesContext services, Viewable viewableRoot, AgentInstanceContext agentInstanceContext, List<StopCallback> stopCallbackList, int subqueryNumber, boolean isRecoveringResilient);
}
