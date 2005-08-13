/*
 * Copyright 2005 Davy Verstappen.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jsmiparser.smi;

import org.apache.log4j.Logger;

import java.util.List;
import java.util.ArrayList;

// TODO idtokens
public class SmiImports {

    private SmiModule m_importingModule;
    private SmiModule m_importedModule;
    private List<SmiSymbol> m_symbols = new ArrayList<SmiSymbol>();

    public SmiImports(SmiModule importingModule, SmiModule importedModule, List<SmiSymbol> symbols) {
        m_importingModule = importingModule;
        m_importedModule = importedModule;
        m_symbols = symbols;
    }

    public SmiModule getImportingModule() {
        return m_importingModule;
    }

    public SmiModule getImportedModule() {
        return m_importedModule;
    }

    public List<SmiSymbol> getSymbols() {
        return m_symbols;
    }
}
