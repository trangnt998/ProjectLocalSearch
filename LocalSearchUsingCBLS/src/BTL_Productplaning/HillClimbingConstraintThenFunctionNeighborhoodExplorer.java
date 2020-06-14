package BTL_Productplaning;

import java.util.ArrayList;
import java.util.Random;
import java.util.function.IntFunction;

import javax.swing.Icon;

import localsearch.constraints.alldifferent.AllDifferent;
import localsearch.constraints.basic.AND;
import localsearch.functions.basic.ConstraintViolations;
import localsearch.functions.basic.FuncMult;
import localsearch.functions.basic.FuncPlus;
import localsearch.model.ConstraintSystem;
import localsearch.model.IConstraint;
import localsearch.model.IFunction;
import localsearch.model.LocalSearchManager;
import localsearch.model.VarIntLS;

public class HillClimbingConstraintThenFunctionNeighborhoodExplorer {
	private IFunction f;
	private  IConstraint c;
	private VarIntLS[] x;
	private Random R = new Random();
	
	class Move{
	int i; int v;
	public Move(int i , int v) {
		this.i = i; 
		this.v = v;
		
	}
	}
	
	public HillClimbingConstraintThenFunctionNeighborhoodExplorer(IConstraint c, IFunction f,VarIntLS[]  x) {
		this.c = c;
		this.f = f; 
		this.x = x;
	}
	
	public void exporeNeighborhood(ArrayList<Move> cand) {
		cand.clear();
		int maxDeltaF  = Integer.MIN_VALUE;
		int minDeltaC = Integer.MAX_VALUE;
		
		for( int i = 0 ; i < x.length; i++) {
			for(int v = x[i].getMinValue(); v <= x[i].getMaxValue(); v++) {
				if (v == x[i].getValue())
					continue;
				int deltaF = f.getAssignDelta(x[i], v);
				int deltaC = c.getAssignDelta(x[i], v);
				if(deltaC < 0 || (deltaC == 0 && deltaF > 0)) {
					if(deltaC < minDeltaC || deltaC == minDeltaC && deltaF > maxDeltaF) {
						cand.clear();
						cand.add(new Move(i,v));
						minDeltaC  = deltaC;
						maxDeltaF = deltaF;
						
					}else if(deltaC == minDeltaC && deltaF == maxDeltaF){
						cand.add(new Move(i,v));
				}
				
			}
		}
		
	}

}
	public void printSol(){
		for(int i = 0; i < x.length; i++) System.out.print("X[" + i + "] = " + x[i].getValue() + " ");
	}
	public void search(int maxIter) {
		int it = 0;
		ArrayList<Move> cand = new ArrayList<>();
		System.out.println("Init F = " + f.getValue());
		while(it < maxIter){
			exporeNeighborhood(cand);
			/*for(Move u : cand) {
				System.out.println(" i = " + u.i + " v = " + u.v );
			} */
			if(cand.size() == 0){
				System.out.println("Reach local optimum"); break;
			}
			Move m = cand.get(R.nextInt(cand.size()));
			//System.out.println(m.i);
			x[m.i].setValuePropagate(m.v);
			it++ ;
			System.out.println("Step " + it + ", c = " + 
					c.violations() + ", f = " + f.getValue());
			
			}
		
		
	}
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		LocalSearchManager mgr = new LocalSearchManager();
		VarIntLS[] X = new VarIntLS[6];
		for(int i = 0; i <= 5; i++)
			X[i] = new VarIntLS(mgr,1,10);
		//ConstraintSystem S = new ConstraintSystem(mgr);
		
		ArrayList<IConstraint> list = new ArrayList<IConstraint>();
		list.add(new AllDifferent(X));
		// create and add other constraints here
		
		IConstraint[] arr = new IConstraint[list.size()];
		for(int i = 0;i < list.size(); i++)
			arr[i]= list.get(i);
		
		
		IConstraint S = new AND(arr);// we can add a constructor of AND with ArrayList parameters (list)
		
		IFunction f = new FuncPlus(new FuncMult(X[0], 3), new FuncMult(X[4], 5));
		
		//IFunction cv = new ConstraintViolations(S);
		//IFunction f1 = new FuncMult(cv, 1000);
		//IFunction f2 = new FuncMult(f, 1);
		//IFunction F = new FuncPlus(f1, f2);
		
		//IFunction F = new FuncPlus(new FuncMult(new ConstraintViolations(S), 1000), new FuncMult(f, 1));
		
		mgr.close();
		System.out.println("S = " + S.violations() + ", f = " + f.getValue());
		//+ ", cv = " + 
		//cv.getValue() + ", F = " + F.getValue() + ", f1 = " + f1.getValue() + ", f2 = " + f2.getValue());
		
		HillClimbingConstraintThenFunctionNeighborhoodExplorer searcher = new HillClimbingConstraintThenFunctionNeighborhoodExplorer(S, f, X);
		
		//searcher.search1(10);
		searcher.search(100);
		searcher.printSol();
		
	}

	
}
