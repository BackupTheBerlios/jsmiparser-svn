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
package org.jsmiparser.phase.file;

import org.apache.log4j.Logger;
import org.jsmiparser.parsetree.asn1.ASNModule;
import org.jsmiparser.parsetree.asn1.Context;
import org.jsmiparser.util.token.IdToken;

import java.util.Map;
import java.util.HashMap;

public class ASNMibParserImpl {
    private static final Logger m_log = Logger.getLogger(ASNMibParserImpl.class);

    private Map<String,ASNModule> m_moduleMap = new HashMap<String, ASNModule>();
    private Context m_context;

    public ASNMibParserImpl() {

    }

    public Context getContext() {
        return m_context;
    }

    public void setContext(Context context) {
        m_context = context;
    }

    public ASNModule use(IdToken idToken) {
        ASNModule result = m_moduleMap.get(idToken.getId());
        if (result == null) {
            // TODO dummy
            result = new ASNModule(m_context, idToken);
            m_moduleMap.put(idToken.getId(), result);
        }
        return result;
    }

    public ASNModule create(IdToken idToken) {
        ASNModule result = m_moduleMap.get(idToken.getId());
        if (result != null) {
            // TODO duplicate
        } else {
            result = new ASNModule(m_context, idToken);
            m_moduleMap.put(idToken.getId(), result);
        }
        return result;
    }

    public ASNModule find(String id) {
        return m_moduleMap.get(id);
    }
}
