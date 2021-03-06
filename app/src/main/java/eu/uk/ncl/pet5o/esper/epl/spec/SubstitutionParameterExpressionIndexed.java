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

public class SubstitutionParameterExpressionIndexed extends SubstitutionParameterExpressionBase {
    private static final long serialVersionUID = -8795823095298045304L;
    private final int index;

    /**
     * Ctor.
     *
     * @param index is the index of the substitution parameter
     */
    public SubstitutionParameterExpressionIndexed(int index) {
        this.index = index;
    }

    protected void toPrecedenceFreeEPLUnsatisfied(StringWriter writer) {
        writer.write("?");
    }

    public int getIndex() {
        return index;
    }
}
