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

import eu.uk.ncl.pet5o.esper.filterspec.MatchedEventMap;
import eu.uk.ncl.pet5o.esper.pattern.PatternAgentInstanceContext;

/**
 * For use by {@link EventObserver} instances to place an event for processing/evaluation.
 */
public interface ObserverEventEvaluator {
    /**
     * Indicate an event for evaluation (sub-expression the observer represents has turned true).
     *
     * @param matchEvent is the matched events so far
     * @param quitted    whether the observer quit, usually "true" for most observers
     */
    public void observerEvaluateTrue(MatchedEventMap matchEvent, boolean quitted);

    /**
     * Indicate that the observer turned permanently false.
     *
     * @param restartable true for whether it can restart
     */
    public void observerEvaluateFalse(boolean restartable);

    public PatternAgentInstanceContext getContext();
}
