import numpy as np
import matplotlib.pyplot as plt

max_makespan = 0
min_makespan = 2e6
max_cost = 0
min_cost = 2e6
for i in range(600):
    path = 'D:\\000000000WPS\\8创新实践3\\DynamicWorkFlowSchedule_2023\\src\\main\\resources\\result\\result_cmswc_' + str(i) + '.txt'
    data1 = np.loadtxt(path)
    makespan = np.array([x for x,y in data1])
    cost = np.array([y for x,y in data1])
    max_m = np.max(makespan)
    min_m = np.min(makespan)
    max_c = np.max(cost)
    min_c = np.min(cost)
    if max_m > max_makespan:
        max_makespan = max_m
    if min_m < min_makespan:
        min_makespan = min_m
    if max_c > max_cost:
        max_cost = max_c
    if min_c < min_cost:
        min_cost = min_c

delta_m = max_makespan - min_makespan
delta_c = max_cost - min_cost
print(f'max makespan: {max_makespan}')
print(f'min makespan: {min_makespan}')
print(f'max makespan: {max_cost}')
print(f'min makespan: {min_cost}')
print(f'delta makespan: {delta_m}')
print(f'delta cost: {delta_c}')
def calculateHV(makespan, cost):
    hv = 0
    hv += (1.1 - makespan[0])*(1.1-cost[0])
    for i in range(1,len(makespan)):
        hv += (1.1 - makespan[i]) * (cost[i - 1] - cost[i])
        if hv < 0:
            print(hv)
    return hv

HV = []
for i in range(100,600):
    path = 'D:\\000000000WPS\\8创新实践3\\DynamicWorkFlowSchedule_2023\\src\\main\\resources\\result\\result_cmswc_' + str(i) + '.txt'
    data1 = np.loadtxt(path)
    data1 = sorted(data1, key=lambda x: x[0]) 
    # print(data1)
    makespan = np.array([x for x,y in data1])
    cost = np.array([y for x,y in data1])
    # if i%20 == 0:
    #     plt.plot([x for x,y in data1], [y for x,y in data1])
    makespan = (makespan - min_makespan)/delta_m
    cost = (cost - min_cost)/delta_c
    HV.append(calculateHV(makespan, cost))
print(f'DNSGA-II HV: {HV[-1]}')

   
# x = list(range(500))
# plt.title('Scatter Plot')
# plt.plot(x,HV,'ro')
# plt.xlabel('task_i')
# plt.ylabel('hv')
# plt.savefig('images\\scatter.png')

for i in range(0,100):
    path = 'D:\\000000000WPS\\8创新实践3\\DynamicWorkFlowSchedule_2023\\src\\main\\resources\\result\\result_cmswc_' + str(i) + '.txt'
    data1 = np.loadtxt(path)
    if i%20==0:
        plt.plot([x for x,y in data1], [y for x,y in data1])
    data1 = sorted(data1, key=lambda x: x[0]) 
    # print(data1)
    makespan = np.array([x for x,y in data1])
    cost = np.array([y for x,y in data1])
    # if i%20 == 0:
    #     plt.plot([x for x,y in data1], [y for x,y in data1])
    makespan = (makespan - min_makespan)/delta_m
    cost = (cost - min_cost)/delta_c
    HV.append(calculateHV(makespan, cost))
print(f'CMSWC HV: {HV[-1]}')
plt.title('Pareto Front of CMSWC when Assigning task20,40,60,80 and 100')
plt.xlabel('Makespan')
plt.ylabel('Cost')
plt.savefig('images\\scatter2.png')