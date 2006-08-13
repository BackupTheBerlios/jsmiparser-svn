/*
 * Copyright 2006 Davy Verstappen.
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

public enum AccessV2 {
    READ_ONLY,
    READ_WRITE,
    WRITE_ONLY,
    NOT_ACCESSIBLE,
    ACCESSIBLE_FOR_NOTIFY,
    READ_CREATE,
    NOT_IMPLEMENTED;

    private String m_keyword;

    private AccessV2() {
        m_keyword = name().toLowerCase().replace('_', '-');
    }

    public String toString() {
        return m_keyword;
    }

    public AccessV2 find(String keyword) {
        return Util.find(AccessV2.class, keyword);
    }
}
