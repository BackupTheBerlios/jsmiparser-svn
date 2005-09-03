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
package org.jsmiparser.phase.oid;

import org.jsmiparser.util.token.BigIntegerToken;
import org.jsmiparser.util.token.IdToken;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

public class OidNode {

    OidProblemReporter m_pr;
    IdToken m_idToken;
    BigIntegerToken m_valueToken;
    OidNode m_parent;

    List<OidNode> m_children = new ArrayList<OidNode>();

    //Map<BigInteger, OidNode> m_valueChildMap = new HashMap<BigInteger, OidNode>();

    public OidNode(OidProblemReporter pr, OidNode parent, IdToken idToken, BigIntegerToken valueToken) {
        super();
        m_pr = pr;
        m_parent = parent;
        m_idToken = idToken;
        m_valueToken = valueToken;
    }

    public OidNode findChild(BigInteger value) {
        for (OidNode oidNode : m_children) {
            if (oidNode.getValue() != null && oidNode.getValue().equals(value)) {
                return oidNode;
            }
        }
        return null;
    }

    public OidNode findChild(String id) {
        for (OidNode oidNode : m_children) {
            if (oidNode.getId() != null && oidNode.getId().equals(id)) {
                return oidNode;
            }
        }
        return null;
    }

    public boolean hasChildren() {
        return m_children.size() > 0;
    }

    // TODO
    public boolean equals(OidNode on) {
        if (on == this) {
            return true;
        } else {
            OidNode on1 = this;
            OidNode on2 = on;
            while (on1 != null && on2 != null) {
//                if ((on1.getId() == null && on2.getId() == null)
//                        ||(!(on1.getId() != null && on2.getId() != null
//                                && on1.getId().equals(on2.getId()))))
//                {
//                    String msg = this.getId() + ".equals(" + on.getId()
//                    	+ ") not equal: " + on1.getId() + " " + on2.getId();
//                    System.out.println(msg);
//                    return false;
//                }
                if (!(on1.getValue() != null && on2.getValue() != null
                        && on1.getValue().equals(on2.getValue()))) {
                    return false;
                }
                on1 = on1.m_parent;
                on2 = on2.m_parent;
            }
            if (!(on1 == null && on2 == null))
                return false;
        }
        return true;
    }

    public void toString(StringBuilder buf) {
        if (m_parent != null) {
            m_parent.toString(buf);
            buf.append("/");
        }
        if (getId() != null)
            buf.append(getId());
        buf.append("#");
        if (getValue() != null)
            buf.append(getValue());
    }

    public OidNode getParent() {
        return m_parent;
    }

    public String getId() {
        return m_idToken == null ? null : m_idToken.getId();
    }

    public BigInteger getValue() {
        return m_valueToken == null ? null : m_valueToken.getValue();
    }

    public OidNode getRoot() {
        OidNode n = this;
        while (n.m_parent != null) {
            n = n.m_parent;
        }
        return n;
    }

    void fixStart(OidNode startNode) {
        OidNode n = this;
        while (n.m_parent.m_parent != null) {
            n = n.m_parent;
        }

        // n should now be the direct child of the root
        if (n.m_parent != getRoot()) {
            m_pr.reportMissingRootChild(getId());
        }

        n.m_parent = startNode;

    }

    public String toString() {
        StringBuilder buf = new StringBuilder();
        toString(buf);
        return buf.toString();
    }

    public String getDecimalDottedStr() {
        StringBuilder buf = new StringBuilder();
        getDecimalDottedStr(buf);
        return buf.toString();
    }

    private void getDecimalDottedStr(StringBuilder buf) {
        if (m_parent != null && m_parent.m_parent != null) {
            m_parent.getDecimalDottedStr(buf);
            buf.append(".");
        }
        buf.append(getValue());
    }


    public OidNode resolveChild(IdToken idToken, BigIntegerToken valueToken) {
        assert(idToken != null || valueToken != null);
        OidNode result = null;

        if (idToken != null) {
            result = findChild(idToken.getId());
            if (result != null && valueToken != null) {
                if (result.getValue() != null) {
                    if (!valueToken.equals(result.getValue())) {
                        m_pr.reportNumberMismatch(valueToken.getLocation(), valueToken.getValue(), result.getValue());
                    }
                } else {
                    result.m_valueToken = valueToken;
                }
            }
        }

        if (result == null && valueToken != null) {
            result = findChild(valueToken.getValue());
            if (result != null && idToken != null) {
                if (result.getId() != null) {
                    if (!idToken.equals(result.getId())) {
                        m_pr.reportIdMismatch(idToken.getLocation(), idToken.getId(), getId());
                    }
                } else {
                    result.m_idToken = idToken;
                }
            }
        }

        if (result == null) {
            result = new OidNode(m_pr, this, idToken, valueToken);
        }

        return result;
    }

    public void check() {
        // TODO check that id and value are filled in

        // TODO check that all children have unique ids and values
    }

    public IdToken getIdToken() {
        return m_idToken;
    }
}
