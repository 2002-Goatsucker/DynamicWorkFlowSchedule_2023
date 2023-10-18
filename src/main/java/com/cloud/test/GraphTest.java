package com.cloud.test;

import com.cloud.entity.TaskGraph;

import java.util.Random;

public class GraphTest {
    public static void main(String[] args) {
        TaskGraph graph = new TaskGraph(5);
        graph.addEdge(0,1);
        graph.addEdge(2,3);
        graph.addEdge(1,4);
        for(int num:graph.TopologicalSorting(new Random())){
            System.out.print(num+" ");
        }
        System.out.println();
        for(int num:graph.TopologicalSorting(new Random())){
            System.out.print(num+" ");
        }
    }
}
