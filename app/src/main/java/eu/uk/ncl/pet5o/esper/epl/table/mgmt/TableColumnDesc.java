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
package eu.uk.ncl.pet5o.esper.epl.table.mgmt;

public abstract class TableColumnDesc {
    private final int positionInDeclaration;
    private final String columnName;

    protected TableColumnDesc(int positionInDeclaration, String columnName) {
        this.positionInDeclaration = positionInDeclaration;
        this.columnName = columnName;
    }

    public String getColumnName() {
        return columnName;
    }

    public int getPositionInDeclaration() {
        return positionInDeclaration;
    }
}
