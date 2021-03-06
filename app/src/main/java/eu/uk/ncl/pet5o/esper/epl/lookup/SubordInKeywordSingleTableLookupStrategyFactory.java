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

import eu.uk.ncl.pet5o.esper.epl.expression.core.ExprEvaluator;
import eu.uk.ncl.pet5o.esper.epl.expression.core.ExprNode;
import eu.uk.ncl.pet5o.esper.epl.expression.core.ExprNodeUtilityCore;
import eu.uk.ncl.pet5o.esper.epl.join.table.EventTable;
import eu.uk.ncl.pet5o.esper.epl.join.table.PropertyIndexedEventTableSingle;
import eu.uk.ncl.pet5o.esper.epl.virtualdw.VirtualDWView;

/**
 * Index lookup strategy for subqueries.
 */
public class SubordInKeywordSingleTableLookupStrategyFactory implements SubordTableLookupStrategyFactory {
    protected final ExprEvaluator[] evaluators;
    protected boolean isNWOnTrigger;
    protected int streamCountOuter;
    protected final LookupStrategyDesc strategyDesc;

    public SubordInKeywordSingleTableLookupStrategyFactory(boolean isNWOnTrigger, int streamCountOuter, ExprNode[] exprNodes) {
        this.streamCountOuter = streamCountOuter;
        this.evaluators = ExprNodeUtilityCore.getEvaluatorsNoCompile(exprNodes);
        this.isNWOnTrigger = isNWOnTrigger;
        this.strategyDesc = new LookupStrategyDesc(LookupStrategyType.INKEYWORDSINGLEIDX, ExprNodeUtilityCore.toExpressionStringsMinPrecedence(exprNodes));
    }

    public SubordTableLookupStrategy makeStrategy(EventTable[] eventTable, VirtualDWView vdw) {
        if (isNWOnTrigger) {
            return new SubordInKeywordSingleTableLookupStrategyNW(evaluators, (PropertyIndexedEventTableSingle) eventTable[0], strategyDesc);
        } else {
            return new SubordInKeywordSingleTableLookupStrategy(streamCountOuter, evaluators, (PropertyIndexedEventTableSingle) eventTable[0], strategyDesc);
        }
    }

    public String toQueryPlan() {
        return this.getClass().getSimpleName();
    }
}
