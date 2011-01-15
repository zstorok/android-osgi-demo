/**
 * Copyright (c) 2011 Zsolt Török.
 *  
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0, 
 * available at http://www.eclipse.org/legal/epl-v10.html  
 */
package com.github.zstorok.android.osgi.demo.service.en;

import com.github.zstorok.android.osgi.demo.service.api.HelloService;

public class EnglishHelloService implements HelloService {

	@Override
	public String getMessage() {
		return "Hello world!";
	}
}
