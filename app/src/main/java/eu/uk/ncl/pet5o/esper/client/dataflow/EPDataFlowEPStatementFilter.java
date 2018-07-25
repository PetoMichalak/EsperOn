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
package eu.uk.ncl.pet5o.esper.client.dataflow;

import eu.uk.ncl.pet5o.esper.client.EPStatement;

/**
 * Filter for use with {@link eu.uk.ncl.pet5o.esper.dataflow.ops.EPStatementSource} operator.
 */
public interface EPDataFlowEPStatementFilter {
    /**
     * Pass or skip the statement.
     *
     * @param statement to test
     * @return indicator whether to include (true) or exclude (false) the statement.
     */
    public boolean pass(EPStatement statement);
}
