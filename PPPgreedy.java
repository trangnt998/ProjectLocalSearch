package project;

public class PPPgreedy {
	int N = 3; //so loai nong san
	int A = 10; // tong dien tich thua ruong
	int C = 10; // so von toi da co duoc
	int F = 0; // tong so loi nhuan thu duoc
	int a[] = {1,2,3}; // a[i] la dien tich can de san xuat 1 san pham loai i
	int c[] = {1,3,2}; // c[i] la chi phi san xuat cua nong san c[i]
	int f[] = {4,2,3}; // f[i] la loi nhuan thu duoc khi san xuat san pham loai i
	int m[] = {2,2,2}; // m[i] la so san pham toi thieu can san xuat khi quyet dinh san xuat san pham loai i
	int t[]; // t[i] la so san pham loai i se duoc san xuat
	int x[]; // x[i] =1 neu san pham loai i duoc san xuat, 0 nguoc lai
	
	public PPPgreedy() {
		x = new int[3];
		t = new int[3];
	}

	public double[] fc() {
		double[] fc = new double[N];
		for(int i = 0; i < N; i++) {
			fc[i] = f[i]*1.0/c[i];	
			
		}
		return fc;
	}
	
	public int max_index(double [] fc) {
		double max = -1;
		int index = 0;		
		for(int i = 0; i < N; i++) {
			if(fc[i] > max) {
				max = fc[i];
				index = i;
			}
		}
		return index;
	}
	
	public void solution() {
		double[] fc = new double[N];
		fc = fc();
		for(int  i= 0; i< N; i++) {
			System.out.println(fc[i]);
		}
		int A1 = A;
		int C1 = C;
		for(int i = 0; i < N; i++) {
			System.out.println("i = " + i);
			int index = max_index(fc);
			fc[index] = 0;
			
			if((m[index]*a[index] <=A1) &&(c[index]*m[index] <= C1)) {
				x[index] = 1;
				F = F + m[index]*f[index];
				A1 -= m[index]*a[index];
				C1 -= c[index]*m[index];
				int dem = 0;
				while(A1 > 0 && C1 > 0) {
					if(a[index] <= A1 && c[index] <= C1) {
						dem++;
						F += f[index];
						A1 -= a[index];
						C1 -= c[index];
					}
				}
				t[index] = m[index] + dem;
			}
			else{
				x[index] = 0;
				t[index] = 0;
			}
			
		}
		
	}
	public void printSol() {
		System.out.println("answer: " );
		for(int i = 0; i < N; i++) {
			System.out.print("x[" + i + "]" +"="+ x[i] + ", " + "t[" + i+ "]" +"=" +t[i]);
		}
		System.out.println("Loi nhuan thu duoc: " + F);
	}
	
	public static void main(String[] args) {
		PPPgreedy P = new PPPgreedy();
		P.solution();
		P.printSol();
		
	}
}
