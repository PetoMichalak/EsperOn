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
package eu.uk.ncl.pet5o.esper.epl.join.plan;

import eu.uk.ncl.pet5o.esper.client.EventType;
import eu.uk.ncl.pet5o.esper.epl.expression.core.ExprEvaluator;
import eu.uk.ncl.pet5o.esper.epl.expression.core.ExprNode;
import eu.uk.ncl.pet5o.esper.epl.expression.core.ExprNodeUtilityCore;
import eu.uk.ncl.pet5o.esper.epl.join.exec.base.InKeywordMultiTableLookupStrategyExpr;
import eu.uk.ncl.pet5o.esper.epl.join.exec.base.JoinExecTableLookupStrategy;
import eu.uk.ncl.pet5o.esper.epl.join.table.EventTable;
import eu.uk.ncl.pet5o.esper.epl.join.table.PropertyIndexedEventTableSingle;
import eu.uk.ncl.pet5o.esper.epl.lookup.LookupStrategyDesc;
import eu.uk.ncl.pet5o.esper.epl.lookup.LookupStrategyType;

import java.util.Collections;

/**
 * Plan to perform an indexed table lookup.
 */
public class InKeywordTableLookupPlanMultiIdx extends TableLookupPlan {
    private ExprNode keyExpr;

    public InKeywordTableLookupPlanMultiIdx(int lookupStream, int indexedStream, TableLookupIndexReqKey[] indexNum, ExprNode keyExpr) {
        super(lookupStream, indexedStream, indexNum);
        this.keyExpr = keyExpr;
    }

    public ExprNode getKeyExpr() {
        return keyExpr;
    }

    public TableLookupKeyDesc getKeyDescriptor() {
        return new TableLookupKeyDesc(Collections.<QueryGraphValueEntryHashKeyed>emptyList(), Collections.<QueryGraphValueEntryRange>emptyList());
    }

    public JoinExecTableLookupStrategy makeStrategyInternal(EventTable[] eventTable, EventType[] eventTypes) {
        ExprEvaluator evaluator = keyExpr.getForge().getExprEvaluator();
        PropertyIndexedEventTableSingle[] singles = new PropertyIndexedEventTableSingle[eventTable.length];
        for (int i = 0; i < eventTable.length; i++) {
            singles[i] = (PropertyIndexedEventTableSingle) eventTable[i];
        }
        return new InKeywordMultiTableLookupStrategyExpr(evaluator, super.getLookupStream(), singles, new LookupStrategyDesc(LookupStrategyType.INKEYWORDMULTIIDX, new String[]{ExprNodeUtilityCore.toExpressionStringMinPrecedenceSafe(keyExpr)}));
    }

    public String toString() {
        return this.getClass().getSimpleName() + " " +
                super.toString() +
                " keyProperties=" + ExprNodeUtilityCore.toExpressionStringMinPrecedenceSafe(keyExpr);
    }
}
