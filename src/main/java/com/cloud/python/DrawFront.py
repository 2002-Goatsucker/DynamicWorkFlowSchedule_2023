import numpy as np
import matplotlib.pyplot as plt

data1 = np.loadtxt('D:\\000000000WPS\\8创新实践3\\DynamicWorkFlowSchedule_2023\\src\\main\\resources\\result\\front_fogmp.txt')
data2 = np.loadtxt('D:\\000000000WPS\\8创新实践3\\DynamicWorkFlowSchedule_2023\\src\\main\\resources\\result\\front_dnsgaiib.txt')
data3 = np.loadtxt('D:\\000000000WPS\\8创新实践3\\DynamicWorkFlowSchedule_2023\\src\\main\\resources\\result\\front_idg.txt')
data4 = np.loadtxt('D:\\000000000WPS\\8创新实践3\\DynamicWorkFlowSchedule_2023\\src\\main\\resources\\result\\front_dmga.txt')
data5 = np.loadtxt('D:\\000000000WPS\\8创新实践3\\DynamicWorkFlowSchedule_2023\\src\\main\\resources\\result\\front_mb.txt')
data6 = np.loadtxt('D:\\000000000WPS\\8创新实践3\\DynamicWorkFlowSchedule_2023\\src\\main\\resources\\result\\front_cmswc.txt')


# 画出第二个文件中的数据,用蓝色

plt.plot([x for x,y in data2], [y for x,y in data2],'x',markersize='5')
plt.plot([x for x,y in data3], [y for x,y in data3],'+',markersize='5')
plt.plot([x for x,y in data4], [y for x,y in data4],'s',markersize='5')
plt.plot([x for x,y in data5], [y for x,y in data5],'>',markersize='5')
plt.plot([x for x,y in data6], [y for x,y in data6],'.',markersize='5')
plt.plot([x for x,y in data1], [y for x,y in data1], '*',markersize='5') 

plt.title('The Last Pareto Front on Montage_100')
plt.xlabel('Makespan(1/s)')
plt.ylabel('Cost($)')
plt.legend(loc='best', labels=['DNSGA-II-B','DNSGA-II-gIDG','DMGA','MBNSGA-II','CMSWC','FOGMP'])
# plt.show()
plt.savefig('images\\scatter.png')