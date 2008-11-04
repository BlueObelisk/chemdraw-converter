package org.xmlcml.cml.chemdraw.components;

public class CodeName {
	int code = -1;
	String name = "uninit";
	String cdxName = "uninit";

	/**
	 * @param code
	 * @param name
	 * @param cdxName
	 */
	public CodeName (int code, String name, String cdxName) {
		this.code = code;
		this.name = name;
		this.cdxName = cdxName;
	}

	/**
	 * @return string
	 */
	public String toString() {
		String s = "CodeName ";
		s += code;
		s += "||"+name;
		s += "||"+cdxName;
		return s;
	}
};
