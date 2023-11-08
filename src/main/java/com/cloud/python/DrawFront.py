import numpy as np
import matplotlib.pyplot as plt

data1 = np.loadtxt('D:\\000000000WPS\\8创新实践3\\DynamicWorkFlowSchedule_2023\\src\\main\\resources\\result\\result_cmswc_99.txt')
# data2 = np.loadtxt('D:\\000000000WPS\\8创新实践3\\DynamicWorkFlowSchedule_2023\\src\\main\\resources\\result\\result_nsgaii.txt')
data2 = np.loadtxt('D:\\000000000WPS\\8创新实践3\\DynamicWorkFlowSchedule_2023\\src\\main\\resources\\result\\result_cmswc_599.txt')
data3 = np.loadtxt('D:\\Tencent\\WeChat\\Wechat Files\\wxid_60j3lef9nr5n22\\FileStorage\\File\\2023-11\\result_nsgaii.txt')
# plt.plot([x for x,y in data1], [y for x,y in data1], 'ro') 

# 画出第二个文件中的数据,用蓝色
# plt.plot([x for x,y in data2], [y for x,y in data2],'bo')
plt.plot([x for x,y in data3], [y for x,y in data3],'o')

plt.title('The Last Pareto Front on Montage_100')
plt.xlabel('Makespan(1/s)')
plt.ylabel('Cost($)')
plt.legend(loc='best', labels=['DCMSWC','DNSGA-II'])
# plt.show()
plt.savefig('images\\scatter.png')