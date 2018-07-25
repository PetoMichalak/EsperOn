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

import eu.uk.ncl.pet5o.esper.core.context.subselect.SubSelectStrategyHolder;
import eu.uk.ncl.pet5o.esper.core.context.util.AgentInstanceContext;
import eu.uk.ncl.pet5o.esper.epl.agg.service.common.AggregationServiceTable;
import eu.uk.ncl.pet5o.esper.epl.expression.prev.ExprPreviousEvalStrategy;
import eu.uk.ncl.pet5o.esper.epl.expression.prev.ExprPreviousNode;
import eu.uk.ncl.pet5o.esper.epl.expression.prior.ExprPriorEvalStrategy;
import eu.uk.ncl.pet5o.esper.epl.expression.prior.ExprPriorNode;
import eu.uk.ncl.pet5o.esper.epl.expression.subquery.ExprSubselectNode;
import eu.uk.ncl.pet5o.esper.epl.expression.table.ExprTableAccessEvalStrategy;
import eu.uk.ncl.pet5o.esper.epl.expression.table.ExprTableAccessNode;
import eu.uk.ncl.pet5o.esper.util.StopCallback;
import eu.uk.ncl.pet5o.esper.view.Viewable;

import java.util.Collections;

public class StatementAgentInstanceFactoryCreateTableResult extends StatementAgentInstanceFactoryResult {

    public StatementAgentInstanceFactoryCreateTableResult(Viewable finalView, StopCallback stopCallback, AgentInstanceContext agentInstanceContext, AggregationServiceTable aggregationReportingService) {
        super(finalView, stopCallback, agentInstanceContext, aggregationReportingService,
                Collections.<ExprSubselectNode, SubSelectStrategyHolder>emptyMap(),
                Collections.<ExprPriorNode, ExprPriorEvalStrategy>emptyMap(),
                Collections.<ExprPreviousNode, ExprPreviousEvalStrategy>emptyMap(),
                null,
                Collections.<ExprTableAccessNode, ExprTableAccessEvalStrategy>emptyMap(),
                Collections.<StatementAgentInstancePreload>emptyList());
    }
}
