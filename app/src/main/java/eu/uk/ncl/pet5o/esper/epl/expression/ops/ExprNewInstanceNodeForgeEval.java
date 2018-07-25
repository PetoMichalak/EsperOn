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
package eu.uk.ncl.pet5o.esper.epl.expression.ops;

import eu.uk.ncl.pet5o.esper.epl.expression.core.ExprEvaluator;
import eu.uk.ncl.pet5o.esper.epl.expression.core.ExprEvaluatorContext;
import eu.uk.ncl.pet5o.esper.event.bean.InstanceManufacturer;

/**
 * Represents the "new Class(...)" operator in an expression tree.
 */
public class ExprNewInstanceNodeForgeEval implements ExprEvaluator {

    private final ExprNewInstanceNodeForge forge;
    private final InstanceManufacturer manufacturer;

    public ExprNewInstanceNodeForgeEval(ExprNewInstanceNodeForge forge, InstanceManufacturer manufacturer) {
        this.forge = forge;
        this.manufacturer = manufacturer;
    }

    public Object evaluate(eu.uk.ncl.pet5o.esper.client.EventBean[] eventsPerStream, boolean isNewData, ExprEvaluatorContext exprEvaluatorContext) {
        return manufacturer.make(eventsPerStream, isNewData, exprEvaluatorContext);
    }

}
