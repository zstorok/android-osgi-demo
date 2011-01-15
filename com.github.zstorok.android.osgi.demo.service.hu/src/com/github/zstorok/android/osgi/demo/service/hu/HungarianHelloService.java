/**
 * Copyright (c) 2011 Zsolt Török.
 *  
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0 
 * which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html  
 */
package com.github.zstorok.android.osgi.demo.service.hu;

import com.github.zstorok.android.osgi.demo.service.api.HelloService;

public class HungarianHelloService implements HelloService {

	@Override
	public String getMessage() {
		return "Helló világ!";
	}
}
