/**
 * Copyright (C) 2001 Peter Murray-Rust (pm286@cam.ac.uk)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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
