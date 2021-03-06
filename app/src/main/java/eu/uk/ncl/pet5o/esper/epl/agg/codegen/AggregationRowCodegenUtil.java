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
package eu.uk.ncl.pet5o.esper.epl.agg.codegen;

import static eu.uk.ncl.pet5o.esper.epl.agg.codegen.AggregationServiceCodegenNames.CLASSNAME_AGGREGATIONROW_LVL;

public class AggregationRowCodegenUtil {
    public static String classnameForLevel(int level) {
        return CLASSNAME_AGGREGATIONROW_LVL + "_" + level;
    }
}
