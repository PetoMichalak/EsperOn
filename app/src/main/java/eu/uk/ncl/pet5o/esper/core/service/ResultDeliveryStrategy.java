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
package eu.uk.ncl.pet5o.esper.core.service;

import eu.uk.ncl.pet5o.esper.collection.UniformPair;

/**
 * Strategy for use with {@link StatementResultService} to dispatch to a statement's subscriber
 * via method invocations.
 */
public interface ResultDeliveryStrategy {
    /**
     * Execute the dispatch.
     *
     * @param result is the insert and remove stream to indicate
     */
    public void execute(UniformPair<eu.uk.ncl.pet5o.esper.client.EventBean[]> result);
}
