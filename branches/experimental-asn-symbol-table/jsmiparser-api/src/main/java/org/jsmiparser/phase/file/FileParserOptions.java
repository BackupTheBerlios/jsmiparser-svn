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

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class FileParserOptions {

    private List<File> m_usedDirList = new ArrayList<File>();
    private List<File> m_inputDirList = new ArrayList<File>();

    private List<File> m_usedFileList = new ArrayList<File>();
    private List<File> m_inputFileList = new ArrayList<File>();

    private List<String> m_extensions = new ArrayList<String>();

    public FileParserOptions() {
        m_extensions.add(".mib");
        m_extensions.add(".smi");
        m_extensions.add(".asn");
    }

    public List<File> getUsedDirList() {
        return m_usedDirList;
    }

    public void setUsedDirList(List<File> usedDirList) {
        m_usedDirList = usedDirList;
    }

    public List<File> getInputDirList() {
        return m_inputDirList;
    }

    public void setInputDirList(List<File> inputDirList) {
        m_inputDirList = inputDirList;
    }

    public List<File> getUsedFileList() {
        return m_usedFileList;
    }

    public void setUsedFileList(List<File> usedFileList) {
        m_usedFileList = usedFileList;
    }

    public List<File> getInputFileList() {
        return m_inputFileList;
    }

    public void setInputFileList(List<File> inputFileList) {
        m_inputFileList = inputFileList;
    }

    public void addFile(File file) {
        m_inputFileList.add(file);
    }

    public List<String> getExtensions() {
        return m_extensions;
    }

    public void setExtensions(List<String> extensions) {
        m_extensions = extensions;
    }

    public File findFile(String moduleName) {
		for (File dir : m_usedDirList) {
			File file = new File(dir, moduleName);
			if (file.exists()) {
				return file;
			}
		}

		for (File dir : m_usedDirList) {
			for (String ext : m_extensions) {
				File file = new File(dir, moduleName + ext);
				if (file.exists()) {
					return file;
				}
			}
		}
		return null;
	}
}
