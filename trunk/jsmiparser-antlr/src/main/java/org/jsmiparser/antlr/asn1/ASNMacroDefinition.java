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

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author  Nigel Sheridan-Smith
 */
public class ASNMacroDefinition extends ASNAssignment {
    
    private List<String> tokens_ = new ArrayList<String>();
    
    /** Creates a new instance of ASNMacroDefinition 
     * @throws TokenStreamException */
    public ASNMacroDefinition(Context context, String name) {
    	super(context, name);
    	
        setType (Type.MACRODEF);
    }
    
	/**
	 * @return Returns the tokens.
	 */
	public List<String> getTokens() {
		return tokens_;
	}
}