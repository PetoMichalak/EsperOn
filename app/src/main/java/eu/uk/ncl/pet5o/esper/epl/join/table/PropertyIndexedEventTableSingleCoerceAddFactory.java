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
package eu.uk.ncl.pet5o.esper.epl.join.table;

import eu.uk.ncl.pet5o.esper.client.EventType;
import eu.uk.ncl.pet5o.esper.epl.expression.core.ExprEvaluatorContext;
import eu.uk.ncl.pet5o.esper.util.JavaClassHelper;
import eu.uk.ncl.pet5o.esper.util.SimpleNumberCoercer;
import eu.uk.ncl.pet5o.esper.util.SimpleNumberCoercerFactory;

public class PropertyIndexedEventTableSingleCoerceAddFactory extends PropertyIndexedEventTableSingleFactory {
    protected final SimpleNumberCoercer coercer;
    protected final Class coercionType;

    /**
     * Ctor.
     *
     * @param streamNum    is the stream number of the indexed stream
     * @param eventType    is the event type of the indexed stream
     * @param propertyName are the property names to get property values
     * @param coercionType are the classes to coerce indexed values to
     */
    public PropertyIndexedEventTableSingleCoerceAddFactory(int streamNum, EventType eventType, String propertyName, Class coercionType) {
        super(streamNum, eventType, propertyName, false, null);
        this.coercionType = coercionType;
        if (JavaClassHelper.isNumeric(coercionType)) {
            coercer = SimpleNumberCoercerFactory.getCoercer(null, coercionType);
        } else {
            coercer = null;
        }
    }

    @Override
    public EventTable[] makeEventTables(EventTableFactoryTableIdent tableIdent, ExprEvaluatorContext exprEvaluatorContext) {
        EventTableOrganization organization = getOrganization();
        return new EventTable[]{new PropertyIndexedEventTableSingleCoerceAdd(propertyGetter, organization, coercer, coercionType)};
    }

    protected EventTableOrganization getOrganization() {
        return new EventTableOrganization(optionalIndexName, unique, true, streamNum, new String[]{propertyName}, EventTableOrganizationType.HASH);
    }
}
