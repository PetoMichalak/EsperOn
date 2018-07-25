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
package eu.uk.ncl.pet5o.esper.epl.virtualdw;

import eu.uk.ncl.pet5o.esper.core.context.util.AgentInstanceContext;

public interface VirtualDWViewProviderForAgentInstance {
    public VirtualDWView getView(AgentInstanceContext agentInstanceContext);
}