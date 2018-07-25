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
package eu.uk.ncl.pet5o.esper.epl.expression.funcs;

import eu.uk.ncl.pet5o.esper.epl.expression.core.ExprEvaluator;
import eu.uk.ncl.pet5o.esper.epl.expression.core.ExprEvaluatorContext;
import eu.uk.ncl.pet5o.esper.metrics.instrumentation.InstrumentationHelper;

public class ExprCastNodeForgeConstEval implements ExprEvaluator {
    private final ExprCastNodeForge forge;
    private final Object theConstant;

    public ExprCastNodeForgeConstEval(ExprCastNodeForge forge, Object theConstant) {
        this.forge = forge;
        this.theConstant = theConstant;
    }

    public Object evaluate(eu.uk.ncl.pet5o.esper.client.EventBean[] eventsPerStream, boolean isNewData, ExprEvaluatorContext context) {
        if (InstrumentationHelper.ENABLED) {
            InstrumentationHelper.get().qExprCast(forge.getForgeRenderable());
            InstrumentationHelper.get().aExprCast(theConstant);
        }
        return theConstant;
    }

}
