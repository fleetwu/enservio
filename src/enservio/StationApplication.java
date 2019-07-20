package enservio;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;

class Connection {
	String from;
	String to;
	Connection(String s1, String s2){
		from = s1;
		to = s2;
	}
	@Override
	public boolean equals(Object o) {
		Connection conn = (Connection)o;
		return this.from.equals(conn.from) && this.to.equals(conn.to);
	}
	
	@Override
	public int hashCode() {
		return 1;
	}
}

public class StationApplication {
	//In this graph, each vertex representing a station. If two vertexes have direct connection, the edge value between them 
	//is one; other wise, it is 0; 
	private int[][] graph;
	//The following maps represent the relation ship of station name and vertex's value.
	private Map<String, Integer> nameToNum = new HashMap<>();
	private Map<Integer, String> numToName = new HashMap<>();
	
	public static void main(String[] args) {
		   
		StationApplication sa = new StationApplication();
		sa.fillMatrix("lines.csv");
		
		try {
			Scanner scanner = new Scanner(System.in);
					
			String stationName = "";
			int stopCount;
			
			while (true) {
				System.out.print("\nEnter station name: ");
				stationName = scanner.nextLine();
				System.out.print("\nEnter stop count: ");
				stopCount = scanner.nextInt();
				sa.getStations(stationName, stopCount);
				scanner.nextLine();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void fillMatrix(String filePath) {
		Set<String> nameSet = new HashSet<>();
		Set<Connection> connections = new HashSet<>();
		
		readData(filePath, nameSet, connections);
		
		List<String> nameList = new ArrayList<>(nameSet);
		Collections.sort(nameList);
		
		for(int i = 0; i < nameList.size(); i++) {
			nameToNum.put(nameList.get(i), i);
			numToName.put(i, nameList.get(i));
		}
		
		int size = nameList.size();
		
		graph = new int[size][size];
		
		for(int i = 0; i < size; i++) {
			for(int j = 0; j < size; j++)
				graph[i][j] = 0;
		}
		
		for(Connection conn : connections) {
			int x, y;
			x = nameToNum.get(conn.from);
			y = nameToNum.get(conn.to);
			graph[y][x] = 1;
			graph[x][y] = 1;
		}		
	}
	
	private void readData(String filePath, Set<String> nameSet, Set<Connection> connSet) {		
        BufferedReader br = null;
        String line = null;
        String cvsSplitBy = ",";
        
        try {

            br = new BufferedReader(new FileReader(filePath));
            br.readLine();
            while ((line = br.readLine()) != null) {

                String[] connData = line.split(cvsSplitBy);
                nameSet.add(connData[1]);
                nameSet.add(connData[2]);
                connSet.add(new Connection(connData[1], connData[2]));
                
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
	}
	
	public void getStations(String stationName, int stopCount) {
		int sv;
		if(nameToNum.containsKey(stationName)) {
			sv = nameToNum.get(stationName);
		}else {
			System.out.println("Invalid station name.");
			return;
		}
		
		int n = graph.length;
		
		//This array stores the distance between the starting station and all other stations. If a station and the starting station 
		//do not have connection at all, the distance value is Integer.MAX_VALUE.
		int[] dist = new int[n];
		boolean[] flag = new boolean[n];
		
		for(int i = 0; i < n; i++) {
			flag[i] = false;
			dist[i] = Integer.MAX_VALUE;
			
		}
		
		dist[sv] = 0;
		
		for(int i = 0; i < n; i++) {
			int u = findMin(flag, dist);
			if(u > -1) {
				flag[u] = true;
				for(int v = 0; v < n; v++) {
					if(flag[v] == false && graph[u][v] != 0) {
						if(dist[v] > dist[u] + graph[u][v]) {
							dist[v] = dist[u] + graph[u][v];
						}
					}
				}	
			}				
		}
		
		for(int i = 0; i < dist.length; i++) {
			if(dist[i] == stopCount) {
			System.out.println(numToName.get(i));
			}
		}
	}
	
	private int findMin(boolean[] flag, int[] dist) {
		int min = Integer.MAX_VALUE;
		int index = -1;
		
		for(int i = 0; i < dist.length; i++) {
			if(flag[i] == false && dist[i] < min) {
				min = dist[i];
				index = i;
			}
		}
		
		return index;
	}	
}
