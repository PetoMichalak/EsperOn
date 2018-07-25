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
import eu.uk.ncl.pet5o.esper.event.ObjectArrayBackedEventBean;

import java.util.Collection;

public abstract class ExprTableEvalStrategyGroupByMethodBase extends ExprTableEvalStrategyGroupByBase implements ExprTableAccessEvalStrategy {

    private final int index;

    protected ExprTableEvalStrategyGroupByMethodBase(TableAndLockProviderGrouped provider, int index) {
        super(provider);
        this.index = index;
    }

    protected Object evaluateInternal(Object groupKey, ExprEvaluatorContext context) {
        ObjectArrayBackedEventBean row = lockTableReadAndGet(groupKey, context);
        if (row == null) {
            return null;
        }
        return ExprTableEvalStrategyUtil.evalMethodGetValue(ExprTableEvalStrategyUtil.getRow(row), index);
    }

    public Object[] evaluateTypableSingle(eu.uk.ncl.pet5o.esper.client.EventBean[] eventsPerStream, boolean isNewData, ExprEvaluatorContext context) {
        return null;
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
