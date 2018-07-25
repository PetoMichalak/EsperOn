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
package eu.uk.ncl.pet5o.esper.epl.agg.factory;

import eu.uk.ncl.pet5o.esper.epl.agg.access.AggregationServicePassThru;
import eu.uk.ncl.pet5o.esper.epl.agg.access.AggregationState;
import eu.uk.ncl.pet5o.esper.epl.agg.service.common.AggregationStateFactory;
import eu.uk.ncl.pet5o.esper.epl.expression.accessagg.ExprPlugInAggMultiFunctionNodeFactory;
import eu.uk.ncl.pet5o.esper.epl.expression.core.ExprNode;
import eu.uk.ncl.pet5o.esper.plugin.PlugInAggregationMultiFunctionStateContext;
import eu.uk.ncl.pet5o.esper.plugin.PlugInAggregationMultiFunctionStateFactory;
import eu.uk.ncl.pet5o.esper.plugin.PlugInAggregationMultiFunctionStateForge;

public class AggregationStatePluginFactory implements AggregationStateFactory {

    protected final ExprPlugInAggMultiFunctionNodeFactory parent;
    protected final PlugInAggregationMultiFunctionStateFactory stateFactory;

    public AggregationStatePluginFactory(ExprPlugInAggMultiFunctionNodeFactory parent, PlugInAggregationMultiFunctionStateForge stateForge) {
        this.parent = parent;
        this.stateFactory = stateForge.getStateFactory();
    }

    public AggregationState createAccess(int agentInstanceId, boolean join, Object groupBy, AggregationServicePassThru passThru) {
        PlugInAggregationMultiFunctionStateContext context = new PlugInAggregationMultiFunctionStateContext(agentInstanceId, groupBy);
        return stateFactory.makeAggregationState(context);
    }

    public ExprNode getAggregationExpression() {
        return parent.getAggregationExpression();
    }
}
