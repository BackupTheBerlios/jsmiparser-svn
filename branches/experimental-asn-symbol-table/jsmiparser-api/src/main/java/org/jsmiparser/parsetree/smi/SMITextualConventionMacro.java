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

import org.jsmiparser.parsetree.asn1.ASNType;
import org.jsmiparser.parsetree.asn1.Context;

/**
 *
 * @author  Nigel Sheridan-Smith
 */
public class SMITextualConventionMacro extends SMIStdMacro {
    
    private String displayHint;
    private ASNType syntax;
    private List<SMINamedBit> syntaxNamedBits = new ArrayList<SMINamedBit>();
    
    /** Creates a new instance of SMITextualConventionMacro */
    public SMITextualConventionMacro(Context context) {
        super(context, Enum.SMITEXTUALCONVENTIONMACRO);
    }
    
    public void setDisplayHint (String d)
    {
        displayHint = d;
    }
    
    public String getDisplayHint ()
    {
        return displayHint;
    }
    
    public void setSyntax (ASNType s)
    {
        syntax = s;
    }
    
    public ASNType getSyntax ()
    {
        return syntax;
    }
    
    public void setSyntaxNamedBits (List<SMINamedBit> n)
    {
        syntaxNamedBits = n;
    }
    
    public List<SMINamedBit> getSyntaxNamedBits ()
    {
        return syntaxNamedBits;
    }
    
}
