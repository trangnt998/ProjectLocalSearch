package mip;


import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Scanner;

import com.google.ortools.linearsolver.MPConstraint;
import com.google.ortools.linearsolver.MPObjective;
import com.google.ortools.linearsolver.MPSolver;
import com.google.ortools.linearsolver.MPVariable;

public class productionPlanningMIP {
	static {
		System.loadLibrary("jniortools");
	}
	
	public int N, A, C;
	public int[] a;
	public int[] c;
	public int[] m;
	public int[] f;
	
	public productionPlanningMIP() {}
	
	
	public void readData(String fn) {
		try {
			Scanner in = new Scanner(new File(fn));
			N = in.nextInt();
			A = in.nextInt();
			C = in.nextInt();
			a = new int[N];
			c = new int[N];
			m = new int[N];
			f = new int[N];
			for(int i  = 0; i < N; i++) {
				a[i] = in.nextInt();
				f[i] = in.nextInt();
				c[i] = in.nextInt();
				m[i] = in.nextInt();
				
			}
			
			in.close();
		}
		catch (Exception ex) {
			ex.printStackTrace();
		}
	}
	
	public void stateModel() {
		MPSolver solver = new MPSolver(
				"MIP", MPSolver.OptimizationProblemType.CBC_MIXED_INTEGER_PROGRAMMING);
		final int M = Integer.max(
				A / Arrays.stream(a).min().getAsInt(), 
				C / Arrays.stream(c).min().getAsInt());
		
		MPVariable[] x = new MPVariable[N];
		MPVariable[] t = new MPVariable[N];
		for (int i=0; i<N; i++) {
			x[i] = solver.makeBoolVar("x"+i);
			t[i] = solver.makeIntVar(0, M, "t"+i);
		}
		
		MPConstraint area = solver.makeConstraint(0, A);
		for (int i=0; i<N; i++)
			area.setCoefficient(t[i], a[i]);
		
		for (int i=0; i<N; i++) {
			MPConstraint c1 = solver.makeConstraint(0, MPSolver.infinity());
			c1.setCoefficient(t[i], 1);
			c1.setCoefficient(x[i], M);
			
			MPConstraint c2 = solver.makeConstraint(0, MPSolver.infinity());
			c2.setCoefficient(t[i], -1);
			c2.setCoefficient(x[i], M);
			
			MPConstraint c3 = solver.makeConstraint(0, MPSolver.infinity());
			c3.setCoefficient(t[i], 1);
			c3.setCoefficient(x[i], -m[i]);
		}
		
		MPConstraint fee = solver.makeConstraint(0, C);
		for (int i=0; i<N; i++)
			fee.setCoefficient(t[i], c[i]);
		
		MPObjective obj = solver.objective();
		for (int i=0; i<N; i++)
			obj.setCoefficient(t[i], f[i]);
		obj.setMaximization();
		
		solver.solve();
		System.out.println("obj = " + obj.value());
		System.out.print("t:");
		for (int i=0; i<N; i++)
			System.out.print(" " + solver.lookupVariableOrNull("t"+i).solutionValue());
	}
	
	public static void main(String[] args) {
		productionPlanningMIP model = new productionPlanningMIP();
		model.readData("./data/productplaning/test_9");
		model.stateModel();
	}
}
