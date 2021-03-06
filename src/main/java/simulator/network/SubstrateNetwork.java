package simulator.network;

import java.util.ArrayList;
import java.util.HashMap;

import org.uncommons.maths.statistics.DataSet;

import simulator.network.components.physical.PhysicalLink;
import simulator.network.components.physical.PhysicalNode;
import simulator.util.Util;

public class SubstrateNetwork {
  private HashMap<Integer, PhysicalNode> physicalNodes;
  private HashMap<String, PhysicalLink> physicalLinks;
  private int amountNodes;
  private int amountLinks;

  public SubstrateNetwork() {
    this.physicalNodes = new HashMap<Integer, PhysicalNode>();
    this.physicalLinks = new HashMap<String, PhysicalLink>();
  }

  public SubstrateNetwork(HashMap<Integer, PhysicalNode> nodes,
                          HashMap<String, PhysicalLink> links) {
    setHashNodes(nodes);
    setHashLinks(links);
  }

  public HashMap<Integer, PhysicalNode> getHashNodes() {
    return physicalNodes;
  }

  public void setHashNodes(HashMap<Integer, PhysicalNode> physicalNodes) {
    this.physicalNodes = physicalNodes;
    this.amountNodes = physicalNodes.size();
  }

  public HashMap<String, PhysicalLink> getHashLinks() {
    return physicalLinks;
  }

  public void setHashLinks(HashMap<String, PhysicalLink> physicalLinks) {
    this.physicalLinks = physicalLinks;
    this.amountLinks = physicalLinks.size();
  }

  public int getAmountNodes() {
    return amountNodes;
  }

  public void setAmountNodes(int amountNodes) {
    this.amountNodes = amountNodes;
  }

  public int getAmountLinks() {
    return amountLinks;
  }

  public void setAmountLinks(int amountLinks) {
    this.amountLinks = amountLinks;
  }

  /*
   * Retorna ArrayList com nós físicos que têm capacidade maior que a
   * especificada como parâmetro.
   */
  public ArrayList<PhysicalNode> getPhysicalNodesWithRemainingCapacityGreaterThan(
                                                              double capacity) {
    ArrayList<PhysicalNode> capablePhysicalNodes = new ArrayList<PhysicalNode>();
    for(PhysicalNode node : physicalNodes.values()) {
      if(node.getRemainingCapacity() > capacity) {
        capablePhysicalNodes.add(node);
      }
    }

    return capablePhysicalNodes;
  }

  public double getAverageNodesLoad() {
    return Util.getAverage(collectNodesLoad());
  }

  public double getMaximumNodesLoad() {
    return Util.getMaximum(collectNodesLoad());
  }

  public double getNodesLoadStandardDeviation() {
    return Util.getStandardDeviation(collectNodesLoad());
  }

  public double getAverageLinksBandwidthLoad() {
    return Util.getAverage(collectLinksBandwidthLoad());
  }

  public double getMaximumLinksBandwidthLoad() {
    return Util.getMaximum(collectLinksBandwidthLoad());
  }

  public double getLinksBandwidthLoadStandardDeviation() {
    return Util.getStandardDeviation(collectLinksBandwidthLoad());
  }

  protected DataSet collectNodesLoad() {
    DataSet data = new DataSet();
    for(PhysicalNode node : physicalNodes.values()) {
      data.addValue(node.getLoad() / node.getCapacity());
    }

    return data;
  }

  protected DataSet collectLinksBandwidthLoad() {
    DataSet data = new DataSet();
    for(PhysicalLink link : physicalLinks.values()) {
      data.addValue(link.getBandwidthLoad() / link.getBandwidthCapacity());
    }

    return data;
  }

  @Override
  public String toString() {
    String representation = "";
    representation = representation.concat("************************************\n");
    representation = representation.concat(String.format("Rede Substrato: %d nós e %d enlaces\n",
        physicalNodes.size(), physicalLinks.size()));
    representation = representation.concat("************************************\n");
    for(PhysicalNode physicalNode : physicalNodes.values().toArray(new PhysicalNode[1])) {
      representation = representation.concat(String.format("Nó %d: \n\tCPU: %f\n\tCusto: %d\n",
          physicalNode.getId(), physicalNode.getCapacity()));
    }
    for(PhysicalLink physicalLink : physicalLinks.values().toArray(new PhysicalLink[1])) {
      representation = representation.concat(String.format("Enlace %s: origem %d, destino %d\n\tBanda: %f\n\tCusto: %d\n\tDelay: %d\n",
          physicalLink.getId(), physicalLink.getSourceNode().getId(), physicalLink.getDestinyNode().getId(),
          physicalLink.getBandwidthCapacity(), physicalLink.getCost(), physicalLink.getDelay()));
    }

    return representation;
  }
}
