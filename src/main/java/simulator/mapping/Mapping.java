package simulator.mapping;

import java.math.BigDecimal;
import java.math.MathContext;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.HashMap;

import simulator.network.components.physical.PhysicalLink;
import simulator.network.components.physical.PhysicalNode;
import simulator.network.components.virtual.VirtualLink;
import simulator.network.components.virtual.VirtualNode;

/**
 *  Virtual Network Mapping Over Substrate Network
 */
public class Mapping {
  private HashMap<VirtualNode, PhysicalNode> nodesMapping;
  private HashMap<VirtualLink, ArrayList<PhysicalLink>> linksMapping;
  private boolean handleResourcesLoad;

  public Mapping() {
    nodesMapping = new HashMap<VirtualNode, PhysicalNode>();
    linksMapping = new HashMap<VirtualLink, ArrayList<PhysicalLink>>();
    handleResourcesLoad = true;
  }

  public void addNodeMapping(VirtualNode virtualNode, PhysicalNode physicalNode) {
    if(nodesMapping.containsKey(virtualNode))
      throw new RuntimeException("Nó virtual já está alocado.");

    if(handleResourcesLoad) {
      physicalNode.addLoad(virtualNode.getCapacity());
    }
    nodesMapping.put(virtualNode, physicalNode);
  }

  public void addLinkMapping(VirtualLink virtualLink,
                                         ArrayList<PhysicalLink> physicalLinks) {
    if(linksMapping.containsKey(virtualLink))
      throw new RuntimeException("Enlace virtual já está alocado.");

    if(handleResourcesLoad) {
      for(PhysicalLink physicalLink : physicalLinks) {
        physicalLink.addBandwidthLoad(virtualLink.getBandwidthCapacity());
      }
    }
    linksMapping.put(virtualLink, physicalLinks);
  }

  public boolean isNodeMapped(VirtualNode virtualNode) {
    return nodesMapping.containsKey(virtualNode);
  }

  public boolean isLinkMapped(VirtualLink virtualLink) {
    return linksMapping.containsKey(virtualLink);
  }

  public PhysicalNode getHostingNodeFor(VirtualNode virtualNode) {
    return nodesMapping.get(virtualNode);
  }

  public ArrayList<PhysicalLink> getHostingLinksFor(VirtualLink virtualLink) {
    return linksMapping.get(virtualLink);
  }

  public boolean isNodeInUse(PhysicalNode node) {
    return nodesMapping.values().contains(node);
  }

  public boolean isLinkInUse(PhysicalLink link) {
    for(ArrayList<PhysicalLink> linksPath : linksMapping.values()) {
      for(PhysicalLink l : linksPath) {
        if(l.equals(link)) {

          return true;
        }
      }
    }

    return false;
  }

  public void clearMappings() {
    if(handleResourcesLoad) {
      for(VirtualNode virtualNode : nodesMapping.keySet()) {
        PhysicalNode hostingNode = nodesMapping.get(virtualNode);
        hostingNode.removeLoad(virtualNode.getCapacity());
      }
      for(VirtualLink virtualLink : linksMapping.keySet()) {
        ArrayList<PhysicalLink> hostingLinks = linksMapping.get(virtualLink);
        for(PhysicalLink hostingLink : hostingLinks) {
          hostingLink.removeBandwidthLoad(virtualLink.getBandwidthCapacity());
        }
      }
    }

    nodesMapping.clear();
    linksMapping.clear();
  }

  public BigDecimal getAvailability() {
    LinkedHashSet<PhysicalLink> uniqPhysicalLinks =
      new LinkedHashSet<PhysicalLink>();
    LinkedHashSet<PhysicalNode> uniqIntermediaryNodes =
      new LinkedHashSet<PhysicalNode>();
    for(VirtualLink virtualLink : linksMapping.keySet()) {
      PhysicalNode sourcePhysicalNode = getHostingNodeFor(
        (VirtualNode) virtualLink.getSourceNode());
      PhysicalNode destinyPhysicalNode = getHostingNodeFor(
        (VirtualNode) virtualLink.getDestinyNode());
      ArrayList<PhysicalLink> hostingLinks = linksMapping.get(virtualLink);
      for(PhysicalLink hostingLink : hostingLinks) {
        uniqPhysicalLinks.add(hostingLink);
        PhysicalNode hostingLinkSourceNode =
          (PhysicalNode) hostingLink.getSourceNode();
        PhysicalNode hostingLinkDestinyNode =
          (PhysicalNode) hostingLink.getDestinyNode();
        PhysicalNode[] hostingLinkNodes = { hostingLinkSourceNode,
          hostingLinkDestinyNode };
        for(PhysicalNode hostingLinkNode : hostingLinkNodes) {
          if(!hostingLinkNode.equals(sourcePhysicalNode)
             && !hostingLinkNode.equals(destinyPhysicalNode)) {
            uniqIntermediaryNodes.add(hostingLinkNode);
          }
        }
      }
    }

    BigDecimal availability = new BigDecimal(1);
    for(PhysicalNode hostingNode : uniqPhysicalNodes()) {
      availability = availability.multiply(hostingNode.getNodeAvailability(),
        MathContext.DECIMAL64);
    }
    for(PhysicalLink hostingLink : uniqPhysicalLinks) {
      availability = availability.multiply(hostingLink.getAvailability(),
        MathContext.DECIMAL64);
    }
    for(PhysicalNode intermediaryNode : uniqIntermediaryNodes) {
      availability = availability.multiply(intermediaryNode.
        getIntermediaryNodeAvailability(), MathContext.DECIMAL64);
    }

    return availability;
  }

  public void deactiveResourcesHandling() {
    handleResourcesLoad = false;
  }

  public double getNodeSharingRate(int nodesNumber) {
    ArrayList<PhysicalNode> nodesInUse = new ArrayList<PhysicalNode>();
    ArrayList<PhysicalNode> nodesBeingShared = new ArrayList<PhysicalNode>();
    ArrayList<PhysicalNode> touchedNodes = new ArrayList<PhysicalNode>();
    for(PhysicalNode physicalNode : nodesMapping.values()) {
      if(nodesInUse.contains(physicalNode) && !touchedNodes.contains(physicalNode)) {
        nodesBeingShared.add(physicalNode);
        touchedNodes.add(physicalNode);
      }
      nodesInUse.add(physicalNode);
    }

    return (double) nodesBeingShared.size() / nodesNumber;
  }

  private ArrayList<PhysicalNode> uniqPhysicalNodes() {
    return new ArrayList<PhysicalNode>(
      new LinkedHashSet<PhysicalNode>(nodesMapping.values()));
  }
}
