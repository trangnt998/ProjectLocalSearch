from __future__ import print_function
from ortools.linear_solver import pywraplp
import numpy as np
# Create the mip solver with the CBC backend.
solver = pywraplp.Solver('simple_mip_program',
                         pywraplp.Solver.CBC_MIXED_INTEGER_PROGRAMMING)


status = solver.Solve()

N = 3
A = 10
C = 10
a = [1,1,1]
c = [2,2,2]
f = [1,5,10]
m = [1]*N
a = np.array(a, dtype=float)
c = np.array(c, dtype = float)
f = np.array(f, dtype = int)
m = np.array(m, dtype = int)
M = int(min(max(A/i for i in a), max(C/cx for cx in c)))

print('N = ' + str(N ) +   ' A = ' +str(A) + ' Cost C =  ' + str(C), ' M = '+ str(M))
print('a = ', a)
print('c = ', c)
print('f = ', f)
print('m = ', m)


# khoi tao cac bien quuyet dinh
x = np.empty(N, dtype= object)
t = np.empty(N, dtype = object)
infinity = solver.infinity()
for i in range(N):
    x[i] = solver.IntVar(0, 1,'X[%d]' %i)
for i in range(N):
    t[i] = solver.IntVar(0.0, M, 'T[%d]' %i)
print('Number of variables =', solver.NumVariables())

#Define the constraints
M = 10 # M la so rat lon
#1 sum Area < A
constraintArea = solver.Constraint(0, A, '')
for j in range(N):
    constraintArea.SetCoefficient(t[j], a[j])
#solver.Add(np.sum(t[i]*a[i] for i in range(N)) <= A
#2 x[i] = 0 => t[i] = 0
#3 so san pham i neu san xuat se lon hon mi

for i in range(N):
    solver.Add(-M*x[i] <= t[i])
    solver.Add(M*x[i] >= t[i])
for i in range(N):
    solver.Add(t[i] >= x[i]*m[i])
#4 tong so von bo ra < C
contraintCost  = solver.Constraint(0, C, '')
for j in range(N):
    contraintCost.SetCoefficient(t[j], c[j])
#solver.Add(np.sum(t[i]*c[i] for i in range(N)) <= C)

print('Number of constraints =', solver.NumConstraints())
    
# Maximize t[i]*f[i].
objective = solver.Objective()
for j in range(N):
    objective.SetCoefficient(t[j], float(f[j]))
objective.SetMaximization()

status = solver.Solve()

if status == pywraplp.Solver.OPTIMAL:
    print('Solution:')
    print('Objective value =', solver.Objective().Value())
    for i in range(N):
        print(x[i].name(), ' = ' ,x[i].solution_value() , end =' ')
    print('\n')
    for i in range(N):
        print(t[i].name(),' = '  ,t[i].solution_value() , end = ' ')

              
else:
    print('The problem does not have an optimal solution.')
