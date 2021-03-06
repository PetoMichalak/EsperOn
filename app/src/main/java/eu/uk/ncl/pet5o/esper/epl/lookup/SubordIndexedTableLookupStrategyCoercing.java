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
import eu.uk.ncl.pet5o.esper.epl.join.table.PropertyIndexedEventTable;
import eu.uk.ncl.pet5o.esper.util.JavaClassHelper;

/**
 * Index lookup strategy that coerces the key values before performing a lookup.
 */
public class SubordIndexedTableLookupStrategyCoercing extends SubordIndexedTableLookupStrategyExpr {
    private Class[] coercionTypes;

    public SubordIndexedTableLookupStrategyCoercing(int numStreamsOuter, ExprEvaluator[] evaluators, PropertyIndexedEventTable index, Class[] coercionTypes, LookupStrategyDesc strategyDesc) {
        super(numStreamsOuter, evaluators, index, strategyDesc);
        this.coercionTypes = coercionTypes;
    }

    @Override
    protected Object[] getKeys(EventBean[] eventsPerStream, ExprEvaluatorContext context) {
        Object[] keys = super.getKeys(eventsPerStream, context);
        for (int i = 0; i < keys.length; i++) {
            Object value = keys[i];

            Class coercionType = coercionTypes[i];
            if ((value != null) && (!value.getClass().equals(coercionType))) {
                if (value instanceof Number) {
                    value = JavaClassHelper.coerceBoxed((Number) value, coercionTypes[i]);
                }
                keys[i] = value;
            }
        }
        return keys;
    }
}
