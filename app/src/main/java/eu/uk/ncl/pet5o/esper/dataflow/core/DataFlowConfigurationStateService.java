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

import eu.uk.ncl.pet5o.esper.client.dataflow.EPDataFlowSavedConfiguration;

public interface DataFlowConfigurationStateService {

    public boolean exists(String savedConfigName);

    public void add(EPDataFlowSavedConfiguration epDataFlowSavedConfiguration);

    public String[] getSavedConfigNames();

    public EPDataFlowSavedConfiguration getSavedConfig(String savedConfigName);

    public Object removePrototype(String savedConfigName);
}
