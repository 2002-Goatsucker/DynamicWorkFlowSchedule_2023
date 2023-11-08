package com.cloud.algorithm.utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

//使用动态规划找出最长公共子序列
public class LCS {
    public static void main(String[] args) {
        //随机生成指定长度的字符串
        int[] a= {1,2,4,5,6,7};
        int[] b= {1,2,4,6,8,10};
        ArrayList<Integer> same=getLCS(a, b);
        for (Integer s:same)
        	System.out.println(s);
    }

    public static String generateRandomStr(int length) {
        String base = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        Random random = new Random();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < length; i++) {
            int number = random.nextInt(base.length());
            sb.append(base.charAt(number));
        }
        return sb.toString();
    }
    public static void PrintLCS(int[][]b,String x,int i,int j){
        if(i == 0 || j == 0){
            return;
        }

        if(b[i][j] == 0){
            PrintLCS(b,x,i-1,j-1);
            System.out.printf("%c",x.charAt(i-1));
        }else if(b[i][j] == 1){
            PrintLCS(b,x,i-1,j);
        }else{
            PrintLCS(b,x,i,j-1);
        }
    }
    
    public static ArrayList<Integer> getLCS(int[] a,int[]b) {
    	int m=a.length;
    	int n=b.length;
    	int[][] c = new int[m+1][n+1];

        //初始化二维数组
        for (int i = 0; i < m+1; i++) {
            c[i][0] = 0;
        }
        for (int i = 0; i < n+1; i++) {
            c[0][i] = 0;
        }

        //实现公式逻辑
        int[][] path = new int[m+1][n+1];//记录通过哪个子问题解决的，也就是递推的路径
        for (int i = 1; i < m+1; i++) {
            for (int j = 1; j < n+1; j++) {
                if(a[i-1] == b[j-1]){
                    c[i][j] = c[i-1][j-1] + 1;
                    path[i][j]=1;
                }else if(c[i-1][j] >= c[i][j-1]){
                    c[i][j] = c[i-1][j];
                    path[i][j] = 2;
                }else{
                    c[i][j] = c[i][j-1];
                    path[i][j] = 3;
                }
            }
        }
        int i=m-1;
        int j=n-1;
        ArrayList<Integer> same=new ArrayList<Integer>();
        int k=0;
        while (i>0 && j>0) {
			if (path[i][j]==1) {
				same.add(a[i-1]);
				
				k++;
				i-=1;
			    j-=1;
			}
			else if (path[i][j]==2) {
				i-=1;
			}
			else {
				j-=1;
			}
		}
        Collections.reverse(same);
        return same;
    	
    }
}
