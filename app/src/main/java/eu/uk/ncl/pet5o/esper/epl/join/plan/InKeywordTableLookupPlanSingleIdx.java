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
import eu.uk.ncl.pet5o.esper.epl.join.exec.base.InKeywordSingleTableLookupStrategyExpr;
import eu.uk.ncl.pet5o.esper.epl.join.exec.base.JoinExecTableLookupStrategy;
import eu.uk.ncl.pet5o.esper.epl.join.table.EventTable;
import eu.uk.ncl.pet5o.esper.epl.join.table.PropertyIndexedEventTableSingle;
import eu.uk.ncl.pet5o.esper.epl.lookup.LookupStrategyDesc;
import eu.uk.ncl.pet5o.esper.epl.lookup.LookupStrategyType;

import java.util.Collections;

/**
 * Plan to perform an indexed table lookup.
 */
public class InKeywordTableLookupPlanSingleIdx extends TableLookupPlan {
    private ExprNode[] expressions;

    /**
     * Ctor.
     *
     * @param lookupStream  - stream that generates event to look up for
     * @param indexedStream - stream to index table lookup
     * @param indexNum      - index number for the table containing the full unindexed contents
     * @param expressions   expressions
     */
    public InKeywordTableLookupPlanSingleIdx(int lookupStream, int indexedStream, TableLookupIndexReqKey indexNum, ExprNode[] expressions) {
        super(lookupStream, indexedStream, new TableLookupIndexReqKey[]{indexNum});
        this.expressions = expressions;
    }

    public ExprNode[] getExpressions() {
        return expressions;
    }

    public TableLookupKeyDesc getKeyDescriptor() {
        return new TableLookupKeyDesc(Collections.<QueryGraphValueEntryHashKeyed>emptyList(), Collections.<QueryGraphValueEntryRange>emptyList());
    }

    public JoinExecTableLookupStrategy makeStrategyInternal(EventTable[] eventTable, EventType[] eventTypes) {
        PropertyIndexedEventTableSingle single = (PropertyIndexedEventTableSingle) eventTable[0];
        ExprEvaluator[] evaluators = new ExprEvaluator[expressions.length];
        for (int i = 0; i < expressions.length; i++) {
            evaluators[i] = expressions[i].getForge().getExprEvaluator();
        }
        return new InKeywordSingleTableLookupStrategyExpr(evaluators,
                super.getLookupStream(), single, new LookupStrategyDesc(LookupStrategyType.INKEYWORDSINGLEIDX, ExprNodeUtilityCore.toExpressionStringsMinPrecedence(expressions)));
    }

    public String toString() {
        return this.getClass().getSimpleName() + " " +
                super.toString() +
                " keyProperties=" + ExprNodeUtilityCore.toExpressionStringMinPrecedenceAsList(expressions);
    }
}
