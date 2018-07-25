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
package eu.uk.ncl.pet5o.esper.core.context.activator;

import eu.uk.ncl.pet5o.esper.core.context.util.AgentInstanceContext;
import eu.uk.ncl.pet5o.esper.epl.expression.core.ExprEvaluator;
import eu.uk.ncl.pet5o.esper.epl.table.mgmt.TableMetadata;
import eu.uk.ncl.pet5o.esper.epl.table.mgmt.TableStateInstance;
import eu.uk.ncl.pet5o.esper.epl.table.mgmt.TableStateViewableInternal;
import eu.uk.ncl.pet5o.esper.util.CollectionUtil;

public class ViewableActivatorTable implements ViewableActivator {

    private final TableMetadata tableMetadata;
    private final ExprEvaluator[] optionalTableFilters;

    public ViewableActivatorTable(TableMetadata tableMetadata, ExprEvaluator[] optionalTableFilters) {
        this.tableMetadata = tableMetadata;
        this.optionalTableFilters = optionalTableFilters;
    }

    public ViewableActivationResult activate(AgentInstanceContext agentInstanceContext, boolean isSubselect, boolean isRecoveringResilient) {
        TableStateInstance state = agentInstanceContext.getStatementContext().getTableService().getState(tableMetadata.getTableName(), agentInstanceContext.getAgentInstanceId());
        return new ViewableActivationResult(new TableStateViewableInternal(tableMetadata, state, optionalTableFilters), CollectionUtil.STOP_CALLBACK_NONE, null, null, null, false, false, null);
    }
}
