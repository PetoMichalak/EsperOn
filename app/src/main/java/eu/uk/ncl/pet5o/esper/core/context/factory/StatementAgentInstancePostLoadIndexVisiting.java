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
package eu.uk.ncl.pet5o.esper.core.context.factory;

import eu.uk.ncl.pet5o.esper.epl.join.base.JoinSetComposer;

public class StatementAgentInstancePostLoadIndexVisiting implements StatementAgentInstancePostLoad {
    private final JoinSetComposer joinSetComposer;

    public StatementAgentInstancePostLoadIndexVisiting(JoinSetComposer joinSetComposer) {
        this.joinSetComposer = joinSetComposer;
    }

    public void executePostLoad() {
    }

    public void acceptIndexVisitor(StatementAgentInstancePostLoadIndexVisitor visitor) {
        joinSetComposer.visitIndexes(visitor);
    }
}
