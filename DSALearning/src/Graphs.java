/**
 * GRAPHS - Network of Connected Nodes
 *
 * Definitions:
 * - Vertex: Node in the graph
 * - Edge: Connection between vertices
 * - Directed: Edges have direction (A->B)
 * - Undirected: Edges have no direction (A-B)
 * - Weighted: Edges have weights/costs
 * - Cycle: Path that starts and ends at same vertex
 *
 * Representations:
 * 1. Adjacency List: Best for sparse graphs (fewer edges)
 * 2. Adjacency Matrix: Best for dense graphs (many edges)
 *
 * Key Algorithms:
 * - DFS: Depth-first search (stack-based or recursive)
 * - BFS: Breadth-first search (queue-based)
 * - Dijkstra: Shortest path in weighted graph
 * - Floyd-Warshall: All pairs shortest paths
 * - Topological Sort: For directed acyclic graphs
 */

import java.util.*;

public class Graphs {

    // ============ GRAPH USING ADJACENCY LIST ============
    public static class Graph {
        private Map<Integer, List<Integer>> adjacencyList;
        private Map<Integer, Map<Integer, Integer>> weights;  // For weighted graphs
        private int vertices;

        public Graph(int vertices) {
            this.vertices = vertices;
            this.adjacencyList = new HashMap<>();
            this.weights = new HashMap<>();

            for (int i = 0; i < vertices; i++) {
                adjacencyList.put(i, new ArrayList<>());
                weights.put(i, new HashMap<>());
            }
        }

        // Add undirected edge
        public void addEdge(int u, int v) {
            adjacencyList.get(u).add(v);
            adjacencyList.get(v).add(u);
        }

        // Add weighted edge
        public void addWeightedEdge(int u, int v, int weight) {
            adjacencyList.get(u).add(v);
            adjacencyList.get(v).add(u);
            weights.get(u).put(v, weight);
            weights.get(v).put(u, weight);
        }

        // Add directed edge
        public void addDirectedEdge(int u, int v) {
            adjacencyList.get(u).add(v);
        }

        // ============ DEPTH-FIRST SEARCH (DFS) ============
        /**
         * CONCEPT: Explore as far as possible before backtracking
         * Uses stack (recursion is implicit)
         *
         * DRY RUN on graph:
         *   0 --- 1
         *   |     |
         *   2 --- 3
         *
         * Start at 0, visited={}
         *
         * Visit 0: visited={0}, neighbors=[1,2]
         *   Visit 1: visited={0,1}, neighbors=[0,3]
         *     0 already visited, skip
         *     Visit 3: visited={0,1,3}, neighbors=[1,2]
         *       1 already visited, skip
         *       Visit 2: visited={0,1,3,2}, neighbors=[0,3]
         *         All visited, backtrack
         *   Back to 1, all neighbors processed
         * Back to 0, try next neighbor 2
         *   2 already visited, skip
         *
         * Result: [0, 1, 3, 2]
         * Time: O(V + E), Space: O(V) for visited set
         */
        public List<Integer> dfs(int start) {
            List<Integer> result = new ArrayList<>();
            Set<Integer> visited = new HashSet<>();
            dfsHelper(start, visited, result);
            return result;
        }

        private void dfsHelper(int vertex, Set<Integer> visited, List<Integer> result) {
            visited.add(vertex);
            result.add(vertex);

            for (int neighbor : adjacencyList.get(vertex)) {
                if (!visited.contains(neighbor)) {
                    dfsHelper(neighbor, visited, result);
                }
            }
        }

        // ============ BREADTH-FIRST SEARCH (BFS) ============
        /**
         * CONCEPT: Explore all neighbors at current level before going deeper
         * Uses queue
         *
         * DRY RUN on same graph:
         *
         * queue=[0], visited={}
         *
         * Dequeue 0: result=[0], visited={0}
         *   neighbors=[1,2], enqueue both
         *   queue=[1,2]
         *
         * Dequeue 1: result=[0,1], visited={0,1}
         *   neighbors=[0,3], 0 visited, enqueue 3
         *   queue=[2,3]
         *
         * Dequeue 2: result=[0,1,2], visited={0,1,2}
         *   neighbors=[0,3], 0 visited, 3 already in queue
         *   queue=[3]
         *
         * Dequeue 3: result=[0,1,2,3], visited={0,1,2,3}
         *   neighbors=[1,2], all visited
         *   queue=[]
         *
         * Result: [0, 1, 2, 3]
         * Time: O(V + E), Space: O(V)
         */
        public List<Integer> bfs(int start) {
            List<Integer> result = new ArrayList<>();
            Set<Integer> visited = new HashSet<>();
            Queue<Integer> queue = new LinkedList<>();

            queue.offer(start);
            visited.add(start);

            while (!queue.isEmpty()) {
                int vertex = queue.poll();
                result.add(vertex);

                for (int neighbor : adjacencyList.get(vertex)) {
                    if (!visited.contains(neighbor)) {
                        visited.add(neighbor);
                        queue.offer(neighbor);
                    }
                }
            }

            return result;
        }

        // ============ DIJKSTRA'S SHORTEST PATH ============
        /**
         * CONCEPT: Find shortest path from source to all other vertices
         * For weighted graphs with non-negative edges
         * Uses greedy approach with min-heap (priority queue)
         *
         * DRY RUN:
         * Graph:    1 --2-- 2
         *           |       |
         *           4       3
         *           |       |
         *           3 --1-- 4
         *
         * Find shortest path from 0 (node 1):
         *
         * Initial: dist={0:0, 1:∞, 2:∞, 3:∞}
         * pq=[(0,0)]
         *
         * Step 1: Extract (0,0)
         *   Neighbors: 1(cost 2), 2(cost 4)
         *   Update dist[1] = 0+2=2, dist[2] = 0+4=4
         *   pq=[(2,1), (4,2)]
         *
         * Step 2: Extract (2,1)
         *   Neighbors: 0(visited), 3(cost 3)
         *   Update dist[3] = 2+3=5
         *   pq=[(4,2), (5,3)]
         *
         * Step 3: Extract (4,2)
         *   Neighbors: 0(visited), 3(cost 1)
         *   Check: 4+1=5, dist[3]=5, no update
         *   pq=[(5,3)]
         *
         * Step 4: Extract (5,3)
         *   Neighbors: 2(visited), 1(visited)
         *   pq=[]
         *
         * Result: {0:0, 1:2, 2:4, 3:5}
         *
         * Time: O((V+E) log V) with min-heap
         * Space: O(V)
         */
        public Map<Integer, Integer> dijkstra(int start) {
            Map<Integer, Integer> dist = new HashMap<>();
            for (int i = 0; i < vertices; i++) {
                dist.put(i, Integer.MAX_VALUE);
            }
            dist.put(start, 0);

            PriorityQueue<int[]> pq = new PriorityQueue<>((a, b) -> a[0] - b[0]);
            pq.offer(new int[]{0, start});

            while (!pq.isEmpty()) {
                int[] current = pq.poll();
                int d = current[0];
                int u = current[1];

                if (d > dist.get(u)) {
                    continue;
                }

                for (int v : adjacencyList.get(u)) {
                    int weight = weights.get(u).getOrDefault(v, 1);
                    if (dist.get(u) + weight < dist.get(v)) {
                        dist.put(v, dist.get(u) + weight);
                        pq.offer(new int[]{dist.get(v), v});
                    }
                }
            }

            return dist;
        }

        // ============ TOPOLOGICAL SORT ============
        /**
         * CONCEPT: Linear ordering of vertices where for each directed edge u->v,
         * u comes before v in the ordering
         * Only for DAGs (Directed Acyclic Graphs)
         *
         * DRY RUN on directed graph:
         * 0 -> 1
         * 0 -> 2
         * 1 -> 3
         * 2 -> 3
         *
         * Using DFS approach:
         *
         * Visit 0: processing={0}
         *   Visit 1: processing={0,1}
         *     Visit 3: processing={0,1,3}
         *       No unvisited neighbors, add to stack: stack=[3]
         *     1 done, add to stack: stack=[3,1]
         *   Visit 2: processing={0,1,2}
         *     3 already visited
         *     2 done, add to stack: stack=[3,1,2]
         * 0 done, add to stack: stack=[3,1,2,0]
         *
         * Result (reverse stack): [0, 2, 1, 3]
         *
         * Time: O(V + E), Space: O(V)
         */
        public List<Integer> topologicalSort() {
            List<Integer> result = new ArrayList<>();
            Set<Integer> visited = new HashSet<>();
            Stack<Integer> stack = new Stack<>();

            for (int i = 0; i < vertices; i++) {
                if (!visited.contains(i)) {
                    topologicalHelper(i, visited, stack);
                }
            }

            while (!stack.isEmpty()) {
                result.add(stack.pop());
            }

            return result;
        }

        private void topologicalHelper(int vertex, Set<Integer> visited, Stack<Integer> stack) {
            visited.add(vertex);

            for (int neighbor : adjacencyList.get(vertex)) {
                if (!visited.contains(neighbor)) {
                    topologicalHelper(neighbor, visited, stack);
                }
            }

            stack.push(vertex);
        }
    }

    public static void main(String[] args) {
        System.out.println("===== GRAPHS =====\n");

        // Create undirected graph
        Graph undirectedGraph = new Graph(4);
        undirectedGraph.addEdge(0, 1);
        undirectedGraph.addEdge(0, 2);
        undirectedGraph.addEdge(1, 3);
        undirectedGraph.addEdge(2, 3);

        System.out.println("--- Undirected Graph Traversals ---");
        System.out.println("DFS starting from 0: " + undirectedGraph.dfs(0));
        System.out.println("BFS starting from 0: " + undirectedGraph.bfs(0));
        System.out.println();

        // Create weighted graph for Dijkstra
        Graph weightedGraph = new Graph(4);
        weightedGraph.addWeightedEdge(0, 1, 2);
        weightedGraph.addWeightedEdge(0, 2, 4);
        weightedGraph.addWeightedEdge(1, 3, 3);
        weightedGraph.addWeightedEdge(2, 3, 1);

        System.out.println("--- Dijkstra's Shortest Path ---");
        Map<Integer, Integer> distances = weightedGraph.dijkstra(0);
        System.out.println("Shortest distances from 0: " + distances);
        System.out.println();

        // Create directed graph for topological sort
        Graph directedGraph = new Graph(4);
        directedGraph.addDirectedEdge(0, 1);
        directedGraph.addDirectedEdge(0, 2);
        directedGraph.addDirectedEdge(1, 3);
        directedGraph.addDirectedEdge(2, 3);

        System.out.println("--- Topological Sort ---");
        System.out.println("Topological ordering: " + directedGraph.topologicalSort());
    }
}

