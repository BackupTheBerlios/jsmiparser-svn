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

package org.jsmiparser.antlr.asn1;

/**
 *
 * @author  Nigel Sheridan-Smith
 */
public class ASNDefinedType extends ASNType {
    
    private String moduleReference;
    private String typeReference;
    private ASNConstraint constraint;
    
    /** Creates a new instance of ASNDefinedType */
    public ASNDefinedType(Context context) {
        super(context, Enum.DEFINED);
    }
    
    public void setTypeReference (String n)
    {
        typeReference = n;
    }
    
    public String getTypeReference ()
    {
        return typeReference;
    }
    
    public void setModuleReference (String m)
    {
        moduleReference = m;
    }
    
    public String getModuleReference ()
    {
        return moduleReference;
    }
    
    public void setConstraint (ASNConstraint c)
    {
        constraint = c;
    }
    
    public ASNConstraint getConstraint ()
    {
        return constraint;
    }
}
