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

import eu.uk.ncl.pet5o.esper.client.EventBean;
import eu.uk.ncl.pet5o.esper.epl.expression.core.ExprEvaluatorContext;
import eu.uk.ncl.pet5o.esper.epl.expression.table.ExprTableAccessEvalStrategy;
import eu.uk.ncl.pet5o.esper.epl.table.mgmt.TableMetadataColumn;
import eu.uk.ncl.pet5o.esper.event.ObjectArrayBackedEventBean;

import java.util.Collection;
import java.util.Map;

public abstract class ExprTableEvalStrategyGroupByTopLevelBase extends ExprTableEvalStrategyGroupByBase implements ExprTableAccessEvalStrategy {

    private final Map<String, TableMetadataColumn> items;

    protected ExprTableEvalStrategyGroupByTopLevelBase(TableAndLockProviderGrouped provider, Map<String, TableMetadataColumn> items) {
        super(provider);
        this.items = items;
    }

    protected Object evaluateInternal(Object groupKey, eu.uk.ncl.pet5o.esper.client.EventBean[] eventsPerStream, boolean isNewData, ExprEvaluatorContext context) {
        ObjectArrayBackedEventBean row = lockTableReadAndGet(groupKey, context);
        if (row == null) {
            return null;
        }
        return ExprTableEvalStrategyUtil.evalMap(row, ExprTableEvalStrategyUtil.getRow(row), items, eventsPerStream, isNewData, context);
    }

    protected Object[] evaluateTypableSingleInternal(Object groupKey, eu.uk.ncl.pet5o.esper.client.EventBean[] eventsPerStream, boolean isNewData, ExprEvaluatorContext context) {
        ObjectArrayBackedEventBean row = lockTableReadAndGet(groupKey, context);
        if (row == null) {
            return null;
        }
        return ExprTableEvalStrategyUtil.evalTypable(row, ExprTableEvalStrategyUtil.getRow(row), items, eventsPerStream, isNewData, context);
    }

    public Collection<EventBean> evaluateGetROCollectionEvents(eu.uk.ncl.pet5o.esper.client.EventBean[] eventsPerStream, boolean isNewData, ExprEvaluatorContext context) {
        return null;
    }

    public eu.uk.ncl.pet5o.esper.client.EventBean evaluateGetEventBean(eu.uk.ncl.pet5o.esper.client.EventBean[] eventsPerStream, boolean isNewData, ExprEvaluatorContext context) {
        return null;
    }

    public Collection evaluateGetROCollectionScalar(eu.uk.ncl.pet5o.esper.client.EventBean[] eventsPerStream, boolean isNewData, ExprEvaluatorContext context) {
        return null;
    }
}
