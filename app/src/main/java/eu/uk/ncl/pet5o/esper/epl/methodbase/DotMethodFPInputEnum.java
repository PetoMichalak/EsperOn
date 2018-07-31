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
package eu.uk.ncl.pet5o.esper.epl.methodbase;

public enum DotMethodFPInputEnum {
    SCALAR_NUMERIC,
    SCALAR_ANY,
    EVENTCOLL,
    ANY;

    public boolean isScalar() {
        return this == SCALAR_ANY || this == SCALAR_NUMERIC;
    }
}