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
package eu.uk.ncl.pet5o.esper.epl.view;

import eu.uk.ncl.pet5o.esper.client.EventBean;
import eu.uk.ncl.pet5o.esper.collection.MultiKey;
import eu.uk.ncl.pet5o.esper.collection.UniformPair;
import eu.uk.ncl.pet5o.esper.core.service.StatementContext;

import java.util.Set;

public class OutputProcessViewAfterStateImpl implements OutputProcessViewAfterState {
    private final Long afterConditionTime;
    private final Integer afterConditionNumberOfEvents;
    protected boolean isAfterConditionSatisfied;
    private int afterConditionEventsFound;

    public OutputProcessViewAfterStateImpl(Long afterConditionTime, Integer afterConditionNumberOfEvents) {
        this.afterConditionTime = afterConditionTime;
        this.afterConditionNumberOfEvents = afterConditionNumberOfEvents;
    }

    /**
     * Returns true if the after-condition is satisfied.
     *
     * @param newEvents is the view new events
     * @return indicator for output condition
     */
    public boolean checkUpdateAfterCondition(EventBean[] newEvents, StatementContext statementContext) {
        return isAfterConditionSatisfied || checkAfterCondition(newEvents == null ? 0 : newEvents.length, statementContext);
    }

    /**
     * Returns true if the after-condition is satisfied.
     *
     * @param newEvents is the join new events
     * @return indicator for output condition
     */
    public boolean checkUpdateAfterCondition(Set<MultiKey<EventBean>> newEvents, StatementContext statementContext) {
        return isAfterConditionSatisfied || checkAfterCondition(newEvents == null ? 0 : newEvents.size(), statementContext);
    }

    /**
     * Returns true if the after-condition is satisfied.
     *
     * @param newOldEvents is the new and old events pair
     * @return indicator for output condition
     */
    public boolean checkUpdateAfterCondition(UniformPair<EventBean[]> newOldEvents, StatementContext statementContext) {
        return isAfterConditionSatisfied || checkAfterCondition(newOldEvents == null ? 0 : (newOldEvents.getFirst() == null ? 0 : newOldEvents.getFirst().length), statementContext);
    }

    public void destroy() {
        // no action required
    }

    private boolean checkAfterCondition(int numOutputEvents, StatementContext statementContext) {
        if (afterConditionTime != null) {
            long time = statementContext.getTimeProvider().getTime();
            if (time < afterConditionTime) {
                return false;
            }

            isAfterConditionSatisfied = true;
            return true;
        } else if (afterConditionNumberOfEvents != null) {
            afterConditionEventsFound += numOutputEvents;
            if (afterConditionEventsFound <= afterConditionNumberOfEvents) {
                return false;
            }

            isAfterConditionSatisfied = true;
            return true;
        } else {
            isAfterConditionSatisfied = true;
            return true;
        }
    }
}
