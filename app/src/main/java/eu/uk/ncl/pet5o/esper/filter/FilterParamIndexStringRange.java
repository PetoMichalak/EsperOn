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

package eu.uk.ncl.pet5o.esper.filter;

import eu.uk.ncl.pet5o.esper.epl.expression.core.ExprFilterSpecLookupable;
import eu.uk.ncl.pet5o.esper.filterspec.FilterOperator;
import eu.uk.ncl.pet5o.esper.filterspec.StringRange;
import eu.uk.ncl.pet5o.esper.metrics.instrumentation.InstrumentationHelper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.Map;
import java.util.SortedMap;
import java.util.concurrent.locks.ReadWriteLock;

/**
 * Index for filter parameter constants for the range operators (range open/closed/half).
 * The implementation is based on the SortedMap implementation of TreeMap and stores only expression
 * parameter values of type DoubleRange.
 */
public final class FilterParamIndexStringRange extends FilterParamIndexStringRangeBase {
    public FilterParamIndexStringRange(ExprFilterSpecLookupable lookupable, ReadWriteLock readWriteLock, FilterOperator filterOperator) {
        super(lookupable, readWriteLock, filterOperator);

        if (!(filterOperator.isRangeOperator())) {
            throw new IllegalArgumentException("Invalid filter operator " + filterOperator);
        }
    }

    public final void matchEvent(eu.uk.ncl.pet5o.esper.client.EventBean theEvent, Collection<FilterHandle> matches) {
        Object objAttributeValue = lookupable.getGetter().get(theEvent);
        if (InstrumentationHelper.ENABLED) {
            InstrumentationHelper.get().qFilterReverseIndex(this, objAttributeValue);
        }

        if (objAttributeValue == null) {
            if (InstrumentationHelper.ENABLED) {
                InstrumentationHelper.get().aFilterReverseIndex(false);
            }
            return;
        }

        String attributeValue = (String) objAttributeValue;

        StringRange rangeStart = new StringRange(null, attributeValue);
        StringRange rangeEnd = new StringRange(attributeValue, null);
        SortedMap<StringRange, EventEvaluator> subMap = ranges.subMap(rangeStart, true, rangeEnd, true);

        // For not including either endpoint
        // A bit awkward to duplicate the loop code, however better than checking the boolean many times over
        // This may be a bit of an early performance optimization - the optimizer after all may do this better
        if (this.getFilterOperator() == FilterOperator.RANGE_OPEN) {
            // include neither endpoint
            for (Map.Entry<StringRange, EventEvaluator> entry : subMap.entrySet()) {
                if (entry.getKey().getMin().compareTo(attributeValue) < 0 && entry.getKey().getMax().compareTo(attributeValue) > 0) {
                    entry.getValue().matchEvent(theEvent, matches);
                }
            }
        } else if (this.getFilterOperator() == FilterOperator.RANGE_CLOSED) {
            // include all endpoints
            for (Map.Entry<StringRange, EventEvaluator> entry : subMap.entrySet()) {
                if (entry.getKey().getMin().compareTo(attributeValue) <= 0 && entry.getKey().getMax().compareTo(attributeValue) >= 0) {
                    entry.getValue().matchEvent(theEvent, matches);
                }
            }
        } else if (this.getFilterOperator() == FilterOperator.RANGE_HALF_CLOSED) {
            // include high endpoint not low endpoint
            for (Map.Entry<StringRange, EventEvaluator> entry : subMap.entrySet()) {
                if (entry.getKey().getMin().compareTo(attributeValue) < 0 && entry.getKey().getMax().compareTo(attributeValue) >= 0) {
                    entry.getValue().matchEvent(theEvent, matches);
                }
            }
        } else if (this.getFilterOperator() == FilterOperator.RANGE_HALF_OPEN) {
            // include low endpoint not high endpoint
            for (Map.Entry<StringRange, EventEvaluator> entry : subMap.entrySet()) {
                if (entry.getKey().getMin().compareTo(attributeValue) <= 0 && entry.getKey().getMax().compareTo(attributeValue) > 0) {
                    entry.getValue().matchEvent(theEvent, matches);
                }
            }
        } else {
            throw new IllegalStateException("Invalid filter operator " + this.getFilterOperator());
        }
        if (InstrumentationHelper.ENABLED) {
            InstrumentationHelper.get().aFilterReverseIndex(null);
        }
    }

    private static final Logger log = LoggerFactory.getLogger(FilterParamIndexStringRange.class);
}
