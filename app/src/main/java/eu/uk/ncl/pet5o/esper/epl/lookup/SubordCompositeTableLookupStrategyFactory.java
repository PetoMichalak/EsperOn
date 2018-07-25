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

import eu.uk.ncl.pet5o.esper.epl.join.exec.composite.CompositeIndexQuery;
import eu.uk.ncl.pet5o.esper.epl.join.exec.composite.CompositeIndexQueryFactory;
import eu.uk.ncl.pet5o.esper.epl.join.table.EventTable;
import eu.uk.ncl.pet5o.esper.epl.join.table.PropertyCompositeEventTable;
import eu.uk.ncl.pet5o.esper.epl.virtualdw.VirtualDWView;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Index lookup strategy for subqueries.
 */
public class SubordCompositeTableLookupStrategyFactory implements SubordTableLookupStrategyFactory {
    private final CompositeIndexQuery innerIndexQuery;
    private final Collection<SubordPropRangeKey> rangeDescs;
    private final LookupStrategyDesc strategyDesc;

    public SubordCompositeTableLookupStrategyFactory(boolean isNWOnTrigger, int numStreams, Collection<SubordPropHashKey> keyExpr, Class[] coercionKeyTypes, Collection<SubordPropRangeKey> rangeProps, Class[] coercionRangeTypes) {
        this.rangeDescs = rangeProps;
        List<String> expressionTexts = new ArrayList<String>();
        this.innerIndexQuery = CompositeIndexQueryFactory.makeSubordinate(isNWOnTrigger, numStreams, keyExpr, coercionKeyTypes, rangeProps, coercionRangeTypes, expressionTexts);
        this.strategyDesc = new LookupStrategyDesc(LookupStrategyType.COMPOSITE, expressionTexts.toArray(new String[expressionTexts.size()]));
    }

    public SubordTableLookupStrategy makeStrategy(EventTable[] eventTable, VirtualDWView vdw) {
        return new SubordCompositeTableLookupStrategy(innerIndexQuery, (PropertyCompositeEventTable) eventTable[0], strategyDesc);
    }

    public String toQueryPlan() {
        return this.getClass().getSimpleName() + " ranges=" + SubordPropRangeKey.toQueryPlan(rangeDescs);
    }

}
