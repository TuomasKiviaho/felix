/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.cauldron.sigil.repository;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Collection;

import org.cauldron.sigil.model.eclipse.ILibrary;
import org.cauldron.sigil.model.eclipse.ISigilBundle;
import org.cauldron.sigil.model.osgi.IPackageImport;
import org.cauldron.sigil.model.osgi.IRequiredBundle;

public interface IBundleRepository {
	static final int NORMAL_PRIORITY = 0;
	static final int MAXIMUM_PRIORITY = -500;
	static final int MINIMUM_PRIORITY = 500;
	
	String getId();
	
	void addBundleRepositoryListener(IBundleRepositoryListener listener);
	
	void removeBundleRepositoryListener(IBundleRepositoryListener listener);
	
	void accept(IRepositoryVisitor visitor);
	
	void accept(IRepositoryVisitor visitor, int options);
	
	void writeOBR(OutputStream out) throws IOException;
	
	void refresh();
	
	ISigilBundle findProvider(IPackageImport packageImport, int options);
	
	ISigilBundle findProvider(IRequiredBundle bundle, int options);
	
	Collection<ISigilBundle> findAllProviders(IRequiredBundle bundle, int options);
	
	Collection<ISigilBundle> findAllProviders(IPackageImport packageImport, int options);
	
	Collection<ISigilBundle> findProviders(ILibrary library, int options);
}
