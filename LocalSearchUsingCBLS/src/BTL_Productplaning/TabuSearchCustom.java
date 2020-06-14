package BTL_Productplaning;


import java.util.ArrayList;
import java.util.Random;

import localsearch.model.ConstraintSystem;
import localsearch.model.IConstraint;
import localsearch.model.IFunction;
import localsearch.model.VarIntLS;

public class TabuSearchCustom {
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
	Random R = new Random();
	int nic = 0;
	public TabuSearchCustom(IConstraint c, IFunction f, VarIntLS[] x){
		
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

	
	public void search(int maxIter, int tblen, int maxStable){
		this.tbl = tblen;
		bestViolations = c.violations()*10 - f.getValue();
		ArrayList<Move> cand = new ArrayList<>();
		nic = 0;
		//for(int it = 0; it <= maxIter; it++){
		int it = 0;
		while(it <= maxIter){
			int minDelta = Integer.MAX_VALUE;
			cand.clear();
			for(int i = 0; i < N; i++){
				for(int  v= x[i].getMinValue(); v <= x[i].getMaxValue(); v++)
					if(x[i].getValue() != v){
					int delta = c.getAssignDelta(x[i], v)*10 - f.getAssignDelta(x[i], v)*1;							
					if(tabu[i][v] <= it || delta + c.violations()*10 - f.getValue() < bestViolations){
						if(delta < minDelta){
							cand.clear();
							cand.add(new Move(i,v));
							minDelta = delta;
						}else if(delta == minDelta){
							cand.add(new Move(i,v));
						}
					}
				}
			}
			if(cand.size() == 0) {
				
				System.out.println("Reach local optimum"); break;
				
			
				
			}
			
			Move m = cand.get(R.nextInt(cand.size()));
			x[m.i].setValuePropagate(m.v);
			if(c.violations() == 0 && f.getValue() > bestSolutions ) {
				bestSolutions = f.getValue();
				for(int i  = 0; i < bestparameter.length ; i++) {
					bestparameter[i] = x[i].getValue();
					
				}
			}
			tabu[m.i][m.v] = it + tbl;// dua move(i,v) vao DS tabu
			
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
