/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.zookeeper.server;

import java.io.IOException;

import junit.framework.Assert;

import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.ZooDefs.Ids;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.test.ClientBase;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NIOServerCnxnTest extends ClientBase {
    private static final Logger LOG = LoggerFactory
            .getLogger(NIOServerCnxnTest.class);
    /**
     * Test operations on ServerCnxn after socket closure.
     */
    @Test(timeout = 60000)
    public void testOperationsAfterCnxnClose() throws IOException,
            InterruptedException, KeeperException {
        final ZooKeeper zk = createClient();

        final String path = "/a";
        try {
            // make sure zkclient works
            zk.create(path, "test".getBytes(), Ids.OPEN_ACL_UNSAFE,
                    CreateMode.PERSISTENT);
            Assert.assertNotNull("Didn't create znode:" + path,
                    zk.exists(path, false));
            // Defaults ServerCnxnFactory would be instantiated with
            // NIOServerCnxnFactory
            Assert.assertTrue(
                    "Didn't instantiate ServerCnxnFactory with NIOServerCnxnFactory!",
                    serverFactory instanceof NIOServerCnxnFactory);
            Iterable<ServerCnxn> connections = serverFactory.getConnections();
            for (ServerCnxn serverCnxn : connections) {
                serverCnxn.close();
                try {
                    serverCnxn.toString();
                } catch (Exception e) {
                    LOG.error("Exception while getting connection details!", e);
                    Assert.fail("Shouldn't throw exception while "
                            + "getting connection details!");
                }
            }
        } finally {
            zk.close();
        }
    }
}
