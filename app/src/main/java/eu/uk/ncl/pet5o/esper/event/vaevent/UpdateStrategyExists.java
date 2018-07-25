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
package eu.uk.ncl.pet5o.esper.event.vaevent;

import eu.uk.ncl.pet5o.esper.client.EventBean;
import eu.uk.ncl.pet5o.esper.client.EventPropertyGetter;
import eu.uk.ncl.pet5o.esper.util.NullableObject;

/**
 * Strategy for merging update properties using only existing property's values.
 */
public class UpdateStrategyExists extends UpdateStrategyBase {
    /**
     * Ctor.
     *
     * @param spec the specification
     */
    public UpdateStrategyExists(RevisionSpec spec) {
        super(spec);
    }

    public void handleUpdate(boolean isBaseEventType,
                             RevisionStateMerge revisionState,
                             RevisionEventBeanMerge revisionEvent,
                             RevisionTypeDesc typesDesc) {
        eu.uk.ncl.pet5o.esper.client.EventBean underlyingEvent = revisionEvent.getUnderlyingFullOrDelta();

        NullableObject<Object>[] changeSetValues = revisionState.getOverlays();
        if (changeSetValues == null) {
            // optimization - the full event sets it to null, deltas all get a new one
            changeSetValues = new NullableObject[spec.getChangesetPropertyNames().length];
        } else {
            changeSetValues = arrayCopy(changeSetValues);   // preserve the last revisions
        }

        // apply all properties of the delta event
        int[] indexes = typesDesc.getChangesetPropertyIndex();
        EventPropertyGetter[] getters = typesDesc.getChangesetPropertyGetters();
        for (int i = 0; i < indexes.length; i++) {
            int index = indexes[i];

            if (!getters[i].isExistsProperty(underlyingEvent)) {
                continue;
            }

            Object value = getters[i].get(underlyingEvent);
            changeSetValues[index] = new NullableObject<Object>(value);
        }

        revisionState.setOverlays(changeSetValues);
    }
}
