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
package eu.uk.ncl.pet5o.esper.epl.view;

import eu.uk.ncl.pet5o.esper.core.context.util.AgentInstanceContext;
import eu.uk.ncl.pet5o.esper.epl.core.resultset.core.ResultSetProcessor;

/**
 * Factory for output processing views.
 */
public class OutputProcessViewFactoryCallback implements OutputProcessViewFactory {
    private final OutputProcessViewCallback callback;

    public OutputProcessViewFactoryCallback(OutputProcessViewCallback callback) {
        this.callback = callback;
    }

    public OutputProcessViewBase makeView(ResultSetProcessor resultSetProcessor, AgentInstanceContext agentInstanceContext) {
        return new OutputProcessViewBaseCallback(resultSetProcessor, callback);
    }
}
