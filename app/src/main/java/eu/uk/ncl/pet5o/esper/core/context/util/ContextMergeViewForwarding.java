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
package eu.uk.ncl.pet5o.esper.core.context.util;

import com.espertech.esper.client.EventType;

public class ContextMergeViewForwarding extends ContextMergeView {

    public ContextMergeViewForwarding(EventType eventType) {
        super(eventType);
    }

    @Override
    public void update(com.espertech.esper.client.EventBean[] newData, com.espertech.esper.client.EventBean[] oldData) {
        updateChildren(newData, oldData);
    }
}
