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
package eu.uk.ncl.pet5o.esper.epl.spec;

import java.io.StringWriter;

public class SubstitutionParameterExpressionNamed extends SubstitutionParameterExpressionBase {
    private static final long serialVersionUID = -67840305248984147L;
    private final String name;

    public SubstitutionParameterExpressionNamed(String name) {
        this.name = name;
    }

    protected void toPrecedenceFreeEPLUnsatisfied(StringWriter writer) {
        writer.write("?:");
        writer.write(name);
    }

    public String getName() {
        return name;
    }
}
