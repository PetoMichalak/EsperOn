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

public interface ExprTimePeriodEvalDeltaConst extends ExprTimePeriodEvalDeltaConstFactory {
    public long deltaAdd(long fromTime);

    public long deltaSubtract(long fromTime);

    public ExprTimePeriodEvalDeltaResult deltaAddWReference(long fromTime, long reference);

    public boolean equalsTimePeriod(ExprTimePeriodEvalDeltaConst timeDeltaComputation);

    CodegenExpression deltaAddCodegen(CodegenExpression reference, CodegenMethodScope codegenMethodScope, CodegenClassScope codegenClassScope);
}
