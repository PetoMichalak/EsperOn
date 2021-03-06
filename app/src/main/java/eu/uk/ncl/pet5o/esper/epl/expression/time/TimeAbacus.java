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
package eu.uk.ncl.pet5o.esper.epl.expression.time;

import eu.uk.ncl.pet5o.esper.codegen.base.CodegenClassScope;
import eu.uk.ncl.pet5o.esper.codegen.base.CodegenMethodScope;
import eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpression;
import eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionRef;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;

public interface TimeAbacus extends Serializable {
    long deltaForSecondsNumber(Number timeInSeconds);

    long deltaForSecondsDouble(double seconds);

    long calendarSet(long fromTime, Calendar cal);

    long calendarGet(Calendar cal, long remainder);

    long getOneSecond();

    Date toDate(long ts);

    CodegenExpression calendarSetCodegen(CodegenExpression startLong, CodegenExpression cal, CodegenMethodScope codegenMethodScope, CodegenClassScope codegenClassScope);
    CodegenExpression calendarGetCodegen(CodegenExpression cal, CodegenExpression startRemainder, CodegenClassScope codegenClassScope);
    CodegenExpression toDateCodegen(CodegenExpression ts);
    CodegenExpression deltaForSecondsDoubleCodegen(CodegenExpressionRef sec, CodegenClassScope codegenClassScope);
}
