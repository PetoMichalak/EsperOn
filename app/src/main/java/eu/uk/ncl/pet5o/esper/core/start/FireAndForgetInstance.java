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
package eu.uk.ncl.pet5o.esper.core.start;

import eu.uk.ncl.pet5o.esper.client.EventBean;
import eu.uk.ncl.pet5o.esper.core.context.util.AgentInstanceContext;
import eu.uk.ncl.pet5o.esper.epl.join.plan.QueryGraph;
import eu.uk.ncl.pet5o.esper.epl.virtualdw.VirtualDWView;
import eu.uk.ncl.pet5o.esper.view.Viewable;

import java.lang.annotation.Annotation;
import java.util.Collection;

public abstract class FireAndForgetInstance {
    public abstract eu.uk.ncl.pet5o.esper.client.EventBean[] processInsert(EPPreparedExecuteIUDSingleStreamExecInsert insert);

    public abstract eu.uk.ncl.pet5o.esper.client.EventBean[] processDelete(EPPreparedExecuteIUDSingleStreamExecDelete delete);

    public abstract eu.uk.ncl.pet5o.esper.client.EventBean[] processUpdate(EPPreparedExecuteIUDSingleStreamExecUpdate update);

    public abstract Collection<EventBean> snapshotBestEffort(EPPreparedExecuteMethodQuery epPreparedExecuteMethodQuery, QueryGraph queryGraph, Annotation[] annotations);

    public abstract AgentInstanceContext getAgentInstanceContext();

    public abstract Viewable getTailViewInstance();

    public abstract VirtualDWView getVirtualDataWindow();

}
