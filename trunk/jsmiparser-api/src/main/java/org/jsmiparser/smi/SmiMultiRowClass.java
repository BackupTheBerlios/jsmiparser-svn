/*
 * Copyright 2005 Davy Verstappen.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jsmiparser.smi;


public class SmiMultiRowClass extends SmiRow {

    private boolean m_identicalIndexColumns = true;
    private boolean m_identicalIndexTypes = true;

    public SmiMultiRowClass(SmiModule module, String id) {
		super(id, module);
	}

    public boolean isIdenticalIndexColumns() {
        return m_identicalIndexColumns;
    }

    public void setIdenticalIndexColumns(boolean identicalIndexColumns) {
        m_identicalIndexColumns = identicalIndexColumns;
    }

    public boolean isIdenticalIndexTypes() {
        return m_identicalIndexTypes;
    }

    public void setIdenticalIndexTypes(boolean identicalIndexTypes) {
        m_identicalIndexTypes = identicalIndexTypes;
    }

    public void addParentRow(String id) {
		SmiRow row = getModule().getMib().findRow(id);
		assert(row != null); // TODO
		addParentRow(row);
	}

}