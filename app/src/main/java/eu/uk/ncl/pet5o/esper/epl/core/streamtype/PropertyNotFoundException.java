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
package eu.uk.ncl.pet5o.esper.epl.core.streamtype;

/**
 * Exception to indicate that a property name used in a filter doesn't resolve.
 */
public class PropertyNotFoundException extends StreamTypesException {
    private static final long serialVersionUID = -29171552032256573L;

    public PropertyNotFoundException(String messageWithoutDetail, StreamTypesExceptionSuggestionGen msgGen) {
        super(messageWithoutDetail, msgGen);
    }
}
