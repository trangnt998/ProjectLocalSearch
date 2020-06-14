package BTL_Productplaning;


import java.util.ArrayList;
import java.util.Random;

import javafx.util.Pair;
import localsearch.model.ConstraintSystem;
import localsearch.model.IConstraint;
import localsearch.model.IFunction;
import localsearch.model.VarIntLS;

public class TabuNoHope {
	class Move{
		int i;
		int v;
		public Move(int i, int v){
			this.i = i; this.v = v;
		}
	}
	int tabu[][];
	int tbl;
	IConstraint c;
	IFunction f;
	VarIntLS[] x;
	int N;// number of variables;
	int D;// max domain
	int bestViolations;
	int bestSolutions = 0 ;
	int[] bestparameter;
	
	// parameter of knapsack
	int[] xc, a;
	int C,A;
	Random R = new Random();
	int nic = 0;
	public TabuNoHope(IConstraint c, IFunction f, VarIntLS[] x , int[] a, int[] xc, int C, int A){
		this.A = A;
		this.C= C;
		this.a = a;
		this.xc = xc;
		this.x = x;
		this.bestparameter = new int[x.length];
		this.f = f;
		// assumption minValue of domain >= 0
		this.c = c;
		x = c.getVariables();
		N = x.length;
		D = 0;
		for(int i = 0; i < x.length; i++)
			D = D < x[i].getMaxValue() ? x[i].getMaxValue() : D;
		tabu = new int[N][D+1];
		for(int i = 0; i < N; i++)
			for(int v = 0;v <= D; v++)
				tabu[i][v] = -1;
		
	}

	private void restart(){
		for(int i = 0; i < N; i++){
			int v = R.nextInt(x[i].getMaxValue() - x[i].getMinValue()+1) + x[i].getMinValue();
			x[i].setValuePropagate(v);
		}
		if(c.violations()*10 - f.getValue() < bestViolations){
			bestViolations = c.violations()*10 - f.getValue();
		}
		if(c.violations() == 0 && f.getValue() > bestSolutions ) {
			bestSolutions = f.getValue();
			for(int i  = 0; i < bestparameter.length ; i++) {
				bestparameter[i] = x[i].getValue();
				
			}
		}
	}
	
	private int getAssignDelta(int i1, int v1, int i2, int v2) {
		int old = c.violations() * 10 - f.getValue();
		int oldX1 = x[i1].getValue(), oldX2= x[i2].getValue();
		x[i1].setValuePropagate(v1);
		x[i2].setValuePropagate(v2);
		int delta = c.violations() * 10 - f.getValue() - old;
		x[i1].setValuePropagate(oldX1);
		x[i2].setValuePropagate(oldX2);
		
		return delta;
	}
	
	private int calcY1(int i) {
		int sumC = 0, sumA = 0;
		for (int j=0; j<N; j++) {
			sumC += xc[j] * x[j].getValue();
			sumA += a[j] * x[j].getValue();
		}
		
		int m = Integer.max(C - sumC, A - sumA);
		if(m <0) {
			m = 0;
		}
		
		return Integer.max(0, x[i].getValue() - m);
	}
	
	public void search(int maxIter, int tblen, int maxStable){
		this.tbl = tblen;
		bestViolations = c.violations()*10 - f.getValue();
		ArrayList<Pair<Move, Move>> cand = new ArrayList<>();
		nic = 0;
		//for(int it = 0; it <= maxIter; it++){
		int it = 0;
		while(it <= maxIter){
			int minDelta = Integer.MAX_VALUE;
			cand.clear();
			for(int i = 0; i < N; i++){
				int i1 = i, i2 = (i+1) % N;
//				for(int  v= x[i].getMinValue(); v <= x[i].getMaxValue(); v++)
					if(x[i1].getValue() != 0){
						int y1 = calcY1(i1), y2 = x[i1].getValue() + x[i2].getValue() - y1;
						int delta = getAssignDelta(i1, y1, i2, y2);							
						if(tabu[i1][y1] <= it || delta + c.violations()*10 - f.getValue() < bestViolations){
							if(delta < minDelta){
								cand.clear();
								cand.add(new Pair<>(new Move(i1, y1), new Move(i2, y2)));
								minDelta = delta;
							}else if(delta == minDelta){
								cand.add(new Pair<>(new Move(i1, y1), new Move(i2, y2)));
							}
						}
				}
			}
			if(cand.size() == 0) {
				
				System.out.println("Reach local optimum"); break;
				
			
				
			}
			
			Pair<Move, Move> pp = cand.get(R.nextInt(cand.size()));
			Move m1 = pp.getKey();
			Move m2 = pp.getValue();
			x[m1.i].setValuePropagate(m1.v);
			x[m2.i].setValuePropagate(m2.v);
			if(c.violations() == 0 && f.getValue() > bestSolutions ) {
				bestSolutions = f.getValue();
				for(int i  = 0; i < bestparameter.length ; i++) {
					bestparameter[i] = x[i].getValue();
					
				}
			}
			tabu[m1.i][m1.v] = it + tbl;// dua move(i,v) vao DS tabu
			
			if(c.violations()*10 - f.getValue() < bestViolations){
				bestViolations = c.violations()*10 - f.getValue();
				nic = 0;
			}else{
				nic++;
				if(nic >= maxStable){
					restart();
				}
			}
			System.out.println("Step " + it + " violations = " + c.violations()
					+ ", bestViolations = " + bestViolations +", F = " + f.getValue());
			it++;
		}
		for(int i = 0 ; i < bestparameter.length; i ++) {
			x[i].setValuePropagate(bestparameter[i]);
			
		}
		System.out.println("solution =" + bestSolutions);
		
		
	}
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
