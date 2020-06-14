package BTL_Productplaning;
import localsearch.constraints.basic.*;
import localsearch.functions.basic.FuncMult;
import localsearch.functions.basic.FuncPlus;
import localsearch.functions.conditionalsum.ConditionalSum;
import localsearch.functions.max_min.Max;
import localsearch.functions.sum.SumFun;
import localsearch.model.ConstraintSystem;
import localsearch.model.IConstraint;
import localsearch.model.IFunction;
import localsearch.model.LocalSearchManager;
import localsearch.model.VarIntLS;
import localsearch.search.TabuSearch;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Scanner;

public class productionPlanning {
	public int N, A, C;
	public int[] a;
	public int[] c;
	public int[] m;
	public int[] f;
	int F1 = 0; // tong so loi nhuan thu duoc
	 
	LocalSearchManager ls;
	ConstraintSystem S;
	VarIntLS[] x;
	VarIntLS[] t;
	IFunction F;
	
	public productionPlanning() {}
	
	
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
			
			
		}
		catch (Exception ex) {
			ex.printStackTrace();
		}
	}
	public int[] greedy() {
		int[] X = new int[N];
		for(int i =0 ; i< X.length; i++)
			X[i] = 0;
		int k = 0;
		int c1 = this.C;
		int a1 = this.A;
		while((c1>0 && a1>0) || k < N-1 ) {
			X[k] = Math.min((int) (c1/this.c[k]), a1/this.a[k]);
			c1 -= X[k]*c[k];
			a1 -= X[k]*a[k];
			k++;
			
		}
		return X;
	}
	public double[] fca() {
		double[] fca = new double[N];
		for(int i = 0; i < N; i++) {
			fca[i] = f[i]*1.0/(c[i]*a[i]);	
			
		}
		return fca;
	}
	
	public int max_index(double [] fca) {
		double max = -1;
		int index = 0;		
		for(int i = 0; i < N; i++) {
			if(fca[i] > max) {
				max = fca[i];
				index = i;
			}
		}
		return index;
	}
	public int[] greedysolution() {
		double[] fca = new double[N];
		int[] x1 = new int[N];
		int[] t1 = new int[N];
		fca = fca();
		/*for(int  i= 0; i< N; i++) {
			System.out.println(fca[i]);
		}*/
		int A1 = A;
		int C1 = C;
		for(int i = 0; i < N; i++) {
			//System.out.println("i = " + i);
			int index = max_index(fca);
			//System.out.println("index = " + index);
			fca[index] = 0;
			
			if((m[index]*a[index] <=A1) &&(c[index]*m[index] <= C1)) {
				x1[index] = 1;
				F1 = F1 + m[index]*f[index];
				
				A1 -= m[index]*a[index];
				
				C1 -= c[index]*m[index];
				int dem = 0;
				while(A1 > 0 && C1 > 0) {
					if(a[index] <= A1 && c[index] <= C1) {
						
						dem++;
						
						F1 += f[index];
						//System.out.println(A1);
						A1 -= a[index];
						//System.out.println(A1);
						C1 -= c[index];
					}
					else {
						break;
					}
				}
				t1[index] = m[index] + dem;
			}
			else{
				x1[index] = 0;
				t1[index] = 0;
			}
			
		}
		return t1;
		
	}
		
	public void stateModel() {
		ls = new LocalSearchManager();
		S = new ConstraintSystem(ls);
		int X[] = new int[this.N];
		X = greedysolution();
		//x = new VarIntLS[N];
		t = new VarIntLS[N];
		int maxcount = 0;
		for(int i = 0; i < N; i ++ ) {
			int m1 = (int) (A/ a[i]);
			int m2 = (int)(C/ c[i]);
			int max = Math.max(m1, m2);
			if(max > maxcount) {
				maxcount = max;
			}
	
		}
		for(int i = 0 ; i < N; i ++) {
			//x[i] = new VarIntLS(ls, 0,1);
			t[i] = new VarIntLS(ls, 0, maxcount );
			t[i].setValue(X[i]);
		}
		// constraint dien tich
		FuncMult[] area = new FuncMult[this.N];
		for(int i  =0 ; i < this.N; i++) {
			area[i] = new FuncMult(t[i], this.a[i]);
		}
		FuncPlus ar = new FuncPlus(area[0], area[1]);

		for(int i  = 2; i < this.N; i++) {
			ar = new FuncPlus(area[i], ar);
				
		}
		
		/*FuncMult[] area = new FuncMult[this.N];
		for(int i  =0 ; i < this.N; i++) {
			area[i] = new FuncMult(t[i], this.a[i]);
		}
		SumFun ar = new SumFun(area);
		*/
		S.post(new LessOrEqual(ar, this.A));
		
		
		//constraint x[i] = 0 => t[i] = 0
		//for(int i = 0 ; i< this.N; i ++)
		//	S.post(new Implicate(new IsEqual(x[i], 0), new IsEqual(t[i], 0)));
		
		//  constraint t[i] >= x[i]* m[i]
		for(int i = 0; i< this.N; i++)
			S.post(new OR(new IsEqual(t[i], 0), new LessOrEqual(m[i], t[i])));
		// tong so von bo ra < C
		FuncMult[] cost = new FuncMult[this.N];
		for(int i  =0 ; i < this.N; i++) {
			cost[i] = new FuncMult(t[i], this.c[i]);
		}
		//SumFun sumcost = new SumFun(cost);
		
		FuncPlus sumcost = new FuncPlus(cost[0], cost[1]);

		for(int i  = 2; i < this.N; i++) {
			sumcost = new FuncPlus(cost[i], sumcost);
				
		}
		S.post(new LessOrEqual(sumcost, this.C));
		
		
		// objective function
		FuncMult[] obj = new FuncMult[this.N];
		for(int i  =0 ; i < this.N; i++) {
			obj[i] = new FuncMult(t[i], this.f[i]);
		}
		//F  = new SumFun(obj);
		F= new FuncPlus(obj[0], obj[1]);

		for(int i  = 2; i < this.N; i++) {
			F = new FuncPlus(obj[i], F);
				
		}
		
		ls.close();
			
		
	}
	
	public void search(int maxiter) {
		//TabuNoHope searcher = new TabuNoHope(S, F, t, a, c,C, A);
		
		TabuSearchCustom searcher = new TabuSearchCustom(S, F, t);
		//HillClimbingConstraintThenFunctionNeighborhoodExplorer searcher = new HillClimbingConstraintThenFunctionNeighborhoodExplorer(S, F, t);
		searcher.search(maxiter,1000, 1000);
	}
	public void printSol() {
		for(int i =0 ; i < this.N; i++) {
			//System.out.print("X[" + i + "] = " + x[i].getValue() + " ");
			 System.out.print("T[" + i + "] = " + t[i].getValue() + " ");
		
		}
		System.out.println("\n");
		System.out.println("Max = " + F.getValue());
		
	}
	
	public static void main(String[] args) {
		productionPlanning app = new productionPlanning();
		String fn = "./data/productplaning/test_1";
		long time = System.currentTimeMillis();
		app.readData(fn);
		app.stateModel();
		app.search(1000);
		app.printSol();
		long time1 = System.currentTimeMillis() -time;
		System.out.println("time =  " + time1+ " s" );
		
		
	}
	
	

}
