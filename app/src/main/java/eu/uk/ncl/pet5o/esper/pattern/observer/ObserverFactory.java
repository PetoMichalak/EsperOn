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
package eu.uk.ncl.pet5o.esper.pattern.observer;

import eu.uk.ncl.pet5o.esper.epl.expression.core.ExprNode;
import eu.uk.ncl.pet5o.esper.epl.expression.core.ExprValidationContext;
import eu.uk.ncl.pet5o.esper.filterspec.MatchedEventMap;
import eu.uk.ncl.pet5o.esper.pattern.EvalStateNodeNumber;
import eu.uk.ncl.pet5o.esper.pattern.MatchedEventConvertor;
import eu.uk.ncl.pet5o.esper.pattern.PatternAgentInstanceContext;

import java.util.List;

/**
 * Interface for factories for making observer instances.
 */
public interface ObserverFactory {
    /**
     * Sets the observer object parameters.
     *
     * @param observerParameters is a list of parameters
     * @param convertor          for converting partial pattern matches to event-per-stream for expressions
     * @param validationContext  context
     * @throws ObserverParameterException thrown to indicate a parameter problem
     */
    public void setObserverParameters(List<ExprNode> observerParameters, MatchedEventConvertor convertor, ExprValidationContext validationContext) throws ObserverParameterException;

    /**
     * Make an observer instance.
     *
     * @param context                  - services that may be required by observer implementation
     * @param beginState               - start state for observer
     * @param observerEventEvaluator   - receiver for events observed
     * @param stateNodeId              - optional id for the associated pattern state node
     * @param observerState            - state node for observer
     * @param isFilterChildNonQuitting true for non-quitting filter
     * @return observer instance
     */
    public EventObserver makeObserver(PatternAgentInstanceContext context,
                                      MatchedEventMap beginState,
                                      ObserverEventEvaluator observerEventEvaluator,
                                      EvalStateNodeNumber stateNodeId,
                                      Object observerState,
                                      boolean isFilterChildNonQuitting);

    public boolean isNonRestarting();
}
