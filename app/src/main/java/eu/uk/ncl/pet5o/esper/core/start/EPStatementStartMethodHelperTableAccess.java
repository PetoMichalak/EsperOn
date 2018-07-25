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

import eu.uk.ncl.pet5o.esper.core.context.util.AgentInstanceContext;
import eu.uk.ncl.pet5o.esper.core.service.EPServicesContext;
import eu.uk.ncl.pet5o.esper.epl.expression.table.ExprTableAccessEvalStrategy;
import eu.uk.ncl.pet5o.esper.epl.expression.table.ExprTableAccessNode;
import eu.uk.ncl.pet5o.esper.epl.table.mgmt.TableMetadata;
import eu.uk.ncl.pet5o.esper.epl.table.strategy.ExprTableEvalStrategyFactory;
import eu.uk.ncl.pet5o.esper.epl.table.strategy.TableAndLockProvider;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class EPStatementStartMethodHelperTableAccess {
    public static Map<ExprTableAccessNode, ExprTableAccessEvalStrategy> attachTableAccess(EPServicesContext services, AgentInstanceContext agentInstanceContext, ExprTableAccessNode[] tableNodes, boolean isFireAndForget) {
        if (tableNodes == null || tableNodes.length == 0) {
            return Collections.emptyMap();
        }

        Map<ExprTableAccessNode, ExprTableAccessEvalStrategy> strategies = new HashMap<ExprTableAccessNode, ExprTableAccessEvalStrategy>();
        for (ExprTableAccessNode tableNode : tableNodes) {
            boolean writesToTables = agentInstanceContext.getStatementContext().isWritesToTables();
            TableAndLockProvider provider = services.getTableService().getStateProvider(tableNode.getTableName(), agentInstanceContext.getAgentInstanceId(), writesToTables);
            TableMetadata tableMetadata = services.getTableService().getTableMetadata(tableNode.getTableName());
            ExprTableAccessEvalStrategy strategy = ExprTableEvalStrategyFactory.getTableAccessEvalStrategy(tableNode, provider, tableMetadata, isFireAndForget);
            strategies.put(tableNode, strategy);
        }

        return strategies;
    }
}
