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
package eu.uk.ncl.pet5o.esper.filter;

import eu.uk.ncl.pet5o.esper.epl.core.engineimport.EngineImportService;
import eu.uk.ncl.pet5o.esper.epl.expression.core.ExprEvaluator;
import eu.uk.ncl.pet5o.esper.epl.expression.core.ExprEvaluatorContext;
import eu.uk.ncl.pet5o.esper.epl.expression.core.ExprNode;
import eu.uk.ncl.pet5o.esper.epl.variable.VariableService;

import java.lang.annotation.Annotation;

public class ExprNodeAdapterMultiStreamNoTL extends ExprNodeAdapterMultiStream {
    public ExprNodeAdapterMultiStreamNoTL(int filterSpecId, int filterSpecParamPathNum, ExprNode exprNode, ExprEvaluator exprEvaluator, ExprEvaluatorContext evaluatorContext, VariableService variableService, EngineImportService engineImportService, eu.uk.ncl.pet5o.esper.client.EventBean[] prototype, Annotation[] annotations) {
        super(filterSpecId, filterSpecParamPathNum, exprNode, exprEvaluator, evaluatorContext, variableService, engineImportService, prototype, annotations);
    }

    @Override
    public boolean evaluate(eu.uk.ncl.pet5o.esper.client.EventBean theEvent) {
        if (variableService != null) {
            variableService.setLocalVersion();
        }

        eu.uk.ncl.pet5o.esper.client.EventBean[] eventsPerStream = new eu.uk.ncl.pet5o.esper.client.EventBean[prototypeArray.length];
        System.arraycopy(prototypeArray, 0, eventsPerStream, 0, prototypeArray.length);
        eventsPerStream[0] = theEvent;
        return super.evaluatePerStream(eventsPerStream);
    }
}
