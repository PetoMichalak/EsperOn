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

import com.espertech.esper.client.EventPropertyGetter;
import com.espertech.esper.client.PropertyAccessException;
import com.espertech.esper.epl.expression.core.ExprEvaluator;

import java.util.zip.CRC32;

public class ContextControllerHashedGetterCRC32Single implements EventPropertyGetter {

    private final ExprEvaluator eval;
    private final int granularity;

    public ContextControllerHashedGetterCRC32Single(ExprEvaluator eval, int granularity) {
        this.eval = eval;
        this.granularity = granularity;
    }

    public Object get(com.espertech.esper.client.EventBean eventBean) throws PropertyAccessException {
        com.espertech.esper.client.EventBean[] events = new com.espertech.esper.client.EventBean[]{eventBean};
        String code = (String) eval.evaluate(events, true, null);

        long value;
        if (code == null) {
            value = 0;
        } else {
            CRC32 crc = new CRC32();
            crc.update(code.getBytes());
            value = crc.getValue() % granularity;
        }

        int result = (int) value;
        if (result >= 0) {
            return result;
        }
        return -result;
    }

    public boolean isExistsProperty(com.espertech.esper.client.EventBean eventBean) {
        return false;
    }

    public Object getFragment(com.espertech.esper.client.EventBean eventBean) throws PropertyAccessException {
        return null;
    }
}
