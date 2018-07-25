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
package eu.uk.ncl.pet5o.esper.epl.join.exec.base;

import eu.uk.ncl.pet5o.esper.client.EventBean;
import eu.uk.ncl.pet5o.esper.epl.expression.core.ExprEvaluatorContext;
import eu.uk.ncl.pet5o.esper.util.IndentWriter;

import java.util.Collection;

public class ExecNodeNoOp extends ExecNode {
    public void process(EventBean lookupEvent, EventBean[] prefillPath, Collection<EventBean[]> result, ExprEvaluatorContext exprEvaluatorContext) {
    }

    public void print(IndentWriter writer) {
        writer.println("ExecNodeNoOp");
    }
}
