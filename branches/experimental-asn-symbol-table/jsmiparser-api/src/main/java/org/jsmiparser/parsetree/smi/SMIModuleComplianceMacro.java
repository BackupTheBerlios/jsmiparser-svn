/*
 * Copyright 2005 Nigel Sheridan-Smith, Davy Verstappen.
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

package org.jsmiparser.parsetree.smi;

import java.util.*;

import org.jsmiparser.parsetree.asn1.Context;

/**
 *
 * @author  nigelss
 */
public class SMIModuleComplianceMacro extends SMIStdMacro {

    private List<SMIModuleCompliance> modules;
    
    /** Creates a new instance of SMIModuleComplianceMacro */
    public SMIModuleComplianceMacro(Context context) {
        super(context, Enum.SMIMODULECOMPLIANCEMACRO);
        
        /* Do collection */
        modules = new ArrayList<SMIModuleCompliance> ();
    }
    
    public void setModules (List<SMIModuleCompliance> m)
    {
        modules = m;
    }
    
    public List<SMIModuleCompliance> getModules ()
    {
        return modules;
    }
    
}
