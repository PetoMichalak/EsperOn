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

import eu.uk.ncl.pet5o.esper.filter.FilterHandleCallback;

import java.util.Collection;

public interface ContextControllerPartitionedInstanceManageCallback {

    public void createKey(Object key, eu.uk.ncl.pet5o.esper.client.EventBean theEvent, Collection<FilterHandleCallback> allStmtMatches, String initConditionAsName);
}
