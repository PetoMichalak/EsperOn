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
package eu.uk.ncl.pet5o.esper.epl.join.exec.composite;

import com.espertech.esper.client.EventBean;

import java.util.Map;
import java.util.Set;

public interface CompositeIndexLookup {
    public void lookup(Map parent, Set<EventBean> result, CompositeIndexQueryResultPostProcessor postProcessor);

    public void setNext(CompositeIndexLookup action);
}