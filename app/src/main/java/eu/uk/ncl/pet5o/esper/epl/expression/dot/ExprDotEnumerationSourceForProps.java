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
package eu.uk.ncl.pet5o.esper.epl.expression.dot;

import eu.uk.ncl.pet5o.esper.epl.expression.core.ExprEnumerationEval;
import eu.uk.ncl.pet5o.esper.epl.expression.core.ExprEnumerationGivenEvent;
import eu.uk.ncl.pet5o.esper.epl.rettype.EPType;

public class ExprDotEnumerationSourceForProps extends ExprDotEnumerationSource {
    private final ExprEnumerationGivenEvent enumerationGivenEvent;

    public ExprDotEnumerationSourceForProps(ExprEnumerationEval enumeration, EPType returnType, Integer streamOfProviderIfApplicable, ExprEnumerationGivenEvent enumerationGivenEvent) {
        super(returnType, streamOfProviderIfApplicable, enumeration);
        this.enumerationGivenEvent = enumerationGivenEvent;
    }

    public ExprEnumerationGivenEvent getEnumerationGivenEvent() {
        return enumerationGivenEvent;
    }
}
