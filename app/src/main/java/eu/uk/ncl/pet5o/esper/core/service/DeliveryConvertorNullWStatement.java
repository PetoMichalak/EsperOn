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
package eu.uk.ncl.pet5o.esper.core.service;

import eu.uk.ncl.pet5o.esper.client.EPStatement;

/**
 * Implementation that does not convert columns.
 */
public class DeliveryConvertorNullWStatement implements DeliveryConvertor {
    private final EPStatement statement;

    public DeliveryConvertorNullWStatement(EPStatement statement) {
        this.statement = statement;
    }

    public Object[] convertRow(Object[] columns) {
        Object[] deliver = new Object[columns.length + 1];
        deliver[0] = statement;
        System.arraycopy(columns, 0, deliver, 1, columns.length);
        return deliver;
    }
}
