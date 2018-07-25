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

public class OutputProcessViewAfterStateNone implements OutputProcessViewAfterState {
    public final static OutputProcessViewAfterStateNone INSTANCE = new OutputProcessViewAfterStateNone();

    private OutputProcessViewAfterStateNone() {
    }

    public boolean checkUpdateAfterCondition(EventBean[] newEvents, StatementContext statementContext) {
        return true;
    }

    public boolean checkUpdateAfterCondition(Set<MultiKey<EventBean>> newEvents, StatementContext statementContext) {
        return true;
    }

    public boolean checkUpdateAfterCondition(UniformPair<EventBean[]> newOldEvents, StatementContext statementContext) {
        return true;
    }

    public void destroy() {
        // no action required
    }
}
