import random
import csv
from collections import namedtuple
Item = namedtuple("Item", ['index', 'area' , 'fitness', 'cost', 'minnb'])
cross_rate = 0.6  # crossover rate
u_rate = 0.6  # uniform crossover rate aka chance of gene to swap
eli = 2  # number of elites
mutation_rate = 0.1
pop_size = 100
max_gen = 100
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


def repairgene(chromosome, items):
    length  = len(chromosome)
    for i in range(length):
        if(chromosome[i] < items[i].minnb):
            chromosome[i] = 0
    return chromosome
def generate(nbitems,totalarea,totalcost, items):
    chromosome = [0]*nbitems
    c = totalcost
    a = totalarea
    for i in range(nbitems):
        temp1 = int(c/items[i].cost)
        temp2 = int(a/ items[i].area)
        temp = min(temp1,temp2)
        if random.random() < 0.5:
            chromosome[i] = random.randint(items[i].minnb, int(temp))
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
    # sorted_pop = sorted(pop, key=lambda x: fitness(x), reverse=True)
    sum_fits = sum(fitness(x, nbitems,totalarea,totalcost, items) for x in pop)
    chosen = []
    for _ in range(2):
        r = random.random() * sum_fits
        sum_temp = 0
        for i in pop:
            sum_temp += fitness(i,nbitems,totalarea,totalcost, items)
            if sum_temp > r:
                chosen.append(i)
                break
    return chosen

def crossover(dad, mom, nbitems, items):
    # child1 = dad
    # child2 = mom
    for i in range(nbitems):
        if random.random() < u_rate:
            dad[i], mom[i] = mom[i], dad[i]
    # if random.random() < mutation_rate:
    #     mutate(dad)
    #     mutate(mom)
    # # print("done crossover")
    dad = repairgene(dad, items)
    mon = repairgene(mom, items)
    return dad, mom

def mutate(chromosome, chance, items ):
    length = len(chromosome)
    for i in range(length):
        if random.random() < chance:
            swap_indx = random.randint(0, length - 2)
            if swap_indx >= i:
                swap_indx += 1
                chromosome[i], chromosome[swap_indx] = chromosome[swap_indx], chromosome[i]
    chromosome = repairgene(chromosome, items)
    return chromosome,

def elites(sorted_pop):
    return [sorted_pop[i]for i in range(eli)]

def new_population(pop, nbitems,totalarea,totalcost, items):
    new_pop = []
    # print("new pop: {0}".format(new_pop))
    elite_group = elites(pop)
    # print("elites: {0}".format(elite_group))
    new_pop.extend(elite_group)
    # print("new pop with elites: {0}".format(new_pop))
    pop = [x for x in pop if x not in elite_group]
    # print(len(new_pop))
    while len(new_pop) < pop_size:
        # 18+
        parents = roulette_selection(pop, nbitems,totalarea,totalcost, items)
        # print(parents)
        dad = parents[0]
        child1 = dad
        mom = parents[1]
        # if child1 == child2:
        child2 = mom
        if random.random() < cross_rate:
            new_children = crossover(dad, mom, nbitems, items)
            child1 = new_children[0]
            child2 = new_children[1]
            mutate(child1, mutation_rate, items)
            mutate(child2, mutation_rate, items)
        if child1 == child2 and fitness(child1, nbitems,totalarea,totalcost, items) != 0:
            new_pop.append(child1)
        elif child1 != child2:
            if fitness(child1 ,nbitems,totalarea,totalcost, items) != 0:
                new_pop.append(child1)
            if fitness(child2 ,nbitems,totalarea,totalcost, items) != 0:
                new_pop.append(child2)
        # elif child1 != child2 and fitness(child2) != 0:
        #     new_pop.append(child2)
    # print("done new_pop")
    # print(new_pop)
    return new_pop


# get the total weight of each gene from chromosome
# aka gene = 4 => total weight = 4 * item.weight
def get_weight(chromosome ,items):
    length = len(chromosome)
    c_weight = []
    for i in range(length):
        w = chromosome[i]*items[i].cost
        c_weight.append(w)
    # print("done get_weight")
    return c_weight

def max_item(nbitems, items, totalcost, totalarea):
    length = nbitems
    list_max = []
    for i in range(length):
        num = min(int(totalcost/items[i].cost), int(totalarea/items[i].area))
        list_max.append(num)
    # print("done max_item")
    return list_max

def GA(nbitems,totalarea,totalcost, items):
    generation = 1
    pop = [generate(nbitems,totalarea,totalcost, items) for _ in range(pop_size)]
    # print("initilaized pop: {0}".format(pop))
    # pop = sorted(pop, key=lambda x: fitness(x), reverse=True)
    # while pop[1] != pop[2] != pop[3]:
    for _ in range(max_gen):
        print(generation)
        pop = sorted(pop, key=lambda x: fitness(x, nbitems,totalarea,totalcost, items), reverse=True)
        print(pop)
        fit = [fitness(i, nbitems,totalarea,totalcost, items) for i in pop]
        print(fit)

        pop = new_population(pop, nbitems,totalarea,totalcost, items)
        print(pop)

        print("----------------")
        generation += 1
    # print("am i fucked?")
    print("Best gene:" + str(pop[0]))
    print("Best Finness: ",fitness(pop[0],nbitems,totalarea,totalcost,items))











import sys
import time
if __name__ == '__main__':
    import sys
    
    if len(sys.argv) > 1:
        time1 =  time.process_time()
        file_location = sys.argv[1].strip()
        with open(file_location, 'r') as input_data_file:
            input_data = input_data_file.read()
        
        print(solve_it(input_data))
        time2  = time.process_time()
        print(str(time2 - time1) + 's')
    
    else:
        print('This test requires an input file.  Please select one from the data directory. (i.e. python solver.py ./data/gc_4_1)')
    