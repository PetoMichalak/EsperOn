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
package eu.uk.ncl.pet5o.esper.dataflow.ops;

import eu.uk.ncl.pet5o.esper.client.dataflow.EPDataFlowSignal;
import eu.uk.ncl.pet5o.esper.dataflow.annotations.DataFlowContext;
import eu.uk.ncl.pet5o.esper.dataflow.annotations.DataFlowOpParameter;
import eu.uk.ncl.pet5o.esper.dataflow.annotations.DataFlowOpProvideSignal;
import eu.uk.ncl.pet5o.esper.dataflow.annotations.DataFlowOperator;
import eu.uk.ncl.pet5o.esper.dataflow.interfaces.EPDataFlowEmitter;

@DataFlowOperator
@DataFlowOpProvideSignal
public class Emitter implements EPDataFlowEmitter {

    @DataFlowOpParameter
    private String name;

    @DataFlowContext
    private EPDataFlowEmitter dataFlowEmitter;

    public void submit(Object object) {
        dataFlowEmitter.submit(object);
    }

    public void submitSignal(EPDataFlowSignal signal) {
        dataFlowEmitter.submitSignal(signal);
    }

    public void submitPort(int portNumber, Object object) {
        dataFlowEmitter.submitPort(portNumber, object);
    }

    public String getName() {
        return name;
    }
}
