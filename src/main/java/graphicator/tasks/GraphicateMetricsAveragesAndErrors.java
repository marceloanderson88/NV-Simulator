package graphicator.tasks;

import graphicator.graphs.Graph;
import graphicator.io.ViNEYardTraceReader;
import graphicator.io.VirtuaSimulatorTraceReader;

import java.io.File;
import java.util.Arrays;

import org.uncommons.maths.statistics.DataSet;

import simulator.util.Util;

public class GraphicateMetricsAveragesAndErrors {
  public static void main(String[] args) {
    String[] nodeClasses = { "20", "30", "50", "100" };
    String[] approaches = { "HRA", "Greddy", "DViNE" };
    String[] metrics = { "acceptanceRate", "averageNodesLoad", "averageLinksLoad",
      "executionTime", "averageAvailability" };
    DataSet[][][] metricsData = aggregateTraces(nodeClasses, approaches, metrics);
    Graph[] graphs = constructGraphs(metricsData, nodeClasses, approaches, metrics);
    for(Graph graph : graphs) {
      System.out.println(graph);
    }
    System.out.println();
  }

  private static DataSet[][][] aggregateTraces(String[] nodeClasses,
                                        String[] approaches, String[] metrics) {
    DataSet[][][] allData = new DataSet[nodeClasses.length][approaches.length][metrics.length];
    for(int i = 0; i < nodeClasses.length; i++) {
      // VirtuaTraces
      File baseDir = new File("/media/embs/Data/VNMP_Instances/" + nodeClasses[i]);
      DataSet[] metricsData = new DataSet[metrics.length];
      for(int x = 0; x < metricsData.length; x++) {
        metricsData[x] = new DataSet();
      }
      for(int j = 0; j < 30; j++) {
        VirtuaSimulatorTraceReader reader = new VirtuaSimulatorTraceReader();
        reader.readTrace(baseDir.getAbsolutePath() + "/eu_" +
          nodeClasses[i] + "_" + j + "_prob_simulation.txt");
        for(int k = 0; k < metrics.length; k++) {
          metricsData[k].addValue((Double) reader.get(metrics[k]));
        }
      }
      allData[i][0] = metricsData;

      // ViNETraces
      String[] approachesNames = { "HRA", "", ".dvine" };
      for(int a = 1; a < approachesNames.length; a++) {
        String approach = approachesNames[a];
        baseDir = new File("/media/embs/Data/OptFIVNMP_Instances_ViNE_format/" +
          nodeClasses[i]);
        metricsData = new DataSet[metrics.length];
        for(int x = 0; x < metricsData.length; x++) {
          metricsData[x] = new DataSet();
        }
        for(int j = 0; j < 30; j++) {
          ViNEYardTraceReader reader = new ViNEYardTraceReader();
          String s = baseDir.getAbsolutePath() + "/eu_" + nodeClasses[i] + "_" +
            j + "_prob/";
          reader.readTrace(s + "MySimINFOCOM2009" + approach + ".out",
            s + "time" + approach + ".out");
          reader.readMappings(s + "sub.txt", s + "requests",
            s + "mappings" + approach + ".out");
          for(int k = 0; k < metrics.length; k++) {
            metricsData[k].addValue((Double) reader.get(metrics[k]));
          }
        }
        allData[i][a] = metricsData;
      }
    }

    return allData;
  }

  private static Graph[] constructGraphs(DataSet[][][] data, String[] nodeClasses,
                                        String[] approaches, String[] metrics) {
    Graph[] graphs = new Graph[metrics.length];
    for(int i = 0; i < metrics.length; i++) {
      graphs[i] = new Graph("Gráfico para a métrica " + metrics[i], 8, 3);
      for(int j = 0; j < nodeClasses.length; j++) {
        graphs[i].setLineHeader(j, nodeClasses[j]);
        graphs[i].setLineHeader(j+4, nodeClasses[j] + "-err");
        for(int k = 0; k < approaches.length; k++) {
          graphs[i].setColumnHeader(k, approaches[k]);
          graphs[i].setCell(j, k, String.valueOf(Util.getAverage(data[j][k][i])));
          graphs[i].setCell(j+4, k, String.valueOf(Util.getError(data[j][k][i])));
        }
      }
    }

    return graphs;
  }
}