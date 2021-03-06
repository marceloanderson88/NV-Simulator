package simulator.network;

import junit.framework.TestCase;

import java.util.HashMap;

import simulator.network.components.physical.*;

public class SubstrateNetworkTest extends TestCase {

  private SubstrateNetwork substrateNetwork;
  private HashMap<Integer, PhysicalNode> physicalNodes;
  private HashMap<String, PhysicalLink> physicalLinks;

  public void setUp() {
    substrateNetwork = new SubstrateNetwork();
    physicalNodes = new HashMap<Integer, PhysicalNode>();
    physicalLinks = new HashMap<String, PhysicalLink>();
    for(int i = 1; i < 6; i++) {
      physicalNodes.put(i, new PhysicalNode(i, i * 100));
    }
    for(int i = 1; i < 5; i++) {
      String linkId = String.format("%s:%s", i, 5);
      PhysicalLink physicalLink = new PhysicalLink(linkId, physicalNodes.get(i),
                                                   physicalNodes.get(5), 5, 5, 5);
      physicalLinks.put(linkId, physicalLink);
    }
  }

  public void testGetHashNodes() {
    assertNotNull(substrateNetwork.getHashNodes());
  }

  public void testGetHashLinks() {
    assertNotNull(substrateNetwork.getHashLinks());
  }

  public void testGetAmountNodes() {
    assertEquals(0, substrateNetwork.getAmountNodes());
  }

  public void testGetAmountLinks() {
    assertEquals(0, substrateNetwork.getAmountLinks());
  }

  public void testSetHashNodes() {
    substrateNetwork.setHashNodes(physicalNodes);
    assertEquals(physicalNodes.size(), substrateNetwork.getAmountNodes());
  }

  public void testSetHashLinks() {
    substrateNetwork.setHashLinks(physicalLinks);
    assertEquals(physicalLinks.size(), substrateNetwork.getAmountLinks());
  }

  public void testGetPhysicalNodesWithCapacityGreaterThan50() {
    substrateNetwork.setHashNodes(physicalNodes);
    assertEquals(5, substrateNetwork.
      getPhysicalNodesWithRemainingCapacityGreaterThan(50).size());
  }

  public void testGetPhysicalNodesWithCapacityGreaterThan100() {
    substrateNetwork.setHashNodes(physicalNodes);
    assertEquals(4, substrateNetwork.
      getPhysicalNodesWithRemainingCapacityGreaterThan(100).size());
  }

  public void testGetPhysicalNodesWithCapacityGreaterThan500() {
    substrateNetwork.setHashNodes(physicalNodes);
    assertEquals(0, substrateNetwork.
      getPhysicalNodesWithRemainingCapacityGreaterThan(500).size());
  }

  public void testCollectNodesLoad() {
    substrateNetwork.setHashNodes(physicalNodes);
    assertEquals(physicalNodes.size(),
      substrateNetwork.collectNodesLoad().getSize());
  }

  public void testCollectLinksBandwidthLoad() {
    substrateNetwork.setHashLinks(physicalLinks);
    assertEquals(physicalLinks.size(),
      substrateNetwork.collectLinksBandwidthLoad().getSize());
  }

  public void testGetAverageNodesLoad() {
    substrateNetwork.setHashNodes(physicalNodes);
    for(PhysicalNode node : physicalNodes.values()) {
      node.addLoad(10);
    }
    assertEquals(0.04566666666666667,
      substrateNetwork.getAverageNodesLoad());
  }

  public void testGetMaximumNodesLoad() {
    substrateNetwork.setHashNodes(physicalNodes);
    for(PhysicalNode node : physicalNodes.values()) {
      node.addLoad(10);
    }
    assertEquals(0.1,
      substrateNetwork.getMaximumNodesLoad());
  }

  public void testGetNodesLoadStandardDeviation() {
    substrateNetwork.setHashNodes(physicalNodes);
    for(PhysicalNode node : physicalNodes.values()) {
      node.addLoad(10);
    }
    assertEquals(0.029013406862651928,
      substrateNetwork.getNodesLoadStandardDeviation());
  }

  public void testGetAverageLinksBandwidthLoad() {
    substrateNetwork.setHashLinks(physicalLinks);
    for(PhysicalLink link : physicalLinks.values()) {
      link.addBandwidthLoad(0.2);
    }
    assertEquals(0.04,
      substrateNetwork.getAverageLinksBandwidthLoad());
  }

  public void testGetMaximumLinksBandwidthLoad() {
    substrateNetwork.setHashLinks(physicalLinks);
    for(PhysicalLink link : physicalLinks.values()) {
      link.addBandwidthLoad(0.2);
    }
    assertEquals(0.04,
      substrateNetwork.getMaximumLinksBandwidthLoad());
  }

  public void testGetLinksBandwidthLoadStandardDeviation() {
    substrateNetwork.setHashLinks(physicalLinks);
    for(PhysicalLink link : physicalLinks.values()) {
      link.addBandwidthLoad(5);
    }
    assertEquals(0D,
      substrateNetwork.getLinksBandwidthLoadStandardDeviation());
  }
}
