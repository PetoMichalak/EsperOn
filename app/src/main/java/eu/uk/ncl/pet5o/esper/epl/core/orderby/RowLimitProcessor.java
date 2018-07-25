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
package eu.uk.ncl.pet5o.esper.epl.core.orderby;

import com.espertech.esper.epl.variable.VariableReader;

/**
 * An limit-processor for use with "limit" and "offset".
 */
public class RowLimitProcessor {

    private final VariableReader numRowsVariableReader;
    private final VariableReader offsetVariableReader;
    private int currentRowLimit;
    private int currentOffset;

    public RowLimitProcessor(VariableReader numRowsVariableReader, VariableReader offsetVariableReader, int currentRowLimit, int currentOffset) {
        this.numRowsVariableReader = numRowsVariableReader;
        this.offsetVariableReader = offsetVariableReader;
        this.currentRowLimit = currentRowLimit;
        this.currentOffset = currentOffset;
    }

    public int getCurrentRowLimit() {
        return currentRowLimit;
    }

    public int getCurrentOffset() {
        return currentOffset;
    }

    /**
     * Determine the current limit and applies the limiting function to outgoing events.
     *
     * @param outgoingEvents unlimited
     * @return limited
     */
    public com.espertech.esper.client.EventBean[] determineLimitAndApply(com.espertech.esper.client.EventBean[] outgoingEvents) {
        if (outgoingEvents == null) {
            return null;
        }
        determineCurrentLimit();
        return applyLimit(outgoingEvents);
    }

    public void determineCurrentLimit() {
        if (numRowsVariableReader != null) {
            Number varValue = (Number) numRowsVariableReader.getValue();
            if (varValue != null) {
                currentRowLimit = varValue.intValue();
            } else {
                currentRowLimit = Integer.MAX_VALUE;
            }
            if (currentRowLimit < 0) {
                currentRowLimit = Integer.MAX_VALUE;
            }
        }

        if (offsetVariableReader != null) {
            Number varValue = (Number) offsetVariableReader.getValue();
            if (varValue != null) {
                currentOffset = varValue.intValue();
            } else {
                currentOffset = 0;
            }
            if (currentOffset < 0) {
                currentOffset = 0;
            }
        }
    }

    public com.espertech.esper.client.EventBean[] applyLimit(com.espertech.esper.client.EventBean[] outgoingEvents) {

        // no offset
        if (currentOffset == 0) {
            if (outgoingEvents.length <= currentRowLimit) {
                return outgoingEvents;
            }

            if (currentRowLimit == 0) {
                return null;
            }

            com.espertech.esper.client.EventBean[] limited = new com.espertech.esper.client.EventBean[currentRowLimit];
            System.arraycopy(outgoingEvents, 0, limited, 0, currentRowLimit);
            return limited;
        } else {
            // with offset
            int maxInterested = currentRowLimit + currentOffset;
            if (currentRowLimit == Integer.MAX_VALUE) {
                maxInterested = Integer.MAX_VALUE;
            }

            // more rows then requested
            if (outgoingEvents.length > maxInterested) {
                com.espertech.esper.client.EventBean[] limited = new com.espertech.esper.client.EventBean[currentRowLimit];
                System.arraycopy(outgoingEvents, currentOffset, limited, 0, currentRowLimit);
                return limited;
            }

            // less or equal rows to offset
            if (outgoingEvents.length <= currentOffset) {
                return null;
            }

            int size = outgoingEvents.length - currentOffset;
            com.espertech.esper.client.EventBean[] limited = new com.espertech.esper.client.EventBean[size];
            System.arraycopy(outgoingEvents, currentOffset, limited, 0, size);
            return limited;
        }
    }

    public com.espertech.esper.client.EventBean[] determineApplyLimit2Events(com.espertech.esper.client.EventBean first, com.espertech.esper.client.EventBean second) {
        determineCurrentLimit();
        if (getCurrentRowLimit() == 0) {
            return null;
        }
        if (getCurrentRowLimit() == 1) {
            return new com.espertech.esper.client.EventBean[] {first};
        }
        return new com.espertech.esper.client.EventBean[] {first, second};
    }
}
