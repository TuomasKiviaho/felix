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
package org.apache.felix.deploymentadmin.itest;

import static org.osgi.service.deploymentadmin.DeploymentException.CODE_COMMIT_ERROR;
import static org.osgi.service.deploymentadmin.DeploymentException.CODE_OTHER_ERROR;
import static org.osgi.service.deploymentadmin.DeploymentException.CODE_PROCESSOR_NOT_FOUND;

import org.apache.felix.deploymentadmin.itest.util.DeploymentPackageBuilder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.ops4j.pax.exam.junit.JUnit4TestRunner;
import org.osgi.framework.Bundle;
import org.osgi.service.deploymentadmin.DeploymentException;
import org.osgi.service.deploymentadmin.DeploymentPackage;

/**
 * Provides test cases regarding the use of "normal" deployment packages in DeploymentAdmin.
 */
@RunWith(JUnit4TestRunner.class)
public class UninstallDeploymentPackageTest extends BaseIntegrationTest {

    /**
     * Tests that if a resource processor is missing (uninstalled) during the forced uninstallation of a deployment package this will ignored and the uninstall completes.
     */
    @Test
    public void testForcedUninstallDeploymentPackageWithMissingResourceProcessorSucceeds() throws Exception {
        DeploymentPackageBuilder dpBuilder = createNewDeploymentPackageBuilder("1.0.0");
        dpBuilder
            .add(dpBuilder.createBundleResource().setUrl(getTestBundle("bundle1")))
            .add(dpBuilder.createResourceProcessorResource().setUrl(getTestBundle("rp1")))
            .add(dpBuilder.createResource().setResourceProcessorPID(TEST_FAILING_BUNDLE_RP1).setUrl(getTestResource("test-config1.xml")));

        DeploymentPackage dp = m_deploymentAdmin.installDeploymentPackage(dpBuilder.generate());
        assertNotNull("No deployment package returned?!", dp);

        awaitRefreshPackagesEvent();

        assertTrue("Two bundles should be started!", getCurrentBundles().size() == 2);

        Bundle rpBundle = dp.getBundle(getSymbolicName("rp1"));
        rpBundle.uninstall();

        assertTrue("One bundle should be started!", getCurrentBundles().size() == 1);

        assertEquals("Expected no deployment package?!", 1, m_deploymentAdmin.listDeploymentPackages().length);

        assertTrue(dp.uninstallForced());
        
        assertTrue("No bundle should be started!", getCurrentBundles().isEmpty());

        assertEquals("Expected no deployment package?!", 0, m_deploymentAdmin.listDeploymentPackages().length);
    }

    /**
     * Tests that if an exception is thrown during the commit-phase, the installation is continued normally.
     */
    @Test
    public void testUninstallDeploymentPackageWithExceptionThrowingInCommitCausesNoRollbackOk() throws Exception {
        DeploymentPackageBuilder dpBuilder = createNewDeploymentPackageBuilder("1.0.0");
        dpBuilder
            .add(dpBuilder.createBundleResource().setUrl(getTestBundle("bundle1")))
            .add(dpBuilder.createResourceProcessorResource().setUrl(getTestBundle("rp1")))
            .add(dpBuilder.createResource().setResourceProcessorPID(TEST_FAILING_BUNDLE_RP1).setUrl(getTestResource("test-config1.xml")));

        DeploymentPackage dp = m_deploymentAdmin.installDeploymentPackage(dpBuilder.generate());
        assertNotNull("No deployment package returned?!", dp);

        awaitRefreshPackagesEvent();
        
        assertTrue("Two bundles should be started!", getCurrentBundles().size() == 2);

        assertEquals("Expected no deployment package?!", 1, m_deploymentAdmin.listDeploymentPackages().length);
        
        System.setProperty("rp1", "commit");

        dp.uninstall();

        assertTrue("No bundles should be started! " + getCurrentBundles(), getCurrentBundles().isEmpty());

        assertEquals("Expected no deployment package?!", 0, m_deploymentAdmin.listDeploymentPackages().length);
    }

    /**
     * Tests that if an exception is thrown during the dropping of a resource, the installation is rolled back.
     */
    @Test
    public void testUninstallDeploymentPackageWithExceptionThrowingInDropAllResourcesCausesRollbackOk() throws Exception {
        DeploymentPackageBuilder dpBuilder = createNewDeploymentPackageBuilder("1.0.0");
        dpBuilder
            .add(dpBuilder.createBundleResource().setUrl(getTestBundle("bundle1")))
            .add(dpBuilder.createResourceProcessorResource().setUrl(getTestBundle("rp1")))
            .add(dpBuilder.createResource().setResourceProcessorPID(TEST_FAILING_BUNDLE_RP1).setUrl(getTestResource("test-config1.xml")));

        DeploymentPackage dp = m_deploymentAdmin.installDeploymentPackage(dpBuilder.generate());
        assertNotNull("No deployment package returned?!", dp);

        awaitRefreshPackagesEvent();
        
        assertTrue("Two bundles should be started!", getCurrentBundles().size() == 2);

        assertEquals("Expected no deployment package?!", 1, m_deploymentAdmin.listDeploymentPackages().length);
        
        System.setProperty("rp1", "dropAllResources");

        try {
            dp.uninstall();
            fail("Expected uninstall to fail and rollback!");
        }
        catch (DeploymentException exception) {
            // Ok; expected
            assertDeploymentException(CODE_OTHER_ERROR, exception);
        }
        
        assertTrue("Two bundles should be started!", getCurrentBundles().size() == 2);

        assertEquals("Expected no deployment package?!", 1, m_deploymentAdmin.listDeploymentPackages().length);
    }

    /**
     * Tests that if an exception is thrown during the prepare-phase, the installation is rolled back.
     */
    @Test
    public void testUninstallDeploymentPackageWithExceptionThrowingInPrepareCausesRollbackOk() throws Exception {
        DeploymentPackageBuilder dpBuilder = createNewDeploymentPackageBuilder("1.0.0");
        dpBuilder
            .add(dpBuilder.createBundleResource().setUrl(getTestBundle("bundle1")))
            .add(dpBuilder.createResourceProcessorResource().setUrl(getTestBundle("rp1")))
            .add(dpBuilder.createResource().setResourceProcessorPID(TEST_FAILING_BUNDLE_RP1).setUrl(getTestResource("test-config1.xml")));

        DeploymentPackage dp = m_deploymentAdmin.installDeploymentPackage(dpBuilder.generate());
        assertNotNull("No deployment package returned?!", dp);

        awaitRefreshPackagesEvent();
        
        assertTrue("Two bundles should be started!", getCurrentBundles().size() == 2);

        assertEquals("Expected no deployment package?!", 1, m_deploymentAdmin.listDeploymentPackages().length);
        
        System.setProperty("rp1", "prepare");

        try {
            dp.uninstall();
            fail("Expected uninstall to fail and rollback!");
        }
        catch (DeploymentException exception) {
            // Ok; expected
            assertDeploymentException(CODE_COMMIT_ERROR, exception);
        }
        
        assertTrue("Two bundles should be started!", getCurrentBundles().size() == 2);

        assertEquals("Expected no deployment package?!", 1, m_deploymentAdmin.listDeploymentPackages().length);
    }

    /**
     * Tests that if an exception is thrown during the uninstall of a bundle, the installation/update continues and succeeds.
     */
    @Test
    public void testUninstallDeploymentPackageWithExceptionThrownInStopCauseNoRollbackOk() throws Exception {
        DeploymentPackageBuilder dpBuilder = createNewDeploymentPackageBuilder("1.0.0");
        dpBuilder
            .add(dpBuilder.createBundleResource().setUrl(getTestBundle("bundle1")))
            .add(dpBuilder.createBundleResource().setUrl(getTestBundle("bundle3")));

        DeploymentPackage dp = m_deploymentAdmin.installDeploymentPackage(dpBuilder.generate());
        assertNotNull("No deployment package returned?!", dp);

        awaitRefreshPackagesEvent();

        assertBundleExists(getSymbolicName("bundle3"), "1.0.0");

        System.setProperty("bundle3", "stop");
        
        dp.uninstall();

        awaitRefreshPackagesEvent();

        assertEquals("Expected no deployment package?!", 0, m_deploymentAdmin.listDeploymentPackages().length);
        
        assertTrue("Expected no bundles to remain?!", getCurrentBundles().isEmpty());
    }

    /**
     * Tests that if a resource processor is missing (uninstalled) during the uninstallation of a deployment package, this is regarded an error and a rollback is performed.
     */
    @Test
    public void testUninstallDeploymentPackageWithMissingResourceProcessorCausesRollback() throws Exception {
        DeploymentPackageBuilder dpBuilder = createNewDeploymentPackageBuilder("1.0.0");
        dpBuilder
            .add(dpBuilder.createBundleResource().setUrl(getTestBundle("bundle1")))
            .add(dpBuilder.createResourceProcessorResource().setUrl(getTestBundle("rp1")))
            .add(dpBuilder.createResource().setResourceProcessorPID(TEST_FAILING_BUNDLE_RP1).setUrl(getTestResource("test-config1.xml")));

        DeploymentPackage dp = m_deploymentAdmin.installDeploymentPackage(dpBuilder.generate());
        assertNotNull("No deployment package returned?!", dp);

        awaitRefreshPackagesEvent();

        assertTrue("Two bundles should be started!", getCurrentBundles().size() == 2);

        Bundle rpBundle = dp.getBundle(getSymbolicName("rp1"));
        rpBundle.uninstall();

        assertTrue("One bundle should be started!", getCurrentBundles().size() == 1);

        assertEquals("Expected no deployment package?!", 1, m_deploymentAdmin.listDeploymentPackages().length);

        try {
            dp.uninstall();
            fail("Expected uninstall to fail and rollback!");
        }
        catch (DeploymentException exception) {
            // Ok; expected
            assertDeploymentException(CODE_PROCESSOR_NOT_FOUND, exception);
        }
        
        assertTrue("One bundle should be started!", getCurrentBundles().size() == 1);

        assertEquals("Expected no deployment package?!", 1, m_deploymentAdmin.listDeploymentPackages().length);
    }
}
