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
package eu.uk.ncl.pet5o.esper.epl.lookup;

import eu.uk.ncl.pet5o.esper.client.EventType;
import eu.uk.ncl.pet5o.esper.epl.expression.core.ExprEvaluator;
import eu.uk.ncl.pet5o.esper.epl.expression.core.ExprEvaluatorContext;
import eu.uk.ncl.pet5o.esper.epl.expression.core.ExprNode;
import eu.uk.ncl.pet5o.esper.epl.index.quadtree.SubordTableLookupStrategyFactoryQuadTree;
import eu.uk.ncl.pet5o.esper.epl.join.table.EventTable;
import eu.uk.ncl.pet5o.esper.epl.join.table.EventTableOrganization;

import java.util.Map;

public interface EventAdvancedIndexFactory {
    AdvancedIndexConfigContextPartition configureContextPartition(EventType eventType, AdvancedIndexDesc indexDesc, ExprEvaluator[] parameters, ExprEvaluatorContext exprEvaluatorContext, EventTableOrganization organization, EventAdvancedIndexConfigStatement advancedIndexConfigStatement);
    EventTable make(EventAdvancedIndexConfigStatement configStatement, AdvancedIndexConfigContextPartition configContextPartition, EventTableOrganization organization);
    boolean providesIndexForOperation(String operationName, Map<Integer, ExprNode> expressions);
    SubordTableLookupStrategyFactoryQuadTree getSubordinateLookupStrategy(String operationName, Map<Integer, ExprNode> expressions, boolean isNWOnTrigger, int numOuterstreams);
}
