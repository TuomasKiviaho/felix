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

package org.cauldron.bld.core;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.cauldron.bld.core.internal.license.LicenseManager;
import org.cauldron.bld.core.internal.model.eclipse.DownloadJar;
import org.cauldron.bld.core.internal.model.eclipse.Library;
import org.cauldron.bld.core.internal.model.eclipse.LibraryImport;
import org.cauldron.bld.core.internal.model.eclipse.SigilBundle;
import org.cauldron.bld.core.internal.model.osgi.BundleModelElement;
import org.cauldron.bld.core.internal.model.osgi.PackageExport;
import org.cauldron.bld.core.internal.model.osgi.PackageImport;
import org.cauldron.bld.core.internal.model.osgi.RequiredBundle;
import org.cauldron.bld.core.licence.ILicenseManager;
import org.cauldron.sigil.model.ModelElementFactory;
import org.cauldron.sigil.model.eclipse.IDownloadJar;
import org.cauldron.sigil.model.eclipse.ILibrary;
import org.cauldron.sigil.model.eclipse.ILibraryImport;
import org.cauldron.sigil.model.eclipse.INewtonSystem;
import org.cauldron.sigil.model.eclipse.ISCAComposite;
import org.cauldron.sigil.model.eclipse.ISigilBundle;
import org.cauldron.sigil.model.osgi.IBundleModelElement;
import org.cauldron.sigil.model.osgi.IPackageExport;
import org.cauldron.sigil.model.osgi.IPackageImport;
import org.cauldron.sigil.model.osgi.IRequiredBundle;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

public class BldCore implements BundleActivator {
	private static LicenseManager licenceManager = new LicenseManager();
	
	private static final Logger log = Logger.getLogger(BldCore.class.getName());

	public static void error(String string, Throwable e) {
		// TODO 
		log.log( Level.WARNING, string, e );
	}

	public static void error(String string) {
		log.log( Level.WARNING, string );
	}

	public static ILicenseManager getLicenseManager() {
		return licenceManager;
	}

	public void start(BundleContext context) throws Exception {
		init();
	}
	
	public static void init() throws Exception {
		String uri = "http://sigil.codecauldron.org/xml/sigil-namespace";
		ModelElementFactory.getInstance().register(ISigilBundle.class,
				SigilBundle.class, "bundle", "sigil", uri);
		ModelElementFactory.getInstance().register(IDownloadJar.class,
				DownloadJar.class, "download", "sigil", uri);
		ModelElementFactory.getInstance().register(ILibrary.class,
				Library.class, "library", "sigil", uri);
		ModelElementFactory.getInstance().register(ILibraryImport.class,
				LibraryImport.class, "library-import", "sigil", uri);
		
		// osgi elements
		ModelElementFactory.getInstance().register(IBundleModelElement.class,
				BundleModelElement.class, "bundle", null, null);
		ModelElementFactory.getInstance().register(IPackageExport.class,
				PackageExport.class, "package.export", null, null);
		ModelElementFactory.getInstance().register(IPackageImport.class,
				PackageImport.class, "package.import", null, null);
		ModelElementFactory.getInstance().register(IRequiredBundle.class,
				RequiredBundle.class, "required.bundle", null, null);
	}

	public void stop(BundleContext context) throws Exception {
		// TODO Auto-generated method stub
		
	}

}
