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
package eu.uk.ncl.pet5o.esper.epl.named;

import eu.uk.ncl.pet5o.esper.client.EventBean;
import eu.uk.ncl.pet5o.esper.client.EventType;
import eu.uk.ncl.pet5o.esper.collection.OneEventCollection;
import eu.uk.ncl.pet5o.esper.epl.expression.core.ExprEvaluatorContext;
import eu.uk.ncl.pet5o.esper.epl.spec.OnTriggerType;
import eu.uk.ncl.pet5o.esper.metrics.instrumentation.InstrumentationHelper;
import eu.uk.ncl.pet5o.esper.util.CollectionUtil;
import eu.uk.ncl.pet5o.esper.view.ViewSupport;

import java.util.Collections;
import java.util.Iterator;

/**
 * View for the on-delete statement that handles removing events from a named window.
 */
public class NamedWindowOnMergeInsertUnmatched extends ViewSupport implements NamedWindowOnExprView {
    /**
     * The event type of the events hosted in the named window.
     */
    private final ExprEvaluatorContext exprEvaluatorContext;

    /**
     * The root view accepting removals (old data).
     */
    protected final NamedWindowRootViewInstance rootView;

    private final NamedWindowOnMergeViewFactory factory;

    /**
     * Ctor.
     *
     * @param factory              merge view factory
     * @param rootView             to indicate which events to delete
     * @param exprEvaluatorContext context for expression evalauation
     */
    public NamedWindowOnMergeInsertUnmatched(NamedWindowRootViewInstance rootView,
                                             ExprEvaluatorContext exprEvaluatorContext,
                                             NamedWindowOnMergeViewFactory factory) {
        this.rootView = rootView;
        this.exprEvaluatorContext = exprEvaluatorContext;
        this.factory = factory;
    }

    public void update(EventBean[] newData, EventBean[] oldData) {
        if (InstrumentationHelper.ENABLED) {
            InstrumentationHelper.get().qInfraOnAction(OnTriggerType.ON_MERGE, newData, CollectionUtil.EVENTBEANARRAY_EMPTY);
        }

        if (newData == null) {
            return;
        }
        OneEventCollection newColl = new OneEventCollection();
        factory.getNamedWindowOnMergeHelper().getInsertUnmatched().apply(null, newData, newColl, null, exprEvaluatorContext);
        NamedWindowOnMergeView.applyDelta(newColl, null, factory, rootView, this);

        if (InstrumentationHelper.ENABLED) {
            InstrumentationHelper.get().aInfraOnAction();
        }
    }

    /**
     * returns expr context.
     *
     * @return context
     */
    public ExprEvaluatorContext getExprEvaluatorContext() {
        return exprEvaluatorContext;
    }

    public EventType getEventType() {
        return rootView.getEventType();
    }

    public Iterator<EventBean> iterator() {
        return Collections.emptyIterator();
    }
}
