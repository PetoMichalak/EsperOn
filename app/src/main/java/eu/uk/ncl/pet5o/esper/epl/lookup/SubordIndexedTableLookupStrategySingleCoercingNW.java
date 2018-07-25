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

import eu.uk.ncl.pet5o.esper.client.EventBean;
import eu.uk.ncl.pet5o.esper.epl.expression.core.ExprEvaluator;
import eu.uk.ncl.pet5o.esper.epl.expression.core.ExprEvaluatorContext;
import eu.uk.ncl.pet5o.esper.epl.join.table.PropertyIndexedEventTableSingle;
import eu.uk.ncl.pet5o.esper.event.EventBeanUtility;

/**
 * Index lookup strategy that coerces the key values before performing a lookup.
 */
public class SubordIndexedTableLookupStrategySingleCoercingNW extends SubordIndexedTableLookupStrategySingleExprNW {
    private Class coercionType;

    public SubordIndexedTableLookupStrategySingleCoercingNW(ExprEvaluator evaluator, PropertyIndexedEventTableSingle index, Class coercionType, LookupStrategyDesc strategyDesc) {
        super(evaluator, index, strategyDesc);
        this.coercionType = coercionType;
    }

    @Override
    protected Object getKey(EventBean[] eventsPerStream, ExprEvaluatorContext context) {
        Object key = super.getKey(eventsPerStream, context);
        return EventBeanUtility.coerce(key, coercionType);
    }
}
