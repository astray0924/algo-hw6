package hw6.problemA;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.StringTokenizer;

import org.gnu.glpk.GLPK;
import org.gnu.glpk.SWIGTYPE_p_double;
import org.gnu.glpk.SWIGTYPE_p_int;
import org.gnu.glpk.glp_graph;
import org.gnu.glpk.glp_iocp;
import org.gnu.glpk.glp_prob;
import org.gnu.glpk.glp_smcp;

public class Test {
	public static void main(String args[]) throws IOException{
		
		BufferedReader f = new BufferedReader(new FileReader(new File("src/main/resources/test8.in")));
		
		//Read the number of nodes
		int n = Integer.parseInt(f.readLine());
		System.out.println("n (the number of nodes):	" + n);
		
		//Read the number of edges
		int m = Integer.parseInt(f.readLine());
		System.out.println("m (the number of edges):	" + m);
		
		//Read node weight and generate node array.
		//the index of array is same as the index of node.
		//the value of array is the weight of the node.
		int[] nodes = new int[n+1];
		StringTokenizer st = new StringTokenizer(f.readLine());
		for(int i=1; i <= n; i++){
			nodes[i] = Integer.parseInt(st.nextToken());
		}
		
		//Read edges and generate ArrayList of edges.
		int[][] edges = new int[m+1][2];
		for(int i=1; i <= m; i++){
			StringTokenizer edgeTokenizer = new StringTokenizer(f.readLine());
			edges[i][0] = Integer.parseInt(edgeTokenizer.nextToken()) + 1;
			edges[i][1] = Integer.parseInt(edgeTokenizer.nextToken()) + 1;
			
		}
		
		System.out.println("start glp_solver");
		glp_solver(n, m, nodes, edges);
		
		f.close();
	}
	
	
	public static void glp_solver(int n, int m, int[] nodes, int[][] edges){
		glp_smcp parm = new glp_smcp();
		glp_prob glp = GLPK.glp_create_prob();
		GLPK.glp_set_prob_name(glp, "hw6");
		GLPK.glp_set_obj_dir(glp, GLPK.GLP_MIN);
		
		System.out.println("create arrays");
        SWIGTYPE_p_int ai = GLPK.new_intArray(m*2);
        SWIGTYPE_p_int aj = GLPK.new_intArray(m*2);
        SWIGTYPE_p_double ar = GLPK.new_doubleArray(m*2);
        
        System.out.println("add rows");
        GLPK.glp_add_rows(glp, m);
		for(int i=1; i <= m; i++){
			GLPK.glp_set_row_name(glp, i, "c"+i);
			GLPK.glp_set_row_bnds(glp, i, GLPK.GLP_LO, 1, 2); //DB로 하면 빨라지나?
			
			int index2 = i * 2;
			int index1 = index2 - 1;
			
			GLPK.intArray_setitem(ai, index1, i);
			GLPK.intArray_setitem(aj, index1, edges[i][0]);
			GLPK.doubleArray_setitem(ar, index1, 1);
			   
			GLPK.intArray_setitem(ai, index2, i);
			GLPK.intArray_setitem(aj, index2, edges[i][1]);
            GLPK.doubleArray_setitem(ar, index2, 1);
//            System.out.println(i + " " + edges[i][0] + ": " + 1 );
//            System.out.println(i + " " + edges[i][1] + ": " + 1 );
//            System.out.println(edges[i][0] + " " + edges[i][1] + " >= 1");
		}
		
		
		System.out.println("add cols");
		GLPK.glp_add_cols(glp, n);
		for(int i=1; i <= n; i++){
			GLPK.glp_set_col_name(glp, i, "x"+i);
			GLPK.glp_set_col_bnds(glp, i, GLPK.GLP_DB, 0, 1);
			GLPK.glp_set_obj_coef(glp, i, nodes[i]);
		}
		
		
		
		
		System.out.println("matrix load");
		GLPK.glp_load_matrix(glp, m*2, ai, aj, ar);
		
		
        GLPK.glp_init_smcp(parm);
        int ret = GLPK.glp_simplex(glp, parm);
        // Retrieve solution
        if (ret == 0) {
            write_lp_solution(glp);
        } else {
            System.out.println("The problem could not be solved");
        }
		
//		System.out.println("start optimizer");
//		glp_iocp parm = new glp_iocp();
//		System.out.println("start optimizer");
//		GLPK.glp_init_iocp(parm);
//		System.out.println("start optimizer");
//		parm.setPresolve(GLPK.GLP_ON);
//		System.out.println("start optimizer");
//		int err = GLPK.glp_intopt(glp, parm);
//		
//		System.out.println("z: "+GLPK.glp_mip_obj_val(glp));
//		for(int i=1; i <= n; i++){
//			System.out.println("x"+i+": "+GLPK.glp_mip_col_val(glp, i));
//		}
		
		GLPK.glp_delete_prob(glp);
	}

	
	/**
     * write simplex solution
     * @param lp problem
     */
    static void write_lp_solution(glp_prob lp) {
        int i;
        int n;
        String name;
        double val;
        name = GLPK.glp_get_obj_name(lp);
        val = GLPK.glp_get_obj_val(lp);
        System.out.print(name);
        System.out.print(" = ");
        System.out.println(val);
        n = GLPK.glp_get_num_cols(lp);
        for (i = 1; i <= n; i++) {
            name = GLPK.glp_get_col_name(lp, i);
            val = GLPK.glp_get_col_prim(lp, i);
            System.out.print(name);
            System.out.print(" = ");
            System.out.println(val);
        }
    }
}
