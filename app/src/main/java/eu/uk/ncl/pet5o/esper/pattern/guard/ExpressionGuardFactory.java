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
package eu.uk.ncl.pet5o.esper.pattern.guard;

import eu.uk.ncl.pet5o.esper.epl.expression.core.ExprNode;
import eu.uk.ncl.pet5o.esper.filterspec.MatchedEventMap;
import eu.uk.ncl.pet5o.esper.pattern.EvalStateNodeNumber;
import eu.uk.ncl.pet5o.esper.pattern.MatchedEventConvertor;
import eu.uk.ncl.pet5o.esper.pattern.PatternAgentInstanceContext;
import eu.uk.ncl.pet5o.esper.util.JavaClassHelper;

import java.io.Serializable;
import java.util.List;

/**
 * Factory for {@link eu.uk.ncl.pet5o.esper.pattern.guard.TimerWithinGuard} instances.
 */
public class ExpressionGuardFactory implements GuardFactory, Serializable {
    private static final long serialVersionUID = -5107582730824731419L;

    protected ExprNode expression;

    /**
     * For converting matched-events maps to events-per-stream.
     */
    protected transient MatchedEventConvertor convertor;

    public void setGuardParameters(List<ExprNode> parameters, MatchedEventConvertor convertor) throws GuardParameterException {
        String errorMessage = "Expression pattern guard requires a single expression as a parameter returning a true or false (boolean) value";
        if (parameters.size() != 1) {
            throw new GuardParameterException(errorMessage);
        }
        expression = parameters.get(0);

        if (JavaClassHelper.getBoxedType(parameters.get(0).getForge().getEvaluationType()) != Boolean.class) {
            throw new GuardParameterException(errorMessage);
        }

        this.convertor = convertor;
    }

    public Guard makeGuard(PatternAgentInstanceContext context, MatchedEventMap beginState, Quitable quitable, EvalStateNodeNumber stateNodeId, Object guardState) {
        return new ExpressionGuard(convertor, expression.getForge().getExprEvaluator(), quitable);
    }
}
