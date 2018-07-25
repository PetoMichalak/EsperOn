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
package eu.uk.ncl.pet5o.esper.epl.table.strategy;

import eu.uk.ncl.pet5o.esper.epl.expression.core.ExprEvaluator;
import eu.uk.ncl.pet5o.esper.epl.expression.core.ExprEvaluatorContext;
import eu.uk.ncl.pet5o.esper.epl.table.mgmt.TableMetadataColumn;

import java.util.Map;

public class ExprTableEvalStrategyGroupByTopLevelSingle extends ExprTableEvalStrategyGroupByTopLevelBase {

    private final ExprEvaluator groupExpr;

    public ExprTableEvalStrategyGroupByTopLevelSingle(TableAndLockProviderGrouped provider, Map<String, TableMetadataColumn> items, ExprEvaluator groupExpr) {
        super(provider, items);
        this.groupExpr = groupExpr;
    }

    public Object evaluate(eu.uk.ncl.pet5o.esper.client.EventBean[] eventsPerStream, boolean isNewData, ExprEvaluatorContext exprEvaluatorContext) {
        Object groupKey = groupExpr.evaluate(eventsPerStream, isNewData, exprEvaluatorContext);
        return super.evaluateInternal(groupKey, eventsPerStream, isNewData, exprEvaluatorContext);
    }

    public Object[] evaluateTypableSingle(eu.uk.ncl.pet5o.esper.client.EventBean[] eventsPerStream, boolean isNewData, ExprEvaluatorContext context) {
        Object groupKey = groupExpr.evaluate(eventsPerStream, isNewData, context);
        return super.evaluateTypableSingleInternal(groupKey, eventsPerStream, isNewData, context);
    }
}
