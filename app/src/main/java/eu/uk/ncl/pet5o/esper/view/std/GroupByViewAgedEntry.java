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
package eu.uk.ncl.pet5o.esper.view.std;

import eu.uk.ncl.pet5o.esper.view.View;

public class GroupByViewAgedEntry {
    private final View subview;
    private long lastUpdateTime;

    public GroupByViewAgedEntry(View subview, long lastUpdateTime) {
        this.subview = subview;
        this.lastUpdateTime = lastUpdateTime;
    }

    public View getSubview() {
        return subview;
    }

    public long getLastUpdateTime() {
        return lastUpdateTime;
    }

    public void setLastUpdateTime(long lastUpdateTime) {
        this.lastUpdateTime = lastUpdateTime;
    }
}
