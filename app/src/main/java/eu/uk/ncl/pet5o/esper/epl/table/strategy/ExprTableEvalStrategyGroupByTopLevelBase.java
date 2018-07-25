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

import com.espertech.esper.client.EventBean;
import com.espertech.esper.epl.expression.core.ExprEvaluatorContext;
import com.espertech.esper.epl.expression.table.ExprTableAccessEvalStrategy;
import com.espertech.esper.epl.table.mgmt.TableMetadataColumn;
import com.espertech.esper.event.ObjectArrayBackedEventBean;

import java.util.Collection;
import java.util.Map;

public abstract class ExprTableEvalStrategyGroupByTopLevelBase extends ExprTableEvalStrategyGroupByBase implements ExprTableAccessEvalStrategy {

    private final Map<String, TableMetadataColumn> items;

    protected ExprTableEvalStrategyGroupByTopLevelBase(TableAndLockProviderGrouped provider, Map<String, TableMetadataColumn> items) {
        super(provider);
        this.items = items;
    }

    protected Object evaluateInternal(Object groupKey, com.espertech.esper.client.EventBean[] eventsPerStream, boolean isNewData, ExprEvaluatorContext context) {
        ObjectArrayBackedEventBean row = lockTableReadAndGet(groupKey, context);
        if (row == null) {
            return null;
        }
        return ExprTableEvalStrategyUtil.evalMap(row, ExprTableEvalStrategyUtil.getRow(row), items, eventsPerStream, isNewData, context);
    }

    protected Object[] evaluateTypableSingleInternal(Object groupKey, com.espertech.esper.client.EventBean[] eventsPerStream, boolean isNewData, ExprEvaluatorContext context) {
        ObjectArrayBackedEventBean row = lockTableReadAndGet(groupKey, context);
        if (row == null) {
            return null;
        }
        return ExprTableEvalStrategyUtil.evalTypable(row, ExprTableEvalStrategyUtil.getRow(row), items, eventsPerStream, isNewData, context);
    }

    public Collection<EventBean> evaluateGetROCollectionEvents(com.espertech.esper.client.EventBean[] eventsPerStream, boolean isNewData, ExprEvaluatorContext context) {
        return null;
    }

    public com.espertech.esper.client.EventBean evaluateGetEventBean(com.espertech.esper.client.EventBean[] eventsPerStream, boolean isNewData, ExprEvaluatorContext context) {
        return null;
    }

    public Collection evaluateGetROCollectionScalar(com.espertech.esper.client.EventBean[] eventsPerStream, boolean isNewData, ExprEvaluatorContext context) {
        return null;
    }
}
