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
package eu.uk.ncl.pet5o.esper.view;

import eu.uk.ncl.pet5o.esper.collection.ViewUpdatedCollection;
import eu.uk.ncl.pet5o.esper.core.context.util.AgentInstanceViewFactoryChainContext;
import eu.uk.ncl.pet5o.esper.view.ext.IStreamSortRankRandomAccess;
import eu.uk.ncl.pet5o.esper.view.ext.IStreamSortRankRandomAccessImpl;
import eu.uk.ncl.pet5o.esper.view.window.IStreamRandomAccess;
import eu.uk.ncl.pet5o.esper.view.window.IStreamRelativeAccess;
import eu.uk.ncl.pet5o.esper.view.window.RandomAccessByIndexGetter;
import eu.uk.ncl.pet5o.esper.view.window.RelativeAccessByEventNIndexGetter;

public class ViewServicePreviousFactoryImpl implements ViewServicePreviousFactory {
    public ViewUpdatedCollection getOptPreviousExprRandomAccess(AgentInstanceViewFactoryChainContext agentInstanceViewFactoryContext) {
        IStreamRandomAccess randomAccess = null;
        if (agentInstanceViewFactoryContext.getPreviousNodeGetter() != null) {
            RandomAccessByIndexGetter getter = (RandomAccessByIndexGetter) agentInstanceViewFactoryContext.getPreviousNodeGetter();
            randomAccess = new IStreamRandomAccess(getter);
            getter.updated(randomAccess);
        }
        return randomAccess;
    }

    public ViewUpdatedCollection getOptPreviousExprRelativeAccess(AgentInstanceViewFactoryChainContext agentInstanceViewFactoryContext) {
        IStreamRelativeAccess relativeAccessByEvent = null;

        if (agentInstanceViewFactoryContext.getPreviousNodeGetter() != null) {
            RelativeAccessByEventNIndexGetter getter = (RelativeAccessByEventNIndexGetter) agentInstanceViewFactoryContext.getPreviousNodeGetter();
            IStreamRelativeAccess.IStreamRelativeAccessUpdateObserver observer = (IStreamRelativeAccess.IStreamRelativeAccessUpdateObserver) getter;
            relativeAccessByEvent = new IStreamRelativeAccess(observer);
            observer.updated(relativeAccessByEvent, null);
        }

        return relativeAccessByEvent;
    }

    public IStreamSortRankRandomAccess getOptPreviousExprSortedRankedAccess(AgentInstanceViewFactoryChainContext agentInstanceViewFactoryContext) {
        IStreamSortRankRandomAccess rankedRandomAccess = null;

        if (agentInstanceViewFactoryContext.getPreviousNodeGetter() != null) {
            RandomAccessByIndexGetter getter = (RandomAccessByIndexGetter) agentInstanceViewFactoryContext.getPreviousNodeGetter();
            rankedRandomAccess = new IStreamSortRankRandomAccessImpl(getter);
            getter.updated(rankedRandomAccess);
        }

        return rankedRandomAccess;
    }
}
