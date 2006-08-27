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

import org.jsmiparser.util.token.IdToken;

import java.util.*;

public class SmiRow extends SmiObjectType implements SmiClass {

	private List<SmiRow> m_parentRows = new ArrayList<SmiRow>();
	private List<SmiRow> m_childRows = new ArrayList<SmiRow>();
	private List<SmiColumn> m_columns = new ArrayList<SmiColumn>();
	private SmiRow m_augments;
	private List<SmiIndex> m_indexes = new ArrayList<SmiIndex>();

	public SmiRow(IdToken idToken, SmiModule module) {
		super(idToken, module);
	}
	
	public SmiTable getTable()
	{
		return (SmiTable) getParent();
	}

    public List<SmiColumn> getColumns()
    {
        return m_columns;
    }
	
	public SmiRow getAugments()
	{
		return m_augments;
	}
	
	public void setAugments(SmiRow augments) {
		m_augments = augments;
		m_augments.m_childRows.add(this);
	}

	public List<SmiIndex> getIndexes()
	{
		return m_indexes;
	}

	public List<? extends SmiClass> getParentClasses() {
		return m_parentRows;
	}

	public List<? extends SmiClass> getChildClasses() {
		return m_childRows;
	}

    public SmiAttribute findAttribute(String id) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

	public List<SmiColumn> getAttributes() {
		return m_columns;
	}

	public Set<SmiColumn> getAllAttributes() {
		return getAllColumns();
	}

    public boolean isRowClass() {
        return true;
    }

    public boolean isScalarsClass() {
        return false;
    }

    private Set<SmiColumn> getAllColumns() {
		Set<SmiColumn> result = new LinkedHashSet<SmiColumn>();
		addAllColumns(result);
		return result;
	}

	private void addAllColumns(Set<SmiColumn> allColumns) {
		for (SmiRow parent : m_parentRows) {
			parent.addAllColumns(allColumns);
		}
		allColumns.addAll(m_columns);
	}

	public List<SmiRow> getChildRows() {
		return m_childRows;
	}

	public List<SmiRow> getParentRows() {
		return m_parentRows;
	}

	public SmiColumn findColumn(String id) {
		for (SmiColumn c : m_columns) {
			if (c.getId().equals(id)) {
				return c;
			}
		}
		return null;
	}

	public SmiIndex addIndex(SmiColumn col, boolean isImplied) {
		SmiIndex index = new SmiIndex(this, col, isImplied);
		m_indexes.add(index);
		return index;
	}

	public boolean hasSameIndexes(SmiRow other) {
		boolean result = false;
		if (m_indexes.size() == other.m_indexes.size()) {
			boolean tmpResult = true;
			Iterator<SmiIndex> i = m_indexes.iterator();
			Iterator<SmiIndex> j = other.getIndexes().iterator();
			while (tmpResult && i.hasNext() && j.hasNext()) {
				SmiIndex i1 = i.next();
				SmiIndex i2 = j.next();
				if (i1.getColumn() != i2.getColumn()) {
					tmpResult = false;
				}
				if (i1.isImplied() != i2.isImplied()) {
					System.out.printf("implied index is not the same for %s and %s", getId(), other.getId())
						.println();
					tmpResult = false;
				}
			}
			result = tmpResult;
		}
		//System.out.printf("IndexCheck for %s and %s : %b%n", getId(), other.getId(), result);
		return result;
	}

	public void addParentRow(SmiRow row) {
		m_parentRows.add(row);
		row.m_childRows.add(this);
	}
}
