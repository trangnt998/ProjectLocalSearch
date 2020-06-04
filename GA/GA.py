import random
import csv
from collections import namedtuple
Item = namedtuple("Item", ['index', 'area' , 'fitness', 'cost', 'minnb'])
cross_rate = 0.6 # crossover rate
u_rate = 0.6  # uniform crossover rate aka chance of gene to swap
eli = 1  # number of elites
mutation_rate = 0.05
pop_size = 50
max_gen = 100
fiss = namedtuple("Fitness", ['maxf' , 'meanf'])
histogram_fit = []

def solve_it(input_data):
    items = []
    nbitems: int  # so loai san pham
    totalarea: int # dien tich
    totalcost: int # tong chi phi

    lines = input_data.split('\n')

    first_line = lines[0].split()
    nbitems = int(first_line[0])
    totalarea = float(first_line[1])
    totalcost = int(first_line[2])
    
    for i in range(1, nbitems +1):
        line = lines[i]
        parts = line.split()
        items.append(Item(i-1, int(parts[0]), int(parts[1]), int(parts[2]), int(parts[3])))
    

    GA(nbitems,totalarea,totalcost, items)


def repairgene(chromosome, items, totalcost, totalarea):
    length  = len(chromosome)
    cost, area = get_weight(chromosome, items)
    while(cost > totalcost or area > totalarea):
        r = random.random()
        idx = random.randint(0,length-1)
        if(chromosome[idx] > 0):
            chromosome[idx] = int(r*chromosome[idx])
        cost, area = get_weight(chromosome,items)
    for i in range(length):
        if(chromosome[i] < items[i].minnb):
            chromosome[i] = 0
 
    return chromosome
def generate(nbitems,totalarea,totalcost, items):
    chromosome = [0]*nbitems
    maxitem1 = max_item(nbitems,items,totalcost,totalarea)
    for i in range(nbitems):
        if random.random() > 0.5:
            chromosome[i] = random.randint(items[i].minnb,  maxitem1[i])
    chromosome = repairgene(chromosome, items, totalcost, totalarea)
    return chromosome
def fitness(chromosome: list, nbitems,totalarea,totalcost, items):
    values = 0
    weights= 0
    areas = 0
    for i in range(len(chromosome)):
        values += chromosome[i]*items[i].fitness
        weights += chromosome[i]*items[i].cost
        areas += chromosome[i]*items[i].area
    if weights > totalcost or areas > totalarea:
        return 0
    # # print("done fitness")
    return values
def roulette_selection(pop, nbitems,totalarea,totalcost, items):
    #sorted_pop = sorted(pop, key=lambda x: fitness(x,  nbitems,totalarea,totalcost, items), reverse=True)
    sum_fits = sum(fitness(x, nbitems,totalarea,totalcost, items) for x in pop)
    chosen = []
    for _ in range(2):
        r = random.random() * sum_fits
        sum_temp = 0
        for i in pop:
            sum_temp += fitness(i,nbitems,totalarea,totalcost, items)
            if sum_temp >= r:
                chosen.append(i)
                break
    return chosen
def selection( numselect, pop, nbitems,totalarea,totalcost, items):
    #sorted_pop = sorted(pop, key=lambda x: fitness(x,  nbitems,totalarea,totalcost, items), reverse=True)
    sum_fits = sum(fitness(x, nbitems,totalarea,totalcost, items) for x in pop)
    chosen = []
    for _ in range(numselect):
        r = random.random() * sum_fits
        sum_temp = 0
        for i in pop:
            sum_temp += fitness(i,nbitems,totalarea,totalcost, items)
            if sum_temp >= r:
                chosen.append(i)
                break
    return chosen

def crossover(dad, mom, nbitems, items, totalcost, totalarea):
    # child1 = dad
    # child2 = mom
    r = random.randint(0 , nbitems-1)

    child1 = dad[:r]
    child1.extend(mom[r:])
    child2 = mom[:r]
    child2.extend(dad[r:])
    child1 = repairgene(child1, items, totalcost, totalarea)
    child2 = repairgene(child2,items, totalcost, totalarea)
    return child1, child2

def mutate(chromosome,chance, items, totalcost, totalarea ):
    length = len(chromosome)
    up = max_item(length,items,totalarea,totalarea)
    for i in range(length):
        if(random.random() < chance):          
            r = random.randint(0, up[i])
            chromosome[i] = r
    chromosome = repairgene(chromosome, items ,totalcost, totalarea)
    return chromosome

def elites(sorted_pop):
    return [sorted_pop[i]for i in range(eli)]

def new_population(pop, nbitems,totalarea,totalcost, items):
    new_pop = []
    new_pop.extend(pop)
    elite_group = elites(pop)

    # crossover
    for _ in range(len(pop)):
        parents = roulette_selection(pop, nbitems,totalarea,totalcost, items)

        dad = parents[0]
        mom = parents[1]
        child1 = dad
        child2 = mom
        #print(parents)
        if(random.random() < cross_rate):
            child = crossover(dad, mom, nbitems, items, totalcost, totalarea)
            child1 = child[0]
            child2 = child[1]
        new_pop.append(child1)
        new_pop.append(child2)

    # mutation
    mutation_list = []
    for ividual in new_pop:
        new_ividual = mutate(ividual, mutation_rate, items, totalcost, totalarea)
        mutation_list.append(new_ividual)
        new_pop.remove(ividual)
    new_pop.extend(mutation_list)
    #print(new_pop)
    # selection
    new_pop = selection(pop_size -eli, new_pop, nbitems,totalarea,totalcost, items)
    new_pop.extend(elite_group)
    return new_pop



# get the total weight of each gene from chromosome
# aka gene = 4 => total weight = 4 * item.weight
def get_weight(chromosome ,items):
    length = len(chromosome)
    c_weight = 0
    c_area = 0 
    for i in range(length):
        w = chromosome[i]*items[i].cost
        a = chromosome[i]*items[i].area
        c_weight += w
        c_area += a
    # print("done get_weight")
    return c_weight, c_area

def max_item(nbitems, items, totalcost, totalarea):
    length = nbitems
    list_max = []
    for i in range(length):
        num = min(int(totalcost/items[i].cost), int(totalarea/items[i].area))
        list_max.append(num)
    # print("done max_item")
    return list_max

from statistics import mean
def GA(nbitems,totalarea,totalcost, items):
    max_fit = 0
    optimal = []
    generation = 1
    pop = [generate(nbitems,totalarea,totalcost, items) for _ in range(pop_size)]
    # print("initilaized pop: {0}".format(pop))
    # pop = sorted(pop, key=lambda x: fitness(x), reverse=True)
    # while pop[1] != pop[2] != pop[3]:
    for _ in range(max_gen):
        print('Vong Lap thu: ' +str(generation))

        pop = sorted(pop, key=lambda x: fitness(x, nbitems,totalarea,totalcost, items), reverse=True)
        #print(pop)
        fit = [fitness(i, nbitems,totalarea,totalcost, items) for i in pop]
        
        histogram_fit.append(fiss(fit[0], mean(fit)))
        
        print("the max fitness of %d-th =  " %generation + str(fit[0]) )
        if(max_fit <= fit[0]):
            optimal = pop[0]
            max_fit = fit[0]
        print("The best Fitness = " ,max_fit)
        print("The fitness:")
        print(fit)

        if(generation > 3):
            if(histogram_fit[generation-1].maxf == histogram_fit[generation-2].maxf == histogram_fit[generation-3].maxf == max_fit ):
                break

        pop = new_population(pop, nbitems,totalarea,totalcost, items)
        


        print("----------------")
        generation += 1
    # vong lap cuoi
    if(len(histogram_fit) == max_gen):
        pop = sorted(pop, key=lambda x: fitness(x, nbitems,totalarea,totalcost, items), reverse=True)
        fit = [fitness(i, nbitems,totalarea,totalcost, items) for i in pop]
        print("the max fitness of %d-th =  " %generation + str(fit[0]) )
        histogram_fit.append(fiss(fit[0], mean(fit)))
        if(max_fit < fit[0]):
            optimal = pop[0]
            max_fit = fit[0]
        print("The fitness:")
        print(fit)
    print(" The optimal gen :" , optimal)
    print("The best Fitness = " + str(max_fit))

import matplotlib.pyplot as plt
import numpy as np
def visualize(histogram_fit):
    num_generations = len(histogram_fit)
    fitness_history_mean = []
    fitness_history_max = []
    for i in range(num_generations):
        fitness_history_mean.append(histogram_fit[i].meanf)
        fitness_history_max.append(histogram_fit[i].maxf)
        
    plt.plot(list(range(num_generations)), fitness_history_mean, label = 'Mean Fitness')
    plt.plot(list(range(num_generations)), fitness_history_max, label = 'Max Fitness')
    plt.legend()
    plt.title('Fitness through the generations')
    plt.xlabel('Generations')
    plt.ylabel('Fitness')
    plt.show()


    
    











import sys
import time
if __name__ == '__main__':
    import sys
    
    #if len(sys.argv) > 1:
    time1 =  time.process_time()
    file_location = 'test_2'
        #file_location = sys.argv[1].strip()
    with open(file_location, 'r') as input_data_file:
        input_data = input_data_file.read()
        
    print(solve_it(input_data))
    time2  = time.process_time()
    print(str(time2 - time1) + 's')
    visualize(histogram_fit)
    
    #else:
    #    print('This test requires an input file.  Please select one from the data directory. (i.e. python solver.py ./data/gc_4_1)')
    