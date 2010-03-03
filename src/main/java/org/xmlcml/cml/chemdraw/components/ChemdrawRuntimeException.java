package org.xmlcml.cml.chemdraw.components;

public class ChemdrawRuntimeException extends RuntimeException {

	private int iProp;
	private int byteCount;

	public int getByteCount() {
		return byteCount;
	}

	public ChemdrawRuntimeException() {
		super();
	}

	public ChemdrawRuntimeException(String msg, Throwable throwable) {
		super(msg, throwable);
	}

	public ChemdrawRuntimeException(String msg) {
		super(msg);
	}

	public ChemdrawRuntimeException(Throwable throwable) {
		super(throwable);
	}

	public void setProperty(int iProp) {
		this.iProp = iProp;
	}

	public int getProperty() {
		return this.iProp;
	}

	public void setByteCount(int byteCount) {
		this.byteCount = byteCount;
	}

	public String toString() {
		return this.getMessage()+"; Prop "+iProp+" byteCount: "+byteCount;
	}
}
