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
package eu.uk.ncl.pet5o.esper.epl.named;

import eu.uk.ncl.pet5o.esper.core.context.util.AgentInstanceContext;
import eu.uk.ncl.pet5o.esper.epl.core.resultset.core.ResultSetProcessor;
import eu.uk.ncl.pet5o.esper.epl.lookup.SubordWMatchExprLookupStrategy;

/**
 * View for the on-delete statement that handles removing events from a named window.
 */
public interface NamedWindowOnExprFactory {
    public NamedWindowOnExprView make(SubordWMatchExprLookupStrategy lookupStrategy,
                                      NamedWindowRootViewInstance namedWindowRootViewInstance,
                                      AgentInstanceContext agentInstanceContext,
                                      ResultSetProcessor resultSetProcessor);
}