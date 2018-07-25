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
package eu.uk.ncl.pet5o.esper.core.context.mgr;

import eu.uk.ncl.pet5o.esper.client.EventPropertyGetter;
import eu.uk.ncl.pet5o.esper.client.PropertyAccessException;
import eu.uk.ncl.pet5o.esper.epl.expression.core.ExprEvaluator;

public class ContextControllerHashedGetterHashSingle implements EventPropertyGetter {

    private final ExprEvaluator eval;
    private final int granularity;

    public ContextControllerHashedGetterHashSingle(ExprEvaluator eval, int granularity) {
        this.eval = eval;
        this.granularity = granularity;
    }

    public Object get(eu.uk.ncl.pet5o.esper.client.EventBean eventBean) throws PropertyAccessException {
        eu.uk.ncl.pet5o.esper.client.EventBean[] events = new eu.uk.ncl.pet5o.esper.client.EventBean[]{eventBean};
        Object code = eval.evaluate(events, true, null);

        int value;
        if (code == null) {
            value = 0;
        } else {
            value = code.hashCode() % granularity;
        }

        if (value >= 0) {
            return value;
        }
        return -value;
    }

    public boolean isExistsProperty(eu.uk.ncl.pet5o.esper.client.EventBean eventBean) {
        return false;
    }

    public Object getFragment(eu.uk.ncl.pet5o.esper.client.EventBean eventBean) throws PropertyAccessException {
        return null;
    }
}
