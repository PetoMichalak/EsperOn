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
package eu.uk.ncl.pet5o.esper.dataflow.core;

import eu.uk.ncl.pet5o.esper.client.dataflow.EPDataFlowRuntime;
import eu.uk.ncl.pet5o.esper.core.context.util.AgentInstanceContext;
import eu.uk.ncl.pet5o.esper.core.service.EPServicesContext;
import eu.uk.ncl.pet5o.esper.core.service.StatementContext;
import eu.uk.ncl.pet5o.esper.epl.expression.core.ExprValidationException;
import eu.uk.ncl.pet5o.esper.epl.spec.CreateDataFlowDesc;

public interface DataFlowService extends EPDataFlowRuntime {

    public void addStartGraph(CreateDataFlowDesc desc, StatementContext statementContext, EPServicesContext servicesContext, AgentInstanceContext agentInstanceContext, boolean newStatement)
            throws ExprValidationException;

    public void removeGraph(String graphName);

    public void stopGraph(String graphName);

    public void destroy();
}
