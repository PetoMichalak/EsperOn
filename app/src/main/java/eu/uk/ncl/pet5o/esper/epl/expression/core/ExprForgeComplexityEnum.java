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
package eu.uk.ncl.pet5o.esper.epl.expression.core;

public enum ExprForgeComplexityEnum {
    NOT_APPLICABLE, // such as expressions that cannot be evaluated
    SELF, // such as expressions that have referenced state and self-evaluate
    NONE, // such as constants or expressions that return constant values
    SINGLE, // such as event property and context property
    INTER  // interdependent expressions
}
