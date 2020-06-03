N = [5 ,10, 20, 50 , 100, 200 ,300,  500, 1000, 5000, 10000]
A = [1000, 2000, 5000, 10000, 100000]
C = [20000, 50000, 100000 , 500000 ]
import random as rd
for i in range(len(N)):
    f = open("test_%d" %i, "w")
    n = N[i]
    a :int
    c :int
    if(N[i] < 100):
        a = A[0]
        c = C[0]
    elif(N[i]>=100 and N[i] < 500):
        a = A[1]
        c = C[1]
    elif(N[i] >= 500 and N[i] <= 1000 ):
        a =  A[2]
        c = C[2]
    elif(N[i] == 5000):
        a = A[3]
        c = C[3]
    else:
        a = A[-1]
        c = C[-1]
    f.write(str(n) + " " +str(a)+ " " + str(c) + "\n")
    for i in range(n):
        finess = rd.randint(int(1.5*c//n) , int(4*c//n))
        cost = rd.randint(int(finess//4), int(finess//2))
        area = rd.randint(int(a//(2*n)), int((2*a)//n))
        minnb = rd.randint(int(c//(cost*n)), int(c//(4*cost)))  
        f.write(str(area) + " " + str(finess) + " " + str(cost) + " " + str(minnb) + "\n")


    




