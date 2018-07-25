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
package eu.uk.ncl.pet5o.esper.core.context.factory;

import eu.uk.ncl.pet5o.esper.core.context.util.AgentInstanceContext;
import eu.uk.ncl.pet5o.esper.util.CollectionUtil;
import eu.uk.ncl.pet5o.esper.view.Viewable;

public class StatementAgentInstanceFactoryNoAgentInstance implements StatementAgentInstanceFactory {

    private final Viewable sharedFinalView;

    public StatementAgentInstanceFactoryNoAgentInstance(Viewable sharedFinalView) {
        this.sharedFinalView = sharedFinalView;
    }

    public StatementAgentInstanceFactoryCreateSchemaResult newContext(final AgentInstanceContext agentInstanceContext, boolean isRecoveringResilient) {
        return new StatementAgentInstanceFactoryCreateSchemaResult(sharedFinalView, CollectionUtil.STOP_CALLBACK_NONE, agentInstanceContext);
    }

    public void assignExpressions(StatementAgentInstanceFactoryResult result) {
    }

    public void unassignExpressions() {
    }
}
