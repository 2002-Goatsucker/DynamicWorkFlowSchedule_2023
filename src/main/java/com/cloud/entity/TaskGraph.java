package com.cloud.entity;

import java.util.*;

public class TaskGraph implements Cloneable{

    //graph的大小，不含虚拟头节点
    private int size;

    //虚拟头节点
    Vertex start;

    //虚拟尾结点
    Vertex end = new Vertex(Integer.MAX_VALUE);

    //记录全部节点
    Vertex[] vertices;

    int depth = 0;


    private boolean isBroken = false;

    //记录全部被添加的边
    public final List<int[]> edges = new LinkedList<>();
    public TaskGraph(int n) {
        //创建n个vertex放到数组中，并定义一个虚拟头节点vertex（-1）
        vertices = new Vertex[n];
        for (int i = 0; i < n; ++i) {
            vertices[i] = new Vertex(i);
        }
        start = new Vertex(-1);
        size = n;
    }

    //添加有向边：ver1->ver2
    //节点从0~n-1
    public void addEdge(int ver1, int ver2) {
        if (!vertices[ver1].next.contains(vertices[ver2])){
            vertices[ver1].next.add(vertices[ver2]);
            vertices[ver2].fa.add(vertices[ver1]);
            vertices[ver2].inner++;
            vertices[ver1].outer++;
            edges.add(new int[]{ver1,ver2});
        }
    }

    //拓扑排序（随机拓扑）
    public int[] TopologicalSorting(Random random) {
        if(!isBroken) {
            isBroken = true;
            List<Integer> list = new LinkedList<>();
            //起始节点与终止节点分别对入度为0，出度为0的节点进行连接
            for (Vertex vertex : vertices) {
                if (vertex == null) continue;
                if (vertex.inner == 0) {
                    start.next.add(vertex);
                    start.outer++;
                    vertex.inner++;
                }
                if (vertex.outer == 0) {
                    vertex.next.add(end);
                    vertex.outer++;
                    end.inner++;
                }
            }
            int[] ans = new int[size];
            int k = 0;
            Queue<Integer> queue = new ArrayDeque<>();
            queue.add(-1);
            while (!queue.isEmpty()) {
                int index = queue.poll();
                if (index == end.id) break;
                Vertex temp;
                if (index != -1) temp = vertices[index];
                else temp = start;
                if (index != -1) {
                    addRandomNum(list, temp.id, random);
                }

                for (Vertex vertex : temp.next) {
                    //伪删除前驱节点
                    vertex.inner -= 1;
                    if (!vertex.isVisited) {
                        if (vertex.inner == 0) {
                            queue.add(vertex.id);
                            vertex.isVisited = true;
                        }
                    }
                }
            }
            for (int i = 0; i < list.size(); ++i) {
                ans[i] = list.get(i);
            }
            return ans;
        }else {
            TaskGraph graph = clone();
            return graph.TopologicalSorting(random);
        }
    }

    //辅助方法
    private void addRandomNum(List<Integer> list,int index, Random random){
        List<Vertex> fa=vertices[index].fa;
        List<Vertex> next=vertices[index].next;
        int start = 0;
        int end = list.size();
        for(int i=list.size()-1;i>=0;--i){
            if(fa.contains(vertices[list.get(i)])){
                start=i+1;
                break;
            }
        }
        for(int i=0;i<list.size();++i){
            if(next.contains(vertices[list.get(i)])){
                end=i;
                break;
            }
        }
        int pos = random.nextInt(start,end+1);
        list.add(pos,index);
    }

    @Override
    public TaskGraph clone() {
        //创建一个新图
        TaskGraph taskGraph=new TaskGraph(size);
        //复制加边操作
        for (int[] arr : edges) {
            taskGraph.addEdge(arr[0], arr[1]);
        }
        return taskGraph;
    }
}

class Vertex{
    //节点入度
    int id;
    int inner = 0;
    int outer = 0;
    boolean isVisited = false;
    List<Vertex> next = new ArrayList<>();
    List<Vertex> fa = new ArrayList<>();

//    @Override
//    public int hashCode() {
//        return id;
//    }
//
//    @Override
//    public boolean equals(Object obj) {
//        return id == ((Vertex) obj).id;
//    }

    public Vertex(int id) {
        this.id = id;
    }
}