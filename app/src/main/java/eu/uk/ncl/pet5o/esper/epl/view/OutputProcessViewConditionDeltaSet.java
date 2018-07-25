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

import java.util.List;
import java.util.Set;

public interface OutputProcessViewConditionDeltaSet {
    int getNumChangesetRows();

    void addView(UniformPair<EventBean[]> events);

    void addJoin(UniformPair<Set<MultiKey<EventBean>>> events);

    void clear();

    List<UniformPair<Set<MultiKey<EventBean>>>> getJoinEventsSet();

    List<UniformPair<EventBean[]>> getViewEventsSet();

    void destroy();
}
