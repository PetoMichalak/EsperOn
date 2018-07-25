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
package eu.uk.ncl.pet5o.esper.dataflow.core;

import eu.uk.ncl.pet5o.esper.client.dataflow.EPDataFlowSignal;
import eu.uk.ncl.pet5o.esper.dataflow.interfaces.EPDataFlowEmitter;
import eu.uk.ncl.pet5o.esper.dataflow.util.DataFlowSignalManager;

public class EPDataFlowEmitterNoTarget implements EPDataFlowEmitter {

    protected final int operatorNum;
    protected final DataFlowSignalManager dataFlowSignalManager;

    public EPDataFlowEmitterNoTarget(int operatorNum, DataFlowSignalManager dataFlowSignalManager) {
        this.operatorNum = operatorNum;
        this.dataFlowSignalManager = dataFlowSignalManager;
    }

    public void submit(Object object) {
    }

    public void submitSignal(EPDataFlowSignal signal) {
        dataFlowSignalManager.processSignal(operatorNum, signal);
    }

    public void submitPort(int portNumber, Object object) {
    }
}
